package client;

import requests.store.GetRequest;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class GetOp extends TcpOp {
    String key;

    public GetOp(String nodeAp, String key) {
        super(nodeAp);
        this.key = key;
    }

    @Override
    public void execute() {
        Socket clientSocket = null;
        
        try {
            clientSocket = new Socket(getHost(), getPort());

            OutputStream outStr = clientSocket.getOutputStream();
            InputStream inStr = clientSocket.getInputStream();

            GetRequest request = new GetRequest(key);

            request.send(outStr);
            inStr.transferTo(System.out);
        } catch (SocketException e) {
            try {
                assert clientSocket != null;
                clientSocket.getInputStream().transferTo(System.out);
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
