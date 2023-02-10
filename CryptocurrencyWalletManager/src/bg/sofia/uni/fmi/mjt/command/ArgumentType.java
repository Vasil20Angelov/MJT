package bg.sofia.uni.fmi.mjt.command;

public enum ArgumentType {
    MONEY("--money="),
    OFFERING("--offering=");

    private final String argument;

    ArgumentType(String argument) {
        this.argument = argument;
    }

    public String getArgument() {
        return argument;
    }
}
