package org.fii.buildingevacuationsimulator;

import org.jgrapht.Graph;

public interface EvacuationSolver {
    public void solve(Graph<Room, Door> flowNetwork);

    public Room getFlowDirection(Door door);
    public int getFlow(Door door);
}
