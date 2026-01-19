package Commands;

import ConsoleOperations.Printable;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;

public class RemoveAnyByDistanceCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public RemoveAnyByDistanceCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_any_by_distance";
    }

    @Override
    public String getDescription() {
        return "удалить один элемент по значению distance";
    }

    @Override
    public String getUsage() {
        return "distance";
    }

    @Override
    public void execute(String args) throws WrongAmountOfArgsException {
        if (args == null || args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("remove_any_by_distance distance");
        }
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 1) {
            throw new WrongAmountOfArgsException("remove_any_by_distance distance");
        }

        long d;
        try {
            d = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            console.printErr("distance должно быть числом типа long.");
            return;
        }

        boolean removed = collectionManager.removeAnyByDistance(d);
        if (removed) {
            console.println("Элемент удалён (distance=" + d + ").");
        } else {
            console.printErr("Элемент с distance=" + d + " не найден.");
        }
    }
}
