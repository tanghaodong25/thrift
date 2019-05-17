package org.apache.thrift.transport;

import com.intel.hpnl.service.Server;
import org.apache.thrift.server.RDMATServer;
import org.apache.thrift.server.ServerRecvCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.channels.Selector;

public class RDMAServerSocket extends TNonblockingServerTransport {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDMAServerSocket.class.getName());
    private String ip;
    private String port;
    private ServerRecvCallback recvCallback = null;
    private RDMATServer.Args rargs = null;
    private Server server=null;

    public RDMAServerSocket(String ip,String port) {
        this.ip = ip;
        this.port = port;
        connect();
    }

    private void connect() {
        server=new Server(3,32);
        server.initBufferPool(32, 65536, 32);
    }

    public void setServer(RDMATServer.Args rargs) {
        this.rargs = rargs;
        recvCallback = new ServerRecvCallback(true, rargs);
        server.setRecvCallback(recvCallback);
    }

    @Override
    public void listen() throws TTransportException {
        if (server!=null){
            server.start();
            server.listen(ip,port);
        }
    }

    public void waitshutdown() {
        server.join();
    }

    @Override
    public void close() {
       server.shutdown();
       server.join();
    }

    @Override
    protected TTransport acceptImpl() throws TTransportException {
        return null;
    }

    @Override
    public void registerSelector(Selector selector) {
    }
}
