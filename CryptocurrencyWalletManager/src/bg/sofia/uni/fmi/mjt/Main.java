package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.accounts.AccountsManager;
import bg.sofia.uni.fmi.mjt.coin.api.CoinClient;
import bg.sofia.uni.fmi.mjt.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.server.Server;

import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) {

        CoinClient coinClient = new CoinClient(HttpClient.newBuilder().build());

        final int port = 7778;
        final String databaseFilePath = "./data/database.txt";
        final String hashingAlgorithm = "SHA-256";

        AccountsManager accountsManager = new AccountsManager(hashingAlgorithm, databaseFilePath);
        Server server = new Server(port, coinClient, new CommandExecutor(accountsManager));
        server.start();
    }
}
