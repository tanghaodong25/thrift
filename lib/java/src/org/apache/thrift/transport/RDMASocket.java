package transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.CqService;
import com.intel.hpnl.core.EqService;
import com.intel.hpnl.core.RdmaBuffer;
import rdmacallback.ClientRecvCallback;
import rdmacallback.ConnectedCallback;
import rdmacallback.ShutdownCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RDMASocket extends RDMATransport {

    private  String ip;
    private  String port;
    private  EqService eqService=null;
    private  CqService cqService=null;
    private  ConnectedCallback connectedCallback=null;
    private  ShutdownCallback shutdownCallback=null;
    private  List<Connection> conList=null;
    ClientRecvCallback clientRecvCallback=null;

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
       conList = new CopyOnWriteArrayList<Connection>();
        connectedCallback = new ConnectedCallback(conList, false);
        shutdownCallback = new ShutdownCallback();
        RdmaBuffer buffer = eqService.getRmaBuffer(4096*1024);
         clientRecvCallback = new ClientRecvCallback(false, buffer);
        eqService.setConnectedCallback(connectedCallback);
        eqService.setRecvCallback(clientRecvCallback);
        eqService.setSendCallback(null);
        eqService.setShutdownCallback(shutdownCallback);
        eqService.initBufferPool(32, 65536, 32);
        cqService.start();
        eqService.start();

    }

    @Override
    public boolean isOpen() {
        if (connectedCallback==null)
            return false;
        return true;
    }

    public void open(){

        eqService.waitToConnected();
        System.out.println(conList.size());

    }

    public void close(){
        cqService.shutdown();
        cqService.join();
        eqService.shutdown();
        eqService.join();

    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
//        System.out.println("read rdmabyte"+" "+len);
//        RdmaBuffer rdmaBuffer=eqService.getRecvBuffer(1024*(len+1));
//        ClientRecvCallback recvCallback = new ClientRecvCallback(false, rdmaBuffer);
//        ClientReadCallback readCallback = new ClientReadCallback();
//        eqService.setRecvCallback(recvCallback);
//        eqService.setReadCallback(readCallback);
//        eqService.setShutdownCallback(shutdownCallback);
        return len+1;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        ByteBuffer buffer=ByteBuffer.wrap(buf,off,len);
        System.out.println("wrdbuffer");
        for (Connection con:conList) {
            RdmaBuffer rdmaBuffer = con.takeSendBuffer(true);
            rdmaBuffer.put(buffer, (byte)0, 10);
            con.send(rdmaBuffer.remaining(), rdmaBuffer.getRdmaBufferId());
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
        System.out.println("read buffer");
        RdmaBuffer rdmaBuffer=eqService.getRecvBuffer(1024*4096);
        ClientRecvCallback recvCallback = new ClientRecvCallback(false, rdmaBuffer);
        eqService.setRecvCallback(recvCallback);
        eqService.setSendCallback(null);
        eqService.setShutdownCallback(shutdownCallback);
        return -1;
    }

    @Override
    public int write(ByteBuffer buffer) throws IOException {
        System.out.println("buffer write");
        Charset charset = Charset.forName("UTF-8");
        System.out.println(new String(buffer.array(),charset));

        for (Connection con:conList) {
            RdmaBuffer rdmaBuffer = con.takeSendBuffer(true);
            rdmaBuffer.put(buffer, (byte)0, 10);
            return con.send(rdmaBuffer.remaining(), rdmaBuffer.getRdmaBufferId());
        }
        return 0;
    }
}
