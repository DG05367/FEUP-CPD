package client;

public abstract class TcpOp implements OpExec {
    private final String host;
    private final int port;

    public TcpOp(String nodeAp) {
        String[] accessPoint = nodeAp.split(":");
        host = accessPoint[0];
        port = Integer.parseInt(accessPoint[1]);
    }

    protected String getHost() {
        return host;
    }

    protected int getPort() {
        return port;
    }
}
