package handler.store;

import requests.store.DeleteRequest;
import store.nodes.NodeState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

public class DeleteRequestHandler extends StoreRequestHandler {
    private static final String SUCCESS_MESSAGE = "SUCCESS: File deleted with success";
    private static final String ERROR_DELETING_FILE = "ERROR: Could not delete file";

    public DeleteRequestHandler(NodeState state) {
        super(state);
    }


    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        DeleteRequest request = DeleteRequest.fromNetworkStream(headers);
        String requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
        System.out.println("Received DELETE request of file with key " + headers[1]);
        
        if (requestDest.equals(getNodeState().getNodeId())) {
            try {
                Files.delete(getNodeState().getFileStorer().getFilePath(request.getKey()));
            } catch (FileNotFoundException e) {}
            return;
        }
        // Handles communication errors with other servers
        // Communication errors between server and client should be handled elsewhere
        for (int i = 0; i < NUM_RETRIES; i++) {
            requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
            try(Socket rightDest = new Socket(requestDest, getNodeState().getTcpConnectionAddress().getPort())) {
                request.send(rightDest.getOutputStream());
                rightDest.getInputStream().transferTo(responseStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
