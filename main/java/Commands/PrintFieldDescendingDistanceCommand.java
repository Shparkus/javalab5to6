package Commands;

import ConsoleOperations.Printable;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;

import java.util.List;

public class PrintFieldDescendingDistanceCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public PrintFieldDescendingDistanceCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "print_field_descending_distance";
    }

    @Override
    public String getDescription() {
        return "вывести значения distance в порядке убывания";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(String args) throws WrongAmountOfArgsException {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("print_field_descending_distance");
        }

        List<Long> distances = collectionManager.getDistancesDescending();
        if (distances.isEmpty()) {
            console.println("Коллекция пуста или distance отсутствует.");
            return;
        }
        for (Long d : distances) {
            console.println(String.valueOf(d));
        }
    }
}