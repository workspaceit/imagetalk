package controller.service;

import model.*;
import model.datamodel.app.ReportType;
import model.datamodel.app.ReportWallPost;
import model.datamodel.app.WallPost;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Application Name : ImageTalk
 * Package Name     : controller.service
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 4/27/16
 */
public class ReportAppIssueController extends HttpServlet {


    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setCharacterEncoding("utf8");

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

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
            case "/report/issue/add":
                pw.print(this.addReport(req));
                break;
            case "/report/wall-post":
                pw.print(this.addWallPostReport(req));
                break;
            default:
                break;
        }
        baseController.closeDbConnection();
        pw.close();
    }

    public String addReport(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        ReportAppIssueModel reportAppIssueModel = new ReportAppIssueModel();

        if(!baseController.checkParam("report_text", req, true)){

            baseController.serviceResponse.responseStat.msg = "report_text required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        reportAppIssueModel.setReporterId(baseController.appCredential.id);
        reportAppIssueModel.setReportText(req.getParameter("report_text"));

        if(reportAppIssueModel.insert()==0){
            baseController.serviceResponse.responseStat.msg = "Unable to comment on the post,database error";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }


        //================ MAIL ===========================//

        final String username = "pass.reset1479@gmail.com";
        final String password = "reset1479";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                                                  protected PasswordAuthentication getPasswordAuthentication() {
                                                      return new PasswordAuthentication(username, password);
                                                  }
                                              });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("pass.reset1479@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("rafsanhasin@gmail.com"));
            message.setSubject("User Report");
            message.setText("Dear Mr, ,"
                            + "\n\n"+req.getParameter("report_text"));

            Transport.send(message);

            System.out.println("Message Sent successfully !!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }



        baseController.serviceResponse.responseStat.msg = "Successfully Reported";
        return baseController.getResponse();

    }
    public String addWallPostReport(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        WallPostModel wallPostModel = new WallPostModel();
        ReportWallPostModel reportWallPostModel = new ReportWallPostModel();
        ReportTypeModel reportTypeModel = new ReportTypeModel();
        ReportWallPost reportWallPost = new ReportWallPost();

        int reportTypeId=0;
        String description = "";
        int wallPostId=0;


        if(!baseController.checkParam("report_type_id", req, true)){

            baseController.serviceResponse.responseStat.msg = "Report type is missing";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(baseController.checkParam("description", req, true)){

            description = req.getParameter("description").trim();
        }
        if(!baseController.checkParam("wall_post_id", req, true)){

            baseController.serviceResponse.responseStat.msg = "Wall post missing";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }




        try{
            reportTypeId = Integer.parseInt(req.getParameter("report_type_id"));
        }catch (NumberFormatException nfe){
            baseController.serviceResponse.responseStat.msg = "Report type id int required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        try{
            wallPostId = Integer.parseInt(req.getParameter("wall_post_id"));
        }catch (NumberFormatException nfe){
            baseController.serviceResponse.responseStat.msg = "Wall post id int required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }


        ReportType reportType =  reportTypeModel.getById(reportTypeId);

        if(reportType.id==0){
            baseController.serviceResponse.responseStat.msg = "Report type not found";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        wallPostModel.setId(wallPostId);
        WallPost wallPost = wallPostModel.getById();

        if(wallPost.id==0){
            baseController.serviceResponse.responseStat.msg = "Wall post id not found";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        reportWallPost.reportType = reportType;
        reportWallPost.description = description;
        reportWallPost.reporter = baseController.appCredential;
        reportWallPost.wallPost = wallPost;

        reportWallPostModel.insert(reportWallPost);




        baseController.serviceResponse.responseStat.msg = "Successfully Reported";
        return baseController.getResponse();

    }
}
