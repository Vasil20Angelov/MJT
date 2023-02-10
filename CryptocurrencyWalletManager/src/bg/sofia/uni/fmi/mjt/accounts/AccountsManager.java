package bg.sofia.uni.fmi.mjt.accounts;

import bg.sofia.uni.fmi.mjt.converters.InterfaceSerializer;
import bg.sofia.uni.fmi.mjt.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.PasswordEncryptionException;
import bg.sofia.uni.fmi.mjt.exceptions.TakenUsernameException;
import bg.sofia.uni.fmi.mjt.exceptions.WeakPasswordException;
import bg.sofia.uni.fmi.mjt.wallet.CryptoWallet;
import bg.sofia.uni.fmi.mjt.wallet.Wallet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AccountsManager {
    private static final String FILE_PATH = "./database.txt";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String PASSWORD_SALT = "kjva!ne235roiv";
    private static final int SIGNUM = 1;
    private static final int RADIX = 16;
    private final String hashingAlgorithm;

    private Map<String, Account> accounts = new HashMap<>();

    public AccountsManager(String hashingAlgorithm) {
        this.hashingAlgorithm = hashingAlgorithm;
        loadAccounts();
    }

    public Account register(String username, String password, Wallet cryptoWallet)
            throws TakenUsernameException, WeakPasswordException {

        validateNewUsername(username);
        validatePassword(password);

        if (cryptoWallet == null) {
            throw new IllegalArgumentException("Invalid crypto wallet!");
        }

        String encryptedPassword = encryptPassword(password);
        Account account = new Account(username, encryptedPassword, cryptoWallet);
        accounts.put(username, account);
        saveAccounts();

        return account;
    }

    public Account login(String username, String password) throws AccountNotFoundException {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Invalid username or password!");
        }

        Account account = accounts.get(username);
        String encryptedPassword = encryptPassword(password);
        if (account == null || !account.isCorrectPassword(encryptedPassword)) {
            throw new AccountNotFoundException("Incorrect username or password!");
        }

        return account;
    }

    private void validateNewUsername(String username) throws TakenUsernameException {
        validateString(username, "Invalid username! The username cannot be null or contain whitespaces!");

        if (accounts.containsKey(username)) {
            throw new TakenUsernameException("The username has been taken!");
        }
    }

    private void validatePassword(String password) throws WeakPasswordException {
        validateString(password, "Invalid password! The password cannot be null or contain whitespaces!");

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new WeakPasswordException(String.format(
                    "The selected password is too weak! Make it at least %s symbols long!", MIN_PASSWORD_LENGTH));
        }
    }

    private void validateString(String string, String message) {
        if (string == null || string.contains(" ")) {
            throw new IllegalArgumentException(message);
        }
    }

    private String encryptPassword(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(hashingAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordEncryptionException("Password encryption failed!", e);
        }

        String saltedPassword = PASSWORD_SALT + password;
        byte[] encryptedPassword = md.digest(saltedPassword.getBytes());
        BigInteger number = new BigInteger(SIGNUM, encryptedPassword);

        return number.toString(RADIX);
    }

    public void saveAccounts() {
        Gson gson = new Gson();
        String output = gson.toJson(accounts);
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(output);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save the data!");
        }
    }

    private void loadAccounts() {
        String output;
        try {
            output = Files.readString(Path.of(FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load the data!");
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Wallet.class, new InterfaceSerializer<>(CryptoWallet.class))
                .create();

        TypeToken<Map<String, Account>> type = new TypeToken<>() { };
        accounts = gson.fromJson(output, type.getType());
    }
}
