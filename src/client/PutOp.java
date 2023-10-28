package client;

import requests.store.PutRequest;
import utils.InvalidArgumentsException;

import static utils.FileKeyGeneration.keyGen;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PutOp extends TcpOp {
    private Path filePath;

    public PutOp(String nodeAp, String filePath) throws InvalidArgumentsException {
        super(nodeAp);
        this.filePath = Paths.get(filePath);

        if (!Files.isRegularFile(this.filePath)) {
            throw new InvalidArgumentsException("File does not exist");
        }
    }

    @Override
    public void execute() {
        Socket clientSocket = null;

        try {
            String fileName = filePath.getFileName().toString();
            PutRequest putRequest = new PutRequest(keyGen(new FileInputStream(fileName)), filePath.toString());

            System.out.println("\nFile Hash:" + putRequest.getKey() + "\n");

            clientSocket = new Socket(getHost(), getPort());

            putRequest.send(clientSocket.getOutputStream());

            clientSocket.getInputStream().transferTo(System.out);
            clientSocket.close();

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
