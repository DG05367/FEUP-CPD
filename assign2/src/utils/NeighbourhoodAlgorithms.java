package utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import store.nodes.Node;
import store.nodes.NodeState;

public class NeighbourhoodAlgorithms {
    private final NodeState state;

    public NeighbourhoodAlgorithms(NodeState state) {
        this.state = state;
    }

    private String findNearestNeighbour(List<? extends Node> candidates, String fileKey) {
        try {
            List<? extends Node> sortedCandidates = candidates.stream().sorted(Comparator.comparing(Node::getHashedNodeId)).toList();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            BigInteger requestHash = new BigInteger(1, digest.digest(fileKey.getBytes(StandardCharsets.UTF_8)));
            for (int i = 1; i < sortedCandidates.size(); i++) {
                BigInteger curHash = sortedCandidates.get(i).getHashedNodeId();
                BigInteger antHash = sortedCandidates.get(i - 1).getHashedNodeId();
                if (requestHash.compareTo(curHash) < 0 && requestHash.compareTo(antHash) >= 0) {
                    return sortedCandidates.get(i).getNodeId();
                }
            }
            //If it arrives here the circular list was all traversed and the next node is the starting node
            return sortedCandidates.get(0).getNodeId();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String findRequestDest(String key) {
        List<Node> neighbours = new ArrayList<>(state.getNeighbourNodes());
        neighbours.add(state);
        return findNearestNeighbour(neighbours, key);
    }

    public String findHeir(String key) {
        return findNearestNeighbour(state.getNeighbourNodes(), key);
    }
}
