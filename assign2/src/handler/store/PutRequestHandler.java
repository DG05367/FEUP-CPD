package handler.store;

import requests.store.PutRequest;
import store.nodes.NodeState;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PutRequestHandler extends StoreRequestHandler {
    private static final String SUCCESS_MESSAGE = "SUCCESS: File stored";
    private static final String ERROR_SENDING_FILE = "ERROR: Couldn't send the file";
    private static final String ERROR_WRONG_DEST = "ERROR: This node doesn't handle this key";
    private static final String ERROR_REJ_FILE = "ERROR: This node doesn't handle this key";

    public PutRequestHandler(NodeState state) {
        super(state);
    }


    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientStream) throws IOException {
        PutRequest request = PutRequest.fromNetworkStream(getNodeState(), headers, clientStream);
        System.out.println("Received PUT request of file with key " + headers[1]);
        
        String requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());

        if (getNodeState().getFileStorer().hasTombstone(request.getKey())) {
            responseStream.write(ERROR_REJ_FILE.getBytes(StandardCharsets.US_ASCII));
        }

        if (requestDest.equals(getNodeState().getNodeId())) {
            responseStream.write(SUCCESS_MESSAGE.getBytes(StandardCharsets.US_ASCII));
            return;
        }

        // Handles communication errors with other servers
        // Communication errors between server and client should be handled elsewhere
        for (int i = 0; i < NUM_RETRIES; i++) {
            requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
            try(Socket rightDest = new Socket(requestDest, getNodeState().getTcpConnectionAddress().getPort())) {
                request.send(rightDest.getOutputStream());
                rightDest.getInputStream().transferTo(responseStream);
                Files.delete(Paths.get(request.getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
