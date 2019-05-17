package org.apache.thrift.async;


import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;

public class RDMATAsyncClient {
    protected final TProtocolFactory ___protocolFactory;
    protected final TNonblockingTransport ___transport;
    protected final RDMATAsyncClientManager ___manager;
    private long ___timeout;

    public RDMATAsyncClient(TProtocolFactory ___protocolFactory, RDMATAsyncClientManager ___manager, TNonblockingTransport ___transport) {
        this.___protocolFactory = ___protocolFactory;
        this.___transport = ___transport;
        this.___manager = ___manager;
        this.___timeout = 0;
    }

    protected void onComplete() {
    }

    /**
     * Called by delegate method on error
     */
    protected void onError(Exception exception) {
    }

    public TProtocolFactory getProtocolFactory() {
        return ___protocolFactory;
    }
}
