package bg.sofia.uni.fmi.mjt.cocktail.command;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.InvalidIngredientException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CommandExecutor {
    private static final String CREATE = "create";
    private static final String GET = "get";
    private static final String GET_ALL = "all";
    private static final String GET_BY_NAME = "by-name";
    private static final String GET_BY_INGREDIENT = "by-ingredient";
    private static final String UNKNOWN_COMMAND = "Unknown command";

    private final CocktailStorage storage;

    public CommandExecutor(CocktailStorage storage) {
        this.storage = storage;
    }

    public String execute(Command cmd) {
        return switch (cmd.command()) {
            case CREATE -> createCocktail(cmd.arguments());
            case GET -> getCocktails(cmd.arguments());
            default -> UNKNOWN_COMMAND;
        };
    }

    private String createCocktail(List<String> arguments) {
        if (arguments.size() < 2) {
            return UNKNOWN_COMMAND;
        }

        String cocktailName = arguments.get(0);
        Set<Ingredient> ingredients = new HashSet<>();

        for (int i = 1; i < arguments.size(); i++) {
            try {
                ingredients.add(Ingredient.of(arguments.get(i)));
            }
            catch (InvalidIngredientException e) {
                return constructErrorMessage(e.getMessage());
            }
        }

        Cocktail cocktail = new Cocktail(cocktailName, ingredients);
        try {
            storage.createCocktail(cocktail);
        }
        catch (CocktailAlreadyExistsException e) {
            return constructErrorMessage(e.getMessage());
        }

        return "{\"status\":\"CREATED\"}";
    }

    private String getCocktails(List<String> arguments) {

        if (arguments.size() == 1 && arguments.get(0).equals(GET_ALL)) {
            return getCocktailsInJSONFormat(storage.getCocktails());
        }

        if (arguments.size() == 2 && arguments.get(0).equals(GET_BY_NAME)) {
            try {
                return "{\"status\":\"OK\",\"cocktail\":" +
                        getCocktailInJSONFormat(storage.getCocktail(arguments.get(1))) + "}";
            } catch (CocktailNotFoundException e) {
                return constructErrorMessage(e.getMessage());
            }
        }

        if (arguments.size() == 2 && arguments.get(0).equals(GET_BY_INGREDIENT)) {
            return getCocktailsInJSONFormat(storage.getCocktailsWithIngredient(arguments.get(1)));
        }

        return UNKNOWN_COMMAND;
    }

    private String constructErrorMessage(String exception) {
        return "{\"status\":\"ERROR\",\"errorMessage\":" + exception + "\"}";
    }

    private String getCocktailsInJSONFormat(Collection<Cocktail> cocktails) {
        StringBuilder stringBuilder = new StringBuilder("{\"status\":\"OK\",\"cocktails\":[");
        Iterator<Cocktail> cocktailIterator = cocktails.iterator();
        while (cocktailIterator.hasNext()) {
            stringBuilder.append(getCocktailInJSONFormat(cocktailIterator.next()));

            if (cocktailIterator.hasNext()) {
                stringBuilder.append(",");
            }
        }

        stringBuilder.append("]}");
        return stringBuilder.toString();
    }

    private String getCocktailInJSONFormat(Cocktail cocktail) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"name\":\"").append(cocktail.name()).append("\",\"ingredients\":[");

        Iterator<Ingredient> ingredientIterator = cocktail.ingredients().iterator();
        while (ingredientIterator.hasNext()) {
            Ingredient ingredient = ingredientIterator.next();
            stringBuilder.append("{\"name\":\"")
                    .append(ingredient.name())
                    .append("\",\"amount\":\"")
                    .append(ingredient.amount())
                    .append("\"}");

            if (ingredientIterator.hasNext()) {
                stringBuilder.append(",");
            }
        }

        stringBuilder.append("]}");

        return stringBuilder.toString();
    }
}
