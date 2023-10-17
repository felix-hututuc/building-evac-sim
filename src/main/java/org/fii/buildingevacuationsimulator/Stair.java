package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.GraphicsContext;

import java.util.Objects;

public class Stair extends Door {
    private final Floor floor1;
    private final Floor floor2;
    public Stair(Floor floor1, Floor floor2, Room room1, Room room2, double capacity, double x, double y) {
        super(room1, room2, capacity, x, y);
        this.floor1 = floor1;
        this.floor2 = floor2;
    }

    public Floor getFloor1() {
        return floor1;
    }

    public Floor getFloor2() {
        return floor2;
    }

//    @Override
//    public void draw(GraphicsContext gc) {
//
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stair stair)) return false;
        if (!super.equals(o)) return false;
        return floor1.equals(stair.floor1) &&
                floor2.equals(stair.floor2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), floor1, floor2);
    }

    @Override
    public String toString() {
        return "Stair{" +
                "floor1=" + floor1 +
                ", floor2=" + floor2 +
                '}';
    }
}
