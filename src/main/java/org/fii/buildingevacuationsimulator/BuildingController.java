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
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class BuildingController {
    Graph<Room, Door> flowNetwork;
    private Room source;
    private Room sink;
    private final List<Floor> floors = new ArrayList<>();
    private Floor currentFloor;

    private EdmondsKarpMFImpl<Room, Door> maxFlowAlgorithm;

    public BuildingController() {
        flowNetwork = new SimpleWeightedGraph<>(Door.class);

        source = new Room(1,1,0,0, -1);
        sink = new Room(0,0,0,0, -1);
        flowNetwork.addVertex(source);
        flowNetwork.addVertex(sink);

        currentFloor = new Floor(0);

        Room firstRoom = new Room(300, 100, 600, 600, 0);
        currentFloor.addRoom(firstRoom);
        flowNetwork.addVertex(firstRoom);

        currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
        currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
        currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());

        floors.add(currentFloor);
    }

    public BuildingController(List<Floor> floors, Floor currentFloor, Graph<Room, Door> flowNetwork, Room sink) {
        this.floors.addAll(floors);
        this.currentFloor = currentFloor;
        this.flowNetwork = flowNetwork;
        this.source = new Room(1,1,0,0, -1);
        flowNetwork.addVertex(source);
        this.sink = sink;

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
            System.out.println("Number of rooms= " + currentFloor.getRooms().size());
            System.out.println("Number of doors= " + ((flowNetwork.edgeSet().size() + sink.getDoors().size()) / 2));
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
                    flowNetwork.addVertex(newRoom1);
                    flowNetwork.addVertex(newRoom2);
                    flowNetwork.removeVertex(room);
                    flowNetwork.removeAllEdges(room.getDoors());
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
                    flowNetwork.addVertex(newRoom1);
                    flowNetwork.addVertex(newRoom2);
                    flowNetwork.removeVertex(room);
                    flowNetwork.removeAllEdges(room.getDoors());
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

                            Door door1 = new Door(room, neighbour, Double.parseDouble(result.get()), nearestEdge[0], nearestEdge[1]);

                            room.addDoor(door1);
                            neighbour.addDoor(door1);
                            flowNetwork.addEdge(room, neighbour, door1);
                            flowNetwork.setEdgeWeight(door1, Double.parseDouble(result.get()));

                            draw();
                            return;
                        }
                    }
                    // add door to sink
                    System.out.println("Adding door to sink");
