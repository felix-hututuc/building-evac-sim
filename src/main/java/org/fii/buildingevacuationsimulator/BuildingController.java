package org.fii.buildingevacuationsimulator;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;

public class BuildingController {

//    private Canvas canvas;

    private double mouseX, mouseY;
//    private final List<Room> rooms = new ArrayList<>();
    Graph<Room, Door> flowNetwork;
    private Room source;
    private final Room sink;

    private final List<Floor> floors = new ArrayList<>();
    private Floor currentFloor;

    public BuildingController() {
        flowNetwork = new DirectedWeightedMultigraph<>(Door.class);

        sink = new Room(0,0,0,0);
        flowNetwork.addVertex(sink);
//        rooms.add(sink);

        currentFloor = new Floor(new Canvas(1200, 800));
        Room first_room = new Room(300, 100, 600, 600);
        currentFloor.addRoom(first_room);
        flowNetwork.addVertex(first_room);

        floors.add(currentFloor);
        this.getCanvas().setOnMousePressed(canvasClickResize());
        this.getCanvas().setOnMouseReleased(canvasClickRelease());
        this.getCanvas().setOnMouseDragged(canvasDragResize());
    }

    public Canvas getCanvas() {
        return currentFloor.getCanvas();
    }

    public EventHandler<MouseEvent> canvasClickResize() {
        return event -> {
            mouseX = event.getX();
            mouseY = event.getY();
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
                if (room.isDraggingRight()) {
                    if (event.getX() > 0 && event.getX() < currentFloor.getCanvas().getWidth()) {
                        for (var door : room.getDoors()) {
                            if (door.getX() == room.getX() + room.getWidth()) {
                                door.setX(room.getX() + event.getX() - room.getX());
                            }
                        }
                        room.setWidth(event.getX() - room.getX());
                    }
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
                    Room newRoom1 = new Room(room.getX(), room.getY(), x - room.getX(), room.getHeight());
                    Room newRoom2 = new Room(x, room.getY(), room.getWidth() - (x - room.getX()), room.getHeight());

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
                    }
                    newRoom1.addNeighbour(newRoom2);
                    newRoom2.addNeighbour(newRoom1);
                    currentFloor.removeRoom(room);
                    currentFloor.addRoom(newRoom1);
                    currentFloor.addRoom(newRoom2);
                    flowNetwork.addVertex(newRoom1);
                    flowNetwork.addVertex(newRoom2);
                    flowNetwork.removeVertex(room);
                    for (var door : room.getDoors()) {
                        flowNetwork.removeEdge(door);
                    }
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
                    Room newRoom1 = new Room(room.getX(), room.getY(), room.getWidth(), y - room.getY());
                    Room newRoom2 = new Room(room.getX(), y, room.getWidth(), room.getHeight() - (y - room.getY()));

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
                    }
                    newRoom1.addNeighbour(newRoom2);
                    newRoom2.addNeighbour(newRoom1);
                    currentFloor.removeRoom(room);
                    currentFloor.addRoom(newRoom1);
                    currentFloor.addRoom(newRoom2);
                    flowNetwork.addVertex(newRoom1);
                    flowNetwork.addVertex(newRoom2);
                    flowNetwork.removeVertex(room);
                    for (var door : room.getDoors()) {
                        flowNetwork.removeEdge(door);
                    }
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
                                result = Optional.of("0");
                            }
                            double[] nearestEdge = room.getNearestEdge(x, y);

//                            if (rooms.indexOf(room) > rooms.indexOf(neighbour)) {
//                                Room temp = room;
//                                room = neighbour;
//                                neighbour = temp;
//                            }
                            Door door1 = new Door(room, neighbour, Double.parseDouble(result.get()), nearestEdge[0], nearestEdge[1]);
                            Door door2 = new Door(neighbour, room, Double.parseDouble(result.get()), nearestEdge[0], nearestEdge[1]);

