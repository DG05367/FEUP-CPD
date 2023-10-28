package handler.membership;

import requests.NetworkSerializable;
import requests.RequestType;
import store.nodes.NodeState;
import utils.InvalidByteArray;

import java.io.IOException;
import java.io.InputStream;

public class DispatchMulticastMessage implements Runnable {
    private final NodeState nodeState;
    private final InputStream inputStream;

    public DispatchMulticastMessage(NodeState nodeState, InputStream inputStream) {
        this.nodeState = nodeState;
        this.inputStream = inputStream;
    }

    private MulticastMessageHandler getRequestHandler(String requestType) throws InvalidByteArray {
        switch (requestType) {
            case RequestType.MEMBERSHIP -> {
                return new MembershipMessageHandler(nodeState);
            }
            case RequestType.JOIN -> {
                return new JoinRequestHandler(nodeState);
            }
            case RequestType.LEAVE -> {
                return new LeaveRequestHandler(nodeState);
            }
            default -> throw new InvalidByteArray("Invalid Request Type");
        }
    }

    @Override
    public void run() {
        try {
            String[] headers = NetworkSerializable.getHeader(inputStream);
            assert headers != null;
            MulticastMessageHandler handler = this.getRequestHandler(headers[0]);
            handler.execute(headers, inputStream);
        } catch (IOException | InvalidByteArray e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
