package bg.sofia.uni.fmi.mjt.accounts;

import bg.sofia.uni.fmi.mjt.wallet.Wallet;
import com.google.gson.annotations.Expose;

public class Account {
    @Expose
    private final String username;
    @Expose
    private final String password;
    @Expose
    private final Wallet wallet;

    private Boolean isLoggedIn;

    public Account(String username, String password, Wallet wallet) {
        this.username = username;
        this.password = password;
        this.wallet = wallet;
    }

    public Boolean isAlreadyLoggedIn() {
        if (isLoggedIn == null) {
            isLoggedIn = false;
        }
        return isLoggedIn;
    }

    public void changeLoggedInState() {
        if (isLoggedIn == null) {
            isLoggedIn = true;
        } else {
            isLoggedIn = !isLoggedIn;
        }
    }

    public String getUsername() {
        return username;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public boolean isCorrectPassword(String givenPassword) {
        return password.equals(givenPassword);
    }
}
