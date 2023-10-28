package requests.multicast;

import requests.NetworkSerializable;
import requests.RequestType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LeaveMembershipMessage extends NetworkSerializable {

    private final int membershipCounter;
    private final String id;

    public LeaveMembershipMessage(String id, int membershipCounter) {
        this.id = id;
        this.membershipCounter = membershipCounter;
    }


    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.LEAVE + CRLF
                        + id + CRLF
                        + membershipCounter + CRLF
                        + CRLF;

        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public String toString() {
        return RequestType.LEAVE + CRLF + id + CRLF + membershipCounter + CRLF + CRLF;
    }
}
