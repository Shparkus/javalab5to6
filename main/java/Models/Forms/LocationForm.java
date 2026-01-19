package Models.Forms;

import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Exceptions.InFileModeException;
import Exceptions.InvalidFormException;
import Managers.ScannerManager;
import Models.Location;
import java.util.NoSuchElementException;

public class LocationForm implements Form<Location> {
    private final Printable console;

    public LocationForm(Printable console) {
        this.console = console;
    }

    @Override
    public Location build() throws InvalidFormException, InFileModeException, ExitPoint {
        Double x = askDouble("Введите x (Double, not null): ");
        int y = askInt("Введите y (int): ");
        Float z = askFloat("Введите z (Float, not null): ");
        String name = askNullableString("Введите name (пустая строка = null): ");

        Location loc = new Location(x, y, z, name);
        if (!loc.validate()) {
            throw new InvalidFormException("Location не прошёл валидацию.");
        }
        return loc;
    }

    private Double askDouble(String prompt) throws InFileModeException, ExitPoint {
        while (true) {
            if (!Console.isFileMode()) console.print(prompt);
            String line = readLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                if (Console.isFileMode()) {
                    throw new InFileModeException("Ожидалось число Double, получено: " + line);
                }
                console.printErr("Ожидалось число типа Double.");
            }
        }
    }

    private int askInt(String prompt) throws InFileModeException, ExitPoint {
        while (true) {
            if (!Console.isFileMode()) console.print(prompt);
            String line = readLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                if (Console.isFileMode()) {
                    throw new InFileModeException("Ожидалось число int, получено: " + line);
                }
                console.printErr("Ожидалось число типа int.");
            }
        }
    }

    private Float askFloat(String prompt) throws InFileModeException, ExitPoint {
        while (true) {
            if (!Console.isFileMode()) console.print(prompt);
            String line = readLine();
            try {
                return Float.parseFloat(line.trim());
            } catch (NumberFormatException e) {
                if (Console.isFileMode()) {
                    throw new InFileModeException("Ожидалось число Float, получено: " + line);
                }
                console.printErr("Ожидалось число типа Float.");
            }
        }
    }

    private String askNullableString(String prompt) throws ExitPoint {
        if (!Console.isFileMode()) console.print(prompt);
        String line = readLine();
        String trimmed = line.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String readLine() throws ExitPoint {
        try {
            return ScannerManager.getScannerManager().getUserScanner().nextLine();
        } catch (NoSuchElementException e) {
            throw new ExitPoint("Ввод завершён.");
        }
    }
}
