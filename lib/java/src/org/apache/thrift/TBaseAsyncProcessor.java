package org.apache.thrift;

import java.util.Collections;
import java.util.Map;

import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.*;
import org.apache.thrift.server.RDMATServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TBaseAsyncProcessor<I> implements TAsyncProcessor, TProcessor {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    final I iface;
    final Map<String,AsyncProcessFunction<I, ? extends TBase,?>> processMap;

    public TBaseAsyncProcessor(I iface, Map<String, AsyncProcessFunction<I, ? extends TBase,?>> processMap) {
        this.iface = iface;
        this.processMap = processMap;
    }

    public Map<String,AsyncProcessFunction<I, ? extends TBase,?>> getProcessMapView() {
        return Collections.unmodifiableMap(processMap);
    }

    public void process(final RDMATServer.AsyncFrameBuffer fb) throws TException {
        final TProtocol in = fb.getInputProtocol();
        final TProtocol out = fb.getOutputProtocol();
        //Find processing function
        final TMessage msg = in.readMessageBegin();
        AsyncProcessFunction fn = processMap.get(msg.name);
        if (fn == null) {
            System.out.println("fn==null?");
            TProtocolUtil.skip(in, TType.STRUCT);
            in.readMessageEnd();
            if (!fn.isOneway()) {
              TApplicationException x = new TApplicationException(TApplicationException.UNKNOWN_METHOD, "Invalid method name: '"+""+"'");
              out.writeMessageBegin(new TMessage(msg.name, TMessageType.EXCEPTION, msg.seqid));
              x.write(out);
              out.writeMessageEnd();
              out.getTransport().flush();
            }
            return ;
        }

        //Get Args
        TBase args = fn.getEmptyArgsInstance();
        try {
            args.read(in);
        } catch (Exception e) {
            in.readMessageEnd();
            if (!fn.isOneway()) {
              TApplicationException x = new TApplicationException(TApplicationException.PROTOCOL_ERROR, e.getMessage());
              out.writeMessageBegin(new TMessage(msg.name, TMessageType.EXCEPTION, msg.seqid));
              x.write(out);
              out.writeMessageEnd();
              out.getTransport().flush();
            }
            return ;
        }
        in.readMessageEnd();

        if (fn.isOneway()) {
        }

        //start off processing function
        AsyncMethodCallback resultHandler = fn.getResultHandler(fb, msg.seqid);
        try {
          fn.start(iface, args, resultHandler);
        } catch (Exception e) {
          resultHandler.onError(e);
        }
        return ;
    }

    @Override
    public void process(TProtocol in, TProtocol out) throws TException {
    }
}
