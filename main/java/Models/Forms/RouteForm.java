package Models.Forms;

import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Exceptions.InFileModeException;
import Exceptions.InvalidFormException;
import Managers.ScannerManager;
import Models.Coordinates;
import Models.Location;
import Models.Location;
import Models.Route;

import java.util.NoSuchElementException;


public class RouteForm implements Form<Route> {
    private final Printable console;

    public RouteForm(Printable console) {
        this.console = console;
    }

    @Override
    public Route build() throws InvalidFormException, InFileModeException, ExitPoint {
        String name = askNonEmptyString("Введите name (непустая строка): ");

        Coordinates coordinates = new CoordinatesForm(console).build();
        Location from = new LocationForm(console).build();
        Location to = askNullableLocationTo();

        Long distance = askDistance("Введите distance (Long, > 1): ");

        Route route = new Route(name, coordinates, from, to, distance);
        return route;
    }

    private Location askNullableLocationTo() throws InvalidFormException, InFileModeException, ExitPoint {
        if (!Console.isFileMode()) {
            console.print("Введите to (Enter — чтобы установить null, иначе введите любой символ и Enter): ");
        }
        String marker = readLine();
        if (marker.trim().isEmpty()) {
            return null;
        }
        return new LocationForm(console).build();
    }

    private String askNonEmptyString(String prompt) throws InFileModeException, ExitPoint {
        while (true) {
            if (!Console.isFileMode()) console.print(prompt);
            String s = readLine().trim();
            if (!s.isEmpty()) return s;
            if (Console.isFileMode()) {
                throw new InFileModeException("Ожидалась непустая строка.");
            }
            console.printErr("Строка не может быть пустой.");
        }
    }

    private Long askDistance(String prompt) throws InFileModeException, ExitPoint {
        while (true) {
            if (!Console.isFileMode()) console.print(prompt);
            String s = readLine().trim();
            try {
                long v = Long.parseLong(s);
                if (v <= 1) {
                    if (Console.isFileMode()) {
                        throw new InFileModeException("Число должно быть > 1, получено: " + v);
                    }
                    console.printErr("Число должно быть больше 1.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                if (Console.isFileMode()) {
                    throw new InFileModeException("Ожидалось число Long, получено: " + s);
                }
                console.printErr("Ожидалось число типа Long.");
            }
        }
    }

    private String readLine() throws ExitPoint {
        try {
            return ScannerManager.getScannerManager().getUserScanner().nextLine();
        } catch (NoSuchElementException e) {
            throw new ExitPoint("Ввод завершён.");
        }
    }
}