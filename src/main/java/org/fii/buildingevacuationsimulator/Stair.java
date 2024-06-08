package org.fii.buildingevacuationsimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.util.Objects;
import java.util.UUID;

public class Stair extends Door {
    private final String uuid;
    private final Floor floor1;
    private final Floor floor2;
    public Stair(Floor floor1, Floor floor2, Room room1, Room room2, int capacity, double x, double y) {
        super(room1, room2, capacity, x, y);
        this.uuid = UUID.randomUUID().toString();
        this.floor1 = floor1;
        this.floor2 = floor2;
    }

    public Floor getFloor1() {
        return floor1;
    }

    public Floor getFloor2() {
        return floor2;
    }

    public void draw(GraphicsContext gc, int currentFloor) {
        gc.setFill(Color.BLACK);
        if (currentFloor == floor2.getFloorNumber()) {
            gc.strokeLine(getX(), getY(), getX() + 10, getY());
            gc.strokeLine(getX() + 10, getY(), getX() + 10, getY() + 10);
            gc.strokeLine(getX() + 10, getY() + 10, getX() + 20, getY() + 10);
            gc.strokeLine(getX() + 20, getY() + 10, getX() + 20, getY() + 20);
        } else {
            gc.strokeLine(getX(), getY(), getX(), getY() - 10);
            gc.strokeLine(getX(), getY() - 10, getX() + 10, getY() - 10);
            gc.strokeLine(getX() + 10, getY() - 10, getX() + 10, getY() - 20);
            gc.strokeLine(getX() + 10, getY() - 20, getX() + 20, getY() - 20);
        }
        gc.setFill(Color.RED);
        gc.fillText(String.valueOf(getWeight()), getX() + 15, getY() + 5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stair stair)) return false;
        if (!super.equals(o)) return false;
        return floor1.equals(stair.floor1) &&
                floor2.equals(stair.floor2);
    }

    //export as a json object using the json library
    @Override
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("uuid", uuid)
                .add("floor1", floor1.getFloorNumber())
                .add("floor2", floor2.getFloorNumber())
                .add("room1", getSource().getUuid())
                .add("room2", getTarget().getUuid())
                .add("capacity", getWeight())
                .add("x", getX())
                .add("y", getY())
                .build();
    }

    // import a door from a json object using the json library
    public static Stair fromJson(JsonObject jsonObject, Floor floor1, Floor floor2) {
        return new Stair(floor1, floor2,
                floor1.getRoomByUuid(jsonObject.getString("room1")),
                floor2.getRoomByUuid(jsonObject.getString("room2")),
                jsonObject.getJsonNumber("capacity").intValue(),
                jsonObject.getJsonNumber("x").doubleValue(),
                jsonObject.getJsonNumber("y").doubleValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uuid, floor1, floor2);
    }

    @Override
    public String toString() {
        return "Stair{" +
                "floor1=" + floor1 +
                ", floor2=" + floor2 +
                '}';
    }
}
