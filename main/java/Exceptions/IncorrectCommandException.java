package Exceptions;

public class IncorrectCommandException extends Exception {
    public IncorrectCommandException() {
        super("Неизвестная команда. Введите 'help' для справки.");
    }

    public IncorrectCommandException(String message) {
        super(message);
    }
}
