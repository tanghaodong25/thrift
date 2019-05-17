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

public class RDMATServer extends TServer {

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
        ((RDMAServerSocket) serverTransport_).waitshutdown();
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
                context_ = null;
            }
        }

        public FrameBuffer() {
            trans_ = null;
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
                context_ = null;
            }
        }

        public void apendBuffer_(ByteBuffer buffer) {
            buffer_.put(buffer);
            buffer_.flip();
        }

        public void initalBuffer(int framesize) {
            buffer_ = ByteBuffer.allocate(framesize + 4);
            buffer_.putInt(framesize);
        }

        /**
         * Give this FrameBuffer a chance to read. The selector loop should have
         * received a read event for this FrameBuffer.
         *
         * @return true if the connection should live on, false if it should be
         *         closed
         */

        /**
         * Give this FrameBuffer a chance to write its output to the final client.
         */

        /**
         * Give this FrameBuffer a chance to set its interest to write, once data
         * has come in.
         */

        /**
         * Shut the connection down.
         */

        /**
         * After the processor has processed the invocation, whatever thread is
         * managing invocations should call this method on this FrameBuffer so we
         * know it's time to start trying to write again. Also, if it turns out that
         * there actually isn't any data in the response buffer, we'll skip trying
         * to write and instead go back to reading.
         */

        /**
         * Actually invoke the method signified by this FrameBuffer.
         */

        /**
         * Perform a read into buffer.
         *
         * @return true if the read succeeded, false if there was an error or the
         * connection closed.
         */
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

        public ByteBuffer getBuffer_() {
            return buffer_;
        }

        public void responseReady() {
            buffer_ = ByteBuffer.wrap(response_.get(), 0, response_.len());
        }

        /**
         * We're done writing, so reset our interest ops and change state
         * accordingly.
         */

        /**
         * When this FrameBuffer needs to change its select interests and execution
         * might not be in its select thread, then this method will make sure the
         * interest change gets done when the select thread wakes back up. When the
         * current thread is this FrameBuffer's select thread, then it just does the
         * interest change immediately.
         */
    } // FrameBuffer

    public class AsyncFrameBuffer extends FrameBuffer {
        public AsyncFrameBuffer(TNonblockingTransport trans, SelectionKey selectionKey, AbstractNonblockingServer.AbstractSelectThread selectThread) {
            super(trans, selectionKey, selectThread);
        }

        public AsyncFrameBuffer() {
            super();
        }

        public TProtocol getInputProtocol() {
            return inProt_;
        }

        public TProtocol getOutputProtocol() {
            return outProt_;
        }

        public void invoke() {
            frameTrans_.reset(buffer_.array());
            response_.reset();
            try {
                if (eventHandler_ != null) {
                    eventHandler_.processContext(context_, inTrans_, outTrans_);
                }
                ((TAsyncProcessor) processorFactory_.getProcessor(inTrans_)).process(this);
                return;
            } catch (TException te) {
                LOGGER.warn("Exception while invoking!", te);
            } catch (Throwable t) {
                LOGGER.error("Unexpected throwable while invoking!", t);
            }
            // This will only be reached when there is a throwable.
        }
    }


}
