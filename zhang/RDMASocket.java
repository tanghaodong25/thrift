package thrift.bio.transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;
import com.intel.hpnl.core.RdmaBuffer;

import java.io.*;
import java.nio.ByteBuffer;
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
    private  List<Connection> conList=null;

    protected InputStream inputStream_ = null;
    protected OutputStream outputStream_ = null;
    public RDMASocket(String ip,String port){
        this.ip=ip;
        this.port=port;
        connect();
    }

    private void connect(){
        eqService = new EqService(ip, port, 1, 32, false).init();
        cqService = new CqService(eqService, eqService.getNativeHandle()).init();
        List<Connection> conList = new CopyOnWriteArrayList<Connection>();
        connectedCallback = new ConnectedCallback(conList, false);
        readCallback = new ReadCallback(false, eqService);
        shutdownCallback = new ShutdownCallback();
        eqService.setConnectedCallback(connectedCallback);
        eqService.setRecvCallback(readCallback);
        eqService.setSendCallback(null);
        eqService.setShutdownCallback(shutdownCallback);
        eqService.initBufferPool(32, 65536, 32);

    }

    @Override
    public boolean isOpen() {
        if (connectedCallback==null||readCallback==null)
            return false;
        return true;
    }

    public void open(){

        cqService.start();
        eqService.start();

    }

    public void close(){
        eqService.shutdown();
        cqService.shutdown();
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        ByteBuffer buffer=ByteBuffer.wrap(buf,off,len);
        for (Connection con:conList) {
            RdmaBuffer rdmaBuffer=con.takeSendBuffer(true);
            long address = buffer.getLong();
            long rkey = buffer.getLong();
            return con.read(rdmaBuffer.getRdmaBufferId(), 0, 4096*1024, address, rkey);
        }
        return 0;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        ByteBuffer buffer=ByteBuffer.wrap(buf,off,len);
        for (Connection con:conList) {
            RdmaBuffer rdmaBuffer = con.takeSendBuffer(true);
            rdmaBuffer.put(buffer, (byte)0, 10);
            con.send(rdmaBuffer.remaining(), rdmaBuffer.getRdmaBufferId());
        }

    }


    @Override
    public int read(ByteBuffer buffer) throws IOException {
        for (Connection con:conList) {
            RdmaBuffer rdmaBuffer=con.takeSendBuffer(true);
            long address = buffer.getLong();
            long rkey = buffer.getLong();
            return con.read(rdmaBuffer.getRdmaBufferId(), 0, 4096*1024, address, rkey);
        }
        return 0;
    }

    @Override
    public int write(ByteBuffer buffer) throws IOException {
        for (Connection con:conList) {
            RdmaBuffer rdmaBuffer = con.takeSendBuffer(true);
            rdmaBuffer.put(buffer, (byte)0, 10);
            return con.send(rdmaBuffer.remaining(), rdmaBuffer.getRdmaBufferId());
        }
        return 0;
    }
}
