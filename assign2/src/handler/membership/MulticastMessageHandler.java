package handler.membership;

import java.io.IOException;
import java.io.InputStream;

import store.nodes.NodeState;

public abstract class MulticastMessageHandler {
    private final NodeState nodeState;

    protected MulticastMessageHandler(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public abstract void execute(String headers[], InputStream inputStream) throws IOException;

    public NodeState getNodeState() {
        return nodeState;
    }

}
