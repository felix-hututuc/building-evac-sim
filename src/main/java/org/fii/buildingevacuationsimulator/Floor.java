package org.fii.buildingevacuationsimulator;

import jakarta.json.JsonString;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Floor {
    private final String uuid;
    private int floorNumber = 0;
    private final List<Room> rooms = new ArrayList<>();
    private final List<Stair> stairs = new ArrayList<>();
    private final Canvas canvas = new Canvas(1200, 800);

    public Floor (int floorNumber) {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.floorNumber = floorNumber;
        this.canvas.getGraphicsContext2D().setStroke(Color.BLACK);
    }

    public Floor (String uuid, int floorNumber) {
        this.uuid = uuid;
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

    public void draw() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Room room : rooms) {
            room.draw(canvas.getGraphicsContext2D());
        }
        for (Stair stair : stairs) {
            if (stair.getFloor1().getFloorNumber() == floorNumber) {
                stair.draw(canvas.getGraphicsContext2D());
            }
        }
    }

    public Set<Door> getDoors() {
        Set<Door> doors = new HashSet<>();
        for (Room room : rooms) {
            doors.addAll(room.getDoors());
        }
        doors.addAll(stairs);
        return doors;
    }

    public Room getRoomByUuid(String room1) {
        for (Room room : rooms) {
            if (room.getUuid().equals(room1)) {
                return room;
            }
        }
        return null;
    }
}
