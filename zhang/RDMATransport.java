package thrift.bio.transport;



import com.intel.hpnl.core.RdmaBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;


public abstract class RDMATransport extends TTransport {

    public abstract int read(ByteBuffer buffer) throws IOException;

    public abstract int write(ByteBuffer buffer) throws IOException;
}
