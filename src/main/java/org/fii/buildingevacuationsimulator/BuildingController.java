package org.fii.buildingevacuationsimulator;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;

public class BuildingController {
//    @FXML
//    private AnchorPane rootPane;
//    @FXML
//    private Canvas canvas;

    private Canvas canvas;
    private double mouseX, mouseY;
    private final List<Room> rooms = new ArrayList<>();
    Graph<Room, Door> flowNetwork;
    private Room source;
    private final Room sink;


//    @FXML
//    private void handleCanvasClick() {
//        canvas.setOnMouseClicked(canvasClickResize());
//    }

    public BuildingController() {
        flowNetwork = new SimpleDirectedWeightedGraph<>(Door.class);

        sink = new Room(0,0,0,0);
        flowNetwork.addVertex(sink);   // sink
        rooms.add(sink);

        Room first_room = new Room(300, 100, 600, 600);
        rooms.add(first_room);
        flowNetwork.addVertex(first_room);
    }

    public EventHandler<MouseEvent> canvasClickResize() {
        return event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            for (var room : rooms) {
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
            System.out.println("Number of rooms= " + rooms.size());
            System.out.println("Number of doors=" + flowNetwork.edgeSet().size());
        };
    }

    public EventHandler<MouseEvent> canvasDragResize(GraphicsContext gc) {
        return event -> {
            for (var room : rooms) {
                if (room.isDraggingRight()) {
                    if (event.getX() > 0 && event.getX() < canvas.getWidth()) {
                        for (var door : room.getDoors()) {
                            if (door.getX() == room.getX() + room.getWidth()) {
                                door.setX(room.getX() + event.getX() - room.getX());
                            }
                        }
                        room.setWidth(event.getX() - room.getX());
                    }
                }
                if (room.isDraggingLeft()) {
                    if (event.getX() > 0 && event.getX() < canvas.getWidth()) {
                        for (var door : room.getDoors()) {
                            if (door.getX() == room.getX()) {
                                door.setX(event.getX());
                            }
                        }
                        room.setWidth(room.getWidth() + room.getX() - event.getX());
                        room.setX(event.getX());
                    } else if (event.getX() <= 0) {
                        room.setX(0);
                    } else if (event.getX() >= canvas.getWidth()) {
                        room.setX(canvas.getWidth());
                    }
                }
                if (room.isDraggingUp()) {
                    if (event.getY() > 0 && event.getY() < canvas.getHeight()) {
                        for (var door : room.getDoors()) {
                            if (door.getY() == room.getY()) {
                                door.setY(event.getY());
                            }
                        }
                        room.setHeight(room.getHeight() + room.getY() - event.getY());
                        room.setY(event.getY());
                    } else if (event.getY() <= 0) {
                        room.setY(0);
                    } else if (event.getY() >= canvas.getHeight()) {
                        room.setY(canvas.getHeight());
                    }
                }
                if (room.isDraggingDown()) {
                    if (event.getY() > 0 && event.getY() < canvas.getHeight()) {
                        for (var door : room.getDoors()) {
                            if (door.getY() == room.getY() + room.getHeight()) {
                                door.setY(room.getY() + event.getY() - room.getY());
                            }
                        }
                        room.setHeight(event.getY() - room.getY());
                    }
                }
            }
            draw(gc);
        };
    }

    public EventHandler<MouseEvent> canvasClickRelease() {
        return event -> {
            for (var room : rooms) {
                room.setDraggingRight(false);
                room.setDraggingLeft(false);
                room.setDraggingUp(false);
                room.setDraggingDown(false);
            }
        };
    }

