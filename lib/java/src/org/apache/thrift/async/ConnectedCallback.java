package org.apache.thrift.async;

import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.Handler;
import org.apache.thrift.server.RDMATServer;

import java.util.List;
import java.util.Map;

public class ConnectedCallback  implements Handler {
  public ConnectedCallback(List<Connection> conList, boolean isServer, RDMATServer.Args arggs) {
    this.conList = conList;
    this.isServer = isServer;
  }

  public  ConnectedCallback(List<Connection> conList, boolean isServer){
    this.conList = conList;
    this.isServer = isServer;
  }

  public void handle(Connection con, int rdmaBufferId, int blockBufferSize) {


    this.conList.add(con);
//    map.put(con,buff);

  }
  List<Connection> conList;
  boolean isServer;
  Map<Connection, RDMATServer.FrameBuffer> map;
  RDMATServer.Args arggs=null;
  RDMATServer.FrameBuffer buff=null;
}
