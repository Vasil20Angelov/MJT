package bg.sofia.uni.fmi.mjt.accounts;

import bg.sofia.uni.fmi.mjt.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.exceptions.TakenUsernameException;
import bg.sofia.uni.fmi.mjt.exceptions.WeakPasswordException;
import bg.sofia.uni.fmi.mjt.wallet.CryptoWallet;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountManagerTest {

    private final static String TEMP_FILE = "tempFile.txt";
    private final static String HASHING_ALG = "SHA-256";
    private final static String username = "user1";
    private final static String password = "123456";

    @TempDir
    private Path tempDir;

    private Path pathToFile;

    @Mock
    private final CryptoWallet cryptoWalletMock = Mockito.mock(CryptoWallet.class);

    private AccountsManager accountsManager;

    @BeforeEach
    public void setUp() {

        // The used password in the account is the encrypted: "123456"
        Account account1 = new Account(username,
                "9235596fb1ef43e8912d4e800b1731cc7da708ec4b709528b5d867b036b3ae81",
                cryptoWalletMock);

        Map<String, Account> accountMap = Map.of(username, account1);

        Gson gson = new Gson();
        String output = gson.toJson(accountMap);

        try {
            pathToFile = tempDir.resolve(TEMP_FILE);
            try (BufferedWriter bw = Files.newBufferedWriter(pathToFile)) {
                bw.write(output);
            }

        } catch (InvalidPathException | IOException ex) {
            System.err.println("Error creating temporary test file in " + this.getClass().getSimpleName());
        }

        accountsManager = new AccountsManager(HASHING_ALG, pathToFile.toString());
    }

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
        var accManager = new AccountsManager("Non existing hashing algorithm", pathToFile.toString());

        assertThrows(PasswordEncryptionException.class,
                () -> accManager.register("usr2", "password1", cryptoWalletMock),
                "Expected PasswordEncryptionException to be thrown when password encryption fails!");
    }

    @Test
    public void testRegisterSuccessfullyAddsAnAccount() throws TakenUsernameException, WeakPasswordException {
        String username = "usr2";
        Account result = accountsManager.register(username, "password2", cryptoWalletMock);

        assertEquals(username, result.getUsername(), "The returned account is not the one that was added!");
    }

    @Test
    public void testRegisterWithExistingUsernameAccount() throws TakenUsernameException, WeakPasswordException {
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
    public void testLoginWithCorrectAccountData() throws AccountNotFoundException {
        Account result = accountsManager.login(username, password);
        assertEquals(username, result.getUsername(), "The returned account is not the searched one!");
    }

    @Test
    public void testLoginWhenNoAccountsAreRegistered() throws IOException {
        pathToFile = tempDir.resolve("tempFile2.txt");
        File file = pathToFile.toFile();
        file.createNewFile();

        accountsManager = new AccountsManager(HASHING_ALG, pathToFile.toString());

        assertThrows(AccountNotFoundException.class,
                () -> accountsManager.login("someUsr1", "password"),
                "Expected AccountNotFoundException to be thrown when the account is not found by name!");
    }

    @Test
    public void testLoginWithWrongUsername() {
        String wrongUsername = "wrongName";
        assertThrows(AccountNotFoundException.class,
                () -> accountsManager.login(wrongUsername, password),
                "Expected AccountNotFoundException to be thrown when the account is not found by name!");
    }

    @Test
    public void testLoginWithWrongPassword()  {
        String wrongPassword = "wrongPass";

        assertThrows(AccountNotFoundException.class,
                () -> accountsManager.login(username, wrongPassword),
                "Expected AccountNotFoundException to be thrown when the password is incorrect!");
    }
}
