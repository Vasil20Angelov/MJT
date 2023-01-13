package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DefaultCocktailStorageTest {

    private DefaultCocktailStorage cocktailStorage = new DefaultCocktailStorage();

    @Test
    public void testCreateCocktailAddsCocktailToTheCollection() {

        Cocktail cocktail = new Cocktail("c1", new HashSet<>());
        List<Cocktail> expected = new ArrayList<>() { {add(cocktail);} };

        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail), "The call should not throw!");
        assertIterableEquals(expected, cocktailStorage.getCocktails(), "Unexpected returned collection!");
    }

    @Test
    public void testCreateCocktailThrowsWhenTryingToAddExistingOne() {
        Cocktail cocktail = new Cocktail("c1", new HashSet<>());

        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail), "The call should not throw!");
        assertThrows(CocktailAlreadyExistsException.class, () -> cocktailStorage.createCocktail(cocktail),
                "Expected CocktailAlreadyExistsException to be thrown");
    }

    @Test
    public void testGetCocktailWithIngredientWithIngredientReturnsTheCorrectSet() {
        Ingredient ingredient1 = new Ingredient("vodKa", "100mil");
        Ingredient ingredient2 = new Ingredient("whiSky", "200mil");
        Ingredient ingredient3 = new Ingredient("Wine", "300mil");
        Ingredient ingredient4 = new Ingredient("rakiq", "500mil");

        Set<Ingredient> ingredientSet1 = new HashSet<>() { {
            add(ingredient1);
            add(ingredient2);
        } };
        Set<Ingredient> ingredientSet2 = new HashSet<>() { {
            add(ingredient2);
            add(ingredient3);
        } };
        Set<Ingredient> ingredientSet3 = new HashSet<>() { {
            add(ingredient1);
            add(ingredient3);
            add(ingredient4);
        } };

        Cocktail cocktail1 = new Cocktail("c1", ingredientSet1);
        Cocktail cocktail2 = new Cocktail("c2", ingredientSet2);
        Cocktail cocktail3 = new Cocktail("c3", ingredientSet3);

        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail1), "The call should not throw!");
        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail2), "The call should not throw!");
        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail3), "The call should not throw!");

        Set<Cocktail> result = new HashSet<>(cocktailStorage.getCocktailsWithIngredient("whisky"));
        assertTrue(result.contains(cocktail1));
        assertTrue(result.contains(cocktail2));
        assertFalse(result.contains(cocktail3));
    }

    @Test
    public void testGetCocktailWithIngredientReturnsEmptyCollectionWhenThereArentCocktailsWithThatIngredient() {
        Ingredient ingredient = new Ingredient("vodKa", "100mil");

        Set<Ingredient> ingredientSet = new HashSet<>() { {
            add(ingredient);
        } };

        Cocktail cocktail = new Cocktail("c1", ingredientSet);

        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail), "The call should not throw!");
        assertTrue(cocktailStorage.getCocktailsWithIngredient("Whisky").isEmpty(),
                "The returned collection should be empty!");
    }

    @Test
    public void testGetCocktailsReturnsEmptyCollectionWhenThereWereNotAddedAnyCocktailsBefore() {
        assertTrue(cocktailStorage.getCocktails().isEmpty(),
                "The returned collection should be empty!");
    }

    @Test
    public void testGetCocktailThrowsWhenTheCocktailDoesNotExistInTheStorage() {
        assertThrows(CocktailNotFoundException.class, () -> cocktailStorage.getCocktail("c1"),
                "Expected CocktailNotFoundException to be thrown!");
    }

    @Test
    public void testGetCocktailReturnsTheCorrectCocktail() {
        Cocktail cocktail1 = new Cocktail("c1", new HashSet<>());
        Cocktail cocktail2 = new Cocktail("c2", new HashSet<>());

        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail1), "The call should not throw!");
        assertDoesNotThrow(() -> cocktailStorage.createCocktail(cocktail2), "The call should not throw!");

        try {
            assertEquals(cocktail1, cocktailStorage.getCocktail("C1"), "Unexpected returned cocktail!");
            assertEquals(cocktail2, cocktailStorage.getCocktail("c2"), "Unexpected returned cocktail!");
        } catch (CocktailNotFoundException e) {
            fail("The calls must not throw!");
        }
    }
}
