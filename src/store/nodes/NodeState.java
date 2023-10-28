package store.nodes;

import utils.InvalidArgumentsException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

import store.filesystem.FileStorer;
import store.filesystem.MembershipLogger;

// Current state of the node
 
public class NodeState extends Node {
    public static final int NUM_ARGS = 4;   // Number of arguments that are expected
    private State state;
    private final InetAddress mCastIpAddr;
    private final int mCastPort;
    private final int storePort;
    private final InetSocketAddress tcpConAddress;
    private final MembershipLogger membershipLogger;
    private final FileStorer storeFiles;

    private NodeState(String nodeId, InetAddress mCastIpAddr, int mCastPort, int storePort) throws IOException {
        super(nodeId);
        this.mCastIpAddr = mCastIpAddr;
        this.mCastPort = mCastPort;
        this.storePort = storePort;
        this.state = State.WAITING;

        tcpConAddress = new InetSocketAddress(nodeId, storePort);
        this.membershipLogger = new MembershipLogger(this);
        this.storeFiles = new FileStorer(this);
    }

    public static NodeState fromArguments(String[] args) throws InvalidArgumentsException, IOException {
        if (args.length != NUM_ARGS) {
            throw new InvalidArgumentsException(String.format("Invalide Number of arguments.%nExpected %d arguments but %d were given", NUM_ARGS, args.length));
        }
        String nodeId = args[2];
        InetAddress mCastIpAddr;
        int mCastPort;
        int storePort;

        try {
            mCastPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("Multicast Port provided is not a valid integer...");
        }

        try {
            storePort = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("Store Port provided is not a valid integer...");
        }

        try {
            mCastIpAddr = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            throw new InvalidArgumentsException("Multicast host provided cannot be found...");
        }

        return new NodeState(nodeId, mCastIpAddr, mCastPort, storePort);
    }

    public synchronized void setState(State state) {
        this.state = state;
    }

    public InetSocketAddress getTcpConnectionAddress() {
        return tcpConAddress;
    }

    public FileStorer getFileStorer() { return this.storeFiles; }

    public InetAddress getMultiCastIpAddress() {
        return mCastIpAddr;
    }

    public MembershipLogger getMembershipLogger() {
        return membershipLogger;
    }

    public int getMultiCastPort() {
        return mCastPort;
    }

    public List<Neighbour> getNeighbourNodes() {
        List<Neighbour> neighbours = membershipLogger.getActiveNodes();
        neighbours.removeIf(elem -> elem.getNodeId().equals(getNodeId()));
        return neighbours;
    }

    public synchronized State getState() {
        return this.state;
    }
}
