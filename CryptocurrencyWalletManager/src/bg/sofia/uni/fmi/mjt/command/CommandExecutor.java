package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.accounts.Account;
import bg.sofia.uni.fmi.mjt.accounts.AccountsManager;
import bg.sofia.uni.fmi.mjt.dto.Asset;
import bg.sofia.uni.fmi.mjt.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.AuthorizationException;
import bg.sofia.uni.fmi.mjt.exceptions.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.InsufficientAmountException;
import bg.sofia.uni.fmi.mjt.exceptions.TakenUsernameException;
import bg.sofia.uni.fmi.mjt.exceptions.WeakPasswordException;
import bg.sofia.uni.fmi.mjt.wallet.CryptoWallet;
import bg.sofia.uni.fmi.mjt.wallet.Wallet;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CommandExecutor {

    private static final int ENTERING_SYSTEM_COMMANDS_PARAMETERS_COUNT = 2;
    private static final int BUY_COMMAND_PARAMETERS_COUNT = 2;
    private static final int SELL_COMMAND_PARAMETERS_COUNT = 1;
    private static final int DEPOSIT_COMMAND_PARAMETERS_COUNT = 1;
    private static final int DEFAULT_COMMAND_PARAMETERS_COUNT = 0;
    private static final int MAX_OFFERINGS_DISPLAYED = 30;
    private final AccountsManager accountsManager;

    public CommandExecutor(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public AccountsManager getAccountsManager() {
        return accountsManager;
    }

    public Account authorize(Command userCommand) throws AuthorizationException {
        Account account;
        try {
            switch (userCommand.command()) {
                case LOGIN -> account = login(userCommand.arguments());
                case REGISTER -> account = register(userCommand.arguments(), new CryptoWallet());
                default -> throw new AuthorizationException("You must login/register first!");
            }
        } catch (AccountNotFoundException
                 | TakenUsernameException
                 | WeakPasswordException
                 | IllegalArgumentException e) {

            throw new AuthorizationException(e.getMessage());
        }

        return account;
    }

    private Account login(List<String> parameters) throws AccountNotFoundException {
        assertCorrectNumberOfParameters(ENTERING_SYSTEM_COMMANDS_PARAMETERS_COUNT, parameters.size());
        return accountsManager.login(parameters.get(0), parameters.get(1));
    }

    private Account register(List<String> parameters, Wallet wallet)
            throws TakenUsernameException, WeakPasswordException {

        assertCorrectNumberOfParameters(ENTERING_SYSTEM_COMMANDS_PARAMETERS_COUNT, parameters.size());
        return accountsManager.register(parameters.get(0), parameters.get(1), wallet);
    }

    public String execute(Command command, Wallet wallet, Map<String, Asset> assets) {

        String output;
        try {
            output = switch (command.command()) {
                case BUY -> buy(command.arguments(), wallet, assets);
                case SELL -> sell(command.arguments(), wallet, assets);
                case DEPOSIT -> deposit(command.arguments(), wallet);
                case LIST -> listOfferings(command.arguments(), assets);
                case WALLET_SUMMARY -> walletSummary(command.arguments(), wallet);
                case WALLET_OVERALL -> walletSummaryOverall(command.arguments(), wallet, assets);
                case HELP -> help(command.arguments());
                default -> "Cannot execute that command!";
            };
        } catch (InsufficientAmountException
                 | CryptoNotFoundException
                 | IllegalArgumentException e) {

            output = e.getMessage();
        }

        return output;
    }

    private String deposit(List<String> parameters, Wallet wallet) {
        assertCorrectNumberOfParameters(DEPOSIT_COMMAND_PARAMETERS_COUNT, parameters.size());

        double money = parseNumber(parameters.get(0));
        wallet.depositMoney(money);

        return String.format("Your balance is now %.2f$", wallet.getBalance());
    }

    private String buy(List<String> parameters, Wallet wallet, Map<String, Asset> assets)
            throws CryptoNotFoundException, InsufficientAmountException {

        assertCorrectNumberOfParameters(BUY_COMMAND_PARAMETERS_COUNT, parameters.size());

        String parameterOfferingPrefix = ArgumentType.OFFERING.getArgument();
        String offeringParameter = getParameter(parameters, parameterOfferingPrefix);

        String parameterMoneyPrefix = ArgumentType.MONEY.getArgument();
        String moneyParameter = getParameter(parameters, parameterMoneyPrefix);

        String assetID = offeringParameter.substring(parameterOfferingPrefix.length());
        double money = parseNumber(moneyParameter.substring(parameterMoneyPrefix.length()));

        assertAssetsMapIsNotNull(assets);
        Asset asset = getAsset(assetID, assets);
        double boughtAmount = wallet.buyAsset(assetID, asset.getPrice(), money);

        return String.format("Successfully bought %s of %s!", boughtAmount, assetID);
    }

    private String sell(List<String> parameters, Wallet wallet, Map<String, Asset> assets)
            throws CryptoNotFoundException {

        assertCorrectNumberOfParameters(SELL_COMMAND_PARAMETERS_COUNT, parameters.size());
        assertAssetsMapIsNotNull(assets);

        String parameterOfferingPrefix = ArgumentType.OFFERING.getArgument();
        String offeringParameter = getParameter(parameters, parameterOfferingPrefix);

        String assetID = offeringParameter.substring(parameterOfferingPrefix.length());
        Asset asset = getAsset(assetID, assets);
        double earnings = wallet.sellAsset(assetID, asset.getPrice());

        return String.format("Successfully earned %.2f from selling your %s!", earnings, assetID);
    }

    private String listOfferings(List<String> parameters, Map<String, Asset> assets) {
        assertCorrectNumberOfParameters(DEFAULT_COMMAND_PARAMETERS_COUNT, parameters.size());
        assertAssetsMapIsNotNull(assets);

        StringBuilder stringBuilder = new StringBuilder();
        assets.values()
                .stream()
                .sorted(Comparator.comparing(Asset::getPrice).reversed())
                .limit(MAX_OFFERINGS_DISPLAYED)
                .forEach(x -> stringBuilder.append(formatAsset(x)));

        return stringBuilder.toString();
    }

    private String walletSummary(List<String> parameters, Wallet wallet) {
        assertCorrectNumberOfParameters(DEFAULT_COMMAND_PARAMETERS_COUNT, parameters.size());

        for (int i = 0; i < 1_000_000; i++) {
            System.out.println(i);
        }

        return wallet.getWalletInfo();
    }

    private String walletSummaryOverall(List<String> parameters, Wallet wallet, Map<String, Asset> assets) {
        assertCorrectNumberOfParameters(DEFAULT_COMMAND_PARAMETERS_COUNT, parameters.size());
        assertAssetsMapIsNotNull(assets);

        return wallet.getWalletOverallStats(assets);
    }

    public String help(List<String> parameters) {
        assertCorrectNumberOfParameters(DEFAULT_COMMAND_PARAMETERS_COUNT, parameters.size());

        return """
                Commands and their usage:
                login [username] [password]
                register [username] [password]
                list-offerings
                deposit-money [amount]
                buy --offering=[offeringCode] --money=[amount]
                sell --offering=[offeringCode]
                get-wallet-summary
                get-wallet-overall-summary
                exit/disconnect
                """;
    }

    public void saveData() {
        accountsManager.saveAccounts();
    }

    private String getParameter(List<String> parameters, String searched) {
        return parameters.stream()
                .filter(x -> x.startsWith(searched))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid parameters given!"));
    }

    private void assertCorrectNumberOfParameters(int expectedParameters, int actualParameters) {
        if (expectedParameters != actualParameters) {
            throw new IllegalArgumentException("Invalid command parameters!");
        }
    }

    private void assertAssetsMapIsNotNull(Map<String, Asset> assets) {
        if (assets == null) {
            throw new IllegalArgumentException("Stock exchange information is missing!");
        }
    }

    private double parseNumber(String input) {
        double number;
        try {
            number = Double.parseDouble(input);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid number parameter given!");
        }

        return number;
    }

    private Asset getAsset(String assetID, Map<String, Asset> assets) throws CryptoNotFoundException {
        Asset asset = assets.get(assetID);
        if (asset == null) {
            throw new CryptoNotFoundException("There is not an asset with that name!");
        }

        return asset;
    }

    private String formatAsset(Asset asset) {
        return String.format("ID: %s, Name: %s, Price: %.2f%s",
                asset.getId(), asset.getName(), asset.getPrice(), System.lineSeparator());
    }
}
