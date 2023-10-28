package handler.membership;

import java.io.IOException;
import java.io.InputStream;

import store.nodes.Neighbour;
import store.nodes.NodeState;

public class LeaveRequestHandler extends MulticastMessageHandler{

    protected LeaveRequestHandler(NodeState nodeState){
        super(nodeState);
    }

    @Override
    public void execute(String[] headers, InputStream inputStream) throws IOException {

        String nodeId = headers[1];
        String memberShipCounter = headers[2];

        if (nodeId.equals(getNodeState().getNodeId())) return;

        System.out.println("Received" + "multicast LEAVE message from node '" + nodeId + "'");

        this.getNodeState().getMembershipLogger().addLogEvent(new Neighbour(nodeId, memberShipCounter));
    }
}
