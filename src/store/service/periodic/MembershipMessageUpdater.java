package store.service.periodic;

import java.io.IOException;
import java.util.List;

import store.MulticastMessage;
import store.nodes.Neighbour;
import store.nodes.Node;
import store.nodes.NodeState;
import store.nodes.State;

public class MembershipMessageUpdater extends PeriodicActor {

    public MembershipMessageUpdater(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    protected long getInterval() { return 1; }

    @Override
    public void run() {
        if (!shouldSendMembership()) return;
        try {
            MulticastMessage.multicastMembership(nodeState.getMembershipLogger().getLog(), nodeState.getMembershipLogger().getActiveNodes(), nodeState.getNodeId(),
                    nodeState.getMultiCastIpAddress(), nodeState.getMultiCastPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean shouldSendMembership() {
        List<Neighbour> activeNodes = this.nodeState.getMembershipLogger().getActiveNodes();
        if (activeNodes.isEmpty()) {
            System.err.println("No active nodes");
            return false;
        }
        Node node1 = nodeState;
        Neighbour smallestNeighbour = null;
        for (Neighbour n : activeNodes) {
            if (smallestNeighbour == null) smallestNeighbour = n;
            String id = n.getNodeId();
            String smallestId = smallestNeighbour.getNodeId();
            int number = Integer.parseInt(String.valueOf(id.charAt(id.length() - 1)));
            int smallestNumber = Integer.parseInt(String.valueOf(smallestId.charAt(smallestId.length() - 1)));
            if (number < smallestNumber) smallestNeighbour = n;
        }
        return (node1.equals(smallestNeighbour) && nodeState.getState().equals(State.JOINED));
    }
}
