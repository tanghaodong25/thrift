package async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.*;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Comparator;

public class RDMATAsyncClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDMATAsyncClientManager.class.getName());
    private  TTransport tTransport=null;
    private ByteBuffer frameBuffer ;
    private final byte[] sizeBufferArray = new byte[4];
    public void stop() {
        tTransport.close();
    }

    public RDMATAsyncClientManager(TTransport tTransport) {
        this.tTransport=tTransport;
    }

    public boolean isRunning(){
        if (tTransport!=null){
            System.out.println("running");
            return true;
        }
        return false;
    }

    public void call(TAsyncMethodCall method,String param){
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
            frameBuffer= method.getFrameBuffer();
            ((RDMASocket)tTransport).write(frameBuffer);
//            ((RDMASocket)tTransport).write(param.getBytes());
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
