package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.accounts.AccountsManager;
import bg.sofia.uni.fmi.mjt.coinAPI.CoinClient;
import bg.sofia.uni.fmi.mjt.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.server.Server;

import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) {

        CoinClient coinClient = new CoinClient(HttpClient.newBuilder().build());
        final String filePath = "./data/database.txt";

        AccountsManager accountsManager = new AccountsManager("SHA-256", filePath);
        Server server = new Server(7778, coinClient, new CommandExecutor(accountsManager));
        server.start();
    }
}
