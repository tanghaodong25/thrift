package org.apache.thrift.transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;
import com.intel.hpnl.core.HpnlBuffer;
import org.apache.thrift.async.ClientRecvCallback;
import org.apache.thrift.async.ConnectedCallback;
import org.apache.thrift.async.ShutdownCallback;
import org.apache.thrift.server.RDMATServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RDMASocket extends RDMATransport {

    private  String ip;
    private  String port;
    private  EqService eqService=null;
    private  CqService cqService=null;
    private ConnectedCallback connectedCallback=null;
    private ShutdownCallback shutdownCallback=null;
    private  List<Connection> conList=null;
    ClientRecvCallback clientRecvCallback=null;
    TNonblockingTransport transport=null;
    RDMATServer.Args rargs=null;
    protected InputStream inputStream_ = null;
    protected OutputStream outputStream_ = null;
    public RDMASocket(String ip,String port){
        this.ip=ip;
        this.port=port;
        connect();

    }

    private void connect(){
        eqService = new EqService(1, 32, false).init();
        cqService = new CqService(eqService).init();
        transport=this;
        conList = new CopyOnWriteArrayList<Connection>();
        connectedCallback = new ConnectedCallback(conList, false);
        shutdownCallback = new ShutdownCallback();
        HpnlBuffer buffer = eqService.getRmaBuffer(4096*1024);
        clientRecvCallback = new ClientRecvCallback(false, buffer, transport);
        eqService.setConnectedCallback(connectedCallback);
        eqService.setRecvCallback(clientRecvCallback);
        eqService.setSendCallback(null);
        eqService.setShutdownCallback(shutdownCallback);
        eqService.initBufferPool(32, 65536, 32);
        eqService.connect(this.ip, this.port, 0);
        cqService.start();
    }

    @Override
    public boolean isOpen() {
        if (connectedCallback==null)
            return false;
        return true;
    }

    public void open(){
    }

    public void close(){
        cqService.shutdown();
        cqService.join();
        eqService.shutdown();
        eqService.join();

    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        return len+1;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        ByteBuffer buffer=ByteBuffer.wrap(buf,off,len);
        System.out.println("wrdbuffer");
        for (Connection con:conList) {
            HpnlBuffer rdmaBuffer = con.takeSendBuffer(true);
            rdmaBuffer.put(buffer, (byte)0, 10);
            con.send(rdmaBuffer.remaining(), rdmaBuffer.getBufferId());
        }

    }


    @Override
    public boolean startConnect() throws IOException {
        return false;
    }

    @Override
    public boolean finishConnect() throws IOException {
        return false;
    }

    @Override
    public SelectionKey registerSelector(Selector selector, int interests) throws IOException {
        return null;
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException {
        return -1;
    }

    @Override
    public int write(ByteBuffer buffer) throws IOException {
        System.out.println("buffer write");
        Charset charset = Charset.forName("UTF-8");
        System.out.println(new String(buffer.array(),charset));
        for (Connection con:conList) {
            HpnlBuffer rdmaBuffer = con.takeSendBuffer(true);
            rdmaBuffer.put(buffer, (byte)0, 10);
            con.send(rdmaBuffer.remaining(), rdmaBuffer.getBufferId());
        }
        return 0;
    }
}
