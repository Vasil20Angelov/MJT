package bg.sofia.uni.fmi.mjt.cocktail;

import bg.sofia.uni.fmi.mjt.cocktail.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cocktail.server.Server;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.DefaultCocktailStorage;

public class Main {
    public static void main(String[] args) {
        final int port = 7777;
        CocktailStorage storage = new DefaultCocktailStorage();
        CommandExecutor commandExecutor = new CommandExecutor(storage);
        Server server = new Server(port, commandExecutor);
        server.start();
    }
}
