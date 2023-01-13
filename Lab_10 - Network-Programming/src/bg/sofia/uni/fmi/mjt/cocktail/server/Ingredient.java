package bg.sofia.uni.fmi.mjt.cocktail.server;

import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.InvalidIngredientException;

import java.util.Objects;

public record Ingredient(String name, String amount) {

    public static Ingredient of(String ingredient) throws InvalidIngredientException {
        int index = ingredient.indexOf('=');
        if (index == -1 || index == 0 || index == ingredient.length() - 1) {
            throw new InvalidIngredientException("Invalid ingredient format!");
        }

        ingredient = ingredient.toLowerCase();
        String name = ingredient.substring(0, index);
        String amount = ingredient.substring(index + 1);

        return new Ingredient(name, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
