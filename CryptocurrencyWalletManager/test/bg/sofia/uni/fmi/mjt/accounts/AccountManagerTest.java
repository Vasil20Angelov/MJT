package bg.sofia.uni.fmi.mjt.accounts;

import bg.sofia.uni.fmi.mjt.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.exceptions.TakenUsernameException;
import bg.sofia.uni.fmi.mjt.exceptions.WeakPasswordException;
import bg.sofia.uni.fmi.mjt.wallet.CryptoWallet;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountManagerTest {

    private final AccountsManager accountsManager = new AccountsManager("SHA-256");
    @Mock
    private final CryptoWallet cryptoWalletMock = Mockito.mock(CryptoWallet.class);

    @Test
    public void testRegisterWithNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> accountsManager.register(null, "somePassword", cryptoWalletMock),
                "Expected IllegalArgumentException to be thrown with null username!");
    }

    @Test
    public void testRegisterWithUsernameThatContainsWhitespace() {
        assertThrows(IllegalArgumentException.class,
                () -> accountsManager.register("white space", "somePassword", cryptoWalletMock),
                "Expected IllegalArgumentException to be thrown when username contains whitespace!");
    }

    @Test
    public void testRegisterWithNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> accountsManager.register("usr2", null, cryptoWalletMock),
                "Expected IllegalArgumentException to be thrown with null password!");
    }

    @Test
    public void testRegisterWithPasswordThatContainsWhitespace() {
        assertThrows(IllegalArgumentException.class,
                () -> accountsManager.register("usr2", "pass word", cryptoWalletMock),
                "Expected IllegalArgumentException to be thrown when password contains whitespace!");
    }

    @Test
    public void testRegisterWithNullCryptoWallet() {
        assertThrows(IllegalArgumentException.class,
                () -> accountsManager.register("usr2", "password1", null),
                "Expected IllegalArgumentException to be thrown with null cryptoWallet!");
    }

    @Test
    public void testRegisterWithShortPassword() {
        assertThrows(WeakPasswordException.class,
                () -> accountsManager.register("usr2", "weakP", cryptoWalletMock),
                "Expected WeakPasswordException to be thrown when password is too short!");
    }

    @Test
    public void testRegisterWithInvalidHashingAlgorithm() {
        AccountsManager accManager = new AccountsManager("Non existing hashing algorithm");

        assertThrows(PasswordEncryptionException.class,
                () -> accManager.register("usr2", "password1", cryptoWalletMock),
                "Expected PasswordEncryptionException to be thrown when password encryption fails!");
    }

    @Test
    public void testRegisterSuccessfullyAddsAnAccount() throws TakenUsernameException, WeakPasswordException {
        String username = "usr1";
        Account result = accountsManager.register(username, "password1", cryptoWalletMock);

        assertEquals(username, result.getUsername(), "The returned account is not the one that was added!");
    }

    @Test
    public void testRegisterWithExistingUsernameAccount() throws TakenUsernameException, WeakPasswordException {
        String username = "usr1";
        accountsManager.register(username, "password1", cryptoWalletMock);

        assertThrows(TakenUsernameException.class,
                () -> accountsManager.register(username, "password2", cryptoWalletMock),
                "Expected TakenUsernameException to be thrown when there is already an account with that name");
    }

    @Test
    public void testLoginWithNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> accountsManager.login(null, "somePassword"),
                "Expected IllegalArgumentException to be thrown with null username!");
    }

    @Test
    public void testLoginWithNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> accountsManager.login("usr2", null),
                "Expected IllegalArgumentException to be thrown with null password!");
    }

    @Test
    public void testLoginWithCorrectAccountData()
            throws TakenUsernameException, WeakPasswordException, AccountNotFoundException {

        String username = "usr1";
        String password = "password1";
        accountsManager.register(username, password, cryptoWalletMock);

        Account result = accountsManager.login(username, password);

        assertEquals(username, result.getUsername(), "The returned account is not the searched one!");
    }

    @Test
    public void testLoginWhenNoAccountsAreRegistered() {
        assertThrows(AccountNotFoundException.class,
                () -> accountsManager.login("someUsr1", "password"),
                "Expected AccountNotFoundException to be thrown when the account is not found by name!");
    }

    @Test
    public void testLoginWithWrongUsername() throws TakenUsernameException, WeakPasswordException {
        String username = "usr1";
        String wrongUsername = "usr2";
        String password = "password1";
        accountsManager.register(username, password, cryptoWalletMock);

        assertThrows(AccountNotFoundException.class,
                () -> accountsManager.login(wrongUsername, password),
                "Expected AccountNotFoundException to be thrown when the account is not found by name!");
    }

    @Test
    public void testLoginWithWrongPassword() throws TakenUsernameException, WeakPasswordException {
        String username = "usr1";
        String password = "password1";
        String wrongPassword = "password2";
        accountsManager.register(username, password, cryptoWalletMock);

        assertThrows(AccountNotFoundException.class,
                () -> accountsManager.login(username, wrongPassword),
                "Expected AccountNotFoundException to be thrown when the password is incorrect!");
    }
}
