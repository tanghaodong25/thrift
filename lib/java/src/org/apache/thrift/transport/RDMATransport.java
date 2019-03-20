package thrift.bio.transport;


import com.intel.hpnl.core.RdmaBuffer;

import java.io.IOException;


public abstract class RDMATransport extends TTransport {

    public abstract int read(RdmaBuffer buffer) throws IOException;

    public abstract int write(RdmaBuffer buffer) throws IOException;
}
