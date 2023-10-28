package requests.multicast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import requests.NetworkSerializable;
import requests.RequestType;

public class JoinMembershipMessage extends NetworkSerializable {
    private final String nodePort;
    private final int membershipCounter;
    private final String id;
    

    public JoinMembershipMessage(String nodePort, int membershipCounter, String id) {
        this.nodePort = nodePort;
        this.membershipCounter = membershipCounter;
        this.id = id;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.JOIN + CRLF
                        + id + CRLF
                        + nodePort + CRLF
                        + membershipCounter + CRLF
                        + CRLF;
                        
        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public String toString(){
        return RequestType.JOIN + CRLF + id + CRLF + nodePort + CRLF + membershipCounter + CRLF + CRLF;
    }

}
