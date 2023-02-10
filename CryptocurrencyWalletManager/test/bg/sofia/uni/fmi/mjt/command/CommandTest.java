package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidCommandException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandTest {
    @Test
    public void testCreationOfCommandWithNullParameter() {
        assertThrows(InvalidCommandException.class, () -> Command.of(null),
                "Expected InvalidCommandException when the input is null!");
    }

    @Test
    public void testCreationOfCommandWithBlankParameter() {
        assertThrows(InvalidCommandException.class, () -> Command.of("  "),
                "Expected InvalidCommandException when the input is blank string!");
    }

    @Test
    public void testCreationOfCommandWithUnknownCommand() {
        assertThrows(InvalidCommandException.class, () -> Command.of("UnknownCommand"),
                "Expected InvalidCommandException when the input is unknown command!");
    }

    @Test
    public void testCreationOfCommandWithValidCommand() {
        Command command = Command.of("buy");
        assertEquals(CommandType.BUY, command.command(), "The input is converted to wrong command!");
        assertEquals(0, command.arguments().size(), "No arguments have been given!");
    }

    @Test
    public void testCreationOfCommandWithValidCommandAndAdditionalArguments() {
        Command command = Command.of("sell --offering=BTC --all");
        List<String> expectedArguments = List.of("--offering=BTC", "--all");

        assertEquals(CommandType.SELL, command.command(), "The input is converted to wrong command!");
        assertIterableEquals(expectedArguments, command.arguments(), "Wrongly parsed arguments list!");
    }

    @Test
    public void testIsEntryCommand() {
        Command command1 = Command.of("login");
        Command command2 = Command.of("register");
        Command command3 = Command.of("buy");

        assertTrue(command1.isEntryCommand(), "Login is an entry command!");
        assertTrue(command2.isEntryCommand(), "Register is an entry command!");
        assertFalse(command3.isEntryCommand(), "Buy is not an entry command!");
    }
}
