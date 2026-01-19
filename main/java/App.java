import ConsoleOperations.Console;
import ConsoleOperations.Printable;

public class App {
    public static void main(String[] args) {
        Printable console = new Console();
        console.println("Запуск приложения разделён на клиент и сервер.");
        console.println("Используйте Client.ClientApp для клиента и Server.ServerApp для сервера.");
    }
}