//                    TextInputDialog dialog = new TextInputDialog();
//                    dialog.setTitle("Input");
//                    dialog.setHeaderText("Input door capacity");
//                    Optional<String> result = dialog.showAndWait();
//                    if (result.isEmpty() || result.get().isEmpty()) {
//                        return;
//                    }
                    double[] nearestEdge = room.getNearestEdge(x, y);
                    Door door = new Door(room, sink, 1, nearestEdge[0], nearestEdge[1]);
                    room.addDoor(door);
                    sink.addDoor(door);
                    flowNetwork.addEdge(room, sink, door);
                    flowNetwork.setEdgeWeight(door, 1);

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
                            Stair stair1 = new Stair(currentFloor, floors.get(currentFloorIndex - 1), room, roomBellow, Double.parseDouble(result.get()), x, y);
                            currentFloor.addStair(stair1);
                            floors.get(currentFloorIndex - 1).addStair(stair1);

                            room.addDoor(stair1);

                            roomBellow.addDoor(stair1);

                            flowNetwork.addEdge(room, roomBellow, stair1);
                            flowNetwork.setEdgeWeight(stair1, Double.parseDouble(result.get()));
                            draw();
                            return;
                        }
                    }
                }
            }
        };
    }


    // select source node and return it by clicking on it
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
                    if (source == null) {
                        System.out.println("Virtual source not initialized");
                        return;
                    }
                    // add edge between source and room with capacity 1
                    flowNetwork.addEdge(source, room, new Door(source, room, 1, x, y));
                    flowNetwork.setEdgeWeight(source, room, 1);
                    System.out.println("Room selected as source");

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

    public EventHandler<ActionEvent> maxFlowHandle() {
        return event -> {
            if (source == null) {
                System.out.println("Source not selected");
                return;
            }
            // reset all doors color to black
            for (Door door : flowNetwork.edgeSet()) {
                door.setColor("black");
            }

            draw();
            maxFlowAlgorithm = new EdmondsKarpMFImpl<>(flowNetwork);
            var maxFlow = maxFlowAlgorithm.calculateMaximumFlow(source, sink);
            var flowMap = maxFlowAlgorithm.getFlowMap();

            System.out.println("Max Flow = " + maxFlow);
            colorEdges();

            for (var edge : currentFloor.getDoors()) {
                try {
                    var nextRoom = maxFlowAlgorithm.getFlowDirection(edge);

                    if (nextRoom == null || flowMap.get(edge) == 0) {
                        continue;
                    }

                    var xDoor = edge.getX();
                    var yDoor = edge.getY();
                    currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                    if (nextRoom.isOnLeftEdge(xDoor, yDoor)) {
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor + 20, yDoor);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor - 6);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor + 6);

                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor + 25, yDoor);
                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                    } else if (nextRoom.isOnRightEdge(xDoor, yDoor)) {
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor - 20, yDoor);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor - 6);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor + 6);

                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor - 30, yDoor - 6);
                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                    } else if (nextRoom.isOnTopEdge(xDoor, yDoor)) {
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor + 20);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor - 6, yDoor + 14);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor + 6, yDoor + 14);

                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor + 30);
                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                    } else if (nextRoom.isOnBottomEdge(xDoor, yDoor)) {
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor - 20);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor - 6, yDoor - 14);
                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor + 6, yDoor - 14);

                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor - 25);
                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                    } else if (nextRoom == sink) {
                        Room sourceRoom = edge.getTarget() == nextRoom ? edge.getSource() : edge.getTarget();
                        if (sourceRoom.isOnLeftEdge(xDoor, yDoor)) {
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor - 20, yDoor);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor - 6);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor + 6);

                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor - 25, yDoor);
                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                        } else if (sourceRoom.isOnRightEdge(xDoor, yDoor)) {
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor + 20, yDoor);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor - 6);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor + 6);

                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor + 25, yDoor);
                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                        } else if (sourceRoom.isOnTopEdge(xDoor, yDoor)) {
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor - 20);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor - 6, yDoor - 14);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor + 6, yDoor - 14);

                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor - 25);
                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                        } else if (sourceRoom.isOnBottomEdge(xDoor, yDoor)) {
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor + 20);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor - 6, yDoor + 14);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor + 6, yDoor + 14);

                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor + 25);
                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                        }
                    } else if (edge.getClass() == Stair.class) {
                        Room sourceRoom = edge.getTarget() == nextRoom ? edge.getSource() : edge.getTarget();
                        if (sourceRoom.getFloorNumber() > nextRoom.getFloorNumber()) {
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor + 20);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor - 6, yDoor + 14);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor + 6, yDoor + 14);

                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor + 25);
                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                        } else {
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor - 20);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor - 6, yDoor - 14);
                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor + 6, yDoor - 14);

                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor - 25);
                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);

                        }
                    }

                    currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.BLACK);
                } catch (IllegalArgumentException e) {
//                    System.out.println("Edge not in flow network");
//                    System.out.println(edge.getWeight());
                }
            }
        };
    }

    public void draw() {
        currentFloor.getCanvas().getGraphicsContext2D().clearRect(0, 0, currentFloor.getCanvas().getWidth(), currentFloor.getCanvas().getHeight());
        for (var room : currentFloor.getRooms()) {
            room.draw(currentFloor.getCanvas().getGraphicsContext2D());
            // draw room index
            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.RED);
            currentFloor.getCanvas().getGraphicsContext2D().fillText(room.getUuid().split("-")[0], room.getX() + room.getWidth() / 2, room.getY() + room.getHeight() / 2);
            // display current floor number
            currentFloor.getCanvas().getGraphicsContext2D().fillText("Floor " + currentFloor.getFloorNumber(), currentFloor.getCanvas().getWidth() * ((double) 9 / 10), currentFloor.getCanvas().getHeight() * ((double) 1 / 10));
        }
    }

    public EventHandler<ActionEvent> newFloorHandle(BorderPane root) {
        return event -> {
            currentFloor = new Floor(currentFloor.getFloorNumber() + 1);

            Room firstRoom = new Room(300, 100, 600, 600, currentFloor.getFloorNumber());
            currentFloor.addRoom(firstRoom);
            flowNetwork.addVertex(firstRoom);

            floors.add(currentFloor);
            this.getCanvas().setOnMousePressed(canvasClickResize());
            this.getCanvas().setOnMouseReleased(canvasClickRelease());
            this.getCanvas().setOnMouseDragged(canvasDragResize());
            draw();
            root.setCenter(currentFloor.getCanvas());
        };
    }

    public EventHandler<ActionEvent> previousFloorHandle(BorderPane root) {
        return event -> {
            if (floors.indexOf(currentFloor) >= 1) {
                currentFloor = floors.get(floors.indexOf(currentFloor) - 1);
                draw();
                root.setCenter(currentFloor.getCanvas());
            }
        };
    }

    public EventHandler<ActionEvent> nextFloorHandle(BorderPane root) {
        return event -> {
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

    public void printEdges() {
        for (Room room : flowNetwork.vertexSet()) {
            if (room == sink) {
                continue;
            }
            Floor roomFloor = floors.get(room.getFloorNumber());
            System.out.println("Floor " + room.getFloorNumber() + " - Room " + roomFloor.getRooms().indexOf(room) + " has edges:");
            for (Door door : flowNetwork.edgesOf(room)) {
                Room edgeSource = flowNetwork.getEdgeSource(door);
                Room edgeTarget = flowNetwork.getEdgeTarget(door);
                Floor sourceFloor = floors.get(edgeSource.getFloorNumber());
                if (edgeTarget == sink) {
                    System.out.println("Door from Floor " + edgeSource.getFloorNumber() + " - Room " + sourceFloor.getRooms().indexOf(edgeSource) + " to sink with capacity " + door.getWeight());
                    continue;
                }
                Floor targetFloor = floors.get(edgeTarget.getFloorNumber());
                System.out.println("Door from Floor " + edgeSource.getFloorNumber() + " - Room " + sourceFloor.getRooms().indexOf(edgeSource) + " to Floor " + edgeTarget.getFloorNumber() + " - Room " + targetFloor.getRooms().indexOf(edgeTarget) + " with capacity " + door.getWeight());
            }
        }
    }

    private void colorEdges() {
        Random rand = new Random();
        System.out.println("Coloring edges");
        for (Door door : flowNetwork.edgesOf(source)) {
            //to get rainbow, pastel colors
            final float hue = rand.nextFloat();
            final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
            final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
            var color = java.awt.Color.getHSBColor(hue, saturation, luminance);
            String colorStr = "#" + Integer.toHexString(color.getRGB()).substring(2);
            door.setColor(colorStr);
            Room nextRoom = maxFlowAlgorithm.getFlowDirection(door);
            if (maxFlowAlgorithm.getFlowMap().get(door) == 0) continue; // skip source from which no flow leaves
            while (nextRoom != sink) {
                for (Door nextDoor : flowNetwork.edgesOf(nextRoom)) {
                    if (maxFlowAlgorithm.getFlowMap().get(nextDoor) != 0 && nextDoor.getColor().equals("black") && maxFlowAlgorithm.getFlowDirection(nextDoor) != nextRoom) {
                        nextDoor.setColor(colorStr);
                        nextRoom = maxFlowAlgorithm.getFlowDirection(nextDoor);
                        break;
                    }
                }
            }
        }
    }

    public EventHandler<ActionEvent> showGraphHandle() {
        return event -> {
            File imgFile = new File("src/main/resources/graph.dot");
            try {
                imgFile.createNewFile();
                // export the graph as a png image
                DOTExporter<Room, Door> graphExporter = new DOTExporter<>(room -> {
                    if (room == sink) {
                        return "t";
                    } else if (room == source) {
                        return "s";
                    }
                    return "v" + room.getFloorNumber() + "_" + floors.get(room.getFloorNumber()).getRooms().indexOf(room);
                });
                if (maxFlowAlgorithm == null) {
                    graphExporter.setEdgeAttributeProvider(door -> Map.of("label", new DefaultAttribute<>(door.getWeightAsString(), AttributeType.STRING)));
                } else {
                    graphExporter.setEdgeAttributeProvider(door -> {
                        Map<String, Attribute> edgeAttributes = new HashMap<>();
                        edgeAttributes.put("label", new DefaultAttribute<>(door.getWeightAsString() + "/" + maxFlowAlgorithm.getFlowMap().get(door).intValue(), AttributeType.STRING));
                        edgeAttributes.put("color", new DefaultAttribute<>(door.getColor(), AttributeType.STRING));
                        return edgeAttributes;
                    });
                }
                graphExporter.exportGraph(flowNetwork, imgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    // export as a JSON object using the json library
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("sink", sink.toJson())
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

        Graph<Room, Door> flowNetwork = new SimpleWeightedGraph<>(Door.class);
        flowNetwork.addVertex(sink);

        int floorsNr = jsonObject.getInt("floorsNr");
        int currentFloorNr = jsonObject.getInt("currentFloor");
        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i < floorsNr; i++) {
            floors.add(new Floor(i));
        }

        BuildingController buildingController = new BuildingController(floors, floors.get(currentFloorNr), flowNetwork, sink);

        List<Room> rooms = new ArrayList<>();
        jsonObject.getJsonArray("rooms").forEach(roomJson -> {
            Room room = Room.fromJson((JsonObject) roomJson);
            rooms.add(room);
            buildingController.floors.get(room.getFloorNumber()).addRoom(room);
            buildingController.flowNetwork.addVertex(room);
        });
        jsonObject.getJsonArray("doors").forEach(doorJson -> {
            Room room1 = rooms.stream()
                    .filter(room -> room.getUuid().equals(((JsonObject) doorJson).getString("room1")))
                    .findFirst()
                    .orElseThrow();
            Room room2;
            if (((JsonObject) doorJson).getString("room2").equals(buildingController.sink.getUuid())) {
                room2 = buildingController.sink;
            } else {
                room2 = rooms.stream()
                        .filter(room -> room.getUuid().equals(((JsonObject) doorJson).getString("room2")))
                        .findFirst()
                        .orElseThrow();
                if (room1.getFloorNumber() != room2.getFloorNumber()) {
                    return;
                }
            }
            Door door = Door.fromJson((JsonObject) doorJson, room1, room2);
            room1.addDoor(door);
            room2.addDoor(door);
            boolean added = buildingController.flowNetwork.addEdge(room1, room2, door);
            if (!added) {
                System.out.println("Edge not added");
            }
            buildingController.flowNetwork.setEdgeWeight(door, door.getWeight());
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
            boolean added = buildingController.flowNetwork.addEdge(room1, room2, stair);
            if (!added) {
                System.out.println("Edge not added");
            }
            buildingController.flowNetwork.setEdgeWeight(stair, stair.getWeight());
        });

        return buildingController;
    }

    public EventHandler<ActionEvent> importHandle(BorderPane root) {
        return event -> {
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
                    flowNetwork = buildingController.flowNetwork;
                    source = buildingController.source;
                    sink = buildingController.sink;
                    buildingController.draw();
                    root.setCenter(currentFloor.getCanvas());
                } catch (IOException e) {
                    System.out.println("Error while opening file");
                }
            }
        };
    }

    public EventHandler<ActionEvent> removeDoorHandle() {
        return event -> {
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
                    flowNetwork.removeEdge(Door);
                    System.out.println("Door removed");
                }
            }
            draw();
        };
    }

    public EventHandler<ActionEvent> resetHandle(BorderPane root) {
        return event -> {
            floors.clear();
            currentFloor = new Floor(0);
            floors.add(currentFloor);
            flowNetwork = new SimpleWeightedGraph<>(Door.class);
            sink = new Room(0, 0, 0, 0, 0);
            flowNetwork.addVertex(sink);

            Room firstRoom = new Room(300, 100, 600, 600, 0);
            currentFloor.addRoom(firstRoom);
            flowNetwork.addVertex(firstRoom);

            draw();
            root.setCenter(currentFloor.getCanvas());

            currentFloor.getCanvas().setOnMousePressed(canvasClickResize());
            currentFloor.getCanvas().setOnMouseReleased(canvasClickRelease());
            currentFloor.getCanvas().setOnMouseDragged(canvasDragResize());
        };
    }

    public EventHandler<ActionEvent> clearSourceButtonHandle() {
        return event -> {
            flowNetwork.vertexSet().stream().filter(room -> room != sink).forEach(room -> {
                if (flowNetwork.containsEdge(source, room)) {
                    flowNetwork.removeEdge(source, room);
                }
            });
            System.out.println("Sources cleared");
        };
    }
}
