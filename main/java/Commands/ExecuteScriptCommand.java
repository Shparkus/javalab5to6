package Commands;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Exceptions.ScriptRecursionException;
import Exceptions.WrongAmountOfArgsException;
import Managers.RuntimeManager;

public class ExecuteScriptCommand implements Command {
    private final Printable console;
    private final RuntimeManager runtimeManager;

    public ExecuteScriptCommand(Printable console, RuntimeManager runtimeManager) {
        this.console = console;
        this.runtimeManager = runtimeManager;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "считать и исполнить скрипт из указанного файла";
    }

    @Override
    public String getUsage() {
        return "file_name";
    }

    @Override
    public void execute(String args) throws WrongAmountOfArgsException, ScriptRecursionException, ExitPoint {
        if (args == null || args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("execute_script file_name");
        }
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 1) {
            throw new WrongAmountOfArgsException("execute_script file_name");
        }
        runtimeManager.executeScript(parts[0]);
    }
}
