package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;

import java.util.Random;

public class DisjointPathsProblemSolver implements EvacuationSolver {
    Graph<Room, Door> flowNetworkCopy;
    EdmondsKarpMFImpl<Room, Door> maxFlowAlgorithm;
    Room source;
    Room sink;
    Random rand = new Random();

    public String solve(Graph<Room, Door> flowNetwork, Room source, Room sink) {
        this.flowNetworkCopy = createFlowNetworkCopy(flowNetwork);
        this.source = source;
        this.sink = sink;

        init();

        this.maxFlowAlgorithm = new EdmondsKarpMFImpl<>(this.flowNetworkCopy);
        var maxFlow = maxFlowAlgorithm.calculateMaximumFlow(source, sink);

        colorEdges();
        int numberOfSources = this.flowNetworkCopy.edgesOf(source).size();

        for (Door door : flowNetwork.edgeSet()) {
            Door copyEdge = flowNetworkCopy.getEdge(door.getSource(), door.getTarget());
            door.setColor(copyEdge.getColor());

            door.setFlowDirection(copyEdge.getFlowDirection());
        }

        String result;
        if (maxFlow == numberOfSources) {
            System.out.println("Enough disjoint paths exist");
            result = "Enough disjoint paths exist!";
        } else {
            System.out.println("Not enough disjoint paths exist");
            result = "Not enough disjoint paths exist!";
        }

        return result;
    }

    private void init() {
        for (Door door : this.flowNetworkCopy.edgeSet()) {
            door.setWeight(1);
            this.flowNetworkCopy.setEdgeWeight(door, 1);

            door.setColor("black");
        }
    }

    private String getRandomColor() {
        final float hue = rand.nextFloat();
        final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
        final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
        var color = java.awt.Color.getHSBColor(hue, saturation, luminance);

        return "#" + Integer.toHexString(color.getRGB()).substring(2);
    }

    private void colorEdges() {
        System.out.println("Coloring edges");
        for (Door door : this.flowNetworkCopy.edgesOf(source)) {
            String color = getRandomColor();
            door.setColor(color);
            door.setFlowDirection(FlowDirection.TARGET);
            Room nextRoom = getFlowDirection(door);
            if (getFlow(door) == 0) continue; // skip source from which no flow leaves
            while (nextRoom != sink) {
                for (Door nextDoor : this.flowNetworkCopy.edgesOf(nextRoom)) {
                    if (getFlow(nextDoor) != 0 && nextDoor.getColor().equals("black") && getFlowDirection(nextDoor) != nextRoom) {
                        nextDoor.setColor(color);
                        if (nextDoor.getSource() == nextRoom) {
                            nextDoor.setFlowDirection(FlowDirection.TARGET);
                        } else {
                            nextDoor.setFlowDirection(FlowDirection.SOURCE);
                        }
                        nextRoom = getFlowDirection(nextDoor);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Room getFlowDirection(Door door) {
        if (maxFlowAlgorithm == null) {
            System.out.println("Max flow algorithm not initialized");
            return null;
        }

        return maxFlowAlgorithm.getFlowDirection(door);
    }

    @Override
    public int getFlow(Door door) {
        if (maxFlowAlgorithm == null) {
            System.out.println("Max flow algorithm not initialized");
            return 0;
        }

        return maxFlowAlgorithm.getFlowMap().get(door).intValue();
    }
}
