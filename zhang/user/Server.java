package thrift.bio.user;

//import org.apache.thrift.server.TServer;
//import org.apache.thrift.server.TSimpleServer;
//import org.apache.thrift.transport.TServerSocket;
//import org.apache.thrift.transport.TTransportException;
import thrift.bio.server.TServer;
import thrift.bio.server.TSimpleServer;
import thrift.bio.transport.RDMAServerSocket;
import thrift.bio.transport.TServerSocket;
import thrift.bio.transport.TServerTransport;
import thrift.bio.transport.TTransportException;

public class Server {
    public static void main(String[] args) {
        HelloServiceImp helloServiceImp = new HelloServiceImp();
        HelloService.Processor processor = new HelloService.Processor(helloServiceImp);
        try {
            RDMAServerSocket rdmaServerSocket = new RDMAServerSocket("127.0.0.1","7788");
            TServer.Args tArgs=new TServer.Args(rdmaServerSocket);
            tArgs.processor(processor);
            TServer server=new TSimpleServer(tArgs);
//            TServer server=new TSimpleServer(new TServer.Args(tServerSocket).processor(processor));
            System.out.println("server start");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
