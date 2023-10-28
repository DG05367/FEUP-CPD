package store;

import utils.InvalidArgumentsException;

import java.io.IOException;
import java.rmi.AlreadyBoundException;

import store.nodes.NodeState;
import store.service.ServiceProvider;

public class Store {
    public static void main(String[] args) throws IOException {
        try {
            ServiceProvider provider = new ServiceProvider(NodeState.fromArguments(args));
            provider.setupConnectionService();
            provider.setupDataService();
        } catch (InvalidArgumentsException invalidArgumentsException) {
            System.out.println(printFormat());
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {}
    }

    public static String printFormat() {
        return "Usage: java store.Store <IP_mcast_addr> <IP_mcast_port> <node_id>  <Store_port>";
    }
}
