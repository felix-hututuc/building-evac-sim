package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final List<Room> rooms = new ArrayList<>();
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


    public Canvas getCanvas() {
        return canvas;
    }
}
