package Exceptions;

public class WrongAmountOfArgsException extends Exception {
    public WrongAmountOfArgsException(String usage) {
        super("Неверное количество аргументов. Использование: " + usage);
    }
}
