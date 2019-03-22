package thrift.bio.transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RDMAServerSocket extends TServerTransport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TServerSocket.class.getName());

    private  String ip;
    private  String port;
    private EqService eqService=null;
    private CqService cqService=null;
    private  ConnectedCallback connectedCallback=null;
    private  ReadCallback readCallback=null;
    private  ShutdownCallback shutdownCallback=null;
    private List<Connection> conList=null;
    public RDMAServerSocket(String ip,String port){
        this.ip=ip;
        this.port=port;
        connect();
    }

    private void connect(){
        eqService = new EqService("localhost", port, 3, 32, true).init();
        cqService = new CqService(eqService, eqService.getNativeHandle()).init();
         conList = new ArrayList<Connection>();
        connectedCallback = new ConnectedCallback(conList, true);
        readCallback = new ReadCallback(true, eqService);
        eqService.setConnectedCallback(connectedCallback);
        eqService.setRecvCallback(readCallback);
        eqService.initBufferPool(32, 65536, 32);

    }


    @Override
    public void listen() throws TTransportException {

        if (eqService!=null&&cqService!=null){
            System.out.println("a");
            eqService.start();
            cqService.start();
        }
    }

    @Override
    public void close() {
        eqService.shutdown();
        cqService.shutdown();
    }

    @Override
    protected RDMASocket acceptImpl() throws TTransportException {
        /*
        现在最关键的就是在写这个，我的理解thrift 的serversocket在这个方法里拿到 client socket 的信息

         */
        if (eqService==null){
            throw new TTransportException(TTransportException.NOT_OPEN, "No underlying server socket.");
        }else {
            RDMASocket rdmaSocket = new RDMASocket("127.0.0.1","7788");
            return rdmaSocket;
        }
    }
}
