package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.util.*;

public class Room {
    private final String uuid;
    private double x;
    private double y;
    private double width;
    private double height;
    private double space = 0;
    private int floorNumber;
    private final Set<Room> neighbours = new HashSet<>();
    private final Set<Door> doors = new HashSet<>();

    private boolean draggingRight = false;
    private boolean draggingLeft = false;
    private boolean draggingUp = false;
    private boolean draggingDown = false;

    public Room(double x, double y, double width, double height, int floorNumber) {
        this.uuid = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.floorNumber = floorNumber;
    }

    public Room(String uuid, double x, double y, double width, double height, int floorNumber) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.floorNumber = floorNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getSpace() {
        return space;
    }

    public void setSpace(double space) {
        this.space = space;
    }

    public void addNeighbour(Room room) {
        neighbours.add(room);
    }

    public boolean isDraggingRight() {
        return draggingRight;
    }

    public void setDraggingRight(boolean draggingRight) {
        this.draggingRight = draggingRight;
    }

    public boolean isDraggingLeft() {
        return draggingLeft;
    }

    public void setDraggingLeft(boolean draggingLeft) {
        this.draggingLeft = draggingLeft;
    }

    public boolean isDraggingUp() {
        return draggingUp;
    }

    public void setDraggingUp(boolean draggingUp) {
        this.draggingUp = draggingUp;
    }

    public boolean isDraggingDown() {
        return draggingDown;
    }

    public void setDraggingDown(boolean draggingDown) {
        this.draggingDown = draggingDown;
    }

    public boolean isInside(double x, double y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    public boolean isOnRightEdge(double x, double y) {
        return x >= this.x + this.width - 5 && x <= this.x + this.width + 5 && y >= this.y && y <= this.y + this.height;
    }

    public boolean isOnLeftEdge(double x, double y) {
        return x >= this.x - 5 && x <= this.x + 5 && y >= this.y && y <= this.y + this.height;
    }

    public boolean isOnTopEdge(double x, double y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y - 5 && y <= this.y + 5;
    }

    public boolean isOnBottomEdge(double x, double y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y + this.height - 5 && y <= this.y + this.height + 5;
    }

    public boolean isOnEdge(double x, double y) {
        return isOnRightEdge(x, y) ||
                isOnLeftEdge(x, y) ||
                isOnTopEdge(x, y) ||
                isOnBottomEdge(x, y);
    }

    public double[] getNearestEdge(double x, double y) {
        double[] nearestEdge = new double[2];
        double[] edges = new double[4];
        edges[0] = Math.abs(y - this.y);
        edges[1] = Math.abs(y - (this.y + this.height));
        edges[2] = Math.abs(x - this.x);
        edges[3] = Math.abs(x - (this.x + this.width));
        double min = edges[0];
        int index = 0;
        for (int i = 1; i < edges.length; i++) {
            if (edges[i] < min) {
                min = edges[i];
                index = i;
            }
        }
        switch (index) {
            case 0 -> {
                nearestEdge[0] = x;
                nearestEdge[1] = this.y;
            }
            case 1 -> {
                nearestEdge[0] = x;
                nearestEdge[1] = this.y + this.height;
            }
            case 2 -> {
                nearestEdge[0] = this.x;
                nearestEdge[1] = y;
            }
            case 3 -> {
                nearestEdge[0] = this.x + this.width;
                nearestEdge[1] = y;
            }
            default -> throw new IllegalStateException("Unexpected value: " + index);
        }
        return nearestEdge;
    }

    public Set<Room> getNeighbours() {
        return neighbours;
    }

    public void removeNeighbour(Room room) {
        neighbours.remove(room);
    }

    public void addDoor(Door door) {
        doors.add(door);
    }

    public Set<Door> getDoors() {
        return doors;
    }

    public void draw(GraphicsContext gc) {
        gc.strokeRect(x, y, width, height);
        for (var door : doors) {
            if ((door.getClass() == Stair.class && ((Stair) door).getFloor1().getFloorNumber() == floorNumber) || door.getClass() == Door.class) {
                door.draw(gc);
            }
        }
    }

    // export a room as a JSON object using the Json library
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("uuid", uuid)
                .add("x", x)
                .add("y", y)
                .add("width", width)
                .add("height", height)
                .add("floor", floorNumber)
                .build();
    }

    // import a room from a JSON object using the Json library
    public static Room fromJson(JsonObject jsonObject) {
        return new Room(
                jsonObject.getString("uuid"),
                jsonObject.getJsonNumber("x").doubleValue(),
                jsonObject.getJsonNumber("y").doubleValue(),
                jsonObject.getJsonNumber("width").doubleValue(),
                jsonObject.getJsonNumber("height").doubleValue(),
                jsonObject.getInt("floor")
        );
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void removeDoor(Door stair) {
        doors.remove(stair);
    }
}
