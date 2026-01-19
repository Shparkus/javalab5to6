package Models.Forms;

import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Exceptions.InFileModeException;
import Exceptions.InvalidFormException;
import Managers.ScannerManager;
import Models.Coordinates;
import java.util.NoSuchElementException;

public class CoordinatesForm implements Form<Coordinates> {
    private final Printable console;

    public CoordinatesForm(Printable console) {
        this.console = console;
    }

    @Override
    public Coordinates build() throws InvalidFormException, InFileModeException, ExitPoint {
        float x = askFloat("Введите координату x (float): ");
        long y = askLong("Введите координату y (long): ");
        Coordinates c = new Coordinates(x, y);
        if (!c.validate()) {
            throw new InvalidFormException("Координаты не прошли валидацию.");
        }
        return c;
    }

    private float askFloat(String prompt) throws InFileModeException, ExitPoint {
        while (true) {
            if (!Console.isFileMode()) console.print(prompt);
            String line = readLine();
            try {
                return Float.parseFloat(line.trim());
            } catch (NumberFormatException e) {
                if (Console.isFileMode()) {
                    throw new InFileModeException("Ожидалось число типа float, получено: " + line);
                }
                console.printErr("Ожидалось число типа float.");
            }
        }
    }

    private long askLong(String prompt) throws InFileModeException, ExitPoint {
        while (true) {
            if (!Console.isFileMode()) console.print(prompt);
            String line = readLine();
            try {
                return Long.parseLong(line.trim());
            } catch (NumberFormatException e) {
                if (Console.isFileMode()) {
                    throw new InFileModeException("Ожидалось число типа long, получено: " + line);
                }
                console.printErr("Ожидалось число типа long.");
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