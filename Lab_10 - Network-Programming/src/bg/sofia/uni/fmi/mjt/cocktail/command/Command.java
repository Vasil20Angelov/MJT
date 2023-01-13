package bg.sofia.uni.fmi.mjt.cocktail.command;

import java.util.ArrayList;
import java.util.List;

public record Command(String command, List<String> arguments) {
    public static Command of(String input) {
        if (input == null || input.isBlank()) {
            return new Command(null, null);
        }

        List<String> parameters = List.of(input.split(" "));
        String command = parameters.get(0);

        List<String> arguments;
        if (parameters.size() > 1) {
            arguments = parameters.subList(1, parameters.size());
        }
        else {
            arguments = new ArrayList<>();
        }

        return new Command(command, arguments);
    }
}
