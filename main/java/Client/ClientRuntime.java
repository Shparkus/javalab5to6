package Client;

import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Exceptions.InFileModeException;
import Exceptions.InvalidFormException;
import Exceptions.ScriptRecursionException;
import Managers.ScannerManager;
import Models.Route;
import Models.Forms.RouteForm;
import Network.CommandType;
import Network.Request;
import Network.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientRuntime {
    private final Printable console;
    private final ClientNetwork network;
    private final Deque<String> scriptStack = new ArrayDeque<>();

    public ClientRuntime(Printable console, ClientNetwork network) {
        this.console = console;
        this.network = network;
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

    private void processLine(String line) throws Exception {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return;
        String[] parts = trimmed.split("\\s+", 2);
        String name = parts[0].toLowerCase();
        String args = (parts.length == 2) ? parts[1].trim() : "";

        if ("execute_script".equals(name)) {
            executeScript(args);
            return;
        }

        Request request = buildRequest(name, args);
        if (request == null) {
            return;
        }

        Response response = network.send(request, 3);
        if (response == null) {
            console.printErr("Сервер временно недоступен. Попробуйте позже.");
            return;
        }

        if (response.getMessage() != null && !response.getMessage().isEmpty()) {
            if (response.isSuccess()) {
                console.println(response.getMessage());
            } else {
                console.printErr(response.getMessage());
            }
        }

        List<Route> routes = response.getRoutes();
        if (routes != null) {
            if (routes.isEmpty()) {
                console.println("Коллекция пуста.");
            } else {
                for (Route r : routes) {
                    console.println(r.toString());
                }
            }
        }

        List<Long> distances = response.getDistances();
        if (distances != null) {
            if (distances.isEmpty()) {
                console.println("Список расстояний пуст.");
            } else {
                distances.forEach(d -> console.println(String.valueOf(d)));
            }
        }
    }

    private Request buildRequest(String name, String args) throws Exception {
        switch (name) {
            case "help":
                return new Request(CommandType.HELP);
            case "info":
                return new Request(CommandType.INFO);
            case "show":
                return new Request(CommandType.SHOW);
            case "add":
                return new Request(CommandType.ADD, null, buildRoute());
            case "update": {
                Integer id = parseIntArg(args, "update");
                if (id == null) return null;
                return new Request(CommandType.UPDATE, id, buildRoute());
            }
            case "remove_by_id": {
                Integer id = parseIntArg(args, "remove_by_id");
                if (id == null) return null;
                return new Request(CommandType.REMOVE_BY_ID, id);
            }
            case "clear":
                return new Request(CommandType.CLEAR);
            case "add_if_max":
                return new Request(CommandType.ADD_IF_MAX, null, buildRoute());
            case "add_if_min":
                return new Request(CommandType.ADD_IF_MIN, null, buildRoute());
            case "remove_lower":
                return new Request(CommandType.REMOVE_LOWER, null, buildRoute());
            case "remove_any_by_distance": {
                Long distance = parseLongArg(args, "remove_any_by_distance");
                if (distance == null) return null;
                return new Request(CommandType.REMOVE_ANY_BY_DISTANCE, distance);
            }
            case "filter_greater_than_distance": {
                Long distance = parseLongArg(args, "filter_greater_than_distance");
                if (distance == null) return null;
                return new Request(CommandType.FILTER_GREATER_THAN_DISTANCE, distance);
            }
            case "print_field_descending_distance":
                return new Request(CommandType.PRINT_FIELD_DESCENDING_DISTANCE);
            case "exit":
                throw new ExitPoint("Работа клиента завершена.");
            case "save":
                console.printErr("Команда save доступна только на сервере.");
                return null;
            default:
                console.printErr("Неизвестная команда: " + name);
                return null;
        }
    }

    private Route buildRoute() throws InvalidFormException, InFileModeException, ExitPoint {
        return new RouteForm(console).build();
    }

    private Integer parseIntArg(String args, String command) throws InFileModeException {
        if (args == null || args.trim().isEmpty()) {
            String message = "Команда " + command + " требует числовой аргумент.";
            if (Console.isFileMode()) {
                throw new InFileModeException(message);
            }
            console.printErr(message);
            return null;
        }
        try {
            return Integer.parseInt(args.trim());
        } catch (NumberFormatException e) {
            String message = "Ожидалось целое число для команды " + command + ".";
            if (Console.isFileMode()) {
                throw new InFileModeException(message);
            }
            console.printErr(message);
            return null;
        }
    }

    private Long parseLongArg(String args, String command) throws InFileModeException {
        if (args == null || args.trim().isEmpty()) {
            String message = "Команда " + command + " требует числовой аргумент.";
            if (Console.isFileMode()) {
                throw new InFileModeException(message);
            }
            console.printErr(message);
            return null;
        }
        try {
            return Long.parseLong(args.trim());
        } catch (NumberFormatException e) {
            String message = "Ожидалось число Long для команды " + command + ".";
            if (Console.isFileMode()) {
                throw new InFileModeException(message);
            }
            console.printErr(message);
            return null;
        }
    }

    private void executeScript(String fileName) throws ScriptRecursionException, ExitPoint {
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
            ScannerManager.getScannerManager().setUserScanner(previousScanner);
            Console.setFileMode(previousMode);
            scriptStack.pop();
        }
    }
}
