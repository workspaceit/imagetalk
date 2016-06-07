package controller.service;

import model.ReportAppIssueModel;
import model.UserReviewModel;
import model.WallPostModel;

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

            default:
                break;
        }
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

}
