package Commands;

import ConsoleOperations.Printable;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;
import Models.Route;

import java.util.List;

public class FilterGreaterThanDistanceCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public FilterGreaterThanDistanceCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_greater_than_distance";
    }

    @Override
    public String getDescription() {
        return "вывести элементы с distance больше заданного";
    }

    @Override
    public String getUsage() {
        return "distance";
    }

    @Override
    public void execute(String args) throws WrongAmountOfArgsException {
        if (args == null || args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("filter_greater_than_distance distance");
        }
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 1) {
            throw new WrongAmountOfArgsException("filter_greater_than_distance distance");
        }

        long d;
        try {
            d = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            console.printErr("distance должно быть числом типа long.");
            return;
        }

        List<Route> list = collectionManager.filterGreaterThanDistance(d);
        if (list.isEmpty()) {
            console.println("Подходящие элементы отсутствуют.");
            return;
        }
        for (Route r : list) {
            console.println(r.toString());
        }
    }
}