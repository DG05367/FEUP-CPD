package requests.multicast;

import requests.NetworkSerializable;
import requests.RequestType;
import store.nodes.Neighbour;
import store.nodes.NodeState;
import utils.InvalidByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MembershipMessage extends NetworkSerializable {

    private final List<Neighbour> logList;
    private final List<Neighbour> activeNodes;
    private final String nodeId;

    public MembershipMessage(List<Neighbour> logList, List<Neighbour> activeNodes, String nodeId) {
        this.logList = logList;
        this.activeNodes = activeNodes;
        this.nodeId = nodeId;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.MEMBERSHIP + CRLF + nodeId + CRLF + CRLF;
        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
        String section1 = "LOG" + CRLF;
        outputStream.write(section1.getBytes(StandardCharsets.US_ASCII));
        for (int i = 0; i < this.logList.size(); i++) {
            String entry = this.logList.get(i).toString() + CRLF;
            outputStream.write(entry.getBytes(StandardCharsets.US_ASCII));
        }
        String section2 = "ACTIVE" + CRLF;
        outputStream.write(section2.getBytes(StandardCharsets.US_ASCII));
        for (Neighbour n : activeNodes) {
            String entry = n.toString() + CRLF;
            outputStream.write(entry.getBytes(StandardCharsets.US_ASCII));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        String header = RequestType.MEMBERSHIP + CRLF + nodeId + CRLF + CRLF;
        String section1 = "LOG" + CRLF;
        String section2 = "ACTIVE" + CRLF;
        for (Neighbour n : this.logList)
            stringBuilder1.append(n.toString()).append(CRLF);
        for (Neighbour n : activeNodes)
            stringBuilder2.append(n.toString()).append(CRLF);
        return header + section1 + stringBuilder1 + section2 + stringBuilder2;
    }

    public static void processMessage(NodeState nodeState, InputStream inputStream) {
        List<Neighbour> neighbours = new ArrayList<>();
        try (Scanner scanner = new Scanner(inputStream)) {
            String logString = scanner.nextLine();
            if (!logString.equals("LOG")) {
                throw new InvalidByteArray("Unexpected byte sequence in Membership message");
            }
            while (true) {
                String entry = scanner.nextLine();
                if (entry.equals("ACTIVE")) break;
                neighbours.add(Neighbour.fromString(entry));
            }
            nodeState.getMembershipLogger().updateLog(neighbours);
        } catch (InvalidByteArray e) {
            throw new RuntimeException(e);
        }
    }
}
