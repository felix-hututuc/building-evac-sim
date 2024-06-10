package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;


public class MinimumEvacTimeProblemSolver implements EvacuationSolver {
    Graph<Room, Door> flowNetwork;
    Room source;
    Room sink;

    public String solve(Graph<Room, Door> flowNetwork, Room source, Room sink) {
        this.flowNetwork = createFlowNetworkCopy(flowNetwork);
        this.source = source;
        this.sink = sink;

        addEdgesFromSourceToAllRooms();
        int time = computeEvacuationTime();

        return "Evacuation time = " + time;
    }

    @Override
    public Room getFlowDirection(Door door) {
        return null;
    }

    @Override
    public int getFlow(Door door) {
        return 0;
    }

    private int getTotalNumberOfPersonsInside() {
        int total = 0;
        for (Room room : flowNetwork.vertexSet()) {
            if (room == source || room == sink) {
                continue;
            }
            if (flowNetwork.containsEdge(source, room)) {
                total += (int) flowNetwork.getEdge(source, room).getWeight();
            }
        }
        return total;
    }

    private void addEdgesFromSourceToAllRooms() {
        for (Room room : flowNetwork.vertexSet()) {
            if (room == source || room == sink || flowNetwork.containsEdge(source, room)) {
                continue;
            }
            flowNetwork.addEdge(source, room, new Door(source, room, 0, 0, 0));
            flowNetwork.setEdgeWeight(source, room, 0);
        }
    }

    private int computeEvacuationTime() {
        int personsInside = getTotalNumberOfPersonsInside();
        int time = 0;
        while (personsInside > 0) {
            var maxFlowAlgorithm = new EdmondsKarpMFImpl<>(flowNetwork);
            var maxFlow = maxFlowAlgorithm.calculateMaximumFlow(source, sink);
            System.out.println("Evacuated " + maxFlow + " persons");
            var flowMap = maxFlowAlgorithm.getFlowMap();

            for (Room room : flowNetwork.vertexSet()) {
                if (room == source || room == sink) {
                    continue;
                }
                var sourceEdge = flowNetwork.getEdge(source, room);
                int oldCapacity = (int) sourceEdge.getWeight();
                int flowValue = flowMap.get(sourceEdge).intValue();
                flowNetwork.getEdge(source, room).setWeight(oldCapacity - flowValue);
                flowNetwork.setEdgeWeight(source, room, (double)oldCapacity - flowValue);
            }
            personsInside -= (int) maxFlow;
            time++;
        }
        System.out.println("Evacuation time = " + time);
        return time;
    }
}
