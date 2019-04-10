package transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rdmacallback.ConnectedCallback;
import rdmacallback.ServerRecvCallback;
import rdmacallback.ShutdownCallback;
import server.AbstractNonblockingServer;

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
    Map<Connection, AbstractNonblockingServer.FrameBuffer> map=new HashMap<Connection,AbstractNonblockingServer.FrameBuffer>();
    public RDMAServerSocket(String port){
        this.ip="localhost";
        this.port=port;
        connect();
    }

    private void connect(){
        eqService = new EqService("localhost", port, 3, 32, true).init();
        cqService = new CqService(eqService, eqService.getNativeHandle()).init();
        conList = new ArrayList<Connection>();
        connectedCallback = new ConnectedCallback(conList, true);
        recvCallback = new ServerRecvCallback(eqService, true);
        eqService.setConnectedCallback(connectedCallback);
        eqService.setRecvCallback(recvCallback);
        eqService.initBufferPool(32, 65536, 32);

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
