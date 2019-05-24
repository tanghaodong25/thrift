package org.apache.thrift.async;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.Handler;
import com.intel.hpnl.core.HpnlBuffer;
import java.nio.ByteBuffer;

public class ClientRecvCallback implements Handler {
    public ClientRecvCallback(boolean is_server) {
        this.is_server = is_server;
    }

    public synchronized void handle(final Connection con, int rdmaBufferId, int blockBufferSize) {
        HpnlBuffer recvBuffer = con.getRecvBuffer(rdmaBufferId);
        ByteBuffer recvByteBuffer = recvBuffer.get(blockBufferSize);
        ByteBuffer onheap = ByteBuffer.allocate(recvByteBuffer.capacity());
        onheap.put(recvByteBuffer);
        ByteBuffer b = ByteBuffer.allocate(onheap.capacity() - 4);
        b.put(onheap.array(), 4, onheap.capacity() - 4);
        b.flip();
        SaveBuffer.pushBuffer(b);
    }

    boolean is_server = false;
    private HpnlBuffer buffer;
}
