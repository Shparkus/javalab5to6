package Commands;

import ConsoleOperations.Printable;
import Exceptions.FileAccessException;
import Exceptions.WrongAmountOfArgsException;
import Managers.CollectionManager;
import Managers.FileManager;

public class SaveCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public SaveCommand(Printable console, CollectionManager collectionManager, FileManager fileManager) {
        this.console = console;
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "сохранить коллекцию в файл";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(String args) throws WrongAmountOfArgsException {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("save");
        }
        try {
            fileManager.writeCollection(collectionManager.getRoutes());
            console.println("Коллекция сохранена в файл: " + fileManager.getFilePath());
        } catch (FileAccessException e) {
            console.printErr(e.getMessage());
        }
    }
}