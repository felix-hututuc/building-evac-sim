package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber = 0;
    private final List<Room> rooms = new ArrayList<>();
    private final List<Stair> stairs = new ArrayList<>();
    private final Canvas canvas = new Canvas(1200, 800);

    public Floor (int floorNumber) {
        this.floorNumber = floorNumber;
        this.canvas.getGraphicsContext2D().setStroke(Color.BLACK);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }


    public List<Room> getRooms() {
        return rooms;
    }

    public void addStair(Stair stair) {
        stairs.add(stair);
    }

    public void removeStair(Stair stair) {
        stairs.remove(stair);
    }

    public List<Stair> getStairs() {
        return stairs;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
