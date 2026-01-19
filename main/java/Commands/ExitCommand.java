package Commands;
import Exceptions.ExitPoint;
import Exceptions.WrongAmountOfArgsException;

public class ExitCommand implements Command {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "завершить программу (без сохранения)";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(String args) throws WrongAmountOfArgsException, ExitPoint {
        if (args != null && !args.trim().isEmpty()) {
            throw new WrongAmountOfArgsException("exit");
        }
        throw new ExitPoint();
    }
}
