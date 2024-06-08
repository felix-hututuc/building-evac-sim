package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;


public class MinimumEvacTimeProblemSolver implements EvacuationSolver {
    public void solve(Graph<Room, Door> flowNetwork, Room source, Room sink) {
        Graph<Room, Door> flowNetworkCopy = createFlowNetworkCopy(flowNetwork);
        addEdgesFromSourceToAllRooms(flowNetworkCopy, source, sink);
        computeEvacuationTime(flowNetworkCopy, source, sink);
    }

    @Override
    public Room getFlowDirection(Door door) {
        return null;
    }

    @Override
    public int getFlow(Door door) {
        return 0;
    }

    private int getTotalNumberOfPersonsInside(Graph<Room, Door> flowNetwork, Room source, Room target) {
        int total = 0;
        for (Room room : flowNetwork.vertexSet()) {
            if (room == source || room == target) {
                continue;
            }
            if (flowNetwork.containsEdge(source, room)) {
                total += (int) flowNetwork.getEdge(source, room).getWeight();
            }
        }
        return total;
    }

    private void addEdgesFromSourceToAllRooms(Graph<Room, Door> flowNetwork, Room source, Room sink) {
        for (Room room : flowNetwork.vertexSet()) {
            if (room == source || room == sink || flowNetwork.containsEdge(source, room)) {
                continue;
            }
            flowNetwork.addEdge(source, room, new Door(source, room, 0, 0, 0));
            flowNetwork.setEdgeWeight(source, room, 0);
        }
    }

    public void computeEvacuationTime(Graph<Room, Door> flowNetwork, Room source, Room sink) {
        int personsInside = getTotalNumberOfPersonsInside(flowNetwork, source, sink);
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
    }
}
