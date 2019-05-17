package org.apache.thrift.user;


import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.RDMATServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.RDMAServerSocket;
import org.apache.thrift.transport.TFramedTransport;

public class Server {
    public static void main(String[] args) {
        try {
            RDMAServerSocket rdmaServerSocket = new RDMAServerSocket("9966");
            TProcessor processor = new HelloServer.AsyncProcessor<HelloServer.AsyncIface>(new HelloServerImp());
            RDMATServer.Args tnbargs = new RDMATServer.Args(rdmaServerSocket);
            tnbargs.processor(processor);
            tnbargs.transportFactory(new TFramedTransport.Factory());
            tnbargs.protocolFactory(new TCompactProtocol.Factory());
            rdmaServerSocket.setServer(tnbargs);
            TServer server = new RDMATServer(tnbargs);
            System.out.println("server starting....");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
