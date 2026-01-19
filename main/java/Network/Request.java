package Network;

import Models.Route;

import java.io.Serializable;

public class Request implements Serializable {
    private final CommandType type;
    private final Serializable argument;
    private final Route route;

    public Request(CommandType type) {
        this(type, null, null);
    }

    public Request(CommandType type, Serializable argument) {
        this(type, argument, null);
    }

    public Request(CommandType type, Serializable argument, Route route) {
        this.type = type;
        this.argument = argument;
        this.route = route;
    }

    public CommandType getType() {
        return type;
    }

    public Serializable getArgument() {
        return argument;
    }

    public Route getRoute() {
        return route;
    }
}
