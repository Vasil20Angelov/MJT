package bg.sofia.uni.fmi.mjt.smartfridge.recipe;

import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RecipeAPI implements Recipe {

    private Map<String, Ingredient<? extends Storable>> ingredients = new HashMap<>();

    @Override
    public Set<Ingredient<? extends Storable>> getIngredients() {
        return new HashSet<>(ingredients.values());
    }

    @Override
    public void addIngredient(Ingredient<? extends Storable> ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Invalid ingredient");
        }

        ingredients.put(ingredient.item().getName(), new DefaultIngredient<>(ingredient.item(), ingredient.quantity()));
    }

    @Override
    public void removeIngredient(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("Invalid ingredient");
        }

        ingredients.remove(itemName);
    }
}
