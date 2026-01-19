package Network;

import Models.Route;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private final boolean success;
    private final String message;
    private final List<Route> routes;
    private final List<Long> distances;

    public Response(boolean success, String message) {
        this(success, message, null, null);
    }

    public Response(boolean success, String message, List<Route> routes) {
        this(success, message, routes, null);
    }

    public Response(boolean success, String message, List<Route> routes, List<Long> distances) {
        this.success = success;
        this.message = message;
        this.routes = routes;
        this.distances = distances;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<Long> getDistances() {
        return distances;
    }
}
