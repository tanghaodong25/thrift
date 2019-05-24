package org.apache.thrift.transport;

import java.io.IOException;
import java.nio.ByteBuffer;


public abstract class RDMATransport extends TNonblockingTransport {

    public abstract int read(ByteBuffer buffer) throws IOException;

    public abstract int write(ByteBuffer buffer) throws IOException;
}
