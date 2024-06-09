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

public class EvacuationProblemInstance {
    private final Graph<Room, Door> flowNetwork = new SimpleWeightedGraph<>(Door.class);
    private final Room source = new Room(1,1,0,0, -1);
    private final Room target = new Room(0,0,0,0, -1);
    private EvacuationSolver evacuationProblemSolver;

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
        Door door = new Door(source, room, capacity, 0, 0);
        flowNetwork.addEdge(source, room, door);
        flowNetwork.setEdgeWeight(door, capacity);
    }

    public void removeEdge(Door door) {
        flowNetwork.removeEdge(door);
    }

    public void removeAllEdges(Collection<Door> doors) {
        flowNetwork.removeAllEdges(doors);
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
        evacuationProblemSolver.solve(flowNetwork, source, target);
    }
}
