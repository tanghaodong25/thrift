package org.apache.thrift.transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;
import org.apache.thrift.async.ConnectedCallback;
import org.apache.thrift.server.RDMATServer;
import org.apache.thrift.server.ServerRecvCallback;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RDMAServerSocket extends TNonblockingServerTransport {

    private  String ip;
    private  String port;
    private EqService eqService=null;
    private CqService cqService=null;
    private ConnectedCallback connectedCallback=null;
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
        eqService = new EqService(3, 32, true).init();
        cqService = new CqService(eqService).init();
        conList = new ArrayList<Connection>();
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
    public void listen() {
        if (eqService!=null&&cqService!=null){
            cqService.start();
            eqService.listen(this.ip, this.port);
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