    public EventHandler<MouseEvent> verticalClickCanvasHandle(GraphicsContext gc) {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : rooms) {
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
                    rooms.remove(room);
                    rooms.add(newRoom1);
                    rooms.add(newRoom2);
                    flowNetwork.addVertex(newRoom1);
                    flowNetwork.addVertex(newRoom2);
                    flowNetwork.removeVertex(room);
                    for (var door : room.getDoors()) {
                        flowNetwork.removeEdge(door);
                    }
                    break;
                }
            }
            draw(gc);
        };
    }

    public EventHandler<ActionEvent> verticalButtonHandle(GraphicsContext gc) {
        return event -> {
            canvas.setOnMousePressed(verticalClickCanvasHandle(gc));
            canvas.setOnMouseReleased(event1 -> {
                canvas.setOnMousePressed(canvasClickResize());
                canvas.setOnMouseReleased(canvasClickRelease());
                canvas.setOnMouseDragged(canvasDragResize(gc));
            });
        };
    }

    public EventHandler<MouseEvent> horizontalClickCanvasHandle(GraphicsContext gc) {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : rooms) {
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
                    rooms.remove(room);
                    rooms.add(newRoom1);
                    rooms.add(newRoom2);
                    flowNetwork.addVertex(newRoom1);
                    flowNetwork.addVertex(newRoom2);
                    flowNetwork.removeVertex(room);
                    for (var door : room.getDoors()) {
                        flowNetwork.removeEdge(door);
                    }
                    break;
                }
            }
            draw(gc);
        };
    }

    public EventHandler<ActionEvent> horizontalButtonHandle(GraphicsContext gc) {
        return event -> {
            canvas.setOnMousePressed(horizontalClickCanvasHandle(gc));
            canvas.setOnMouseReleased(event1 -> {
                canvas.setOnMousePressed(canvasClickResize());
                canvas.setOnMouseReleased(canvasClickRelease());
                canvas.setOnMouseDragged(canvasDragResize(gc));
            });
        };
    }

    public EventHandler<MouseEvent> doorClickCanvasHandle(GraphicsContext gc) {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : rooms) {
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

                            if (rooms.indexOf(room) > rooms.indexOf(neighbour)) {
                                Room temp = room;
                                room = neighbour;
                                neighbour = temp;
                            }
                            Door door1 = new Door(room, neighbour, Double.parseDouble(result.get()), nearestEdge[0], nearestEdge[1]);
//                            Door door2 = new Door(neighbour, room, Double.parseDouble(result.get()), nearestEdge[0], nearestEdge[1]);

                            room.addDoor(door1);
                            neighbour.addDoor(door1);
//                            room.addDoor(door2);
//                            neighbour.addDoor(door2);
                            flowNetwork.addEdge(room, neighbour, door1);
                            flowNetwork.setEdgeWeight(door1, Double.parseDouble(result.get()));
//                            flowNetwork.addEdge(neighbour, room, door2);

                            draw(gc);
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

                    draw(gc);
                    return;
                }
            }
        };
    }

    public EventHandler<ActionEvent> doorButtonHandle(GraphicsContext gc) {
        return event -> {
            canvas.setOnMousePressed(doorClickCanvasHandle(gc));
            canvas.setOnMouseReleased(event1 -> {
                canvas.setOnMousePressed(canvasClickResize());
                canvas.setOnMouseReleased(canvasClickRelease());
                canvas.setOnMouseDragged(canvasDragResize(gc));
            });
        };
    }

    // select source node and return it by clicking on it
    public EventHandler<MouseEvent> sourceClickCanvasHandle() {
        return event -> {
            var x = event.getX();
            var y = event.getY();
            for (var room : rooms) {
                if (room.isInside(x, y)) {
                    source = room;
                    System.out.println("Source room: " + rooms.indexOf(room));
                    break;
                }
            }
        };
    }

    public EventHandler<ActionEvent> sourceButtonHandle(GraphicsContext gc) {
        return event -> {
            canvas.setOnMousePressed(sourceClickCanvasHandle());
            canvas.setOnMouseReleased(event1 -> {
                canvas.setOnMousePressed(canvasClickResize());
                canvas.setOnMouseReleased(canvasClickRelease());
                canvas.setOnMouseDragged(canvasDragResize(gc));
            });
        };
    }

    public EventHandler<ActionEvent> maxFlowHandle() {
        return event -> {
            MaximumFlowAlgorithm<Room, Door> algorithm = new EdmondsKarpMFImpl<>(flowNetwork);
            double maxFlow = algorithm.getMaximumFlow(source, sink).getValue();
            System.out.println(maxFlow);

//            Graph<Object, DefaultWeightedEdge> tempGraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
//            for (int i = 0; i < 5; i++) {
//                tempGraph.addVertex(i);
//            }
//            DefaultWeightedEdge edge = tempGraph.addEdge(0, 2);
//            tempGraph.setEdgeWeight(edge, 99);
//            edge = tempGraph.addEdge(0, 3);
//            tempGraph.setEdgeWeight(edge, 3);
//            edge = tempGraph.addEdge(2, 3);
//            tempGraph.setEdgeWeight(edge, 2);
//            edge = tempGraph.addEdge(2, 1);
//            tempGraph.setEdgeWeight(edge, 3);
//            edge = tempGraph.addEdge(3, 1);
//            tempGraph.setEdgeWeight(edge, 6);
//            edge = tempGraph.addEdge(2, 4);
//            tempGraph.setEdgeWeight(edge, 3);
//            edge = tempGraph.addEdge(1, 4);
//            tempGraph.setEdgeWeight(edge, 99);
//
//
//            MaximumFlowAlgorithm<Object, DefaultWeightedEdge> algorithm = new EdmondsKarpMFImpl<>(tempGraph);
//            double maxFlow = algorithm.getMaximumFlow(0, 4).getValue();
//            System.out.println(maxFlow);

        };
    }

    public void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (var room : rooms) {
            drawRoom(gc, room);
            // draw room index
            gc.setFill(Color.RED);
            gc.fillText(String.valueOf(rooms.indexOf(room)), room.getX() + room.getWidth() / 2, room.getY() + room.getHeight() / 2);
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

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void printEdges() {
        for (Room room : flowNetwork.vertexSet()) {
            System.out.println("Room " + rooms.indexOf(room) + " has edges:");
            for (Door door : flowNetwork.edgesOf(room)) {
                System.out.println("\tDoor " + rooms.indexOf(flowNetwork.getEdgeSource(door)) + " -> " + rooms.indexOf(flowNetwork.getEdgeTarget(door)) + " with capacity " + door.getWeight());
            }
        }
    }

    public EventHandler<ActionEvent> showGraphHandle() {
        return event -> printEdges();
    }
}
