package bg.sofia.uni.fmi.mjt.cocktail.server.storage.command;

import bg.sofia.uni.fmi.mjt.cocktail.command.Command;
import bg.sofia.uni.fmi.mjt.cocktail.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.InvalidIngredientException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class CommandExecutorTest {

    @Mock
    private CocktailStorage cocktailStorageMock = Mockito.mock(CocktailStorage.class);
    private CommandExecutor commandExecutor = new CommandExecutor(cocktailStorageMock);

    @Test
    public void testCreateWithInvalidInput() {
        Command cmd = Command.of("create c1");
        String output = commandExecutor.execute(cmd);

        assertEquals("Unknown command", output, "Unknown command expected to be returned!");
    }

    @Test
    public void testCreateWithInvalidIngredient() {
        Command cmd = Command.of("create c1 milk=");
        String output = commandExecutor.execute(cmd);
        String expected = "{\"status\":\"ERROR\",\"errorMessage\":Invalid ingredient format!\"}";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testCreateWithExistingCocktail() throws CocktailAlreadyExistsException {
        Set<Ingredient> ingredientSet = new HashSet<>();
        ingredientSet.add(new Ingredient("milk", "100ml"));
        Cocktail cocktail = new Cocktail("c1", ingredientSet);

        doThrow(new CocktailAlreadyExistsException("That cocktail have been created already!"))
                .when(cocktailStorageMock)
                .createCocktail(cocktail);

        Command cmd = Command.of("create c1 milk=100ml");
        String output = commandExecutor.execute(cmd);
        String expected = "{\"status\":\"ERROR\",\"errorMessage\":That cocktail have been created already!\"}";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testCreateWithValidCocktail() throws CocktailAlreadyExistsException {
        doNothing().when(cocktailStorageMock).createCocktail(any(Cocktail.class));

        Command cmd = Command.of("create c1 milk=100ml");
        String output = commandExecutor.execute(cmd);
        String expected = "{\"status\":\"CREATED\"}";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testGetAll()  {
        Set<Ingredient> ingredientSet = new TreeSet<>(Comparator.comparing(Ingredient::name)) {
            {
                add(new Ingredient("milk", "100"));
                add(new Ingredient("whisky", "200"));
            }
        };

        List<Cocktail> cocktailList = new ArrayList<>() {
            {
                add(new Cocktail("c1", ingredientSet));
                add(new Cocktail("c2", Set.of(new Ingredient("vodka", "100"))));
            }
        };

        when(cocktailStorageMock.getCocktails()).thenReturn(cocktailList);

        Command cmd = Command.of("get all");
        String output = commandExecutor.execute(cmd);
        String expected = "{\"status\":\"OK\",\"cocktails\":" +
                "[{\"name\":\"c1\",\"ingredients\":[{\"name\":\"milk\",\"amount\":\"100\"}," +
                "{\"name\":\"whisky\",\"amount\":\"200\"}]}," +
                "{\"name\":\"c2\",\"ingredients\":[{\"name\":\"vodka\",\"amount\":\"100\"}]}]}";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testGetByCocktailName() throws CocktailNotFoundException {
        Cocktail cocktail = new Cocktail("c2", Set.of(new Ingredient("vodka", "100")));

        when(cocktailStorageMock.getCocktail("c2")).thenReturn(cocktail);

        Command cmd = Command.of("get by-name c2");
        String output = commandExecutor.execute(cmd);
        String expected = "{\"status\":\"OK\",\"cocktail\":" +
                "{\"name\":\"c2\",\"ingredients\":[{\"name\":\"vodka\",\"amount\":\"100\"}]}}";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testGetByCocktailNameWithNotFoundCocktail() throws CocktailNotFoundException {
        when(cocktailStorageMock.getCocktail("c2"))
                .thenThrow(new CocktailNotFoundException("There is no such cocktail!"));

        Command cmd = Command.of("get by-name c2");
        String output = commandExecutor.execute(cmd);
        String expected = "{\"status\":\"ERROR\",\"errorMessage\":There is no such cocktail!\"}";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testGetByIngredient() {
        List<Cocktail> cocktailList = new ArrayList<>() {
            {
                add(new Cocktail("c1", Set.of(new Ingredient("whisky", "100"))));
            }
        };

        when(cocktailStorageMock.getCocktailsWithIngredient("whisky")).thenReturn(cocktailList);

        Command cmd = Command.of("get by-ingredient whisky");
        String output = commandExecutor.execute(cmd);
        String expected = "{\"status\":\"OK\",\"cocktails\":[" +
                "{\"name\":\"c1\",\"ingredients\":[{\"name\":\"whisky\",\"amount\":\"100\"}]}]}";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testGetWithInvalidCommand() {

        Command cmd = Command.of("get by-cocktailName c1");
        String output = commandExecutor.execute(cmd);
        String expected = "Unknown command";

        assertEquals(expected, output, "Other message expected to be returned!");
    }

    @Test
    public void testExecuteWithUnknownCommand() {

        Command cmd = Command.of("fake command");
        String output = commandExecutor.execute(cmd);
        String expected = "Unknown command";

        assertEquals(expected, output, "Other message expected to be returned!");
    }
}
