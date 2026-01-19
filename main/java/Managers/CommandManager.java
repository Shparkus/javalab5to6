package Managers;
import Commands.Command;
import ConsoleOperations.Printable;
import Exceptions.IncorrectCommandException;
import java.util.LinkedHashMap;
import java.util.Map;


public class CommandManager {
    private final Printable console;
    private final Map<String, Command> commands = new LinkedHashMap<String, Command>();

    public CommandManager(Printable console) {
        this.console = console;
    }

    public void create(Command command) {
        if (command == null) return;
        String name = command.getName();
        if (name == null) return;
        commands.put(name, command);
    }

    public void execute(String name, String args) throws Exception {
        Command command = commands.get(name);
        if (command == null) {
            throw new IncorrectCommandException("Неизвестная команда: " + name);
        }
        command.execute(args);
    }

    public String buildHelp() {
        StringBuilder sb = new StringBuilder();

        for (Command cmd : commands.values()) {
            sb.append(cmd.getName());

            String usage = cmd.getUsage();
            if (usage != null && !usage.trim().isEmpty()) {
                sb.append(" ").append(usage);
            }

            sb.append(" : ");
            sb.append(cmd.getDescription());
            sb.append("\n");
        }

        return sb.toString();
    }
}
