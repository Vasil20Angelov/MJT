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
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandExecutorTest {
    @Mock
    private final AccountsManager accountsManagerMock = Mockito.mock(AccountsManager.class);

    @Mock
    private final Wallet walletMock = Mockito.mock(Wallet.class);

    private final CommandExecutor commandExecutor = new CommandExecutor(accountsManagerMock);

    @Test
    public void testAuthorizeWithNullCommand() {
        assertThrows(IllegalArgumentException.class, () -> commandExecutor.authorize(null),
                "Expected IllegalArgumentException to be thrown when the given command is null!");
    }

    @Test
    public void testAuthorizeWithNonEntryCommand() {
        Command command = new Command(CommandType.SELL, new ArrayList<>());
        assertThrows(AuthorizationException.class, () -> commandExecutor.authorize(command),
                "Expected AuthorizationException to be thrown when the command is not login or register!");
    }

    @Test
    public void testAuthorizeExecutesRegisterWithInvalidArgumentList() {

        Command command = new Command(CommandType.REGISTER, List.of("a1"));

        assertThrows(AuthorizationException.class, () -> commandExecutor.authorize(command),
                "Expected AuthorizationException to be thrown when the passed arguments are unexpected!");
    }

    @Test
    public void testAuthorizeExecutesRegister()
            throws TakenUsernameException, WeakPasswordException, AuthorizationException {

        String username = "a1";
        String password = "123456";
        Wallet wallet = new CryptoWallet();

        Command command = new Command(CommandType.REGISTER, List.of(username, password));
        Account expected = new Account(username, password, wallet);

        when(accountsManagerMock.register(username, password, wallet)).thenReturn(expected);
        doNothing().when(accountsManagerMock).persistData();

        Account result = commandExecutor.authorize(command);

        assertEquals(expected, result, "Unexpected account returned!");
        assertTrue(result.isAlreadyLoggedIn(), "The account must be marked as loggedIn!");
        verify(accountsManagerMock, times(1)).persistData();
    }

    @Test
    public void testAuthorizeExecutesLoginWithInvalidArgumentList() {
        Command command = new Command(CommandType.LOGIN, List.of("a1"));

        assertThrows(AuthorizationException.class, () -> commandExecutor.authorize(command),
                "Expected AuthorizationException to be thrown when the passed arguments are unexpected!");
    }

    @Test
    public void testAuthorizeExecutesLoginWhenTryingToLogInAccountThatIsBeingUsed() throws AccountNotFoundException {
        String username = "a1";
        String password = "123456";
        Wallet wallet = new CryptoWallet();

        Command command = new Command(CommandType.LOGIN, List.of(username, password));
        Account account = new Account(username, password, wallet);
        account.changeLoggedInState();

        when(accountsManagerMock.login(username, password)).thenReturn(account);

        assertThrows(AuthorizationException.class, () -> commandExecutor.authorize(command),
                "Expected AuthorizationException to be thrown when someone else is using the account!");
    }

    @Test
    public void testAuthorizeExecutesLogin() throws AuthorizationException, AccountNotFoundException {
        String username = "a1";
        String password = "123456";
        Wallet wallet = new CryptoWallet();

        Command command = new Command(CommandType.LOGIN, List.of(username, password));
        Account expected = new Account(username, password, wallet);

        when(accountsManagerMock.login(username, password)).thenReturn(expected);

        Account result = commandExecutor.authorize(command);

        assertEquals(expected, result, "Unexpected account returned!");
        assertTrue(result.isAlreadyLoggedIn(), "The account must be marked as loggedIn!");
    }

    @Test
    public void testExecuteWithNullCommand() {
        assertThrows(IllegalArgumentException.class,
                () -> commandExecutor.execute(null, null, null),
                "Expected IllegalArgumentException to be thrown when the given command is null!");
    }

    @Test
    public void testExecuteWithLoginCommand()
    {
        Command command = new Command(CommandType.LOGIN, new ArrayList<>());
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Cannot execute that command!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithRegisterCommand()
    {
        Command command = new Command(CommandType.REGISTER, new ArrayList<>());
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Cannot execute that command!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithDepositCommandAndUnexpectedArgumentsCount()
    {
        Command command = new Command(CommandType.DEPOSIT, List.of("12", "34"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid command parameters!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithDepositCommandAndInvalidNumberAsParameter()
    {
        Command command = new Command(CommandType.DEPOSIT, List.of("1a3"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid number parameter given!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithDepositCommandAndValidArguments()
    {
        Command command = new Command(CommandType.DEPOSIT, List.of("12.5"));

        doNothing().when(walletMock).depositMoney(12.5);
        when(walletMock.getBalance()).thenReturn(30d);
        doNothing().when(accountsManagerMock).persistData();

        String output = commandExecutor.execute(command, walletMock, null);

        assertEquals("Your balance is now 30,00$", output, "Unexpected output returned!");
        verify(accountsManagerMock, times(1)).persistData();
    }

    @Test
    public void testExecuteWithBuyCommandAndUnexpectedArgumentsCount()
    {
        Command command = new Command(CommandType.BUY, List.of("offering=", "12", "34"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid command parameters!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithBuyCommandWithMissingOfferingArgument()
    {
        Command command = new Command(CommandType.BUY, List.of("12", "--money=32"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid parameters given!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithBuyCommandWithMissingMoneyArgument()
    {
        Command command = new Command(CommandType.BUY, List.of("--offering=BTC", "--cash=32"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid parameters given!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithBuyCommandWithInvalidMoneyNumber()
    {
        Command command = new Command(CommandType.BUY, List.of("--offering=BTC", "--money=3a2"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid number parameter given!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithBuyCommandWithNonExistingCrypto()
    {
        Command command = new Command(CommandType.BUY, List.of("--offering=BTC", "--money=32"));
        Map<String, Asset> assets = Map.of("ETH", new Asset("ETH", "Ethereum", 12.3, 1));
        String output = commandExecutor.execute(command, null, assets);

        assertEquals("There is not an asset with that name!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithBuyCommandWithMissingAssetsMap()
    {
        Command command = new Command(CommandType.BUY, List.of("--offering=BTC", "--money=32"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Stock exchange information is missing!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithBuyCommandWithNotEnoughMoneyInTheWallet() throws InsufficientAmountException {
        Command command = new Command(CommandType.BUY, List.of("--offering=BTC", "--money=30"));
        Map<String, Asset> assets = Map.of("BTC", new Asset("BTC", "Bitcoin", 46.3, 1));

        when(walletMock.buyAsset("BTC", 46.3, 30))
                .thenThrow(new InsufficientAmountException("Not enough money!"));

        String output = commandExecutor.execute(command, walletMock, assets);

        assertEquals("Not enough money!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithBuyCommandWithValidArguments() throws InsufficientAmountException {
        Command command = new Command(CommandType.BUY, List.of("--offering=BTC", "--money=23.15"));
        Map<String, Asset> assets = Map.of("BTC", new Asset("BTC", "Bitcoin", 46.3, 1));

        when(walletMock.buyAsset("BTC", 46.3, 23.15)).thenReturn(0.5d);
        doNothing().when(accountsManagerMock).persistData();

        String output = commandExecutor.execute(command, walletMock, assets);

        assertEquals("Successfully bought 0.5 of BTC!", output, "Unexpected output returned!");
        verify(accountsManagerMock, times(1)).persistData();
    }

    @Test
    public void testExecuteWithSellCommandAndUnexpectedArgumentsCount()
    {
        Command command = new Command(CommandType.SELL, List.of("offering=", "BTC"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid command parameters!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithSellCommandWithMissingOfferingArgument()
    {
        Command command = new Command(CommandType.SELL, List.of("BTC"));
        String output = commandExecutor.execute(command, null, new HashMap<>());

        assertEquals("Invalid parameters given!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithSellCommandWithNonExistingCrypto()
    {
        Command command = new Command(CommandType.SELL, List.of("--offering=BTC"));
        Map<String, Asset> assets = Map.of("ETH", new Asset("ETH", "Ethereum", 12.3, 1));
        String output = commandExecutor.execute(command, null, assets);

        assertEquals("There is not an asset with that name!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithSellCommandAndMissingAssetsMap()
    {
        Command command = new Command(CommandType.SELL, List.of("--offering=ETH"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Stock exchange information is missing!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithSellCommandWithValidArguments() throws CryptoNotFoundException {
        Command command = new Command(CommandType.SELL, List.of("--offering=BTC"));
        Map<String, Asset> assets = Map.of("BTC", new Asset("BTC", "Bitcoin", 46.3, 1));

        when(walletMock.sellAsset("BTC", 46.3)).thenReturn(12.432);
        doNothing().when(accountsManagerMock).persistData();

        String output = commandExecutor.execute(command, walletMock, assets);

        assertEquals("Successfully earned 12,43 from selling your BTC!", output,
                "Unexpected output returned!");
        verify(accountsManagerMock, times(1)).persistData();
    }

    @Test
    public void testExecuteWithListCommandAndUnexpectedArgumentsCount()
    {
        Command command = new Command(CommandType.LIST, List.of("show"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid command parameters!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithListCommandAndMissingAssetsMap()
    {
        Command command = new Command(CommandType.LIST, new ArrayList<>());
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Stock exchange information is missing!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithListCommandAndValidArguments()
    {
        Command command = new Command(CommandType.LIST, new ArrayList<>());

        Asset asset1 = new Asset("ETH", "Ethereum", 2.39, 1);
        Asset asset2 = new Asset("BTC", "Bitcoin", 12.234, 1);
        Asset asset3 = new Asset("Au", "Gold", 5.199, 0);

        Map<String, Asset> assetMap = Map.of("ETH", asset1, "BTC", asset2, "Au", asset3);

        String expectedOutput = "ID: BTC, Name: Bitcoin, Price: 12,23" + System.lineSeparator() +
                "ID: Au, Name: Gold, Price: 5,20" + System.lineSeparator() +
                "ID: ETH, Name: Ethereum, Price: 2,39" + System.lineSeparator();

        String output = commandExecutor.execute(command, null, assetMap);

        assertEquals(expectedOutput, output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithWalletSummaryCommandAndUnexpectedArgumentsCount()
    {
        Command command = new Command(CommandType.WALLET_SUMMARY, List.of("show"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid command parameters!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithWalletSummaryCommandWithoutArguments()
    {
        Command command = new Command(CommandType.WALLET_SUMMARY, new ArrayList<>());
        when(walletMock.getWalletInfo()).thenReturn("some info");

        String output = commandExecutor.execute(command, walletMock, null);

        assertEquals("some info", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithWalletOverallCommandAndUnexpectedArgumentsCount()
    {
        Command command = new Command(CommandType.WALLET_OVERALL, List.of("show"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid command parameters!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithWalletOverallCommandAndMissingAssetsMap()
    {
        Command command = new Command(CommandType.WALLET_OVERALL, new ArrayList<>());
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Stock exchange information is missing!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithWalletOverallCommandWithValidArguments()
    {
        Command command = new Command(CommandType.WALLET_OVERALL, new ArrayList<>());
        Map<String, Asset> assetMap = Map.of("a1", new Asset("a1", "aa1", 12, 1));
        when(walletMock.getWalletOverallStats(assetMap)).thenReturn("some info");

        String output = commandExecutor.execute(command, walletMock, assetMap);

        assertEquals("some info", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithHelpCommandAndUnexpectedArgumentsCount()
    {
        Command command = new Command(CommandType.HELP, List.of("show"));
        String output = commandExecutor.execute(command, null, null);

        assertEquals("Invalid command parameters!", output, "Unexpected output returned!");
    }

    @Test
    public void testExecuteWithHelpCommandWithoutArguments()
    {
        Command command = new Command(CommandType.HELP, new ArrayList<>());

        String expected = """
                Commands and their usage:
                login [username] [password]
                register [username] [password]
                list-offerings
                deposit-money [amount]
                buy --offering=[offeringCode] --money=[amount]
                sell --offering=[offeringCode]
                get-wallet-summary
                get-wallet-overall-summary
                exit
                """;

        String output = commandExecutor.execute(command, null, null);

        assertEquals(expected, output, "Unexpected output returned!");
    }

    @Test
    public void testSaveData() {
        doNothing().when(accountsManagerMock).persistData();
        commandExecutor.saveData();

        verify(accountsManagerMock, times(1)).persistData();
    }
}