package Commands;

public interface Command {

    String getName();

    String getDescription();

    String getUsage();

    void execute(String args) throws Exception;
}