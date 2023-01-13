package bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions;

public class InvalidIngredientException extends Exception {
    public InvalidIngredientException() {
    }

    public InvalidIngredientException(String message) {
        super(message);
    }
}
