package controller.service;

import model.*;

import model.datamodel.app.Stickers;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import socket.ImgTalkServerSocket;
import socket.thrift_service.ChatTransport;
import socket.thrift_service.handler.ChatTransportHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
/**
 * Created by mi on 10/2/15.
 */

/**
 * Created by mi on 10/8/15.
 */
public class TestController extends  HttpServlet{

/*    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;*/

    @Override
    public void init() throws ServletException {
        super.init();
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        class testRead extends Thread{

            @Override
            public void run() {

                ImgTalkServerSocket imgTalkServerSocket = new ImgTalkServerSocket();
                imgTalkServerSocket.startServer();
            }
        };
        new testRead().start();

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

    }
    public static void simple(ChatTransport.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9028);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
//
//         TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(port);
//        TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));


            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        PrintWriter pw = res.getWriter();

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/app/test/dbmodel":
                pw.print(this.test(req));
                break;
            case "/app/test/test":
                pw.print(this.test(req));
                break;
            default:
                break;
        }
        pw.close();
    }
    private String testSticker(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//Here you say to java the initial timezone. This is the secret
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//Will print in UTC
        System.out.println(dateFormat.format(new Date()));

//Here you set to your timezone
        dateFormat.setTimeZone(TimeZone.getDefault());
//Will print on your default Timezone
        System.out.println(dateFormat.format(new Date()));
        return "";
    }
    private String test(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        StickersModel stickersModel = new StickersModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                stickersModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            stickersModel.limit = 30;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                stickersModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        stickersModel.setCurrentAppCredentialId(baseController.appCredential.id);
        ArrayList<Stickers> stickers = stickersModel.getAllForPost();

        baseController.serviceResponse.responseData = stickers;
        baseController.serviceResponse.responseStat.status =  (stickers.size()>0);
        baseController.serviceResponse.responseStat.msg = (stickers.size()==0)?"No record found":"";
        return baseController.getResponse();
    }
}
