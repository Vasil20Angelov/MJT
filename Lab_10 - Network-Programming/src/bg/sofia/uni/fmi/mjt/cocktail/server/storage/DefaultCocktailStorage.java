package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCocktailStorage implements CocktailStorage {

    private final Map<String, Cocktail> cocktailsByCocktailName = new HashMap<>();
    private final Map<String, List<Cocktail>> cocktailsByIngredientName = new HashMap<>();

    @Override
    public void createCocktail(Cocktail cocktail) throws CocktailAlreadyExistsException {
        if (cocktailsByCocktailName.containsKey(cocktail.name().toLowerCase())) {
            throw new CocktailAlreadyExistsException("That cocktail have been created already!");
        }

        cocktailsByCocktailName.put(cocktail.name().toLowerCase(), cocktail);

        for (Ingredient ingredient : cocktail.ingredients()) {
            String ingredientName = ingredient.name().toLowerCase();
            cocktailsByIngredientName.putIfAbsent(ingredientName, new ArrayList<>());
            cocktailsByIngredientName.get(ingredientName).add(cocktail);
        }
    }

    @Override
    public Collection<Cocktail> getCocktails() {
        return cocktailsByCocktailName.values();
    }

    @Override
    public Collection<Cocktail> getCocktailsWithIngredient(String ingredientName) {
        Collection<Cocktail> cocktails = cocktailsByIngredientName.get(ingredientName.toLowerCase());
        return cocktails != null ? cocktails : new ArrayList<>();
    }

    @Override
    public Cocktail getCocktail(String name) throws CocktailNotFoundException {
        String searchedCocktailName = name.toLowerCase();
        if (!cocktailsByCocktailName.containsKey(searchedCocktailName)) {
            throw new CocktailNotFoundException("There is no such cocktail!");
        }

        return cocktailsByCocktailName.get(searchedCocktailName);
    }
}
