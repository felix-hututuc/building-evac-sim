package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EvacuationProblemInstance {
    private final Graph<Room, Door> flowNetwork = new SimpleWeightedGraph<>(Door.class);
    private Room source = new Room(1,1,0,0, -1);
    private Room target = new Room(0,0,0,0, -1);
    private EvacuationSolver evacuationProblemSolver;

    Random rand = new Random();

    public EvacuationProblemInstance() {
        flowNetwork.addVertex(source);
        flowNetwork.addVertex(target);
    }

    public Graph<Room, Door> getFlowNetwork() {
        return flowNetwork;
    }

    public void addVertex(Room room) {
        flowNetwork.addVertex(room);
    }

    public void removeVertex(Room room) {
        flowNetwork.removeVertex(room);
    }

    public void addEdge(Door door) {
        flowNetwork.addEdge(door.getSource(), door.getTarget(), door);
        flowNetwork.setEdgeWeight(door, door.getWeight());
    }

    public void addEdge(Room room1, Room room2, int capacity) {
        flowNetwork.addEdge(room1, room2);
        flowNetwork.setEdgeWeight(flowNetwork.getEdge(room1, room2), capacity);
    }

    public void addEdgeToSource(Room room, int capacity) {
        flowNetwork.addEdge(source, room);
        flowNetwork.setEdgeWeight(flowNetwork.getEdge(source, room), capacity);
    }

    public void addEdgeToTarget(Room room, int capacity) {
        Door door = new Door(room, target, capacity, 0, 0);
        flowNetwork.addEdge(room, target);
        flowNetwork.setEdgeWeight(flowNetwork.getEdge(room, target), capacity);
    }

    public void removeEdge(Door door) {
        flowNetwork.removeEdge(door);
    }

    public void removeEdge(Room room1, Room room2) {
        flowNetwork.removeEdge(room1, room2);
    }

    public void removeAllEdges(Collection<Door> doors) {
        flowNetwork.removeAllEdges(doors);
    }

    public void setSource(Room source) {
        this.source = source;
    }

    public void setTarget(Room target) {
        this.target = target;
    }

    public Room getSource() {
        return source;
    }

    public Room getTarget() {
        return target;
    }

    public void setProblem(EvacuationSolver evacProblem) {
        this.evacuationProblemSolver = evacProblem;
    }

    public void clearSourceEdges() {
        flowNetwork.removeAllEdges(flowNetwork.edgesOf(source));
    }

    public double getTotalNumberOfPersonsInside() {
        double total = 0;
        for (Room room : flowNetwork.vertexSet()) {
            if (room == source || room == target) {
                continue;
            }
            if (flowNetwork.containsEdge(source, room)) {
                total += flowNetwork.getEdge(source, room).getWeight();
            }
        }
        return total;
    }

    private void colorEdges() {
        System.out.println("Coloring edges");
        for (Door door : flowNetwork.edgesOf(source)) {
            //to get rainbow, pastel colors
            final float hue = rand.nextFloat();
            final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
            final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
            var color = java.awt.Color.getHSBColor(hue, saturation, luminance);
            String colorStr = "#" + Integer.toHexString(color.getRGB()).substring(2);
            door.setColor(colorStr);
            Room nextRoom = evacuationProblemSolver.getFlowDirection(door);
            if (evacuationProblemSolver.getFlow(door) == 0) continue; // skip source from which no flow leaves
            while (nextRoom != target) {
                for (Door nextDoor : flowNetwork.edgesOf(nextRoom)) {
                    if (evacuationProblemSolver.getFlow(nextDoor) != 0 && nextDoor.getColor().equals("black") && evacuationProblemSolver.getFlowDirection(nextDoor) != nextRoom) {
                        nextDoor.setColor(colorStr);
                        nextRoom = evacuationProblemSolver.getFlowDirection(nextDoor);
                        break;
                    }
                }
            }
        }
    }

    public void showGraph() throws IOException {
        File imgFile = new File("src/main/resources/graph.dot");
        imgFile.createNewFile();
        // export the graph as a png image
        DOTExporter<Room, Door> graphExporter = new DOTExporter<>(room -> {
            if (room == target) {
                return "t";
            } else if (room == source) {
                return "s";
            }
            return "v" + room.getFloorNumber() + "_" + flowNetwork.vertexSet().stream().toList().indexOf(room);
        });

        graphExporter.setEdgeAttributeProvider(door -> {
            Map<String, Attribute> edgeAttributes = new HashMap<>();
            edgeAttributes.put("label", new DefaultAttribute<>(door.getWeightAsString() + "/" + evacuationProblemSolver.getFlow(door), AttributeType.STRING));
            edgeAttributes.put("color", new DefaultAttribute<>(door.getColor(), AttributeType.STRING));
            return edgeAttributes;
        });
        graphExporter.exportGraph(flowNetwork, imgFile);
    }

    public void executeSimulation() {
        evacuationProblemSolver.solve(flowNetwork);
    }
}
