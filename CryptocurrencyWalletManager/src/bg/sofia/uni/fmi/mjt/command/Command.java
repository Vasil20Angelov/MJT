package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidCommandException;

import java.util.ArrayList;
import java.util.List;

public record Command(CommandType command, List<String> arguments) {

    private static final String INPUT_SPLIT_REGEX = " ";

    public static Command of(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidCommandException("Invalid command!");
        }

        List<String> parameters = new ArrayList<>(List.of(input.split(INPUT_SPLIT_REGEX)));
        parameters.replaceAll(String::strip);

        CommandType command = CommandType.of(parameters.get(0));

        List<String> arguments;
        if (parameters.size() > 1) {
            arguments = parameters.subList(1, parameters.size());
        }
        else {
            arguments = new ArrayList<>();
        }

        return new Command(command, arguments);
    }

//    public boolean commandRequiresApiInfo() {
//        return command == CommandType.LIST
//                || command == CommandType.BUY
//                || command == CommandType.SELL
//                || command == CommandType.WALLET_OVERALL;
//    }

    public boolean isEntryCommand() {
        return command == CommandType.REGISTER || command == CommandType.LOGIN;
    }
}