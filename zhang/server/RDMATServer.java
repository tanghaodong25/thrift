package server;


import transport.RDMAServerSocket;
import transport.TNonblockingServerTransport;
import transport.TTransportException;

public class RDMATServer extends TServer {

    public RDMATServer(AbstractServerArgs args) {
        super(args);
    }


    public static class Args extends AbstractServerArgs<RDMATServer.Args> {
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





}
