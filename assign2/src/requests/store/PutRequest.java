package requests.store;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import requests.NetworkRequest;
import requests.NetworkSerializable;
import requests.RequestType;
import store.nodes.NodeState;

public class PutRequest extends NetworkSerializable implements NetworkRequest {
    private final String fileKey;
    private final String filePath;
    private int fileSize;

    public PutRequest(String fileKey, String filePath) {
        this.fileKey = fileKey;
        this.filePath = filePath;
    }

    @Override
    public String getKey() {
        return fileKey;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileSize() {
        return fileSize;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        Path fPath = Path.of(filePath);

        String header = RequestType.PUT + CRLF +
            fileKey + CRLF +
            Files.size(fPath) + CRLF + CRLF;
        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));

        Files.copy(fPath, outputStream);
    }

    public static PutRequest fromNetworkStream(NodeState nodeState, String[] headers, InputStream fileStream) throws IOException {
        String key = headers[1];
        long fileSize = Long.parseLong(headers[2]);
        String filePath = nodeState.getFileStorer().saveFile(key, fileSize, fileStream);

        return new PutRequest(key, filePath);
    }
}
