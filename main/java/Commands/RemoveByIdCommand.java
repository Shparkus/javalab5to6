package Commands;

import ConsoleOperations.Printable;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;

public class RemoveByIdCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public RemoveByIdCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "удалить элемент из коллекции по его id";
    }

    @Override
    public String getUsage() {
        return "id";
    }

    @Override
    public void execute(String args) throws WrongAmountOfArgsException {
        if (args == null || args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("remove_by_id id");
        }
        // \\s+ - 1 или несколько пробелов
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 1) {
            throw new WrongAmountOfArgsException("remove_by_id id");
        }
        int id;
        try {
            id = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            console.printErr("id должен быть целым числом.");
            return;
        }

        if (collectionManager.removeById(id)) {
            console.println("Элемент удалён (id=" + id + ").");
        } else {
            console.printErr("Элемент с id=" + id + " не найден.");
        }
    }
}
