package org.apache.thrift.user;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.RDMATAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.RDMASocket;
import org.apache.thrift.transport.TNonblockingTransport;

/**
 * Test multithreading concurrency
 *
 */
public class MainThread {
    public static void main(String[] args) {
        for (int i = 0; i <5 ; i++) {
            new RunThread().start("zhang"+i);
        }
    }
}

class RunThread extends Thread{
    public void start(String param){
        try {
            TNonblockingTransport transport = new RDMASocket("localhost", "9966");
            RDMATAsyncClientManager clientManager = new RDMATAsyncClientManager(transport);
            TProtocolFactory tProtocolFactory = new TCompactProtocol.Factory();
            HelloServer.AsyncClient asyncClient = new HelloServer.AsyncClient(tProtocolFactory, clientManager, transport);
            try {
                for (int i = 0; i < 5; i++) {
                    asyncClient.sayString(param+"--"+i, new AsyncMethodCallback<String>() {
                        public void onComplete(String s) {
                            System.out.println(s + "  final");
                        }

                        public void onError(Exception e) {
                            System.out.println("sss");
                        }
                    });
                }
            } catch (TException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
