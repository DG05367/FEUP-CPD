package requests.store;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import requests.NetworkRequest;
import requests.NetworkSerializable;
import requests.RequestType;

public class DeleteRequest extends NetworkSerializable implements NetworkRequest {
    private final String key;

    public DeleteRequest(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.DELETE + CRLF +
        key + CRLF + CRLF;

        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
    }

    public static DeleteRequest fromNetworkStream(String[] headers) {
        return new DeleteRequest(headers[1]);
    }
}
