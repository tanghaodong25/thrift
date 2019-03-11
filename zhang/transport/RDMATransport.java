package thrift.bio.transport;

import com.intel.hpnl.core.Buffer;

import java.io.IOException;


public abstract class RDMATransport extends TTransport {

    public abstract int read(Buffer buffer) throws IOException;

    public abstract int write(Buffer buffer) throws IOException;
}
