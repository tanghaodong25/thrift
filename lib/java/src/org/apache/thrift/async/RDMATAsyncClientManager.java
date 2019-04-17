package org.apache.thrift.async;

import org.apache.thrift.TException;
import org.apache.thrift.transport.RDMASocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Comparator;

public class RDMATAsyncClientManager extends TAsyncClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDMATAsyncClientManager.class.getName());
    private ByteBuffer frameBuffer ;
    private final byte[] sizeBufferArray = new byte[4];

    public RDMATAsyncClientManager() throws IOException {
        super();
    }

    public void stop() {
        tTransport.close();
    }
    private  TAsyncMethodCall method;
    public RDMATAsyncClientManager(TNonblockingTransport tTransport) {
        super(tTransport);
    }

    public boolean isRunning(){
        if (tTransport!=null){
            System.out.println("running");
            return true;
        }
        return false;
    }

    public void call(TAsyncMethodCall method){
        this.method=method;
        if (!isRunning()) {
            try {
                throw new Exception("RDMASocket is not running");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            tTransport.open();

            method.prepareMethodCall();
            ByteBuffer sizeBuffer=method.getSizeBuffer();

            frameBuffer= method.getFrameBuffer();
            (tTransport).write(sizeBuffer);
            Thread.sleep(3000);

            ((RDMASocket)tTransport).write(frameBuffer);



        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static class TAsyncMethodCallTimeoutComparator implements Comparator<TAsyncMethodCall>, Serializable {
        public int compare(TAsyncMethodCall left, TAsyncMethodCall right) {
            if (left.getTimeoutTimestamp() == right.getTimeoutTimestamp()) {
                return (int)(left.getSequenceId() - right.getSequenceId());
            } else {
                return (int)(left.getTimeoutTimestamp() - right.getTimeoutTimestamp());
            }
        }
    }


}
