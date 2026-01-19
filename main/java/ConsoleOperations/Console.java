package ConsoleOperations;

import java.io.PrintStream;

public class Console implements Printable {
    private static volatile boolean fileMode = false;

    private final PrintStream out;
    private final PrintStream err;

    public Console() {
        this(System.out, System.err);
    }

    public Console(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public static boolean isFileMode() {
        return fileMode;
    }

    public static void setFileMode(boolean fileMode) {
        Console.fileMode = fileMode;
    }

    @Override
    public void print(String message) {
        out.print(message);
    }

    @Override
    public void println(String message) {
        out.println(message);
    }

    @Override
    public void printErr(String message) {
        err.println("Ошибка: " + message);
    }

    @Override
    public void printHint(String message) {
        out.println("Подсказка: " + message);
    }
}
