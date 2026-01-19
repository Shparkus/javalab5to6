package Client;

import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.ExitPoint;
import Managers.ScannerManager;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        Printable console = new Console();
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5555;

        ScannerManager.init(new Scanner(System.in));

        try {
            ClientNetwork network = new ClientNetwork(host, port, 2000);
            ClientRuntime runtime = new ClientRuntime(console, network);
            runtime.interactiveMode();
        } catch (IOException e) {
            console.printErr("Не удалось запустить клиент: " + e.getMessage());
        } catch (ExitPoint e) {
            console.println("Работа клиента завершена.");
        }
    }
}
