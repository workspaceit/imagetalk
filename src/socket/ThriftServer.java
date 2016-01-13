package socket;

/**
 * Created by mi on 1/13/16.
 */


import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import socket.thrift_service.ChatTransport;
import socket.thrift_service.handler.ChatTransportHandler;

/**
 *
 * @author mi
 */
public class ThriftServer {
    public static ChatTransportHandler handler;

    public static ChatTransport.Processor processor;
    public final static int port = 9028;
    public static void main(String [] args) {
        try {
            handler = new ChatTransportHandler();
            processor = new ChatTransport.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(ChatTransport.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
//
//         TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(port);
//        TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));


            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
