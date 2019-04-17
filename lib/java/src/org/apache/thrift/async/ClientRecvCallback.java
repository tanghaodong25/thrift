package org.apache.thrift.async;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.Handler;
import com.intel.hpnl.core.HpnlBuffer;
import org.apache.thrift.transport.TNonblockingTransport;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ClientRecvCallback implements Handler {
  public ClientRecvCallback(boolean is_server, HpnlBuffer buffer, TNonblockingTransport transport) {
    this.is_server = is_server;
    this.buffer = buffer;
    this.transport=transport;
  }
  
  public synchronized void handle(final Connection con, int rdmaBufferId, int blockBufferSize) {
    HpnlBuffer recvBuffer = con.getRecvBuffer(rdmaBufferId);
    ByteBuffer recvByteBuffer = recvBuffer.get(blockBufferSize);
    System.out.println(recvByteBuffer.toString());
    System.out.println("client recv.");

    ByteBuffer onheap=ByteBuffer.allocate(recvByteBuffer.capacity());
    System.out.println(onheap.toString());
    onheap.put(recvByteBuffer);
    Charset charset = Charset.forName("UTF-8");
    System.out.println(new String(onheap.array(),charset));
  }



  TNonblockingTransport transport=null;
  boolean is_server = false;
  private HpnlBuffer buffer;
  private int count = 0;
}
