package Commands;

import ConsoleOperations.Printable;
import Managers.CollectionManager;

import java.util.Date;

public class InfoCommand implements Command {
    private final Printable console;
    private final CollectionManager collectionManager;

    public InfoCommand(Printable console, CollectionManager collectionManager) {
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "вывести информацию о коллекции";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(String args) {
        console.println("Тип коллекции: java.util.HashSet");
        Date init = collectionManager.getInitDate();
        console.println("Дата инициализации: " + init);
        console.println("Количество элементов: " + collectionManager.size());
    }
}
