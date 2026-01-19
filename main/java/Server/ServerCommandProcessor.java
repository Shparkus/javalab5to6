package Server;

import Exceptions.EmptyCollectionException;
import Exceptions.FileAccessException;
import Managers.CollectionManager;
import Managers.FileManager;
import Models.Route;
import Network.CommandType;
import Network.Request;
import Network.Response;

import java.util.List;

public class ServerCommandProcessor {
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public ServerCommandProcessor(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    public Response process(Request request) {
        if (request == null || request.getType() == null) {
            return new Response(false, "Некорректный запрос.");
        }

        CommandType type = request.getType();
        try {
            switch (type) {
                case HELP:
                    return new Response(true, buildHelp());
                case INFO:
                    return new Response(true, collectionManager.getInfoString());
                case SHOW:
                    return new Response(true, "Содержимое коллекции:", collectionManager.getSortedByLocation());
                case ADD:
                    return handleAdd(request.getRoute());
                case UPDATE:
                    return handleUpdate(request);
                case REMOVE_BY_ID:
                    return handleRemoveById(request.getArgument());
                case CLEAR:
                    collectionManager.clear();
                    return new Response(true, "Коллекция очищена.");
                case ADD_IF_MAX:
                    return handleAddIfMax(request.getRoute());
                case ADD_IF_MIN:
                    return handleAddIfMin(request.getRoute());
                case REMOVE_LOWER:
                    return handleRemoveLower(request.getRoute());
                case REMOVE_ANY_BY_DISTANCE:
                    return handleRemoveAnyByDistance(request.getArgument());
                case FILTER_GREATER_THAN_DISTANCE:
                    return handleFilterGreaterThanDistance(request.getArgument());
                case PRINT_FIELD_DESCENDING_DISTANCE:
                    return new Response(true, "Список расстояний:", null, collectionManager.getDistancesDescending());
                case EXIT:
                    return new Response(true, "Команда exit подтверждена.");
                default:
                    return new Response(false, "Неизвестная команда.");
            }
        } catch (Exception e) {
            return new Response(false, "Ошибка выполнения команды: " + e.getMessage());
        }
    }

    private Response handleAdd(Route route) {
        if (route == null) {
            return new Response(false, "Команда add требует объект Route.");
        }
        collectionManager.add(route);
        return new Response(true, "Элемент добавлен.");
    }

    private Response handleUpdate(Request request) throws EmptyCollectionException {
        if (!(request.getArgument() instanceof Integer) || request.getRoute() == null) {
            return new Response(false, "Команда update требует id и объект Route.");
        }
        int id = (Integer) request.getArgument();
        boolean updated = collectionManager.updateById(id, request.getRoute());
        if (updated) {
            return new Response(true, "Элемент обновлён.");
        }
        return new Response(false, "Элемент с указанным id не найден.");
    }

    private Response handleRemoveById(Object arg) {
        if (!(arg instanceof Integer)) {
            return new Response(false, "Команда remove_by_id требует целое число.");
        }
        int id = (Integer) arg;
        boolean removed = collectionManager.removeById(id);
        if (removed) {
            return new Response(true, "Элемент удалён.");
        }
        return new Response(false, "Элемент с указанным id не найден.");
    }

    private Response handleAddIfMax(Route route) throws EmptyCollectionException {
        if (route == null) {
            return new Response(false, "Команда add_if_max требует объект Route.");
        }
        boolean added = collectionManager.addIfMax(route);
        if (added) {
            return new Response(true, "Элемент добавлен.");
        }
        return new Response(false, "Элемент не превышает максимальный.");
    }

    private Response handleAddIfMin(Route route) throws EmptyCollectionException {
        if (route == null) {
            return new Response(false, "Команда add_if_min требует объект Route.");
        }
        boolean added = collectionManager.addIfMin(route);
        if (added) {
            return new Response(true, "Элемент добавлен.");
        }
        return new Response(false, "Элемент не меньше минимального.");
    }

    private Response handleRemoveLower(Route route) {
        if (route == null) {
            return new Response(false, "Команда remove_lower требует объект Route.");
        }
        int count = collectionManager.removeLower(route);
        return new Response(true, "Удалено элементов: " + count + ".");
    }

    private Response handleRemoveAnyByDistance(Object arg) {
        if (!(arg instanceof Long)) {
            return new Response(false, "Команда remove_any_by_distance требует число Long.");
        }
        boolean removed = collectionManager.removeAnyByDistance((Long) arg);
        if (removed) {
            return new Response(true, "Элемент удалён.");
        }
        return new Response(false, "Элемент с указанной дистанцией не найден.");
    }

    private Response handleFilterGreaterThanDistance(Object arg) {
        if (!(arg instanceof Long)) {
            return new Response(false, "Команда filter_greater_than_distance требует число Long.");
        }
        Long distance = (Long) arg;
        List<Route> routes = collectionManager.sortByLocation(collectionManager.filterGreaterThanDistance(distance));
        return new Response(true, "Отфильтрованные элементы:", routes);
    }

    public void saveCollection() throws FileAccessException {
        fileManager.writeCollection(collectionManager.getRoutes());
    }

    private String buildHelp() {
        return String.join("\n",
                "help : вывести справку по доступным командам",
                "info : вывести информацию о коллекции",
                "show : вывести элементы коллекции",
                "add {element} : добавить новый элемент",
                "update id {element} : обновить элемент по id",
                "remove_by_id id : удалить элемент по id",
                "clear : очистить коллекцию",
                "add_if_max {element} : добавить, если больше максимального",
                "add_if_min {element} : добавить, если меньше минимального",
                "remove_lower {element} : удалить элементы меньше заданного",
                "remove_any_by_distance distance : удалить один элемент по distance",
                "filter_greater_than_distance distance : вывести элементы больше distance",
                "print_field_descending_distance : вывести расстояния по убыванию",
                "execute_script file_name : выполнить скрипт (клиент) ",
                "exit : завершить работу клиента"
        );
    }
}
