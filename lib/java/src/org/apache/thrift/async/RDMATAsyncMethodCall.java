package org.apache.thrift.async;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TNonblockingTransport;
import java.nio.ByteBuffer;

public class RDMATAsyncMethodCall<T> {

    private static final int INITIAL_MEMORY_BUFFER_SIZE = 128;
    private final TNonblockingTransport transport;
    private final AsyncMethodCallback<T> callback;
    private final TProtocolFactory protocolFactory;
    protected final RDMATAsyncClient client;
    private ByteBuffer frameBuffer;
    private final byte[] sizeBufferArray = new byte[4];
    private ByteBuffer sizeBuffer;

    protected RDMATAsyncMethodCall(RDMATAsyncClient client, TProtocolFactory protocolFactory, TNonblockingTransport transport, AsyncMethodCallback<T> callback, boolean isOneway) {
        this.transport = transport;
        this.callback = callback;
        this.protocolFactory = protocolFactory;
        this.client = client;
    }


    protected void prepareMethodCall() throws TException {
        TMemoryBuffer memoryBuffer = new TMemoryBuffer(INITIAL_MEMORY_BUFFER_SIZE);
        TProtocol protocol = protocolFactory.getProtocol(memoryBuffer);
        write_args(protocol);
        int length = memoryBuffer.length();
        frameBuffer = ByteBuffer.wrap(memoryBuffer.getArray(), 0, length);
        TFramedTransport.encodeFrameSize(length, sizeBufferArray);
        sizeBuffer = ByteBuffer.wrap(sizeBufferArray);
    }

    protected void write_args(TProtocol protocol) throws TException {

    }

    public void reframeBuffer() {
        frameBuffer = ByteBuffer.allocate(TFramedTransport.decodeFrameSize(sizeBufferArray));
    }

    protected ByteBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public ByteBuffer getSizeBuffer() {
        return sizeBuffer;
    }

    public void setFrameBuffer(ByteBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    public void callback() throws Exception {
        T result = this.getResult();
        callback.onComplete(result);
    }

    protected T getResult() throws Exception {
        return null;
    }

}
