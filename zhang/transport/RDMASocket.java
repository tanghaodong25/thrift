package thrift.bio.transport;

import com.intel.hpnl.core.Buffer;
import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RDMASocket extends RDMATransport {
    private  String ip;
    private  String port;
    private  EqService eqService=null;
    private  CqService cqService=null;
    private  ConnectedCallback connectedCallback=null;
    private  ReadCallback readCallback=null;
    private  ShutdownCallback shutdownCallback=null;
    protected InputStream inputStream_ = null;
    protected OutputStream outputStream_ = null;
    public RDMASocket(String ip,String port){
        this.ip=ip;
        this.port=port;
        connect();
    }

    private void connect(){
        eqService = new EqService(ip, port, false);
        cqService = new CqService(eqService, 1, eqService.getNativeHandle());
        List<Connection> conList = new CopyOnWriteArrayList<Connection>();
        connectedCallback = new ConnectedCallback(conList, false);
        readCallback = new ReadCallback(false, eqService);
        shutdownCallback = new ShutdownCallback();

    }

    @Override
    public boolean isOpen() {
        if (connectedCallback==null||readCallback==null)
            return false;
        return true;
    }

    public void open(){
        eqService.setConnectedCallback(connectedCallback);
        eqService.setRecvCallback(readCallback);
        eqService.setSendCallback(null);
        eqService.setShutdownCallback(shutdownCallback);
//        inputStream_ = new BufferedInputStream(eqService.getRecvBuffer(1));
//        outputStream_ = new BufferedOutputStream(eqService.getSendBuffer(1));
        cqService.start();
        eqService.start(1);
    }

    public void close(){
        eqService.shutdown();
        cqService.shutdown();
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        return 0;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {

    }

    @Override
    public int read(Buffer buffer) throws IOException {
        return 0;
    }

    @Override
    public int write(Buffer buffer) throws IOException {
        return 0;
    }
}
