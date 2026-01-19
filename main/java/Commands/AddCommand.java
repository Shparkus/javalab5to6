package Commands;

import ConsoleOperations.Printable;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;
import Models.Route;
import Models.Forms.RouteForm;

public class AddCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public AddCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }

    @Override
    public String getUsage() {
        return "{element}";
    }

    @Override
    public void execute(String args) throws Exception {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("add");
        }
        Route route = new RouteForm(console).build();
        collectionManager.add(route);
        console.println("Элемент добавлен (id=" + route.getId() + ").");
    }
}