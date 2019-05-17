package org.apache.thrift.transport;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.service.Client;
import org.apache.thrift.async.ClientRecvCallback;
import org.apache.thrift.async.ShutdownCallback;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class RDMASocket extends RDMATransport {

    private String ip;
    private String port;
    private ShutdownCallback shutdownCallback = null;
    private Connection con = null;
    private ClientRecvCallback clientRecvCallback = null;
    private Client client=null;

    public RDMASocket(String ip, String port) {
        this.ip = ip;
        this.port = port;
        connect();
    }

    private void connect() {
        client=new Client(1,32);
        shutdownCallback = new ShutdownCallback();
        clientRecvCallback = new ClientRecvCallback(false);
        client.setRecvCallback(clientRecvCallback);
        client.setShutdownCallback(shutdownCallback);
        client.initBufferPool(32, 65536, 32);
    }

    @Override
    public boolean isOpen() {
        if (con == null)
            return false;
        return true;
    }

    public void open() {
        if (!isOpen()){
            client.start();
            con = client.connect(ip, port, 0);
        }
    }

    public void close() {
        client.shutdown();
        client.join();
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        return len + 1;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        ByteBuffer buffer = ByteBuffer.wrap(buf, off, len);
        con.send(buffer, (byte) 0, 10);
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
        con.send(buffer, (byte) 0, 10);
        return 0;
    }
}
