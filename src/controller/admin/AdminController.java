package controller.admin;

import controller.service.ImageTalkBaseController;

import helper.ImageHelper;
import model.*;
import model.datamodel.app.Login;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mi on 9/2/15.
 */
public class AdminController extends HttpServlet {
    Login                   login;
    private static int thriftServerPort = 9028;
    private boolean isThriftServerRunning = false;
    private boolean isChatServerRunning = false;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String url = req.getRequestURI().toString();
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        this.login = new Login();
        PrintWriter pw = resp.getWriter();

        if (!baseController.isSessionValid(req)) {
            pw.print(baseController.getResponse());
             
        }
        if (!baseController.isAdmin(req)) {
            pw.print(baseController.getResponse());

        }
        login = baseController.getUserLoginFromSession(req);

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {

            case "/admin/operation/user/add":
                pw.print(this.addUser(req, resp));
                break;
            case "/admin/operation/admin/add":
                pw.print(this.addAdminUser(req, resp));
                break;

            case "/admin/operation/admin_user/delete":
                pw.print(this.deleteAdminUser(req));
                break;

            case "/admin/operation/change/user/status":
                pw.print(this.changeUserStatus(req));
                break;
            case "/admin/operation/upload/image/ajax":
                pw.print(this.uploadImageByAjax(req));
                break;
            case "/admin/operation/start/chatserver":
                pw.print(this.startChatPushBackServer(req));
                break;
            case "/admin/operation/start/thriftserver":
                pw.print(this.startThriftServer(req));
                break;
            case "/admin/operation/chatserver/running":
                pw.print(this.isChatServerRunning(req));
                break;
            case "/admin/operation/thriftserver/running":
                pw.print(this.isThriftServerRunning(req));
                break;
            default:
                break;
        }
        pw.close();
    }

    public String addUser(HttpServletRequest req, HttpServletResponse resp) {
       
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        UserInfModel    userInfModel    = new UserInfModel();

        if (baseController.checkParam("f_name", req, true)) {
            userInfModel.setF_name(req.getParameter("f_name"));
        } else {
            baseController.serviceResponse.responseStat.msg = "First name empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
            
        }

        if (baseController.checkParam("l_name", req, true)) {
            userInfModel.setL_name(req.getParameter("l_name"));
        } else {
            baseController.serviceResponse.responseStat.msg = "Last name empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        if (baseController.checkParam("address", req, true)) {
            userInfModel.setAddress(req.getParameter("address"));
        } else {
            baseController.serviceResponse.responseStat.msg = "Address empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        if (baseController.checkParam("email", req, true)) {
            // String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
            String testString = req.getParameter("email").trim();

            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(testString);

            if (matcher.matches()) {
                adminLoginModel.email = req.getParameter("email");
            } else {
                baseController.serviceResponse.responseStat.msg = "Email is not valid";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
                 
            }


        } else {
            baseController.serviceResponse.responseStat.msg = "Email empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        if (baseController.checkParam("password", req, true)) {
            adminLoginModel.password = req.getParameter("password");
            if (adminLoginModel.password.length() < 6) {
                baseController.serviceResponse.responseStat.msg = "Password at least 6 digit required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
                 
            }
        } else {
            baseController.serviceResponse.responseStat.msg = "Password empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }


        if (adminLoginModel.isEmailExist(adminLoginModel.email)) {
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Email already used";
            return baseController.getResponse();
             
        }

        if (baseController.checkParam("type", req, true)) {
            String type = req.getParameter("type").trim();
            System.out.println("type = " + type);
            if (type.equals("ADMIN")) {
                System.out.println("type = 1 block");
                adminLoginModel.type = 1;
            } else if (type.equals("TEAM_LEAD")) {
                System.out.println("type = 2 block");
                adminLoginModel.type = 2;
            } else {
                System.out.println("type = 3 block");
                adminLoginModel.type = 3;
            }
        } else {
            adminLoginModel.type = 3;
        }


        int userId = userInfModel.insertData();
        if (userId <= 0) {
            userInfModel.deleteData(userId);
            return baseController.getResponse();
        }

        adminLoginModel.u_id = userId;
        int loginId = adminLoginModel.insertData(userId);

        if (loginId <= 0) {
            userInfModel.deleteData(userId);
            return baseController.getResponse();
        }


        baseController.serviceResponse.responseStat.status = true;
        baseController.serviceResponse.responseStat.msg = "Registration Successfully";
        return baseController.getResponse();
         
    }

    public String addAdminUser(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        baseController = new ImageTalkBaseController();
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        UserInfModel    userInfModel    = new UserInfModel();

        if (baseController.checkParam("f_name", req, true)) {
            userInfModel.setF_name(req.getParameter("f_name"));
        } else {
            baseController.serviceResponse.responseStat.msg = "First name empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        if (baseController.checkParam("l_name", req, true)) {
            userInfModel.setL_name(req.getParameter("l_name"));
        } else {
            baseController.serviceResponse.responseStat.msg = "Last name empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        //        if (this.baseController.checkParam("address",req,true)) {
        //            userInfModel.address = req.getParameter("address");
        //        } else {
        //            baseController.serviceResponse.responseStat.msg = "Address empty";
        //            baseController.serviceResponse.responseStat.status = false;
        //            return baseController.getResponse());
        //             
        //        }

        if (baseController.checkParam("email", req, true)) {
            // String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
            String testString = req.getParameter("email").trim();

            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(testString);

            if (matcher.matches()) {
                adminLoginModel.email = req.getParameter("email");
            } else {
                baseController.serviceResponse.responseStat.msg = "Email is not valid";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
                 
            }


        } else {
            baseController.serviceResponse.responseStat.msg = "Email empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        if (baseController.checkParam("password", req, true)) {
            adminLoginModel.password = req.getParameter("password");
            if (adminLoginModel.password.length() < 6) {
                baseController.serviceResponse.responseStat.msg = "Password at least 6 digit required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
                 
            }
        } else {
            baseController.serviceResponse.responseStat.msg = "Password empty";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }


        if (adminLoginModel.isEmailExist(adminLoginModel.email)) {
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Email already used";
            return baseController.getResponse();
             
        }
        // Setting type is Admin
        adminLoginModel.type = 1;


        int userId = userInfModel.insertData();
        if (userId <= 0) {
            userInfModel.deleteData(userId);
            return baseController.getResponse();
        }

        adminLoginModel.u_id = userId;
        int loginId = adminLoginModel.insertData(userId);

        if (loginId <= 0) {
            userInfModel.deleteData(userId);
            return baseController.getResponse();
        }


        baseController.serviceResponse.responseStat.status = true;
        baseController.serviceResponse.responseStat.msg = "Registration Successfully";
        return baseController.getResponse();
         
    }

    private String deleteAdminUser(HttpServletRequest req) {
        int u_id     = 0;
        int login_id = 0;
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if (!baseController.checkParam("u_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "User id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        } else {
            try {
                u_id = Integer.parseInt(req.getParameter("u_id").trim());
            } catch (NumberFormatException e) {
                baseController.serviceResponse.responseStat.msg = "User id not valid format, int required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
                 
            }
        }

        if (!baseController.checkParam("login_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "User id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        } else {
            try {
                login_id = Integer.parseInt(req.getParameter("login_id").trim());
            } catch (NumberFormatException e) {
                baseController.serviceResponse.responseStat.msg = "login id not valid format, int required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
                 
            }
        }

        AdminLoginModel adminLoginModel = new AdminLoginModel();
        UserInfModel    userInfModel    = new UserInfModel();


        adminLoginModel.login.id = login_id;
        userInfModel.setId(u_id);


        if (adminLoginModel.deleteById() <= 0) {
            baseController.serviceResponse.responseStat.msg = "Internal server error at login model";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }
        if (userInfModel.deleteById() <= 0) {
            baseController.serviceResponse.responseStat.msg = "Internal server error at userinf model";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        baseController.serviceResponse.responseStat.msg = "User successfully deleted";
        return baseController.getResponse();
         

    }

    private String changeUserStatus(HttpServletRequest req) {
        AppLoginCredentialModel appModel = new AppLoginCredentialModel();

        ImageTalkBaseController baseController = new ImageTalkBaseController();

        int userId        = Integer.parseInt(req.getParameter("user_id"));
        int status        = Integer.parseInt(req.getParameter("user_status"));
        int currentStatus = appModel.getUserStatusById(userId);

        if (status == currentStatus) {
            String statusName = "Banned";

            if (status == 1) {
                statusName = "Permitted";
            }

            baseController.serviceResponse.responseStat.msg = "This user already " + statusName;
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
             
        }

        appModel.setId(userId);
        appModel.setBanned(status);

        boolean changeStatus = appModel.updateUserStatus();

        if (changeStatus) {
            baseController.serviceResponse.responseStat.msg = "User Status Changed Successfully";
            baseController.serviceResponse.responseStat.status = true;
        } else {
            baseController.serviceResponse.responseStat.msg = "User Status Changed Failed";
            baseController.serviceResponse.responseStat.status = false;
        }

        return baseController.getResponse();
         
    }

    private String uploadImageByAjax(HttpServletRequest req) {
        boolean isMultipart;
        String  saveDir     = ImageHelper.getStickerFolder();
//        String  filePath    = "/home/touch/Projects/j2ee/" + saveDir;
        String  filePath    = ImageHelper.getStickerGlobalPath() + saveDir;
        ImageTalkBaseController baseController = new ImageTalkBaseController();

        int     maxFileSize = 250 * 1024 * 1024;
        int     maxMemSize  = 10 * 1024 * 1024;
        File    file;

        isMultipart = ServletFileUpload.isMultipartContent(req);

        if (!isMultipart) {
            return "{\"isComplete\":false, \"isSuccess\":false}";
             
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(maxMemSize);
        factory.setRepository(new File(filePath));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(maxFileSize);

        try {
            File fileSaveDir = new File(filePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdir();
            }

            List fileItems = upload.parseRequest(req);
            Iterator i = fileItems.iterator();
            /*List<FileItem> fl = upload.parseRequest(req);

            for (FileItem item : fl) {
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                    String fieldname = item.getFieldName();
                    String fieldvalue = item.getString();
                } else {

                }
            }*/
            while (i.hasNext()) {
                String tempName = saveDir;
                FileItem fi = (FileItem) i.next();
                String fieldName1 = fi.getFieldName();
                String fieldValue1 = fi.getString();
                if (!fi.isFormField()) {
                    // Get the uploaded file parameters
                    String fieldName = fi.getFieldName();
                    String fileName = fi.getName();
                    String contentType = fi.getContentType();
                    boolean isInMemory = fi.isInMemory();
                    long sizeInBytes = fi.getSize();

                    if (sizeInBytes > maxFileSize || contentType == "image/gif") {
                        fi.delete();
                        return "{\"isComplete\":false, \"isSuccess\":false}";

                    }

                    if (fileName.lastIndexOf("\\") >= 0) {
                        tempName = Instant.now().getEpochSecond() + "_" + fileName.substring(fileName.lastIndexOf("\\"));
                        file = new File(filePath + tempName);
                    } else {
                        tempName = Instant.now().getEpochSecond() + "_" + fileName.substring(fileName.lastIndexOf("\\") + 1);
                        file = new File(filePath + tempName);
                    }

                    fi.write(file);
                    return "{\"path\":\"" + saveDir + tempName + "\",\"isComplete\":true, \"isSuccess\":true, \"hasErrors\":false, \"hasWarnings\":false}";
                     
                } else {
                    String fieldName = fi.getFieldName();
                    String fieldValue = fi.getString();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
            return "{\"isComplete\":false, \"isSuccess\":false}";
        }
        return "{\"isComplete\":false, \"isSuccess\":false}";
    }

    private String startChatPushBackServer(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(!isChatServerRunning){
            class testRead extends Thread{

                @Override
                public void run() {

                    ImgTalkServerSocket imgTalkServerSocket = new ImgTalkServerSocket();
                    imgTalkServerSocket.startServer();
                }
            };
            new testRead().start();
        }else{

        }
        return baseController.getResponse();
    }
    private String startThriftServer(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(!isThriftServerRunning){

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


        return baseController.getResponse();
    }
    private String isThriftServerRunning(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(isThriftServerRunning){
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is running";
        }else{
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is not running";
        }
        return baseController.getResponse();
    }
    private String isChatServerRunning(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if(isChatServerRunning){
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is running";
        }else{
            baseController.serviceResponse.responseStat.status = true;
            baseController.serviceResponse.responseStat.msg = "Server is not running";
        }
        return baseController.getResponse();
    }



    public static void simple(ChatTransport.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(thriftServerPort);
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
}
