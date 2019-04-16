package user;

import processor.TProcessor;
import protocol.TCompactProtocol;
import server.RDMATServer;
import server.TServer;
import transport.RDMAServerSocket;
import transport.TFramedTransport;

public class Server {
    public static void main(String[] args) {
        try {
            TProcessor processor = new HelloServer.AsyncProcessor<HelloServer.AsyncIface>(new HelloServerImp());
//            TNonblockingServerSocket tNonblockingServerSocket = new TNonblockingServerSocket(9966);
//            TNonblockingServer.Args tnbargs = new TNonblockingServer.Args(tNonblockingServerSocket);
            RDMAServerSocket rdmaServerSocket = new RDMAServerSocket("9966");
            RDMATServer.Args tnbargs=new RDMATServer.Args(rdmaServerSocket);
            tnbargs.processor(processor);
            tnbargs.transportFactory(new TFramedTransport.Factory());
            tnbargs.protocolFactory(new TCompactProtocol.Factory());
            rdmaServerSocket.setServer(tnbargs);
//            TServer server = new TNonblockingServer(tnbargs);
            TServer server = new RDMATServer(tnbargs);
            System.out.println("server starting....");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
