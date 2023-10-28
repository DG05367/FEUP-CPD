package requests.store;

import requests.NetworkRequest;
import requests.NetworkSerializable;
import requests.RequestType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GetRequest extends NetworkSerializable implements NetworkRequest {
    public static final String ERROR_NOT_FOUND = "ERROR: Key not found\n";
    public static final int ERROR_NOT_FOUND_SIZE = ERROR_NOT_FOUND.getBytes(StandardCharsets.US_ASCII).length;

    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException{
        String header = RequestType.GET + CRLF +
        key + CRLF + CRLF;

        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
    }

    public static GetRequest fromNetworkStream(String[] headers) {
        return new GetRequest(headers[1]);
    }
}