                            room.addDoor(door1);
                            neighbour.addDoor(door1);
                            room.addDoor(door2);
                            neighbour.addDoor(door2);
                            flowNetwork.addEdge(room, neighbour, door1);
                            flowNetwork.setEdgeWeight(door1, Double.parseDouble(result.get()));
                            flowNetwork.addEdge(neighbour, room, door2);
                            flowNetwork.setEdgeWeight(door2, Double.parseDouble(result.get()));

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
                        result = Optional.of("0");
                    }
                    double[] nearestEdge = room.getNearestEdge(x, y);
                    Door door = new Door(room, sink, Double.parseDouble(result.get()), nearestEdge[0], nearestEdge[1]);
                    room.addDoor(door);
                    sink.addDoor(door);
                    flowNetwork.addEdge(room, sink, door);
                    flowNetwork.setEdgeWeight(door, Double.parseDouble(result.get()));

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

    // select source node and return it by clicking on it
    public EventHandler<MouseEvent> sourceClickCanvasHandle() {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : currentFloor.getRooms()) {
                if (room.isInside(x, y)) {
                    source = room;
                    System.out.println("Source room: " + currentFloor.getRooms().indexOf(room));
                    break;
                }
            }
        };
    }

    public EventHandler<ActionEvent> sourceButtonHandle() {
        return event -> {
            currentFloor.getCanvas().setOnMousePressed(sourceClickCanvasHandle());
            currentFloor.getCanvas().setOnMouseReleased(event1 -> {
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
            MaximumFlowAlgorithm<Room, Door> algorithm = new EdmondsKarpMFImpl<>(flowNetwork);
            double maxFlow = algorithm.getMaximumFlow(source, sink).getValue();
            System.out.println(maxFlow);
        };
    }

    public void draw() {
        currentFloor.getCanvas().getGraphicsContext2D().clearRect(0, 0, currentFloor.getCanvas().getWidth(), currentFloor.getCanvas().getHeight());
        for (var room : currentFloor.getRooms()) {
            drawRoom(currentFloor.getCanvas().getGraphicsContext2D(), room);
            // draw room index
            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.RED);
            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(currentFloor.getRooms().indexOf(room)), room.getX() + room.getWidth() / 2, room.getY() + room.getHeight() / 2);
        }
    }

    public void drawRoom(GraphicsContext gc, Room room) {
        gc.strokeRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
        for (var door : room.getDoors()) {
            drawDoor(gc, door);
        }
    }

    public void drawDoor(GraphicsContext gc, Door door) {
        gc.setFill(Color.BLACK);
        gc.fillOval(door.getX() - 5, door.getY() - 5, 10, 10);
        // draw capacity
        gc.setFill(Color.RED);
        gc.fillText(String.valueOf(door.getWeight()), door.getX() - 10, door.getY() - 5);
    }

    public EventHandler<ActionEvent> newFloorHandle(BorderPane root) {
        return event -> {
            currentFloor = new Floor(new Canvas(1200, 800));

            Room first_room = new Room(300, 100, 600, 600);
            currentFloor.addRoom(first_room);
            flowNetwork.addVertex(first_room);

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
            if (floors.indexOf(currentFloor) > 0) {
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

//    public void addRoom(Room room) {
//        rooms.add(room);
//    }

//    public List<Room> getRooms() {
//        return rooms;
//    }

//    public void setCanvas(Canvas canvas) {
//        this.canvas = canvas;
//    }

    public void printEdges() {
        for (Room room : flowNetwork.vertexSet()) {
            System.out.println("Room " + currentFloor.getRooms().indexOf(room) + " has edges:");
            for (Door door : flowNetwork.edgesOf(room)) {
                System.out.println("\tDoor " + currentFloor.getRooms().indexOf(flowNetwork.getEdgeSource(door)) + " -> " + currentFloor.getRooms().indexOf(flowNetwork.getEdgeTarget(door)) + " with capacity " + door.getWeight());
            }
        }
    }

    public EventHandler<ActionEvent> showGraphHandle() {
        return event -> printEdges();
    }
}
