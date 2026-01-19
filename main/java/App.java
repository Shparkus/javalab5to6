import Commands.*;
import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Exceptions.FileAccessException;
import Managers.*;
import Models.Route;
import java.util.HashSet;
import java.util.Scanner;

// Имя файла берётся из переменной окружения (ROUTE_FILE; если пусто — LAB5).
public class App {
    private static String getEnvFilePath() {
        String v = System.getenv("ROUTE_FILE");
        if (v != null && !v.trim().isEmpty()) return v;
        v = System.getenv("LAB5");
        if (v != null && !v.trim().isEmpty()) return v;
        return null;
    }

    public static void main(String[] args) {
        Printable console = new Console();

        String filePath = getEnvFilePath();
        if (filePath == null || filePath.trim().isEmpty()) {
            console.printErr("Переменная окружения ROUTE_FILE (или LAB5) не задана. Коллекция будет пустой, сохранение недоступно.");
        } else {
            console.println("Файл коллекции: " + filePath);
        }

        ScannerManager.init(new Scanner(System.in));
        FileManager fileManager = new FileManager(console, filePath);
        HashSet<Route> initialSet = new HashSet<>();
        try {
            initialSet = fileManager.readCollection();
            console.println("Загружено элементов: " + initialSet.size());
        } catch (FileAccessException e) {
            console.printErr(e.getMessage());
            console.printErr("Коллекция будет инициализирована пустой.");
        }
        // managers
        CollectionManager collectionManager = new CollectionManager(console, initialSet);
        CommandManager commandManager = new CommandManager(console);
        RuntimeManager runtimeManager = new RuntimeManager(console, commandManager);
        //commands
        commandManager.create(new HelpCommand(console, commandManager));
        commandManager.create(new InfoCommand(console, collectionManager));
        commandManager.create(new ShowCommand(console, collectionManager));
        commandManager.create(new AddCommand(console, collectionManager));
        commandManager.create(new UpdateCommand(console, collectionManager));
        commandManager.create(new RemoveByIdCommand(console, collectionManager));
        commandManager.create(new ClearCommand(console, collectionManager));
        commandManager.create(new SaveCommand(console, collectionManager, fileManager));
        commandManager.create(new ExecuteScriptCommand(console, runtimeManager));
        commandManager.create(new ExitCommand());
        commandManager.create(new AddIfMaxCommand(console, collectionManager));
        commandManager.create(new AddIfMinCommand(console, collectionManager));
        commandManager.create(new RemoveLowerCommand(console, collectionManager));
        commandManager.create(new RemoveAnyByDistanceCommand(console, collectionManager));
        commandManager.create(new FilterGreaterThanDistanceCommand(console, collectionManager));
        commandManager.create(new PrintFieldDescendingDistanceCommand(console, collectionManager));

        try {
            runtimeManager.interactiveMode();
        } catch (ExitPoint e) {
            console.println("Работа программы завершена.");
        }
    }
}
