package Models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Route implements Comparable<Route>, Validator, Serializable {
    private int id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private Location from;
    private Location to;
    private Long distance;

    public Route() {
    }

    public Route(String name,
                 Coordinates coordinates,
                 Location from,
                 Location to,
                 Long distance) {
        this.name = name;
        this.coordinates = coordinates;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    @Override
    public boolean validate() {
        if (id <= 0) return false;
        if (name == null || name.trim().isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (from == null || !from.validate()) return false;
        if (distance == null || distance <= 1) return false;
        if (to != null && !to.validate()) return false;
        return true;
    }

    @Override
    public int compareTo(Route other) {
        if (other == null) {
            return 1;
        }
        long d1 = distance;
        if (distance == null) {
            d1 = Long.MIN_VALUE;
        }
        long d2 = other.distance;
        if (other.distance == null) {
            d2 = Long.MIN_VALUE;
        }
        int cmp = Long.compare(d1, d2);
        if (cmp != 0) return cmp;
        cmp = String.valueOf(name).compareTo(String.valueOf(other.name));
        if (cmp != 0) return cmp;
        return Integer.compare(id, other.id);
    }

    @Override
    public String toString() {
        return "Route{" + "id=" + id + ", name='" + name + '\'' +
                ", coordinates=" + coordinates + ", creationDate=" + creationDate +
                ", from=" + from + ", to=" + to + ", distance=" + distance + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return id == route.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
