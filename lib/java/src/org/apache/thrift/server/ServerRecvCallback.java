package org.apache.thrift.server;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.EqService;
import com.intel.hpnl.core.Handler;
import com.intel.hpnl.core.RdmaBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ServerRecvCallback implements Handler {
  public ServerRecvCallback(EqService eqService, boolean is_server, RDMATServer.Args tnbargs) {
    this.is_server = is_server;
    this.eqService = eqService;
    this.arggs=tnbargs;
    RDMATServer server=new RDMATServer(arggs);
    buff=server.new AsyncFrameBuffer();
  }
  public synchronized void handle(Connection con, int rdmaBufferId, int blockBufferSize) {
    RdmaBuffer sendBuffer=con.takeSendBuffer(true);

    RdmaBuffer rdmaBuffer=con.getRecvBuffer(rdmaBufferId);
    ByteBuffer recvByteBuffer = rdmaBuffer.get(blockBufferSize);
    RDMATServer.FrameBuffer frameBuffer=null;
    int framesize=0;
//    buf.put(recvByteBuffer);
    if (map.get(con)==null){
      map.put(con,buff);
      framesize=recvByteBuffer.getInt(0);
      frameBuffer=buff;
      frameBuffer.initalBuffer(framesize);
      System.out.println(framesize +"    "+ recvByteBuffer.toString());
    }else {
      frameBuffer=buff;
      System.out.println(framesize+"   **");
      frameBuffer.apendBuffer_(recvByteBuffer);
      ((RDMATServer.AsyncFrameBuffer) frameBuffer).invoke();
      ByteBuffer b=frameBuffer.getBuffer_();
      Charset charset = Charset.forName("UTF-8");
      System.out.println(new String(b.array(),charset));
      sendBuffer.put(b, (byte)0, 10);
      con.send(sendBuffer.getRawBuffer().remaining(), sendBuffer.getRdmaBufferId());
    }

////    RDMATServer.FrameBuffer buff=map.get(con);
//    System.out.println(recvByteBuffer.toString());
  }

  static  int i=1;
  RDMATServer.FrameBuffer buff=null;
  RDMATServer.Args arggs;
  private boolean is_server = false;
  private EqService eqService;
  private RdmaBuffer buffer;
  private RdmaBuffer buf;
  private int count = 0;
  private Connection connection = null;
  Map<Connection, RDMATServer.FrameBuffer> map=new HashMap<>();
}
