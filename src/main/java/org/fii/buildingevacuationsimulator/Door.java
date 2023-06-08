package org.fii.buildingevacuationsimulator;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Objects;

public class Door extends DefaultWeightedEdge {
    private final Room room1;
    private final Room room2;
    private double capacity;

    private double x;
    private double y;

    public Door(Room room1, Room room2, double capacity, double x, double y) {
        this.room1 = room1;
        this.room2 = room2;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
    }

    public Room getRoom1() {
        return room1;
    }

    public Room getRoom2() {
        return room2;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
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
        return Double.compare(door.capacity, capacity) == 0 && room1.equals(door.room1) && room2.equals(door.room2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room1, room2, capacity);
    }
}
