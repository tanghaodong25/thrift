package org.apache.thrift.user;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.RDMATAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.RDMASocket;
import org.apache.thrift.transport.TNonblockingTransport;

import java.util.Date;


public class Client {
    public static void main(String[] args) throws Exception {
        String ip = "";
        String port = "";
        if (args.length == 0) {
            ip = "localhost";
            port = "9966";
        } else {
            ip = args[0];
            port = args[1];
        }
        double begin=new Date().getTime();
        System.out.println("running");
        TNonblockingTransport transport = null;
        RDMATAsyncClientManager clientManager=null;
         transport = new RDMASocket(ip, port);
         clientManager = new RDMATAsyncClientManager(transport);
        TProtocolFactory tProtocolFactory = new TCompactProtocol.Factory();
        HelloServer.AsyncClient asyncClient = new HelloServer.AsyncClient(tProtocolFactory, clientManager, transport);
        try {
            asyncClient.sayString("zhang", new AsyncMethodCallback<String>() {
                public void onComplete(String s) {
                    System.out.println(s + "  final");
                }

                public void onError(Exception e) {
                    System.out.println("sss");
                }
            });
        } catch (TException e) {
            e.printStackTrace();
        }finally {
//            clientManager.stop();
            transport.close();
        }
        double end=new Date().getTime();
        System.out.println((end-begin)/1000+ "s");
    }
}

