package org.apache.thrift.user;


import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.RDMATServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.RDMAServerSocket;
import org.apache.thrift.transport.TFramedTransport;

public class Server {
    public static void main(String[] args) {
        String ip = "";
        String port = "";
        if (args.length == 0) {
            ip = "localhost";
            port = "9966";
        } else {
            ip = args[0];
            port = args[1];
        }
        try {
            RDMAServerSocket rdmaServerSocket = new RDMAServerSocket(ip, port);
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
