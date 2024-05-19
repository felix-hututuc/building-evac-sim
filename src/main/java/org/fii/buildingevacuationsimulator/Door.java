package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    private final double capacity;

    private double x;
    private double y;

    private String color = "black";

    public Door(Room room1, Room room2, double capacity, double x, double y) {
        this.uuid = UUID.randomUUID().toString();
        this.room1 = room1;
        this.room2 = room2;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
    }

    public Door(String uuid, Room room1, Room room2, double capacity, double x, double y) {
        this.uuid = uuid;
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

    public String getWeightAsString() {
        if (capacity == Double.MAX_VALUE) {
            System.out.println("∞");
            return "∞";
        }
        return String.valueOf((int)capacity);
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Paint.valueOf("#434649"));
        gc.fillOval(x - 10, y - 10, 20, 20);
        // draw capacity
        gc.setFill(Color.RED);
        gc.setFont(javafx.scene.text.Font.font(13));
        gc.fillText(String.valueOf(capacity), x - 20, y - 20);
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
                jsonObject.getJsonNumber("capacity").doubleValue(),
                jsonObject.getJsonNumber("x").doubleValue(),
                jsonObject.getJsonNumber("y").doubleValue());
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
