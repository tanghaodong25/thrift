package user;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import async.AsyncMethodCallback;
import async.RDMATAsyncClientManager;
import async.TAsyncClientManager;
import protocol.TCompactProtocol;
import protocol.TProtocolFactory;
import transport.RDMASocket;
import transport.TException;
import transport.TNonblockingSocket;
import transport.TNonblockingTransport;

public class Client {
    public static void main(String[] args) {
        try {
//            TNonblockingTransport transport=new TNonblockingSocket("localhost",9966);
            TNonblockingTransport transport =new RDMASocket("localhost","9966");
            RDMATAsyncClientManager clientManager=new RDMATAsyncClientManager(transport);
            TProtocolFactory tProtocolFactory=new TCompactProtocol.Factory();
            HelloServer.AsyncClient asyncClient=new HelloServer.AsyncClient(tProtocolFactory,clientManager,transport);

            try {
                asyncClient.sayString("zhang", new AsyncMethodCallback<String>() {
                  public void a(){

                  }
                    public void onComplete(String s) {
                        System.out.println(s);
                    }

                    public void onError(Exception e) {
                        System.out.println("sss");
                    }
                });
            } catch (TException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
