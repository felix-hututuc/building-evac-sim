package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;

public class MinimumEvacTimeProblemSolver implements EvacuationSolver {
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

    public void computeEvacuationTime() {
//        for (Room room : flowNetwork.vertexSet()) {
//            if (room == source || room == sink) {
//                continue;
//            }
//            if (flowNetwork.containsEdge(source, room)) {
//                continue;
//            }
//            flowNetwork.addEdge(source, room, new Door(source, room, 0, 0, 0));
//            flowNetwork.setEdgeWeight(source, room, 0);
//        }
//
//        int time = 0;
//        while (getTotalNumberOfPersonsInside() > 0) {
//            maxFlowAlgorithm = new EdmondsKarpMFImpl<>(flowNetwork);
//            var maxFlow = maxFlowAlgorithm.calculateMaximumFlow(source, sink);
//            System.out.println("Evacuated " + maxFlow + " persons");
//            var flowMap = maxFlowAlgorithm.getFlowMap();
//
//            for (Room room : flowNetwork.vertexSet()) {
//                if (room == source || room == sink) {
//                    continue;
//                }
//                var sourceEdge = flowNetwork.getEdge(source, room);
//                double oldCapacity = sourceEdge.getWeight();
////                for (Door door : room.getDoors()) {
////                    if (flowMap.containsKey(door) && flowMap.get(door) > 0) {
////                         Room nextRoom = maxFlowAlgorithm.getFlowDirection(door);
////                         if (nextRoom == room) {
////                             newCapacity += flowMap.get(door);
////                         } else {
////                             newCapacity -= flowMap.get(door);
////                         }
////                    }
////                }
//                flowNetwork.getEdge(source, room).setWeight(oldCapacity + flowMap.get(sourceEdge));
//                flowNetwork.setEdgeWeight(source, room, oldCapacity + flowMap.get(sourceEdge));
//            }
//            time++;
//        }
//        System.out.println("Evacuation time = " + time);
    }
}
