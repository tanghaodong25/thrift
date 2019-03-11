package thrift.bio.transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;

import java.util.ArrayList;
import java.util.List;

public class RDMAServerSocket extends TServerTransport {

    private  String ip;
    private  String port;
    private EqService eqService=null;
    private CqService cqService=null;
    private  ConnectedCallback connectedCallback=null;
    private  ReadCallback readCallback=null;
    private  ShutdownCallback shutdownCallback=null;
    public RDMAServerSocket(String ip,String port){
        this.ip=ip;
        this.port=port;
        connect();
    }

    private void connect(){
        eqService = new EqService(ip, port, true);
        cqService = new CqService(eqService, 1, eqService.getNativeHandle());
        List<Connection> conList = new ArrayList<Connection>();
        connectedCallback = new ConnectedCallback(conList, true);
        readCallback = new ReadCallback(true, eqService);
    }


    @Override
    public void listen() throws TTransportException {
        if (eqService!=null&&cqService!=null){
            eqService.waitToConnected();
        }
    }

    @Override
    public void close() {
        eqService.shutdown();
        cqService.shutdown();
    }

    @Override
    protected TTransport acceptImpl() throws TTransportException {
        eqService.setConnectedCallback(connectedCallback);
        eqService.setRecvCallback(readCallback);
        if (eqService==null){
            throw new TTransportException(TTransportException.NOT_OPEN, "No underlying server socket.");
        }else {

            return null;
        }
    }
}
