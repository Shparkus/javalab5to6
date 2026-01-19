package ConsoleOperations;

public enum Hint {
    HELP("Введите 'help', чтобы увидеть список команд.");

    private final String text;

    Hint(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}