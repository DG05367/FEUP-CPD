package rmi;

import java.io.IOException;
import java.rmi.RemoteException;

import store.MulticastMessage;
import store.nodes.NodeState;
import store.nodes.State;
import store.service.*;

public class MembershipProtocolRemote implements MembershipCommands {
    private final NodeState nodeState;
    private int retries = 0;
    private static final int MAX_RETRIES = 2;
    private int joinResponsesCounter = 0;
    private final ServiceProvider serviceProvider;

    public MembershipProtocolRemote(NodeState nodeState, ServiceProvider serviceProvider) {
        this.nodeState = nodeState;
        this.serviceProvider = serviceProvider;
    }

    public NodeState getNodeState() {
        return this.nodeState;
    }

    public void joinProtocol() {

        
        // Open TCP port on the joining node, to accept membership messages from the other nodes
         
        JoinServiceThread joinServiceThread = new JoinServiceThread(nodeState);
        joinServiceThread.start();

        // Wait for the private port to be opened in the JoinServiceThread
        while (!joinServiceThread.getPortStatus());
        int privatePort = joinServiceThread.getPort();

        try {
            MulticastMessage.multicastJoin(nodeState.getNodeId(),
                    nodeState.getMembershipLogger().getMembershipCounter(), nodeState.getMultiCastIpAddress(),
                    nodeState.getMultiCastPort(), privatePort);
            joinServiceThread.join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        joinResponsesCounter += joinServiceThread.getConnectionsEstablished();

        if (joinResponsesCounter < 3 && retries < MAX_RETRIES) {
            retries++;
            System.out.println(
                    "Join Protocol repeating. Retry attempts: " + retries + "/" + MAX_RETRIES);
            joinProtocol();
        }
    }

    @Override
    public String join() throws RemoteException {

        State state = nodeState.getState();

        if (state.equals(State.JOINING)) {
            return "Node '" + this.nodeState.getNodeId() + "' is already joining the cluster";
        } else if (state.equals(State.JOINED)) {
            return "Node '" + this.nodeState.getNodeId() + "' has already joined the cluster";
        } else if (state.equals(State.LEAVING)) {
            return "Node '" + this.nodeState.getNodeId() + "' is leaving the cluster";
        }
        try {
            nodeState.getMembershipLogger().updateCounter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nodeState.setState(State.JOINING);
        joinProtocol();
        nodeState.setState(State.JOINED);

        serviceProvider.setupMembershipService();
        System.out.println("JOINED CLUSTER");

        return "Node '" + nodeState.getNodeId() + "' joined cluster successfully";
    }

    @Override
    public String leave() throws RemoteException {

        State state = nodeState.getState();

        if (state.equals(State.JOINING)) {
            return "Node '" + this.nodeState.getNodeId() + "' is already joining the cluster";
        } else if (state.equals(State.WAITING)) {
            return "Node '" + this.nodeState.getNodeId() + "' is waiting for client";
        } else if (state.equals(State.LEAVING)) {
            return "Node '" + this.nodeState.getNodeId() + "' is already leaving the cluster";
        }

        nodeState.setState(State.LEAVING);

        try {
            // Update counter
            nodeState.getMembershipLogger().updateCounter();

            // Send leave message
            MulticastMessage.multicastLeave(nodeState.getNodeId(),
                    nodeState.getMembershipLogger().getMembershipCounter(), nodeState.getMultiCastIpAddress(),
                    nodeState.getMultiCastPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        serviceProvider.stopMembershipService();
        nodeState.setState(State.WAITING);
        System.out.println("LEFT CLUSTER");

        return "Node '" + nodeState.getNodeId() + "' left the cluster successfully";

    }
}
