package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidCommandException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandTypeTest {

    @Test
    public void testCreationOfLoginCommand() {
        CommandType result = CommandType.of("loGin");
        assertEquals(CommandType.LOGIN, result, "Expected login command to be returned!");
    }

    @Test
    public void testCreationOfRegisterCommand() {
        CommandType result = CommandType.of("REGISTER");
        assertEquals(CommandType.REGISTER, result, "Expected register command to be returned!");
    }

    @Test
    public void testCreationOfBuyCommand() {
        CommandType result = CommandType.of("buy");
        assertEquals(CommandType.BUY, result, "Expected buy command to be returned!");
    }

    @Test
    public void testCreationOfSellCommand() {
        CommandType result = CommandType.of("sell");
        assertEquals(CommandType.SELL, result, "Expected sell command to be returned!");
    }

    @Test
    public void testCreationOfDepositCommand() {
        CommandType result = CommandType.of("dePosiT-money");
        assertEquals(CommandType.DEPOSIT, result, "Expected deposit command to be returned!");
    }

    @Test
    public void testCreationOfListCommand() {
        CommandType result = CommandType.of("list-offerings");
        assertEquals(CommandType.LIST, result, "Expected login command to be returned!");
    }

    @Test
    public void testCreationOfWalletSummaryCommand() {
        CommandType result = CommandType.of("get-wallet-summary");
        assertEquals(CommandType.WALLET_SUMMARY, result, "Expected wallet summary to be returned!");
    }

    @Test
    public void testCreationOfWalletOverallCommand() {
        CommandType result = CommandType.of("get-wallet-overall-summary");
        assertEquals(CommandType.WALLET_OVERALL, result, "Expected wallet overall command to be returned!");
    }

    @Test
    public void testCreationOfHelpCommand() {
        CommandType result = CommandType.of("help");
        assertEquals(CommandType.HELP, result, "Expected help command to be returned!");
    }

    @Test
    public void testCreationOfExitCommand() {
        CommandType result = CommandType.of("exit");
        assertEquals(CommandType.EXIT, result, "Expected exit command to be returned!");
    }

    @Test
    public void testWithUnknownCommand() {
        assertThrows(InvalidCommandException.class, () -> CommandType.of("asfgbre"),
                "Expected InvalidCommandException to be thrown when the command is unknown!");
    }

    @Test
    public void testWithNullCommand() {
        assertThrows(InvalidCommandException.class, () -> CommandType.of(null),
                "Expected InvalidCommandException to be thrown when the command is null!");
    }

    @Test
    public void testGetType() {
        assertEquals("get-wallet-overall-summary", CommandType.WALLET_OVERALL.getType(),
                "Unexpected returned value!");

    }
}
