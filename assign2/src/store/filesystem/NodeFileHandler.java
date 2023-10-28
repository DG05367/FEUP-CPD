package store.filesystem;

import java.io.IOException;

import store.nodes.NodeState;

public abstract class NodeFileHandler {

    protected final NodeState nodeState;

    protected NodeFileHandler(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    protected abstract void build() throws IOException;

    public NodeState getNodeState() {
        return nodeState;
    }
}
