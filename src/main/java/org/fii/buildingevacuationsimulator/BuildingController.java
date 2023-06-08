package org.fii.buildingevacuationsimulator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BuildingController {
//    @FXML
//    private AnchorPane rootPane;
//    @FXML
//    private Canvas canvas;

    private Canvas canvas;
    private double mouseX, mouseY;
    private final Set<Room> rooms = new HashSet<>();
    Graph<Room, Door> flowNetwork =
            new SimpleDirectedWeightedGraph<>(Door.class);


//    @FXML
//    private void handleCanvasClick() {
//        canvas.setOnMouseClicked(canvasClickResize());
//    }

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
                }
                if (mouseX >= rectX - 5 && mouseX <= rectX + 5
                        && mouseY >= rectY && mouseY <= rectY + rectHeight) {
                    room.setDraggingLeft(true);
                }
                if (mouseX >= rectX && mouseX <= rectX + rectWidth
                        && mouseY >= rectY - 5 && mouseY <= rectY + 5) {
                    room.setDraggingUp(true);
                }
                if (mouseX >= rectX && mouseX <= rectX + rectWidth
                        && mouseY >= rectY + rectHeight - 5 && mouseY <= rectY + rectHeight + 5) {
                    room.setDraggingDown(true);
                }
            }
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
                            Door door = new Door(room, neighbour, Double.parseDouble(result.get()), nearestEdge[0], nearestEdge[1]);
                            room.addDoor(door);
                            neighbour.addDoor(door);
                            break;
                        }
                    }
                    break;
                }
            }
            draw(gc);
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

    public void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (var room : rooms) {
            drawRoom(gc, room);
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
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}