package org.apache.thrift.async;


import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;

public abstract class TAsyncClient {
    protected final TProtocolFactory ___protocolFactory;
    protected final TNonblockingTransport ___transport;
    protected final TAsyncClientManager ___manager;
    protected TAsyncMethodCall ___currentMethod;
    private Exception ___error;
    private long ___timeout;

    public TAsyncClient(TProtocolFactory protocolFactory, TAsyncClientManager manager, TNonblockingTransport transport) {
        this(protocolFactory, manager, transport, 0);
    }

    public TAsyncClient(TProtocolFactory protocolFactory, TAsyncClientManager manager, TNonblockingTransport transport, long timeout) {
        this.___protocolFactory = protocolFactory;
        this.___manager = manager;
        this.___transport = transport;
        this.___timeout = timeout;
    }

    public TProtocolFactory getProtocolFactory() {
        return ___protocolFactory;
    }

    public long getTimeout() {
        return ___timeout;
    }

    public boolean hasTimeout() {
        return ___timeout > 0;
    }

    public void setTimeout(long timeout) {
        this.___timeout = timeout;
    }

    /**
     * Is the client in an error state?
     *
     * @return If client in an error state?
     */
    public boolean hasError() {
        return ___error != null;
    }

    /**
     * Get the client's error - returns null if no error
     *
     * @return Get the client's error. <p> returns null if no error
     */
    public Exception getError() {
        return ___error;
    }

    protected void checkReady() {
        // Ensure we are not currently executing a method
        if (___currentMethod != null) {
            throw new IllegalStateException("Client is currently executing another method: " + ___currentMethod.getClass().getName());
        }

        // Ensure we're not in an error state
        if (___error != null) {
            throw new IllegalStateException("Client has an error!", ___error);
        }
    }

    /**
     * Called by delegate method when finished
     */
    protected void onComplete() {
        ___currentMethod = null;
    }

    /**
     * Called by delegate method on error
     */
    protected void onError(Exception exception) {
        ___transport.close();
        ___currentMethod = null;
        ___error = exception;
    }
}
