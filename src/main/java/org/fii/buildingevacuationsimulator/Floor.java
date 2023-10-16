package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Door> doors = new ArrayList<>();
    private Canvas canvas;

    public Floor (BuildingController buildingController, Canvas canvas) {
        this.canvas = canvas;
        this.canvas.getGraphicsContext2D().setStroke(Color.BLACK);

        Room first_room = new Room(300, 100, 600, 600);
        rooms.add(first_room);
        buildingController.addVertex(first_room);
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    public void addDoor(Door door) {
        doors.add(door);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void clear() {
        rooms.clear();
        doors.clear();
    }
}
