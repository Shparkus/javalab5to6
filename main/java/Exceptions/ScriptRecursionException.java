package Exceptions;

public class ScriptRecursionException extends Exception {
    public ScriptRecursionException() {
        super("Обнаружена рекурсия при выполнении скрипта.");
    }
}
