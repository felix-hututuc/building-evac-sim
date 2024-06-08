package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;

public class DisjointPathsProblemSolver implements EvacuationSolver {
    public void solve(Graph<Room, Door> flowNetwork) {
        // TODO document why this method is empty
    }

    @Override
    public Room getFlowDirection(Door door) {
        return null;
    }

    @Override
    public int getFlow(Door door) {
        return 0;
    }

    public void computeDisjointPaths() {
        //            if (source == null) {
//                System.out.println("Source not selected");
//                return;
//            }
//            // reset all doors color to black
//            for (Door door : flowNetwork.edgeSet()) {
//                door.setColor("black");
//            }
//
//            draw();
//            maxFlowAlgorithm = new EdmondsKarpMFImpl<>(flowNetwork);
//            var maxFlow = maxFlowAlgorithm.calculateMaximumFlow(source, sink);
//            var flowMap = maxFlowAlgorithm.getFlowMap();
//
//            System.out.println("Max Flow = " + maxFlow);
////            colorEdges();
//
//            for (var edge : currentFloor.getDoors()) {
//                try {
//                    var nextRoom = maxFlowAlgorithm.getFlowDirection(edge);
//
//                    if (nextRoom == null || flowMap.get(edge) == 0) {
//                        continue;
//                    }
//
//                    var xDoor = edge.getX();
//                    var yDoor = edge.getY();
//                    currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                    if (nextRoom.isOnLeftEdge(xDoor, yDoor)) {
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor + 20, yDoor);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor - 6);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor + 6);
//
//                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor + 25, yDoor);
//                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                    } else if (nextRoom.isOnRightEdge(xDoor, yDoor)) {
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor - 20, yDoor);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor - 6);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor + 6);
//
//                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor - 30, yDoor - 6);
//                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                    } else if (nextRoom.isOnTopEdge(xDoor, yDoor)) {
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor + 20);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor - 6, yDoor + 14);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor + 6, yDoor + 14);
//
//                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor + 30);
//                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                    } else if (nextRoom.isOnBottomEdge(xDoor, yDoor)) {
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor - 20);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor - 6, yDoor - 14);
//                        currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor + 6, yDoor - 14);
//
//                        currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                        currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor - 25);
//                        currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                    } else if (nextRoom == sink) {
//                        Room sourceRoom = edge.getTarget() == nextRoom ? edge.getSource() : edge.getTarget();
//                        if (sourceRoom.isOnLeftEdge(xDoor, yDoor)) {
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor - 20, yDoor);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor - 6);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor - 20, yDoor, xDoor - 14, yDoor + 6);
//
//                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor - 25, yDoor);
//                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                        } else if (sourceRoom.isOnRightEdge(xDoor, yDoor)) {
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor + 20, yDoor);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor - 6);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor + 20, yDoor, xDoor + 14, yDoor + 6);
//
//                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor + 25, yDoor);
//                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                        } else if (sourceRoom.isOnTopEdge(xDoor, yDoor)) {
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor - 20);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor - 6, yDoor - 14);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor + 6, yDoor - 14);
//
//                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor - 25);
//                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                        } else if (sourceRoom.isOnBottomEdge(xDoor, yDoor)) {
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor + 20);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor - 6, yDoor + 14);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor + 6, yDoor + 14);
//
//                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor + 25);
//                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                        }
//                    } else if (edge.getClass() == Stair.class) {
//                        Room sourceRoom = edge.getTarget() == nextRoom ? edge.getSource() : edge.getTarget();
//                        if (sourceRoom.getFloorNumber() > nextRoom.getFloorNumber()) {
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor + 20);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor - 6, yDoor + 14);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor + 20, xDoor + 6, yDoor + 14);
//
//                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor + 25);
//                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                        } else {
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor, xDoor, yDoor - 20);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor - 6, yDoor - 14);
//                            currentFloor.getCanvas().getGraphicsContext2D().strokeLine(xDoor, yDoor - 20, xDoor + 6, yDoor - 14);
//
//                            currentFloor.getCanvas().getGraphicsContext2D().setFill(Color.BLUE);
//                            currentFloor.getCanvas().getGraphicsContext2D().fillText(String.valueOf(flowMap.get(edge)), xDoor, yDoor - 25);
//                            currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.RED);
//
//                        }
//                    }
//
//                    currentFloor.getCanvas().getGraphicsContext2D().setStroke(Color.BLACK);

//                } catch (IllegalArgumentException e) {
////                    System.out.println("Edge not in flow network");
////                    System.out.println(edge.getWeight());
//                }
//            }
    }
}
