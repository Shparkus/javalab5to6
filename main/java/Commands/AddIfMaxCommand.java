package Commands;

import ConsoleOperations.Printable;
import Exceptions.EmptyCollectionException;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;
import Models.Route;
import Models.Forms.RouteForm;

public class AddIfMaxCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public AddIfMaxCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }
    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getDescription() {
        return "Добавить элемент, если он больше максимального";
    }

    @Override
    public String getUsage() {
        return "{element}";
    }

    @Override
    public void execute(String args) throws Exception {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("add_if_max");
        }
        Route candidate = new RouteForm(console).build();
        try {
            Route max = collectionManager.getMax();
            if (candidate.compareTo(max) > 0) {
                collectionManager.add(candidate);
                console.println("Элемент добавлен (id=" + candidate.getId() + ").");
            } else {
                console.println("Элемент не добавлен: он не превышает максимальный.");
            }
        } catch (EmptyCollectionException e) {
            collectionManager.add(candidate);
            console.println("Коллекция была пуста. Элемент добавлен (id=" + candidate.getId() + ").");
        }
    }
}