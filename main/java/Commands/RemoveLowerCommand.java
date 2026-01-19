package Commands;

import ConsoleOperations.Printable;
import Exceptions.EmptyCollectionException;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;
import Models.Route;
import Models.Forms.RouteForm;

public class RemoveLowerCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public RemoveLowerCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getDescription() {
        return "удалить все элементы, меньшие заданного";
    }

    @Override
    public String getUsage() {
        return "{element}";
    }

    @Override
    public void execute(String args) throws Exception {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("remove_lower");
        }
        if (collectionManager.size() == 0) {
            throw new EmptyCollectionException();
        }
        Route point = new RouteForm(console).build();
        int removed = collectionManager.removeLower(point);
        console.println("Удалено элементов: " + removed);
    }
}
