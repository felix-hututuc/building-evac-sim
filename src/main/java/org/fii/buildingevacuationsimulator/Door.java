package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.Serial;
import java.util.Objects;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Door extends DefaultWeightedEdge {
    private final String uuid;
    private final Room room1;
    private final Room room2;
    private int capacity;

    private double x;
    private double y;

    private String color = "black";
    private FlowDirection flowDirection = FlowDirection.NONE;

    public Door(Room room1, Room room2, int capacity, double x, double y) {
        this.uuid = UUID.randomUUID().toString();
        this.room1 = room1;
        this.room2 = room2;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
    }

    public Door(String uuid, Room room1, Room room2, int capacity, double x, double y) {
        this.uuid = uuid;
        this.room1 = room1;
        this.room2 = room2;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
    }

    public Door(Door door) {
        this.uuid = door.uuid;
        this.room1 = door.room1;
        this.room2 = door.room2;
        this.capacity = door.capacity;
        this.x = door.x;
        this.y = door.y;
        this.color = door.color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public double getWeight() {
        return capacity;
    }

    public void setWeight(int capacity) {
        this.capacity = capacity;
    }

    public String getWeightAsString() {
        if (capacity == Double.MAX_VALUE) {
            System.out.println("∞");
            return "∞";
        }
        return String.valueOf((int)capacity);
    }

    public void setFlowDirection(FlowDirection flowDirection) {
        this.flowDirection = flowDirection;
    }

    public FlowDirection getFlowDirection() {
        return flowDirection;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Paint.valueOf(color));
        gc.fillOval(x - 10, y - 10, 20, 20);
        // draw capacity
        gc.setFill(Color.RED);
        gc.setFont(Font.font(13));
        gc.fillText(String.valueOf(capacity), x - 20, y - 20);
        
        // draw flow direction
        Room nextRoom = null;
        switch (flowDirection){
            case NONE -> {
                gc.setFill(Color.BLACK);
                return;
            }
            case SOURCE -> nextRoom = room1;
            case TARGET -> nextRoom = room2;
        }
        gc.setStroke(Paint.valueOf(color));
        gc.setLineWidth(2);
        if (nextRoom.isOnLeftEdge(x, y)) {
            gc.strokeLine(x, y, x + 20, y);
            gc.strokeLine(x + 20, y, x + 14, y - 6);
            gc.strokeLine(x + 20, y, x + 14, y + 6);
        } else if (nextRoom.isOnRightEdge(x, y)) {
            gc.strokeLine(x, y, x - 20, y);
            gc.strokeLine(x - 20, y, x - 14, y - 6);
            gc.strokeLine(x - 20, y, x - 14, y + 6);
        } else if (nextRoom.isOnTopEdge(x, y)) {
            gc.strokeLine(x, y, x, y + 20);
            gc.strokeLine(x, y + 20, x - 6, y + 14);
            gc.strokeLine(x, y + 20, x + 6, y + 14);
        } else if (nextRoom.isOnBottomEdge(x, y)) {
            gc.strokeLine(x, y, x, y - 20);
            gc.strokeLine(x, y - 20, x - 6, y - 14);
            gc.strokeLine(x, y - 20, x + 6, y - 14);
        } else {
            Room sourceRoom = getTarget() == nextRoom ? getSource() : getTarget();
            if (sourceRoom.isOnLeftEdge(x, y)) {
                gc.strokeLine(x, y, x - 20, y);
                gc.strokeLine(x - 20, y, x - 14, y - 6);
                gc.strokeLine(x - 20, y, x - 14, y + 6);
            } else if (sourceRoom.isOnRightEdge(x, y)) {
                gc.strokeLine(x, y, x + 20, y);
                gc.strokeLine(x + 20, y, x + 14, y - 6);
                gc.strokeLine(x + 20, y, x + 14, y + 6);
            } else if (sourceRoom.isOnTopEdge(x, y)) {
                gc.strokeLine(x, y, x, y - 20);
                gc.strokeLine(x, y - 20, x - 6, y - 14);
                gc.strokeLine(x, y - 20, x + 6, y - 14);
            } else if (sourceRoom.isOnBottomEdge(x, y)) {
                gc.strokeLine(x, y, x, y + 20);
                gc.strokeLine(x, y + 20, x - 6, y + 14);
                gc.strokeLine(x, y + 20, x + 6, y + 14);
            }
        }
        gc.setStroke(Color.BLACK);
    }

    // export a door as a JSON object using the Json library
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("uuid", uuid)
                .add("room1", room1.getUuid())
                .add("room2", room2.getUuid())
                .add("capacity", capacity)
                .add("x", x)
                .add("y", y)
                .build();
    }

    // import a door from a JSON object using the Json library
    public static Door fromJson(JsonObject jsonObject, Room room1, Room room2) {
        return new Door(jsonObject.getString("uuid"),
                room1,
                room2,
                jsonObject.getJsonNumber("capacity").intValue(),
                jsonObject.getJsonNumber("x").doubleValue(),
                jsonObject.getJsonNumber("y").doubleValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Door door = (Door) o;
        return uuid.equals(door.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
