package bg.sofia.uni.fmi.mjt.smartfridge;

import bg.sofia.uni.fmi.mjt.smartfridge.exception.FridgeCapacityExceededException;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.InsufficientQuantityException;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.StorableItem;

import java.util.*;

public class SmartFridge implements SmartFridgeAPI {

    private int totalCapacity;
    private int takenCapacity = 0;
    private Map<String, Set<StorableItem>> items = new HashMap<>();
    
    public SmartFridge(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    @Override
    public <E extends Storable> void store(E item, int quantity) throws FridgeCapacityExceededException {
        if (item == null || quantity < 1) {
            throw new IllegalArgumentException("Invalid input");
        }

        if (takenCapacity + quantity > totalCapacity) {
            throw new FridgeCapacityExceededException();
        }

        if (!items.containsKey(item.getName())) {
            items.put(item.getName(), new TreeSet<>());
        }

        boolean quantityUpdated = false;
        for (StorableItem storableItem : items.get(item.getName())) {
            if (sameItems(storableItem.getItem(), item)) {
                storableItem.setQuantity(storableItem.getQuantity() + quantity);
                quantityUpdated = true;
            }
        }

        if (!quantityUpdated) {
            items.get(item.getName()).add(new StorableItem<>(item, quantity));
        }

        takenCapacity += quantity;
    }

    private <E extends Storable> boolean sameItems(E item1, E item2) {
        return item1.getName().equals(item2.getName())
                && item1.getExpiration().equals(item2.getExpiration()) &&
                item1.getType().equals(item2.getType());
    }

    @Override
    public List<? extends Storable> retrieve(String itemName) {
        validateItemName(itemName);

        List<Storable> retrievedItems = new ArrayList<>();

        if (items.containsKey(itemName)) {
            for (StorableItem item : items.get(itemName)) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    retrievedItems.add(item.getItem());
                }
            }

            items.remove(itemName);
        }

        return retrievedItems;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName, int quantity) throws InsufficientQuantityException {
        validateItemName(itemName);
        if (quantity < 1) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        int takenItems = 0;
        int itemsToRemove = 0;
        List<Storable> retrievedItems = new ArrayList<>();
        if (items.containsKey(itemName)) {
            for (StorableItem item : items.get(itemName)) {
                int i;
                for (i = 0; i < item.getQuantity() && takenItems < quantity; i++) {
                    retrievedItems.add(item.getItem());
                    takenItems++;
                }

                if (i != item.getQuantity()) {
                    item.setQuantity(item.getQuantity() - i);
                }
                else {
                    itemsToRemove++;
                }

                if (item.getQuantity() == quantity) {
                    break;
                }
            }
        }

        if (quantity > takenItems) {
            throw new InsufficientQuantityException();
        }

        for (int i = 0; i < itemsToRemove; i++) {
            items.get(itemName).remove(items.get(itemName).iterator().next());
        }

        if (itemsToRemove > 0 && items.get(itemName).isEmpty()) {
            items.remove(itemName);
        }

        takenCapacity -= takenItems;
        return retrievedItems;
    }

    @Override
    public int getQuantityOfItem(String itemName) {
        validateItemName(itemName);

        int quantity = 0;
        if (items.containsKey(itemName)) {
            for (StorableItem item : items.get(itemName)) {
                quantity += item.getQuantity();
            }
        }

        return quantity;
    }

    @Override
    public Iterator<Ingredient<? extends Storable>> getMissingIngredientsFromRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Invalid recipe");
        }

        List<Ingredient<? extends Storable>> missingIngredients = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {

            int amountInStock = 0;
            String ingredientName = ingredient.item().getName();
            if (items.containsKey(ingredientName)) {
                for (StorableItem storableItem : items.get(ingredientName)) {
                    if (!storableItem.getItem().isExpired()) {
                        amountInStock += storableItem.getQuantity();
                        if (amountInStock >= ingredient.quantity()) {
                            break;
                        }
                    }
                }
            }

            if (amountInStock < ingredient.quantity()) {
                missingIngredients.add(
                        new DefaultIngredient(ingredient.item(), ingredient.quantity() - amountInStock));
            }
        }

        return missingIngredients.iterator();
    }

    @Override
    public List<? extends Storable> removeExpired() {
        List<Storable> expiredItems = new ArrayList<>();
        List<String> expiredItemsNames = new ArrayList<>();

        for (Map.Entry<String, Set<StorableItem>> entry : items.entrySet()) {
            Set<StorableItem> values = entry.getValue();
            for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                StorableItem item = (StorableItem) iterator.next();
                if (item.getItem().isExpired()) {
                    iterator.remove();
                    for (int i = 0; i < item.getQuantity(); i++) {
                        expiredItems.add(item.getItem());
                    }
                    takenCapacity -= item.getQuantity();
                }
            }

            expiredItemsNames.add(entry.getKey());
        }

        for (String itemName : expiredItemsNames) {
            if (items.get(itemName).isEmpty()) {
                items.remove(itemName);
            }
        }

        return expiredItems;
    }

    private void validateItemName(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("Invalid item's name");
        }
    }
}
