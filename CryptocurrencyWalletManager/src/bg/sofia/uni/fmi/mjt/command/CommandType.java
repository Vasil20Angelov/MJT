package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidCommandException;

public enum CommandType {

    LOGIN("login"),
    REGISTER("register"),
    BUY("buy"),
    SELL("sell"),
    DEPOSIT("deposit-money"),
    LIST("list-offerings"),
    WALLET_SUMMARY("get-wallet-summary"),
    WALLET_OVERALL("get-wallet-overall-summary"),
    HELP("help"),
    EXIT("exit");

    private final String type;
    CommandType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static CommandType of(String command) {

        if (command == null) {
            throw new InvalidCommandException("Invalid command!");
        }

        return switch (command.toLowerCase()) {
            case "login" -> LOGIN;
            case "register" -> REGISTER;
            case "buy" -> BUY;
            case "sell" -> SELL;
            case "deposit-money" -> DEPOSIT;
            case "list-offerings" -> LIST;
            case "get-wallet-summary" -> WALLET_SUMMARY;
            case "get-wallet-overall-summary" -> WALLET_OVERALL;
            case "help" -> HELP;
            case "exit" -> EXIT;
            default -> throw new InvalidCommandException(command + " is not a valid command!");
        };
    }
}
