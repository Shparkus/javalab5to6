package Exceptions;

public class EmptyCollectionException extends Exception {
    public EmptyCollectionException() {
        super("Коллекция пуста.");
    }

    public EmptyCollectionException(String message) {
        super(message);
    }
}
