package Server;

import ConsoleOperations.Console;
import ConsoleOperations.Printable;
import Exceptions.FileAccessException;
import Managers.CollectionManager;
import Managers.FileManager;
import Models.Route;
import Network.Request;
import Network.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;

public class ServerApp {
    private static String getEnvFilePath() {
        String v = System.getenv("ROUTE_FILE");
        if (v != null && !v.trim().isEmpty()) return v;
        v = System.getenv("LAB5");
        if (v != null && !v.trim().isEmpty()) return v;
        return null;
    }

    public static void main(String[] args) {
        Printable console = new Console();
        String filePath = getEnvFilePath();
        if (filePath == null || filePath.trim().isEmpty()) {
            console.printErr("Переменная окружения ROUTE_FILE (или LAB5) не задана. Коллекция будет пустой, сохранение недоступно.");
        } else {
            console.println("Файл коллекции: " + filePath);
        }

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5555;

        FileManager fileManager = new FileManager(console, filePath);
        HashSet<Route> initialSet = new HashSet<>();
        try {
            initialSet = fileManager.readCollection();
            console.println("Загружено элементов: " + initialSet.size());
        } catch (FileAccessException e) {
            console.printErr(e.getMessage());
            console.printErr("Коллекция будет инициализирована пустой.");
        }

        CollectionManager collectionManager = new CollectionManager(console, initialSet);
        ServerCommandProcessor processor = new ServerCommandProcessor(collectionManager, fileManager);

        try (DatagramChannel channel = DatagramChannel.open();
             Selector selector = Selector.open();
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);

            console.println("Сервер запущен на порту: " + port);
            ServerReceiver receiver = new ServerReceiver(channel, selector);
            ServerRequestReader requestReader = new ServerRequestReader();
            ServerResponseSender responseSender = new ServerResponseSender(channel);

            boolean running = true;
            while (running) {
                if (reader.ready()) {
                    String line = reader.readLine();
                    if (line != null) {
                        String trimmed = line.trim().toLowerCase();
                        if ("save".equals(trimmed)) {
                            try {
                                processor.saveCollection();
                                console.println("Коллекция сохранена.");
                            } catch (FileAccessException e) {
                                console.printErr("Ошибка сохранения: " + e.getMessage());
                            }
                        } else if ("exit".equals(trimmed)) {
                            running = false;
                            continue;
                        }
                    }
                }

                ReceivedDatagram datagram = receiver.receive(200);
                if (datagram == null) {
                    continue;
                }

                try {
                    Request request = requestReader.read(datagram.getData());
                    Response response = processor.process(request);
                    responseSender.send(response, datagram.getAddress());
                } catch (Exception e) {
                    console.printErr("Ошибка обработки запроса: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            console.printErr("Ошибка запуска сервера: " + e.getMessage());
        } finally {
            try {
                processor.saveCollection();
                console.println("Коллекция сохранена перед завершением.");
            } catch (Exception e) {
                console.printErr("Не удалось сохранить коллекцию при завершении: " + e.getMessage());
            }
        }
    }
}
