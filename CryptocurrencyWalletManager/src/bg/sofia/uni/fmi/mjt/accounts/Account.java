package bg.sofia.uni.fmi.mjt.accounts;

import bg.sofia.uni.fmi.mjt.wallet.Wallet;

public class Account {
    private final String username;
    private final String password;
    private final Wallet wallet;

    public Account(String username, String password, Wallet wallet) {
        this.username = username;
        this.password = password;
        this.wallet = wallet;
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
