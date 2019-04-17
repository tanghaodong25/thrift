package org.apache.thrift.server;


import org.apache.thrift.TAsyncProcessor;
import org.apache.thrift.TByteArrayOutputStream;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.Charset;

public class RDMATServer extends  TServer{

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    public RDMATServer(AbstractServerArgs args) {
        super(args);
    }


    public static class Args extends AbstractServerArgs<Args> {
        public Args(TNonblockingServerTransport transport) {
            super(transport);
        }
    }

    @Override
    public void serve() {

        try {
            serverTransport_.listen();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        System.out.println("serve");
        ((RDMAServerSocket)serverTransport_).waitshutdown();
        serverTransport_.close();
    }

    public class FrameBuffer {
        private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

        // the actual transport hooked up to the client.
        protected final TNonblockingTransport trans_;

        // the SelectionKey that corresponds to our transport
        protected final SelectionKey selectionKey_;

        // the SelectThread that owns the registration of our transport
        protected final AbstractNonblockingServer.AbstractSelectThread selectThread_;

        // where in the process of reading/writing are we?

        // the ByteBuffer we'll be using to write and read, depending on the state
        protected ByteBuffer buffer_;

        protected final TByteArrayOutputStream response_;

        // the frame that the TTransport should wrap.
        protected final TMemoryInputTransport frameTrans_;

        // the transport that should be used to connect to clients
        protected final TTransport inTrans_;

        protected final TTransport outTrans_;

        // the input protocol to use on frames
        protected final TProtocol inProt_;

        // the output protocol to use on frames
        protected final TProtocol outProt_;

        // context associated with this connection
        protected final ServerContext context_;

        public FrameBuffer(final TNonblockingTransport trans,
                           final SelectionKey selectionKey,
                           final AbstractNonblockingServer.AbstractSelectThread selectThread) {
            trans_ = trans;
            selectionKey_ = selectionKey;
            selectThread_ = selectThread;
            buffer_ = ByteBuffer.allocate(4);

            frameTrans_ = new TMemoryInputTransport();
            response_ = new TByteArrayOutputStream();
            inTrans_ = inputTransportFactory_.getTransport(frameTrans_);
            outTrans_ = outputTransportFactory_.getTransport(new TIOStreamTransport(response_));
            inProt_ = inputProtocolFactory_.getProtocol(inTrans_);
            outProt_ = outputProtocolFactory_.getProtocol(outTrans_);

            if (eventHandler_ != null) {

                context_ = eventHandler_.createContext(inProt_, outProt_);
            } else {
                context_  = null;
            }
        }

        public FrameBuffer(){
            trans_=null;
            selectionKey_ = null;
            selectThread_ = null;

            frameTrans_ = new TMemoryInputTransport();
            response_ = new TByteArrayOutputStream();
            inTrans_ = inputTransportFactory_.getTransport(frameTrans_);
            outTrans_ = outputTransportFactory_.getTransport(new TIOStreamTransport(response_));
            inProt_ = inputProtocolFactory_.getProtocol(inTrans_);
            outProt_ = outputProtocolFactory_.getProtocol(outTrans_);
            if (eventHandler_ != null) {

                context_ = eventHandler_.createContext(inProt_, outProt_);
            } else {
                context_  = null;
            }
        }

        public void apendBuffer_(ByteBuffer buffer){
            buffer_.put(buffer);
            System.out.println(buffer_.toString());
            Charset charset = Charset.forName("UTF-8");
            System.out.println(new String(buffer_.array(),charset));
        }

        public void initalBuffer(int framesize){
            buffer_=ByteBuffer.allocate(framesize+4);
            buffer_.putInt(framesize);
            System.out.println(buffer_.toString()+" inital");
        }

        private boolean internalRead() {
            try {
                if (trans_.read(buffer_) < 0) {
                    return false;
                }
                return true;
            } catch (IOException e) {
                LOGGER.warn("Got an IOException in internalRead!", e);
                return false;
            }
        }
        public  ByteBuffer getBuffer_(){
            return buffer_;
}

        public void responseReady(){
            buffer_ = ByteBuffer.wrap(response_.get(), 0, response_.len());
        }
    }

    public class AsyncFrameBuffer extends FrameBuffer {
        public AsyncFrameBuffer(TNonblockingTransport trans, SelectionKey selectionKey, AbstractNonblockingServer.AbstractSelectThread selectThread) {
            super(trans, selectionKey, selectThread);
        }

        public AsyncFrameBuffer() {
            super();
        }

        public TProtocol getInputProtocol() {
            return  inProt_;
        }

        public TProtocol getOutputProtocol() {
            return outProt_;
        }

        public void invoke() {
            frameTrans_.reset(buffer_.array());
            response_.reset();
            System.out.println(buffer_.toString());
            try {
                if (eventHandler_ != null) {
                    eventHandler_.processContext(context_, inTrans_, outTrans_);
                }
                ((TAsyncProcessor)processorFactory_.getProcessor(inTrans_)).process(this);
                Charset charset = Charset.forName("UTF-8");
                System.out.println(new String(buffer_.array(),charset)+"   after inovke");
                System.out.println(buffer_.toString());
                return;
            } catch (TException te) {
                LOGGER.warn("Exception while invoking!", te);
            } catch (Throwable t) {
                LOGGER.error("Unexpected throwable while invoking!", t);
            }
        }
    }
}
