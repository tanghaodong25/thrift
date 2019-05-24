package org.apache.thrift.user;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.RDMATAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.RDMASocket;
import org.apache.thrift.transport.TNonblockingTransport;

import java.util.Date;

/**
 * Test multithreading concurrency
 */
public class MainThread {
    public static void main(String[] args) {
        String ip = "";
        String port = "";
        int times=0;
        if (args.length == 0) {
            ip = "localhost";
            port = "9966";
            times=10000;
        } else {
            ip = args[0];
            port = args[1];
            times=new Integer(args[2]);
        }
        double begin=new Date().getTime();
        System.out.println("running");
        for (int i = 0; i < times; i++) {
            new RunThread().start("zhang" + i,ip,port);
        }
        double end=new Date().getTime();
        System.out.println((end-begin)/1000+ "s");
    }
}

class RunThread extends Thread {
    public void start(String param,String ip,String port) {
        TNonblockingTransport transport = null;
        try {
            transport = new RDMASocket(ip,port);
            RDMATAsyncClientManager clientManager = new RDMATAsyncClientManager(transport);
            TProtocolFactory tProtocolFactory = new TCompactProtocol.Factory();
            HelloServer.AsyncClient asyncClient = new HelloServer.AsyncClient(tProtocolFactory, clientManager, transport);
            try {
//                for (int i = 0; i < 5; i++) {
                    asyncClient.sayString(param + "--" , new AsyncMethodCallback<String>() {
                        public void onComplete(String s) {

//                            System.out.println(s + "  final");
                        }

                        public void onError(Exception e) {
                            System.out.println("sss");
                        }
                    });
//                }
            } catch (TException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            transport.close();
        }
    }
}
