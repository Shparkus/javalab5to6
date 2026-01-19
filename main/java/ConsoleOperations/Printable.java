package ConsoleOperations;

/**
 * Интерфейс для реализации вывода информации
 */
public interface Printable {

    /**
     * Выводит сообщение без перехода на новую строку.
     */
    void print(String message);

    /**
     * Выводит сообщение с переходом на новую строку.
     */
    void println(String message);

    /**
     * Выводит сообщение об ошибке.
     */
    void printErr(String message);

    /**
     * Выводит подсказку пользователю.
     */
    void printHint(String message);
}
