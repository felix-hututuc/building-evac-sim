package org.fii.buildingevacuationsimulator;

import jakarta.json.JsonString;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

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
            stair.draw(canvas.getGraphicsContext2D());
        }
    }

    //export as a json object using the json library
//    public JsonObject toJson() {
//        JsonArrayBuilder roomsBuilder = Json.createArrayBuilder();
//        for (Room room : rooms) {
//            roomsBuilder.add(room.toJson());
//        }
////        JsonArrayBuilder stairsBuilder = Json.createArrayBuilder();
////        for (Stair stair : stairs) {
////            stairsBuilder.add(stair.toJson());
////        }
//        return Json.createObjectBuilder()
//                .add("floorNumber", floorNumber)
//                .add("rooms", roomsBuilder.build())
////                .add("stairs", stairsBuilder.build())
//                .build();
//    }
//
//    //import from a json object using the json library
//    public static Floor fromJson(JsonObject jsonObject, Room sink) {
//        Floor floor = new Floor(jsonObject.getInt("floorNumber"));
//        // add all rooms
////        floor.addRoom(sink);
//        jsonObject.getJsonArray("rooms").forEach(roomJson -> {
//            Room room = Room.fromJson((JsonObject) roomJson);
//            floor.addRoom(room);
//        });
//        jsonObject.getJsonArray("rooms").forEach(roomJson -> {
//            Room room = Room.fromJson((JsonObject) roomJson);
//            for (String neighbourUuid : ((JsonObject) roomJson).getJsonArray("neighbours").getValuesAs(JsonString::getString)) {
//                room.addNeighbour(floor.rooms.stream()
//                        .filter(r -> r.getUuid().equals(neighbourUuid))
//                        .findFirst()
//                        .orElseThrow());
//            }
//            var doors = ((JsonObject) roomJson).getJsonArray("doors");
//            for (var door : doors.getValuesAs(JsonObject.class)) {
//                var doorObject = Door.fromJson(door, room);
//                if (doorObject != null) {
//                    room.addDoor(doorObject);
//                }
//            }
//        });
//        return floor;
//    }

    public Room getRoomByUuid(String room1) {
        for (Room room : rooms) {
            if (room.getUuid().equals(room1)) {
                return room;
            }
        }
        return null;
    }
}
