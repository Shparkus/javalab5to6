package Commands;

import ConsoleOperations.Printable;
import Managers.CommandManager;

public class HelpCommand implements Command {
    private final Printable console;
    private final CommandManager commandManager;

    public HelpCommand(Printable console, CommandManager commandManager) {
        this.console = console;
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "вывести справку по доступным командам";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(String args) {
        console.println(commandManager.buildHelp());
    }
}