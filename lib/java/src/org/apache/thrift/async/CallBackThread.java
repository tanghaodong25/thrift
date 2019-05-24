package org.apache.thrift.async;

import java.nio.ByteBuffer;

public class CallBackThread extends Thread {

    public  void start(RDMATAsyncMethodCall methodCall) {
        boolean flag=true;
        while (flag) {
            ByteBuffer tempbuffer = SaveBuffer.popBuffer();
            if (tempbuffer != null) {
                methodCall.setFrameBuffer(tempbuffer);
                try {
                    methodCall.callback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
}
