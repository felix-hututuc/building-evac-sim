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
import java.util.List;
import java.util.Map;

public class EvacuationProblemInstance {
    private final Graph<Room, Door> flowNetwork = new SimpleWeightedGraph<>(Door.class);
    private final Room source = new Room(1,1,0,0, -1);
    private final Room sink = new Room(0,0,0,0, -1);
    private EvacuationSolver evacuationProblemSolver;

    public EvacuationProblemInstance() {
        flowNetwork.addVertex(source);
        flowNetwork.addVertex(sink);
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

    public Room getSink() {
        return sink;
    }

    public void setProblem(EvacuationSolver evacProblem) {
        this.evacuationProblemSolver = evacProblem;
    }

    public void clearSourceEdges() {
        var sources = flowNetwork.edgesOf(source);
        flowNetwork.removeAllEdges(sources);
    }

    public void exportGraphAsDot() throws IOException {
        File imgFile = new File("src/main/resources/graph.dot");
        imgFile.createNewFile();
        // export the graph as a png image
        DOTExporter<Room, Door> graphExporter = new DOTExporter<>(room -> {
            if (room == sink) {
                return "t";
            } else if (room == source) {
                return "s";
            }
            return "v" + room.getFloorNumber() + "_" + flowNetwork.vertexSet().stream().toList().indexOf(room);
        });

        graphExporter.setEdgeAttributeProvider(door -> {
            Map<String, Attribute> edgeAttributes = new HashMap<>();
            edgeAttributes.put("label", new DefaultAttribute<>(door.getWeightAsString(), AttributeType.STRING));
            edgeAttributes.put("color", new DefaultAttribute<>(door.getColor(), AttributeType.STRING));
            return edgeAttributes;
        });
        graphExporter.exportGraph(flowNetwork, imgFile);
    }

    public void showGraph() throws IOException {
        // call exportGraphAsDot() to generate the dot file and call graphviz command to render the graph as a png image
        exportGraphAsDot();
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "src/main/resources/graph.dot", "-o", "src/main/resources/graph.png");
        pb.start();
    }

    public String executeSimulation() {
        return evacuationProblemSolver.solve(flowNetwork, source, sink);
    }

    public void resetSimulation() {
        for (Door door : flowNetwork.edgeSet()) {
            door.setColor("black");
            door.setFlowDirection(FlowDirection.NONE);
        }
    }

    public boolean isProblemSet() {
        return evacuationProblemSolver != null;
    }

    public List<Door> getSources() {
        return flowNetwork.edgesOf(source).stream().toList();
    }

    public boolean removeSource(double x, double y) {
        for (Room room : flowNetwork.vertexSet()) {
            if (room.isInside(x, y) && room != source && room != sink) {
                flowNetwork.removeEdge(source, room);
                room.setNrOfPeopleInside(0);
                return true;
            }
        }
        return false;
    }
}
