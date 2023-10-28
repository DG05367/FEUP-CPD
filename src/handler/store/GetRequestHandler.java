package handler.store;

import requests.store.GetRequest;
import store.nodes.NodeState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GetRequestHandler extends StoreRequestHandler {
    public GetRequestHandler(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        GetRequest request = GetRequest.fromNetworkStream(headers);
        String requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
        System.out.println("Received GET request of file with key " + headers[1]);
       /*  
        if (requestDest.equals(getNodeState().getNodeId())) {
            try {
                Files.(getNodeState().getFileStorer().getFilePath(request.getKey()));
            } catch (FileNotFoundException e) {}
            return;
        }
        */
        responseStream.write(GetRequest.ERROR_NOT_FOUND.getBytes(StandardCharsets.US_ASCII));
    }
}
