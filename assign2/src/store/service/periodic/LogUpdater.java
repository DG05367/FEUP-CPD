package store.service.periodic;

import java.io.IOException;

import store.nodes.NodeState;

public class LogUpdater extends PeriodicActor {

    public LogUpdater(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    protected long getInterval() { return 3; }

    @Override
    public void run() {
        try {
            nodeState.getMembershipLogger().updateLogFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
