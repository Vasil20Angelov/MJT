package bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions;

public class CocktailAlreadyExistsException extends Exception {
    public CocktailAlreadyExistsException() {
    }

    public CocktailAlreadyExistsException(String message) {
        super(message);
    }
}
