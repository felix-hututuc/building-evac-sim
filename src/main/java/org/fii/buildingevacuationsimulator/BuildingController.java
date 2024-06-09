package org.fii.buildingevacuationsimulator;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class BuildingController {
    private final List<Floor> floors = new ArrayList<>();
    private Floor currentFloor;

    private EvacuationProblemInstance evacuationProblem = new EvacuationProblemInstance();

    public BuildingController() {
        currentFloor = new Floor(0);

        Room firstRoom = new Room(300, 100, 600, 600, 0);
        currentFloor.addRoom(firstRoom);
        evacuationProblem.addVertex(firstRoom);
        evacuationProblem.setProblem(new DisjointPathsProblemSolver());

        currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
        currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
        currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());

        floors.add(currentFloor);
    }

    public BuildingController(List<Floor> floors, Floor currentFloor) {
        this.floors.addAll(floors);
        this.currentFloor = currentFloor;

        currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
        currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
        currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
    }

    public Canvas getCanvas() {
        return currentFloor.getCanvas();
    }

    public EventHandler<MouseEvent> canvasClickResize() {
        return event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            for (var room : currentFloor.getRooms()) {
                var rectX = room.getX();
                var rectY = room.getY();
                var rectWidth = room.getWidth();
                var rectHeight = room.getHeight();
                if (mouseX >= rectX + rectWidth - 5 && mouseX <= rectX + rectWidth + 5
                        && mouseY >= rectY && mouseY <= rectY + rectHeight) {
                    room.setDraggingRight(true);
                    for (Room neighbour : room.getNeighbours()) {
                        if (neighbour.getX() >= mouseX - 5 && neighbour.getX() <= mouseX + 5)
                            neighbour.setDraggingLeft(true);
                    }
                }
                if (mouseX >= rectX - 5 && mouseX <= rectX + 5
                        && mouseY >= rectY && mouseY <= rectY + rectHeight) {
                    room.setDraggingLeft(true);
                    for (Room neighbour : room.getNeighbours()) {
                        if (neighbour.getX() + neighbour.getWidth() >= mouseX - 5 && neighbour.getX() + neighbour.getWidth() <= mouseX + 5)
                            neighbour.setDraggingRight(true);
                    }
                }
                if (mouseX >= rectX && mouseX <= rectX + rectWidth
                        && mouseY >= rectY - 5 && mouseY <= rectY + 5) {
                    room.setDraggingUp(true);
                    for (Room neighbour : room.getNeighbours()) {
                        if (neighbour.getY() + neighbour.getHeight() >= mouseY - 5 && neighbour.getY() + neighbour.getHeight() <= mouseY + 5)
                            neighbour.setDraggingDown(true);
                    }
                }
                if (mouseX >= rectX && mouseX <= rectX + rectWidth
                        && mouseY >= rectY + rectHeight - 5 && mouseY <= rectY + rectHeight + 5) {
                    room.setDraggingDown(true);
                    for (Room neighbour : room.getNeighbours()) {
                        if (neighbour.getY() >= mouseY - 5 && neighbour.getY() <= mouseY + 5)
                            neighbour.setDraggingUp(true);
                    }
                }
            }
        };
    }

    public EventHandler<MouseEvent> canvasDragResize() {
        return event -> {
            for (var room : currentFloor.getRooms()) {
                if (room.isDraggingRight() && (event.getX() > 0 && event.getX() < currentFloor.getCanvas().getWidth())) {
                        for (var door : room.getDoors()) {
                            if (door.getX() == room.getX() + room.getWidth()) {
                                door.setX(room.getX() + event.getX() - room.getX());
                            }
                        }
                        room.setWidth(event.getX() - room.getX());
                }
                if (room.isDraggingLeft()) {
                    if (event.getX() > 0 && event.getX() < currentFloor.getCanvas().getWidth()) {
                        for (var door : room.getDoors()) {
                            if (door.getX() == room.getX()) {
                                door.setX(event.getX());
                            }
                        }
                        room.setWidth(room.getWidth() + room.getX() - event.getX());
                        room.setX(event.getX());
                    } else if (event.getX() <= 0) {
                        room.setX(0);
                    } else if (event.getX() >= currentFloor.getCanvas().getWidth()) {
                        room.setX(currentFloor.getCanvas().getWidth());
                    }
                }
                if (room.isDraggingUp()) {
                    if (event.getY() > 0 && event.getY() < currentFloor.getCanvas().getHeight()) {
                        for (var door : room.getDoors()) {
                            if (door.getY() == room.getY()) {
                                door.setY(event.getY());
                            }
                        }
                        room.setHeight(room.getHeight() + room.getY() - event.getY());
                        room.setY(event.getY());
                    } else if (event.getY() <= 0) {
                        room.setY(0);
                    } else if (event.getY() >= currentFloor.getCanvas().getHeight()) {
                        room.setY(currentFloor.getCanvas().getHeight());
                    }
                }
                if (room.isDraggingDown()) {
                    if (event.getY() > 0 && event.getY() < currentFloor.getCanvas().getHeight()) {
                        for (var door : room.getDoors()) {
                            if (door.getY() == room.getY() + room.getHeight()) {
                                door.setY(room.getY() + event.getY() - room.getY());
                            }
                        }
                        room.setHeight(event.getY() - room.getY());
                    }
                }
            }
            draw();
        };
    }

    public EventHandler<MouseEvent> canvasClickRelease() {
        return event -> {
            for (var room : currentFloor.getRooms()) {
                room.setDraggingRight(false);
                room.setDraggingLeft(false);
                room.setDraggingUp(false);
                room.setDraggingDown(false);
            }
        };
    }

    public EventHandler<MouseEvent> verticalClickCanvasHandle() {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : currentFloor.getRooms()) {
                if (room.isInside(x, y)) {
                    Room newRoom1 = new Room(room.getX(), room.getY(), x - room.getX(), room.getHeight(), currentFloor.getFloorNumber());
                    Room newRoom2 = new Room(x, room.getY(), room.getWidth() - (x - room.getX()), room.getHeight(), currentFloor.getFloorNumber());

                    for (var neighbour : room.getNeighbours()) {
                        if (neighbour.getX() < x) {
                            newRoom1.addNeighbour(neighbour);
                            neighbour.addNeighbour(newRoom1);
                            if (neighbour.getWidth() + neighbour.getX() > x) {
                                newRoom2.addNeighbour(neighbour);
                                neighbour.addNeighbour(newRoom2);
                            }
                        } else {
                            newRoom2.addNeighbour(neighbour);
                            neighbour.addNeighbour(newRoom2);
                        }
                        neighbour.removeNeighbour(room);
                        neighbour.getDoors().removeAll(room.getDoors());
                    }

                    room.getDoors().stream().filter(door -> door.getClass() == Stair.class).forEach(stair -> {
                        if (stair.getSource() == room) {
                            stair.getTarget().removeDoor(stair);
                        } else {
                            stair.getSource().removeDoor(stair);
                        }
                        ((Stair) stair).getFloor1().removeStair((Stair) stair);
                        ((Stair) stair).getFloor2().removeStair((Stair) stair);
                    });

                    newRoom1.addNeighbour(newRoom2);
                    newRoom2.addNeighbour(newRoom1);
                    currentFloor.removeRoom(room);
                    currentFloor.addRoom(newRoom1);
                    currentFloor.addRoom(newRoom2);
                    evacuationProblem.addVertex(newRoom1);
                    evacuationProblem.addVertex(newRoom2);
                    evacuationProblem.removeVertex(room);
                    evacuationProblem.removeAllEdges(room.getDoors());
                    break;
                }
            }
            draw();
        };
    }

    public EventHandler<ActionEvent> verticalButtonHandle() {
        return event -> {
            currentFloor.getCanvas().setOnMousePressed(verticalClickCanvasHandle());
            currentFloor.getCanvas().setOnMouseReleased(event1 -> {
                currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
                currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
                currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
            });
        };
    }

    public EventHandler<MouseEvent> horizontalClickCanvasHandle() {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : currentFloor.getRooms()) {
                if (room.isInside(x, y)) {
                    Room newRoom1 = new Room(room.getX(), room.getY(), room.getWidth(), y - room.getY(), currentFloor.getFloorNumber());
                    Room newRoom2 = new Room(room.getX(), y, room.getWidth(), room.getHeight() - (y - room.getY()), currentFloor.getFloorNumber());

                    for (var neighbour : room.getNeighbours()) {
                        if (neighbour.getY() < y) {
                            newRoom1.addNeighbour(neighbour);
                            neighbour.addNeighbour(newRoom1);
                            if (neighbour.getHeight() + neighbour.getY() > y) {
                                newRoom2.addNeighbour(neighbour);
                                neighbour.addNeighbour(newRoom2);
                            }
                        } else {
                            newRoom2.addNeighbour(neighbour);
                            neighbour.addNeighbour(newRoom2);
                        }
                        neighbour.removeNeighbour(room);
                        neighbour.getDoors().removeAll(room.getDoors());
                    }

                    room.getDoors().stream().filter(door -> door.getClass() == Stair.class).forEach(stair -> {
                        if (stair.getSource() == room) {
                            stair.getTarget().removeDoor(stair);
                        } else {
                            stair.getSource().removeDoor(stair);
                        }
                        ((Stair) stair).getFloor1().removeStair((Stair) stair);
                        ((Stair) stair).getFloor2().removeStair((Stair) stair);
                    });

                    newRoom1.addNeighbour(newRoom2);
                    newRoom2.addNeighbour(newRoom1);
                    currentFloor.removeRoom(room);
                    currentFloor.addRoom(newRoom1);
                    currentFloor.addRoom(newRoom2);
                    evacuationProblem.addVertex(newRoom1);
                    evacuationProblem.addVertex(newRoom2);
                    evacuationProblem.removeVertex(room);
                    evacuationProblem.removeAllEdges(room.getDoors());
                    break;
                }
            }
            draw();

        };
    }

    public EventHandler<ActionEvent> horizontalButtonHandle() {
        return event -> {
            currentFloor.getCanvas().setOnMousePressed(horizontalClickCanvasHandle());
            currentFloor.getCanvas().setOnMouseReleased(event1 -> {
                currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
                currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
                currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
            });
        };
    }

    public EventHandler<MouseEvent> doorClickCanvasHandle() {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : currentFloor.getRooms()) {
                if (room.isOnEdge(x, y)) {
                    for (var neighbour : room.getNeighbours()) {
                        if (neighbour.isOnEdge(x, y)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Input");
                            dialog.setHeaderText("Input door capacity");
                            Optional<String> result = dialog.showAndWait();
                            if (result.isEmpty() || result.get().isEmpty()) {
                                return;
                            }
                            double[] nearestEdge = room.getNearestEdge(x, y);

                            Door door = new Door(room, neighbour, Integer.parseInt(result.get()), nearestEdge[0], nearestEdge[1]);

                            room.addDoor(door);
                            neighbour.addDoor(door);
                            evacuationProblem.addEdge(door);

                            draw();
                            return;
                        }
                    }
                    // add door to sink
                    System.out.println("Adding door to sink");
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Input");
                    dialog.setHeaderText("Input door capacity");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isEmpty() || result.get().isEmpty()) {
                        return;
                    }
                    double[] nearestEdge = room.getNearestEdge(x, y);
                    Door door = new Door(room, evacuationProblem.getTarget(), Integer.parseInt(result.get()), nearestEdge[0], nearestEdge[1]);
                    room.addDoor(door);
                    evacuationProblem.getTarget().addDoor(door);
                    evacuationProblem.addEdge(door);

                    draw();
                    return;
                }
            }
        };
    }

    public EventHandler<ActionEvent> doorButtonHandle() {
        return event -> {
            currentFloor.getCanvas().setOnMousePressed(doorClickCanvasHandle());
            currentFloor.getCanvas().setOnMouseReleased(event1 -> {
                currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
                currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
                currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
            });
        };
    }

    public EventHandler<ActionEvent> stairButtonHandle() {
        return event -> {
            currentFloor.getCanvas().setOnMousePressed(stairClickCanvasHandle());
            currentFloor.getCanvas().setOnMouseReleased(event1 -> {
                currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
                currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
                currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
            });
        };
    }

    private EventHandler<MouseEvent> stairClickCanvasHandle() {
        return event -> {
            int currentFloorIndex = floors.indexOf(currentFloor);
            if (currentFloorIndex == 0) {
                System.out.println("Cannot add stair from first floor");
                return;
            }
            var x = event.getX();
            var y = event.getY();
            for (Room room : currentFloor.getRooms()) {
                if (room.isInside(x, y)) {
                    for (Room roomBellow : floors.get(currentFloorIndex - 1).getRooms()) {
                        if (roomBellow.isInside(x, y)) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Input");
                            dialog.setHeaderText("Input stair capacity");
                            Optional<String> result = dialog.showAndWait();
                            if (result.isEmpty() || result.get().isEmpty()) {
                                return;
                            }
                            Stair stair = new Stair(currentFloor, floors.get(currentFloorIndex - 1), room, roomBellow, Integer.parseInt(result.get()), x, y);
                            currentFloor.addStair(stair);
                            floors.get(currentFloorIndex - 1).addStair(stair);

                            room.addDoor(stair);

                            roomBellow.addDoor(stair);

                            evacuationProblem.addEdge(stair);
                            draw();
                            return;
                        }
                    }
                }
            }
        };
    }


    public EventHandler<MouseEvent> sourceClickCanvasHandle() {
        // add edge between source and clicked room
        return event -> {
            // check if event was left or right click
            if (event.isSecondaryButtonDown()) {
                return;
            }
            var x = event.getX();
            var y = event.getY();
            for (var room : currentFloor.getRooms()) {
                if (room.isInside(x, y)) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Input");
                    dialog.setHeaderText("Input source initial capacity");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isEmpty() || result.get().isEmpty()) {
                        return;
                    }
                    int resultInt = Integer.parseInt(result.get());
                    evacuationProblem.addEdgeToSource(room, resultInt);
                    room.setNrOfPeopleInside(resultInt);
                    System.out.println("Room selected as source");

                    draw();
                    return;
                }
            }
        };
    }

    public EventHandler<ActionEvent> sourceButtonHandle() {
        return event -> {
            currentFloor.getCanvas().setOnMousePressed(sourceClickCanvasHandle());
            currentFloor.getCanvas().setOnContextMenuRequested(event1 -> {
                currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
                currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
                currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
            });
        };
    }

    public EventHandler<ActionEvent> runSimulationHandle() {
        return _ -> {
            if (!evacuationProblem.isProblemSet()) {
                System.out.println("Problem not set");
                return;
            }
            evacuationProblem.executeSimulation();
            draw();
        };
    }

    public void draw() {
        currentFloor.getCanvas().getGraphicsContext2D().clearRect(0, 0, currentFloor.getCanvas().getWidth(), currentFloor.getCanvas().getHeight());
        for (var room : currentFloor.getRooms()) {
            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLACK);
            room.draw(currentFloor.getCanvas().getGraphicsContext2D());
            // draw room index
            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.RED);
            // room.getUuid().split("-")[0]
            currentFloor.getCanvas().getGraphicsContext2D().setFont(javafx.scene.text.Font.font(15));
            String text = String.valueOf(currentFloor.getRooms().indexOf(room));
            if (room.getNrOfPeopleInside() > 0) {
                text = text + " (" + room.getNrOfPeopleInside() + ")";
            }
            currentFloor.getCanvas().getGraphicsContext2D().fillText(text, room.getX() + room.getWidth() / 2, room.getY() + room.getHeight() / 2);

            // display current floor number
            currentFloor.getCanvas().getGraphicsContext2D().fillText("Floor " + currentFloor.getFloorNumber(), currentFloor.getCanvas().getWidth() * ((double) 9 / 10), currentFloor.getCanvas().getHeight() * ((double) 1 / 10));
        }
    }

    public EventHandler<ActionEvent> newFloorHandle(BorderPane root) {
        return event -> {
            currentFloor = new Floor(currentFloor.getFloorNumber() + 1);

            Room firstRoom = new Room(300, 100, 600, 600, currentFloor.getFloorNumber());
            currentFloor.addRoom(firstRoom);
            evacuationProblem.addVertex(firstRoom);

            floors.add(currentFloor);
            this.getCanvas().setOnMousePressed(canvasClickResize());
            this.getCanvas().setOnMouseReleased(canvasClickRelease());
            this.getCanvas().setOnMouseDragged(canvasDragResize());
            draw();
            root.setCenter(currentFloor.getCanvas());
        };
    }

    public EventHandler<ActionEvent> previousFloorHandle(BorderPane root) {
        return _ -> {
            if (floors.indexOf(currentFloor) >= 1) {
                currentFloor = floors.get(floors.indexOf(currentFloor) - 1);
                draw();
                root.setCenter(currentFloor.getCanvas());
            }
        };
    }

    public EventHandler<ActionEvent> nextFloorHandle(BorderPane root) {
        return _ -> {
            if (floors.indexOf(currentFloor) < floors.size() - 1) {
                currentFloor = floors.get(floors.indexOf(currentFloor) + 1);
                draw();
                root.setCenter(currentFloor.getCanvas());
            }
        };
    }

    public EventHandler<MouseEvent> changeFloorHandle(BorderPane root) {
        return event -> {
            MenuButton menuButton = (MenuButton) event.getSource();
            menuButton.getItems().clear();
            if (floors.indexOf(currentFloor) >= 1) {
                MenuItem previousFloorButton = new MenuItem("Previous Floor");
                previousFloorButton.setOnAction(previousFloorHandle(root));
                menuButton.getItems().add(previousFloorButton);
            }
            if (floors.indexOf(currentFloor) < floors.size() - 1) {
                MenuItem nextFloorButton = new MenuItem("Next Floor");
                nextFloorButton.setOnAction(nextFloorHandle(root));
                menuButton.getItems().add(nextFloorButton);
            }
            else if (floors.indexOf(currentFloor) == floors.size() - 1) {
                MenuItem newFloorButton = new MenuItem("New Floor");
                newFloorButton.setOnAction(newFloorHandle(root));
                menuButton.getItems().add(newFloorButton);
            }
        };
    }

    public EventHandler<ActionEvent> showGraphHandle() {
        return event -> {
            try {
                evacuationProblem.showGraph();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    // export as a JSON object using the json library
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("sink", evacuationProblem.getTarget().toJson())
                .add("floorsNr", floors.size())
                .add("currentFloor", floors.indexOf(currentFloor))
                .add("rooms", Json.createArrayBuilder(
                        floors.stream()
                                .flatMap(floor -> floor.getRooms().stream())
                                .map(Room::toJson)
                                .toList()
                ))
                .add("doors", Json.createArrayBuilder(
                        floors.stream()
                                .flatMap(floor -> floor.getRooms().stream())
                                .flatMap(room -> room.getDoors().stream())
                                .filter(door -> door.getClass() != Stair.class)
                                .map(Door::toJson)
                                .toList()
                ))
                .add("stairs", Json.createArrayBuilder(
                        floors.stream()
                                .flatMap(floor -> floor.getStairs().stream())
                                .map(Stair::toJson)
                                .toList()
                ))
                .build();
    }

    public EventHandler<ActionEvent> exportHandle() {
        return event -> {
            // write the json to a file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialDirectory(new File("./saves"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON", "*.json")
            );
            File file = fileChooser.showSaveDialog(new PopupWindow() {});
            if (file != null) {
                try {
                    boolean fileCreated = file.createNewFile();
                    if (!fileCreated) {
                        System.out.println("File already exists");
                        return;
                    }
                    Map<String, Boolean> config = new HashMap<>();

                    config.put(JsonGenerator.PRETTY_PRINTING, true);
                    JsonWriterFactory writerFactory = Json.createWriterFactory(config);
                    Writer writer = new FileWriter(file);
                    writerFactory.createWriter(writer).write(toJson());
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error while saving file");
                }
            }
        };
    }

    // import from a json object using the json library
    public static BuildingController fromJson(JsonObject jsonObject) {
        Room sink = Room.fromJson(jsonObject.getJsonObject("sink"));

        int floorsNr = jsonObject.getInt("floorsNr");
        int currentFloorNr = jsonObject.getInt("currentFloor");
        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i < floorsNr; i++) {
            floors.add(new Floor(i));
        }

        BuildingController buildingController = new BuildingController(floors, floors.get(currentFloorNr));

        List<Room> rooms = new ArrayList<>();
        jsonObject.getJsonArray("rooms").forEach(roomJson -> {
            Room room = Room.fromJson((JsonObject) roomJson);
            rooms.add(room);
            buildingController.floors.get(room.getFloorNumber()).addRoom(room);
            buildingController.evacuationProblem.addVertex(room);
        });

        jsonObject.getJsonArray("doors").forEach(doorJson -> {
            Room room1 = rooms.stream()
                    .filter(room -> room.getUuid().equals(((JsonObject) doorJson).getString("room1")))
                    .findFirst()
                    .orElseThrow();

            if (((JsonObject) doorJson).getString("room2").equals(sink.getUuid())) {
                Room newSink = buildingController.evacuationProblem.getTarget();
                Door door = Door.fromJson((JsonObject) doorJson, room1, newSink);
                room1.addDoor(door);

                buildingController.evacuationProblem.addEdge(door);

                return;
            }

            Room room2 = rooms.stream()
                    .filter(room -> room.getUuid().equals(((JsonObject) doorJson).getString("room2")))
                    .findFirst()
                    .orElseThrow();
            if (room1.getFloorNumber() != room2.getFloorNumber()) {
                return;
            }

            Door door = Door.fromJson((JsonObject) doorJson, room1, room2);
            room1.addDoor(door);
            room2.addDoor(door);
            buildingController.evacuationProblem.addEdge(door);
        });

        jsonObject.getJsonArray("stairs").forEach(stairJson -> {
            Floor floor1 = buildingController.floors.get(((JsonObject) stairJson).getInt("floor1"));
            Floor floor2 = buildingController.floors.get(((JsonObject) stairJson).getInt("floor2"));
            Room room1 = rooms.stream()
                    .filter(room -> room.getUuid().equals(((JsonObject) stairJson).getString("room1")))
                    .findFirst()
                    .orElseThrow();
            Room room2 = rooms.stream()
                    .filter(room -> room.getUuid().equals(((JsonObject) stairJson).getString("room2")))
                    .findFirst()
                    .orElseThrow();
            Stair stair = Stair.fromJson((JsonObject) stairJson, floor1, floor2);
            floor1.addStair(stair);
            floor2.addStair(stair);
            room1.addDoor(stair);
            room2.addDoor(stair);
            buildingController.evacuationProblem.addEdge(stair);
        });

        return buildingController;
    }

    public EventHandler<ActionEvent> importHandle(BorderPane root) {
        return _ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.setInitialDirectory(new File("./saves"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON", "*.json")
            );
            File file = fileChooser.showOpenDialog(new PopupWindow() {});
            if (file != null) {
                try {
                    JsonObject jsonObject = Json.createReader(file.toURI().toURL().openStream()).readObject();
                    BuildingController buildingController = fromJson(jsonObject);
                    currentFloor = buildingController.currentFloor;
                    floors.clear();
                    floors.addAll(buildingController.floors);
                    evacuationProblem = buildingController.evacuationProblem;
                    buildingController.draw();
                    root.setCenter(currentFloor.getCanvas());
                } catch (IOException e) {
                    System.out.println("Error while opening file");
                }
            }
        };
    }

    public EventHandler<ActionEvent> removeDoorHandle() {
        return _ -> {
            currentFloor.getCanvas().setOnMousePressed(removeDoorClickCanvasHandle());
            currentFloor.getCanvas().setOnMouseReleased(event1 -> {
                currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
                currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
                currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
            });
        };
    }

    public EventHandler<MouseEvent> removeDoorClickCanvasHandle() {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var Door : currentFloor.getDoors()) {
                if (x <= Door.getX() + 5 && x >= Door.getX() - 5 && y <= Door.getY() + 5 && y >= Door.getY() - 5) {
                    Door.getSource().removeDoor(Door);
                    Door.getTarget().removeDoor(Door);
                    if (Door.getClass() == Stair.class) {
                        ((Stair) Door).getFloor1().removeStair((Stair) Door);
                        ((Stair) Door).getFloor2().removeStair((Stair) Door);
                    }
                    evacuationProblem.removeEdge(Door);
                    System.out.println("Door removed");
                }
            }
            draw();
        };
    }

    public EventHandler<ActionEvent> resetSimulationHandle() {
        return event -> {
            evacuationProblem.resetSimulation();
            draw();
        };
    }

    public EventHandler<ActionEvent> resetAll(BorderPane root) {
        return _ -> {
            floors.clear();
            currentFloor = new Floor(0);
            floors.add(currentFloor);
            evacuationProblem = new EvacuationProblemInstance();

            Room firstRoom = new Room(300, 100, 600, 600, 0);
            currentFloor.addRoom(firstRoom);
            evacuationProblem.addVertex(firstRoom);

            draw();
            root.setCenter(currentFloor.getCanvas());

            currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
            currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
            currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
        };
    }

    public EventHandler<ActionEvent> clearSourceButtonHandle() {
        return event -> {
            evacuationProblem.clearSourceEdges();
            System.out.println("Sources cleared");
        };
    }

    public EventHandler<ActionEvent> selectDisjointPathsProblem() {
        return event -> {
            evacuationProblem.resetSimulation();
            draw();
            evacuationProblem.setProblem(new DisjointPathsProblemSolver());
            System.out.println("Disjoint paths problem selected");
        };
    }

    public EventHandler<ActionEvent> selectMinTimeProblem() {
        return event -> {
            evacuationProblem.resetSimulation();
            draw();
            evacuationProblem.setProblem(new MinimumEvacTimeProblemSolver());
            System.out.println("Max flow problem selected");
        };
    }
}
