package bg.sofia.uni.fmi.mjt.cocktail.server.storage.command;

import bg.sofia.uni.fmi.mjt.cocktail.command.Command;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommandTest {

    @Test
    public void testCommandCreationWithValidParameterGiven() {
        Command command = Command.of("get all cocktails");
        List<String> args = List.of("all", "cocktails");

        assertEquals("get", command.command(), "Wrongly parsed command!");
        assertIterableEquals(args, command.arguments(), "Wrongly parsed arguments!");
    }

    @Test
    public void testCommandCreationWithInvalidParameterGiven() {
        Command command = Command.of(" ");

        assertNull(command.command(), "Command should be null!");
        assertNull(command.arguments(), "Arguments should be null!");
    }

    @Test
    public void testCommandCreationWithParameterThatHasNoArguments() {
        Command command = Command.of("disconnect");

        assertEquals("disconnect", command.command(), "Wrongly parsed command!");
        assertEquals(0, command.arguments().size(), "Arguments should be 0!");
    }
}
