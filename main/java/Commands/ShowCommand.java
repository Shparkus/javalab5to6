package Commands;

import ConsoleOperations.Printable;
import Exceptions.EmptyCollectionException;
import Managers.CollectionManager;
import Models.Route;
import java.util.List;

public class ShowCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public ShowCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Вывести все элементы коллекции";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(String args) throws EmptyCollectionException {
        if (collectionManager.size() == 0) {
            throw new EmptyCollectionException();
        }
        List<Route> sorted = collectionManager.getSorted();
        for (Route r : sorted) {
            console.println(r.toString());
        }
    }
}

