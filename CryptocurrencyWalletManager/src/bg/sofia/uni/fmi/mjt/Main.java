package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.accounts.AccountsManager;
import bg.sofia.uni.fmi.mjt.coinAPI.CoinClient;
import bg.sofia.uni.fmi.mjt.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.server.Server;

import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) {
        CoinClient coinClient = new CoinClient(HttpClient.newBuilder().build());


        Server server = new Server(7777, coinClient, new CommandExecutor(new AccountsManager("SHA-256")));
        server.start();
    }
}
