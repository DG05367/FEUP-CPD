package client;

import utils.InvalidArgumentsException;

/*
The test client should be invoked as follows.

java TestClient <node_ap> <operation> [<opnd>]

ARGUMENTS DESCRIPTION

    <node_ap>
        Node access point. The format of the access point must be <IP address>:<port number>,
        where <IP address> and <port number> are respectively the IP address and the port number
        being used by the node.
    <operation>
        Is the string specifying the operation the node must execute.
        It can be either a key-value operation, i.e. "put", "get" or "delete",
        or a membership operation, i.e. "join" or "leave
    <opnd>
        is the argument of the operation. It is used only for key-value operations.
        In the case of:
        <put> - is the file pathname of the file with the value to add
        <get> or <delete> - is the string of hexadecimal symbols encoding the sha-256 key returned by put.

*/

public class TestClient {
    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            System.out.println("Wrong Test Client invoke format");
            printFormat();
            return;
        }
        try {
            String nodeAp = args[0];
            if (nodeAp.split(":").length != 2) {
                System.out.println("Invalid node Access Point");
                printFormat();
            }
            switch (args[1]) {
                case "join" -> new JoinOp(nodeAp).execute();
                case "leave" -> new LeaveOp(nodeAp).execute();
                case "get" -> new GetOp(nodeAp, args[2]).execute();
                case "put" -> new PutOp(nodeAp, args[2]).execute();
                case "delete" -> new DeleteOp(nodeAp, args[2]).execute();
            }
        } catch (InvalidArgumentsException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printFormat() {
        System.out.println("java TestClient <node_ap> <operation> [<opnd>]");
    }
}
