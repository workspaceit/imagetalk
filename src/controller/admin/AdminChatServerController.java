package controller.admin;

import controller.service.ImageTalkBaseController;
import model.datamodel.app.Login;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import socket.ImgTalkServerSocket;
import socket.ServiceThread;
import socket.thrift_service.ChatTransport;
import socket.thrift_service.handler.ChatTransportHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mi on 1/14/16.
 */


public class AdminChatServerController extends HttpServlet{

    private static ServerSocket serverSocket = null;
    private static TServer ts = null;
    private static TServerTransport tsTransport = null;
    private static int thriftServerPort = 9028;
    private static boolean isThriftServerRunning = false;
    private static boolean isChatServerRunning = false;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String url = req.getRequestURI().toString();
        ImageTalkBaseController baseController = new ImageTalkBaseController();

        PrintWriter pw = resp.getWriter();

        if (!baseController.isSessionValid(req)) {
            pw.print(baseController.getResponse());

        }
        if (!baseController.isAdmin(req)) {
            pw.print(baseController.getResponse());

        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/developer/server/access/start/chatserver":
                pw.print(this.startChatPushBackServer(req));
                break;
            case "/developer/server/access/start/thriftserver":
                pw.print(this.startThriftServer(req));
                break;
            case "/developer/server/access/chatserver/running":
                pw.print(this.isChatServerRunning(req));
                break;
            case "/developer/server/access/thriftserver/running":
                pw.print(this.isThriftServerRunning(req));
                break;
            case "/developer/server/access/chatserver/stop":
                pw.print(this.stopChatPushBackServer(req));
                break;
            case "/developer/server/access/thriftserver/stop":
                pw.print(this.stopThriftServer(req));
                break;
            default:
                break;
        }
        pw.close();
    }

    private String stopChatPushBackServer(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(serverSocket == null){

            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Server is stop";
            return baseController.getResponse();
        }else{
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is stopped";
            return baseController.getResponse();
        }
    }
    private String stopThriftServer(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(ts == null){

            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Server is stop";
            return baseController.getResponse();
        }else{
            isThriftServerRunning = false;
            ts.stop();
            tsTransport.close();
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is stopped";
            return baseController.getResponse();
        }
    }

    private String startChatPushBackServer(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(!isChatServerRunning){
            class testRead extends Thread{
                @Override
                public void run() {
                    int postNumber = 9025;
                    boolean runServer =true;
                    int count = 1;

                    try {
                        serverSocket = new ServerSocket(postNumber);
                        isChatServerRunning = true;
                        while(runServer) {
                            System.out.println("Waiting for request");
                            Socket serviceSocket = serverSocket.accept();
                            System.out.println("Request arrived");

                            ServiceThread serviceThread = new ServiceThread(serviceSocket);

                            System.out.println("count :"+count);
                            count++;
                            serviceThread.start();
                        }
                        isChatServerRunning = false;
                        serverSocket.close();
                    } catch (IOException e) {
                        isChatServerRunning = false;
                        e.printStackTrace();
                        try {
                            serverSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }finally {

                    }
                }
            };
            new testRead().start();

            baseController.serviceResponse.responseStat.msg = "Server is running";
        }else{
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Server already running";
        }
        return baseController.getResponse();
    }
    private String startThriftServer(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(!isThriftServerRunning){
            baseController.serviceResponse.responseStat.msg = "Server is running";
            try {
                ChatTransportHandler handler;

                ChatTransport.Processor processor;
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
        }else{
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Server already running";
        }


        return baseController.getResponse();
    }
    private String isThriftServerRunning(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(isThriftServerRunning){
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is running";
        }else{
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Server is Stop";
        }
        return baseController.getResponse();
    }
    private String isChatServerRunning(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(isChatServerRunning){
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is running";
        }else{
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Server is Stop";
        }
        return baseController.getResponse();
    }



    public static void simple(ChatTransport.Processor processor) {
        try {
            tsTransport = new TServerSocket(thriftServerPort);
            ts = new TSimpleServer(new TServer.Args(tsTransport).processor(processor));
//
//         TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(port);
//        TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));


            System.out.println("Starting the simple server...");
            isThriftServerRunning = true;
            ts.serve();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
