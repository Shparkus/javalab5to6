package Models;
import java.io.Serializable;
import java.util.Objects;

public class Location implements Validator, Serializable {
    private Double x;
    private int y;
    private Float z;
    private String name;

    public Location() {
    }

    public Location(Double x, int y, Float z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean validate() {
        return x != null && z != null;
    }

    @Override
    public String toString() {
        return "Location{" + "x=" + x + ", y=" + y +
                ", z=" + z + ", name='" + name + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return y == location.y && Objects.equals(x, location.x) &&
                Objects.equals(z, location.z) && Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, name);
    }
}
