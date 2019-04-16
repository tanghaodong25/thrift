package org.apache.thrift.transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;
import org.apache.thrift.async.ConnectedCallback;
import org.apache.thrift.async.ShutdownCallback;
import org.apache.thrift.server.RDMATServer;
import org.apache.thrift.server.ServerRecvCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RDMAServerSocket extends TNonblockingServerTransport {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDMAServerSocket.class.getName());

    private  String ip;
    private  String port;
    private EqService eqService=null;
    private CqService cqService=null;
    private ConnectedCallback connectedCallback=null;
    private ShutdownCallback shutdownCallback=null;
    private List<Connection> conList=null;
    ServerRecvCallback recvCallback=null;
    RDMATServer.Args rargs=null;
    Map<Connection, RDMATServer.FrameBuffer> map=new HashMap<>();
    public RDMAServerSocket(String port){
        this.ip="localhost";
        this.port=port;
        connect();
    }

    private void connect(){
        eqService = new EqService("localhost", port, 3, 32, true).init();
        cqService = new CqService(eqService, eqService.getNativeHandle()).init();
        conList = new ArrayList<Connection>();
//        connectedCallback = new ConnectedCallback(conList, true,rargs);
//        recvCallback = new ServerRecvCallback(eqService, true,rargs);
//        eqService.setConnectedCallback(connectedCallback);
//        eqService.setRecvCallback(recvCallback);
        eqService.initBufferPool(32, 65536, 32);

    }

    public void  setServer(RDMATServer.Args rargs){
        this.rargs=rargs;
        connectedCallback = new ConnectedCallback(conList, true);
        recvCallback = new ServerRecvCallback(eqService, true,rargs);
        eqService.setRecvCallback(recvCallback);
        eqService.setConnectedCallback(connectedCallback);
    }

    @Override
    public void listen() throws TTransportException {

        if (eqService!=null&&cqService!=null){
            eqService.start();
            cqService.start();
        }
    }

    public void waitshutdown(){
        cqService.join();

    }


    @Override
    public void close() {
        eqService.shutdown();
        cqService.join();
        cqService.shutdown();
    }

    @Override
    protected TTransport acceptImpl() throws TTransportException {
        return null;
    }

    @Override
    public void registerSelector(Selector selector) {

    }
}
