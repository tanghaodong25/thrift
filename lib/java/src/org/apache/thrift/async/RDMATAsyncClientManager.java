package org.apache.thrift.async;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;

public class RDMATAsyncClientManager<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDMATAsyncClientManager.class.getName());
    private ByteBuffer frameBuffer;
    private final byte[] sizeBufferArray = new byte[4];
    private RDMATAsyncMethodCall method;
    private TNonblockingTransport tTransport;

    public void stop() {
        tTransport.close();
    }

    public RDMATAsyncClientManager(TNonblockingTransport tTransport) {
        this.tTransport = tTransport;
    }

    public boolean isRunning() {
        if (tTransport != null) {
            System.out.println("running");
            return true;
        }
        return false;
    }

    public void call(RDMATAsyncMethodCall method) {
        this.method = method;
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
            ByteBuffer sizeBuffer = method.getSizeBuffer();
            frameBuffer = method.getFrameBuffer();
            (tTransport).write(frameBuffer);
            method.reframeBuffer();
            new CallBackThread().start(method);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
