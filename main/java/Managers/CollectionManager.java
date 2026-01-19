package Managers;
import ConsoleOperations.Printable;
import Exceptions.EmptyCollectionException;
import Models.Route;
import java.util.*;

public class CollectionManager {
    private final Printable console;
    private final HashSet<Route> routes;
    private final Date initDate;
    private int nextId = 1;
    public CollectionManager(Printable console, Collection<Route> initialRoutes) {
        this.console = console;
        this.routes = new HashSet<>();

        if (initialRoutes != null) {
            this.routes.addAll(initialRoutes);
        }

        this.initDate = new Date();
        recalculateNextId();
    }

    private void recalculateNextId() {
        int maxId = 0;
        for (Route r : routes) {
            if (r.getId() > maxId) {
                maxId = r.getId();
            }
        }
        this.nextId = maxId + 1;
        if (this.nextId <= 0) {
            this.nextId = 1;
        }
    }

    private int generateId() {
        int id = nextId;
        nextId++;
        if (nextId <= 0) {
            nextId = 1;
        }
        return id;
    }

    public Date getInitDate() {
        return initDate;
    }

    public int size() {
        return routes.size();
    }

    public HashSet<Route> getRoutes() {
        return routes;
    }

    public void add(Route route) {
        if (route == null) return;

        if (route.getId() <= 0) {
            route.setId(generateId());
        }
        if (route.getCreationDate() == null) {
            route.setCreationDate(new Date());
        }

        if (!route.validate()) {
            console.printErr("Невозможно добавить Route: объект не прошёл валидацию.");
            return;
        }

        routes.add(route);
    }

    public void clear() {
        routes.clear();
    }

    public boolean removeById(int id) {
        Route toRemove = null;
        for (Route r : routes) {
            if (r.getId() == id) {
                toRemove = r;
                break;
            }
        }
        if (toRemove != null) {
            routes.remove(toRemove);
            return true;
        }
        return false;
    }

    public boolean updateById(int id, Route newRoute) throws EmptyCollectionException {
        if (routes.isEmpty()) {
            throw new EmptyCollectionException("Коллекция пуста.");
        }
        if (newRoute == null) {
            console.printErr("Невозможно обновить: новый объект равен null.");
            return false;
        }

        Route old = null;
        for (Route r : routes) {
            if (r.getId() == id) {
                old = r;
                break;
            }
        }

        if (old == null) {
            return false;
        }

        newRoute.setId(old.getId());
        newRoute.setCreationDate(old.getCreationDate());

        if (!newRoute.validate()) {
            console.printErr("Новый объект Route не прошёл валидацию, обновление отменено.");
            return false;
        }

        routes.remove(old);
        routes.add(newRoute);
        return true;
    }

    public List<Route> getAllSorted() {
        List<Route> list = new ArrayList<Route>(routes);
        Collections.sort(list);
        return list;
    }

    public String getInfoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип коллекции: ").append(routes.getClass().getName()).append("\n");
        sb.append("Дата инициализации: ").append(initDate).append("\n");
        sb.append("Количество элементов: ").append(routes.size());
        return sb.toString();
    }

    public Route getMax() throws EmptyCollectionException {
        if (routes.isEmpty()) {
            throw new EmptyCollectionException("Коллекция пуста.");
        }

        Route max = null;
        for (Route r : routes) {
            if (max == null || r.compareTo(max) > 0) {
                max = r;
            }
        }
        return max;
    }

    public Route getMin() throws EmptyCollectionException {
        if (routes.isEmpty()) {
            throw new EmptyCollectionException("Коллекция пуста.");
        }

        Route min = null;
        for (Route r : routes) {
            if (min == null || r.compareTo(min) < 0) {
                min = r;
            }
        }
        return min;
    }

    public boolean addIfMax(Route route) throws EmptyCollectionException {
        if (route == null) return false;

        if (routes.isEmpty()) {
            add(route);
            return true;
        }

        Route max = getMax();
        if (route.compareTo(max) > 0) {
            add(route);
            return true;
        }
        return false;
    }

    public boolean addIfMin(Route route) throws EmptyCollectionException {
        if (route == null) return false;

        if (routes.isEmpty()) {
            add(route);
            return true;
        }

        Route min = getMin();
        if (route.compareTo(min) < 0) {
            add(route);
            return true;
        }
        return false;
    }

    public int removeLower(Route pivot) {
        if (pivot == null) return 0;

        List<Route> toRemove = new ArrayList<Route>();
        for (Route r : routes) {
            if (r.compareTo(pivot) < 0) {
                toRemove.add(r);
            }
        }

        int count = toRemove.size();
        for (Route r : toRemove) {
            routes.remove(r);
        }
        return count;
    }

    public boolean removeAnyByDistance(Long distance) {
        if (distance == null) return false;

        Route toRemove = null;
        for (Route r : routes) {
            Long d = r.getDistance();
            if (d != null && d.equals(distance)) {
                toRemove = r;
                break;
            }
        }

        if (toRemove != null) {
            routes.remove(toRemove);
            return true;
        }
        return false;
    }

    public List<Route> filterGreaterThanDistance(Long distance) {
        List<Route> result = new ArrayList<Route>();
        if (distance == null) {
            return result;
        }

        for (Route r : routes) {
            Long d = r.getDistance();
            if (d != null && d > distance) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Long> getDistancesDescending() {
        List<Long> result = new ArrayList<Long>();

        for (Route r : routes) {
            Long d = r.getDistance();
            if (d != null) {
                result.add(d);
            }
        }

        // сортировка по убыванию
        Collections.sort(result, new Comparator<Long>() {
            @Override
            public int compare(Long a, Long b) {
                return b.compareTo(a);
            }
        });

        return result;
    }

    public Route getById(int id) {
        for (Route r : routes) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public List<Route> getSorted() {
        return getAllSorted();
    }
}
