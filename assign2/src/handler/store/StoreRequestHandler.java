package handler.store;

import utils.NeighbourhoodAlgorithms;

import java.io.*;

import store.nodes.NodeState;

public abstract class StoreRequestHandler {
    private final NodeState nodeState;
    private final NeighbourhoodAlgorithms neighbourhoodAlgorithms;

    public static final int NUM_RETRIES = 3;

    protected StoreRequestHandler(NodeState nodeState) {
        this.nodeState = nodeState;
        this.neighbourhoodAlgorithms = new NeighbourhoodAlgorithms(nodeState);
    }

    protected NodeState getNodeState() {
        return nodeState;
    }

    public NeighbourhoodAlgorithms getNeighbourhoodAlgorithms() {
        return neighbourhoodAlgorithms;
    }

    public abstract void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException;
}
