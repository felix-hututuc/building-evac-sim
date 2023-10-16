package org.fii.buildingevacuationsimulator;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Objects;
import java.util.UUID;

public class Door extends DefaultWeightedEdge {

    private final String uuid;
    private final Room room1;
    private final Room room2;
    private final double capacity;

    private double x;
    private double y;

    public Door(Room room1, Room room2, double capacity, double x, double y) {
        this.uuid = UUID.randomUUID().toString();
        this.room1 = room1;
        this.room2 = room2;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
    }
    @Override
    public Room getSource() {
        return room1;
    }

    @Override
    public Room getTarget() {
        return room2;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getWeight() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Door door = (Door) o;
        return uuid.equals(door.uuid) && Double.compare(door.capacity, capacity) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, capacity);
    }
}
