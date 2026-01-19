package Managers;
import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Exceptions.InFileModeException;
import Exceptions.ScriptRecursionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class RuntimeManager {
    private final Printable console;
    private final CommandManager commandManager;

    //Хранит абсолютные пути файлов
    private final Deque<String> scriptStack = new ArrayDeque<>();

    public RuntimeManager(Printable console, CommandManager commandManager) {
        this.console = console;
        this.commandManager = commandManager;
    }

    public void interactiveMode() throws ExitPoint {
        Console.setFileMode(false);

        while (true) {
            console.print("> ");
            String line;
            try {
                line = ScannerManager.getScannerManager().getUserScanner().nextLine();
            } catch (NoSuchElementException e) {
                throw new ExitPoint("Ввод завершён.");
            }

            try {
                processLine(line);
            } catch (ExitPoint e) {
                throw e;
            } catch (Exception e) {
                console.printErr("Ошибка выполнения команды: " + e.getMessage());
            }
        }
    }

    public void executeScript(String fileName) throws ScriptRecursionException, ExitPoint {
        if (fileName == null || fileName.trim().isEmpty()) {
            console.printErr("Имя файла скрипта не задано.");
            return;
        }

        File f = new File(fileName);
        String abs = f.getAbsolutePath();

        if (scriptStack.contains(abs)) {
            throw new ScriptRecursionException();
        }

        if (!f.exists() || !f.isFile()) {
            console.printErr("Файл скрипта не найден: " + fileName);
            return;
        }
        if (!f.canRead()) {
            console.printErr("Нет прав на чтение файла скрипта: " + fileName);
            return;
        }

        scriptStack.push(abs);

        Scanner previousScanner = ScannerManager.getScannerManager().getUserScanner();
        boolean previousMode = Console.isFileMode();

        try (FileInputStream fis = new FileInputStream(f);
             Scanner scriptScanner = new Scanner(fis, StandardCharsets.UTF_8)) {
             Console.setFileMode(true);
             ScannerManager.getScannerManager().setUserScanner(scriptScanner);
             while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine();
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                try {
                    processLine(line);
                } catch (InFileModeException e) {
                    console.printErr("Ошибка в скрипте: " + e.getMessage());
                    break;
                } catch (ExitPoint e) {
                    throw e;
                } catch (Exception e) {
                    console.printErr("Ошибка выполнения команды в скрипте: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            console.printErr("Ошибка доступа к файлу скрипта: " + e.getMessage());
        } finally {
            // Восстанавливаем предыдущий Scanner и режим
            ScannerManager.getScannerManager().setUserScanner(previousScanner);
            Console.setFileMode(previousMode);
            scriptStack.pop();
        }
    }

    private void processLine(String line) throws Exception {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return;
        String[] parts = trimmed.split("\\s+", 2);
        String name = parts[0];
        String args = (parts.length == 2) ? parts[1] : "";
        commandManager.execute(name, args);
    }
}
