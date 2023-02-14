package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.accounts.Account;
import bg.sofia.uni.fmi.mjt.coinAPI.CoinClient;
import bg.sofia.uni.fmi.mjt.command.Command;
import bg.sofia.uni.fmi.mjt.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.command.CommandType;
import bg.sofia.uni.fmi.mjt.dto.Asset;
import bg.sofia.uni.fmi.mjt.exceptions.AuthorizationException;
import bg.sofia.uni.fmi.mjt.exceptions.InvalidCommandException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final Path LOGGER = Path.of("./log.txt");
    private static final int BUFFER_SIZE = 2048;
    private static final String HOST = "localhost";
    private static final int MAX_CACHED_TIME_IN_MINUTES = 30;
    private static final int WAIT_TIME_TO_TRY_RETRIEVE_INFO_ON_FAIL = 30000;
    private static final int INITIAL_DELAY_TIME = 0;
    private final CommandExecutor commandExecutor;

    private final int port;
    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;
    private final CoinClient coinClient;
    private final Map<SocketAddress, Account> accounts = new HashMap<>();
    private Map<String, Asset> assetMap;

    public Server(int port, CoinClient coinClient, CommandExecutor commandExecutor) {
        this.port = port;
        this.coinClient = coinClient;
        this.commandExecutor = commandExecutor;

        File file = LOGGER.toFile();
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Couldn't create a logger file!");
        }
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             ScheduledExecutorService executor = Executors.newScheduledThreadPool(1)) {

            executor.scheduleAtFixedRate(this::loadAssetMap, INITIAL_DELAY_TIME,
                    MAX_CACHED_TIME_IN_MINUTES, TimeUnit.MINUTES);

            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            handleClientRequest(key);
                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    logException(e, "Error occurred while processing client request: ");
                }
            }
        } catch (IOException e) {
            logException(e, "Server is down!");
        } finally {
            commandExecutor.saveData();
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void handleClientRequest(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientInput = getClientInput(clientChannel);
        System.out.println(clientInput);
        if (clientInput == null) {
            return;
        }

        Command userCommand;
        try {
            userCommand = Command.of(clientInput);
        } catch (InvalidCommandException e) {
            writeClientOutput(clientChannel, e.getMessage());
            return;
        }

        Account account = getAccount(clientChannel, userCommand);
        if (account != null && !userCommand.isEntryCommand()) {
            String output = commandExecutor.execute(userCommand, account.getWallet(), assetMap);
            writeClientOutput(clientChannel, output);
        }
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private Account getAccount(SocketChannel clientChannel, Command userCommand) throws IOException {
        Account account = accounts.get(clientChannel.getRemoteAddress());
        if (account == null) {

            if (userCommand.command() == CommandType.EXIT) {
                return null;
            }
            if (userCommand.command() == CommandType.HELP) {
                writeClientOutput(clientChannel, commandExecutor.help(userCommand.arguments()));
                return null;
            }

            try {
                account = commandExecutor.authorize(userCommand);
            } catch (AuthorizationException e) {
                writeClientOutput(clientChannel, e.getMessage());
                return null;
            }

            accounts.put(clientChannel.getRemoteAddress(), account);
            writeClientOutput(clientChannel, "Successfully logged in!");

        } else if (userCommand.command() == CommandType.EXIT) {
            account.changeLoggedInState();
            return null;
        } else if (userCommand.isEntryCommand()) {
            writeClientOutput(clientChannel, "Cannot execute that operation!");
        }

        return account;
    }

    private synchronized void loadAssetMap() {
        Map<String, Asset> newInfo = new HashMap<>();
        try {
            coinClient.getOfferingsList()
                    .stream()
                    .filter(Asset::isCrypto)
                    .forEach(x -> newInfo.put(x.getId(), x));
        } catch (RuntimeException e) {
            logException(e, "Error from CoinClient: ");
            try {
                Thread.sleep(WAIT_TIME_TO_TRY_RETRIEVE_INFO_ON_FAIL);
                loadAssetMap();
            } catch (InterruptedException ex) {
                logException(ex, "Trying to retrieve information has been suspended!");
            }
        }

        assetMap = newInfo;
    }

    private void logException(Exception e, String baseMessage) {
        String errorMessage = baseMessage
                + System.lineSeparator()
                + e.getMessage()
                + Arrays.toString(e.getStackTrace())
                + System.lineSeparator();

        try {
            Files.writeString(LOGGER, errorMessage, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.err.println("Couldn't log an error!");
        }
    }
}
