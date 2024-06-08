package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleWeightedGraph;

public interface EvacuationSolver {
    public void solve(Graph<Room, Door> flowNetwork, Room source, Room sink);

    default Graph<Room, Door> createFlowNetworkCopy(Graph<Room, Door> flowNetwork) {
        Graph<Room, Door> flowNetworkCopy = new SimpleWeightedGraph<>(Door.class);
        for (Room room : flowNetwork.vertexSet()) {
            flowNetworkCopy.addVertex(room);
        }
        for (Door door : flowNetwork.edgeSet()) {
            Door doorCopy = new Door(door);
            flowNetworkCopy.addEdge(doorCopy.getSource(), doorCopy.getTarget(), doorCopy);
            flowNetworkCopy.setEdgeWeight(doorCopy, doorCopy.getWeight());
        }
        return flowNetworkCopy;
    }

    public Room getFlowDirection(Door door);
    public int getFlow(Door door);
}
