package Commands;
import ConsoleOperations.Printable;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;
import Models.Route;
import Models.Forms.RouteForm;

public class UpdateCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public UpdateCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "обновить элемент коллекции по id";
    }

    @Override
    public String getUsage() {
        return "id {element}";
    }

    @Override
    public void execute(String args) throws Exception {
        if (args == null || args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("update id");
        }
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 1) {
            throw new WrongAmountOfArgsException("update id");
        }
        int id;
        try {
            id = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            console.printErr("id должен быть целым числом.");
            return;
        }
        if (collectionManager.getById(id) == null) {
            console.printErr("Элемент с id=" + id + " не найден.");
            return;
        }

        Route newRoute = new RouteForm(console).build();
        boolean updated = collectionManager.updateById(id, newRoute);
        if (updated) {
            console.println("Элемент обновлён (id=" + id + ").");
        } else {
            console.printErr("Элемент с id=" + id + " не найден.");
        }
    }
}