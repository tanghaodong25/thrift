package org.apache.thrift.server;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.Handler;
import com.intel.hpnl.core.HpnlBuffer;
import java.nio.ByteBuffer;

public class ServerRecvCallback implements Handler {
    public ServerRecvCallback( boolean is_server, RDMATServer.Args tnbargs) {
        this.is_server = is_server;
        this.arggs = tnbargs;
        RDMATServer server = new RDMATServer(arggs);
        buff = server.new AsyncFrameBuffer();
    }

    public synchronized void handle(Connection con, int rdmaBufferId, int blockBufferSize) {
        HpnlBuffer rdmaBuffer = con.getRecvBuffer(rdmaBufferId);
        ByteBuffer recvByteBuffer = rdmaBuffer.get(blockBufferSize);
        RDMATServer.FrameBuffer frameBuffer = buff;
        int framesize = 0;
        framesize = recvByteBuffer.capacity();
        frameBuffer.initalBuffer(framesize);
        frameBuffer.apendBuffer_(recvByteBuffer);
        ((RDMATServer.AsyncFrameBuffer) frameBuffer).invoke();
        con.send(frameBuffer.getBuffer_(), (byte) 0, 10);
    }

    private RDMATServer.FrameBuffer buff = null;
    private RDMATServer.Args arggs;
    private boolean is_server = false;
}
