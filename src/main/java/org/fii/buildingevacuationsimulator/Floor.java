package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Stair> stairs = new ArrayList<>();
    private final Canvas canvas;

    public Floor (Canvas canvas) {
        this.canvas = canvas;
        this.canvas.getGraphicsContext2D().setStroke(Color.BLACK);
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
