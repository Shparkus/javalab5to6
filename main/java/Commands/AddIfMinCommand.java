package Commands;

import ConsoleOperations.Printable;
import Exceptions.EmptyCollectionException;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;
import Models.Route;
import Models.Forms.RouteForm;

public class AddIfMinCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public AddIfMinCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String getDescription() {
        return "добавить элемент, если он меньше минимального";
    }

    @Override
    public String getUsage() {
        return "{element}";
    }

    @Override
    public void execute(String args) throws Exception {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("add_if_min");
        }

        Route candidate = new RouteForm(console).build();

        try {
            Route min = collectionManager.getMin();
            if (candidate.compareTo(min) < 0) {
                collectionManager.add(candidate);
                console.println("Элемент добавлен (id=" + candidate.getId() + ").");
            } else {
                console.println("Элемент не добавлен: он не меньше минимального.");
            }
        } catch (EmptyCollectionException e) {
            collectionManager.add(candidate);
            console.println("Коллекция была пуста. Элемент добавлен (id=" + candidate.getId() + ").");
        }
    }
}