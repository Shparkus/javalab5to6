package Managers;
import ConsoleOperations.Printable;
import Exceptions.EmptyCollectionException;
import Models.Location;
import Models.Route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        int maxId = routes.stream()
                .mapToInt(Route::getId)
                .max()
                .orElse(0);
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
        Optional<Route> toRemove = routes.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
        toRemove.ifPresent(routes::remove);
        return toRemove.isPresent();
    }

    public boolean updateById(int id, Route newRoute) throws EmptyCollectionException {
        if (routes.isEmpty()) {
            throw new EmptyCollectionException("Коллекция пуста.");
        }
        if (newRoute == null) {
            console.printErr("Невозможно обновить: новый объект равен null.");
            return false;
        }

        Route old = routes.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);

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
        return routes.stream()
                .sorted()
                .collect(Collectors.toList());
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

        return routes.stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new EmptyCollectionException("Коллекция пуста."));
    }

    public Route getMin() throws EmptyCollectionException {
        if (routes.isEmpty()) {
            throw new EmptyCollectionException("Коллекция пуста.");
        }

        return routes.stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new EmptyCollectionException("Коллекция пуста."));
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

        List<Route> toRemove = routes.stream()
                .filter(r -> r.compareTo(pivot) < 0)
                .collect(Collectors.toList());

        routes.removeAll(toRemove);
        return toRemove.size();
    }

    public boolean removeAnyByDistance(Long distance) {
        if (distance == null) return false;

        Optional<Route> toRemove = routes.stream()
                .filter(r -> distance.equals(r.getDistance()))
                .findFirst();

        toRemove.ifPresent(routes::remove);
        return toRemove.isPresent();
    }

    public List<Route> filterGreaterThanDistance(Long distance) {
        if (distance == null) {
            return new ArrayList<>();
        }

        return routes.stream()
                .filter(r -> r.getDistance() != null && r.getDistance() > distance)
                .collect(Collectors.toList());
    }

    public List<Long> getDistancesDescending() {
        return routes.stream()
                .map(Route::getDistance)
                .filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public Route getById(int id) {
        return routes.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Route> getSorted() {
        return getAllSorted();
    }

    public List<Route> getSortedByLocation() {
        return sortByLocation(new ArrayList<>(routes));
    }

    public List<Route> sortByLocation(List<Route> routesToSort) {
        Comparator<Location> locationComparator = Comparator
                .comparing(Location::getX, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparingInt(Location::getY)
                .thenComparing(Location::getZ, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Location::getName, Comparator.nullsFirst(Comparator.naturalOrder()));

        return routesToSort.stream()
                .sorted(Comparator.comparing(Route::getFrom, Comparator.nullsFirst(locationComparator)))
                .collect(Collectors.toList());
    }
}
