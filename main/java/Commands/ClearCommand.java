package Commands;
import ConsoleOperations.Printable;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;

public class ClearCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public ClearCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }

    @Override
    public String getUsage() {
        return "";
    }


    @Override
    public void execute(String args) throws WrongAmountOfArgsException {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("clear");
        }
        collectionManager.clear();
        console.println("Коллекция очищена.");
    }
}

