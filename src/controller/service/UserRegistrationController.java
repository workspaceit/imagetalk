/**
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 8/21/15
 */

package controller.service;

import model.AdminLoginModel;
import model.UserInfModel;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegistrationController extends HttpServlet {
    private UserInfModel           userInfModel   = null;
    private AdminLoginModel adminLoginModel = null;
    private ImageTalkBaseController baseController = null;
    private PrintWriter            out            = null;

    @Override
    public void init() throws ServletException {
        userInfModel = new UserInfModel();
        adminLoginModel = new AdminLoginModel();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        this.baseController = new ImageTalkBaseController();
        this.out = resp.getWriter();
        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/registration/add/user":
                this.doRegistration(req);
                break;
            case "/registration/active/user":
                this.doRegistration(req);
                break;
            default:
                break;
        }
        this.out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        this.baseController = new ImageTalkBaseController();
        this.out = resp.getWriter();
        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        this.activateUserAccount(req);
        this.out.close();
    }

    private void activateUserAccount(HttpServletRequest req){
        String activation_code = req.getParameter("activation_code");
        System.out.println("activation_code "+activation_code);
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        adminLoginModel.login.activation_code = activation_code;
        String resp="";
        if(adminLoginModel.updateActiveToActivated()){
            resp = "Account is activated";
        }else{
            resp = "Error 404";
        }
        this.out.print(resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private void doRegistration(HttpServletRequest req){

        if (this.baseController.checkParam( "email",req, true)) {
            // String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
            String testString = req.getParameter("email").trim();

            String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(testString);

            if(matcher.matches()){
                this.adminLoginModel.email = req.getParameter("email");
            }else{
                this.baseController.serviceResponse.responseStat.msg = "Email is not valid";
                this.baseController.serviceResponse.responseStat.status = false;
                this.out.print(this.baseController.getResponse());
                return;
            }


        } else {
            this.baseController.serviceResponse.responseStat.msg = "Email empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.out.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("password",req,true)) {
            this.adminLoginModel.password = req.getParameter("password");
            if(this.adminLoginModel.password.length() < 6){
                this.baseController.serviceResponse.responseStat.msg = "Password at least 6 digit required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.out.print(this.baseController.getResponse());
                return;
            }
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Password empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.out.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("f_name",req,true)) {
            this.userInfModel.setF_name(req.getParameter("f_name"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "First name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.out.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("l_name",req,true)) {
            this.userInfModel.setL_name(req.getParameter("l_name"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Last name empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.out.print(this.baseController.getResponse());
            return;
        }

        if (this.baseController.checkParam("address",req,true)) {
            this.userInfModel.setAddress(req.getParameter("address"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Address empty";
            this.baseController.serviceResponse.responseStat.status = false;
            this.out.print(this.baseController.getResponse());
            return;
        }


        if(adminLoginModel.isEmailExist(adminLoginModel.email)){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Email already used";
            this.out.println(this.baseController.getResponse());
            return;
        }

        this.adminLoginModel.type = 3;
        int userId = userInfModel.insertData();
        if (!this.checkId(userId)) {
            this.userInfModel.deleteData(userId);
            this.out.println(this.baseController.getResponse());
            return;
        }

        this.adminLoginModel.u_id = userId;
        int loginId = adminLoginModel.insertData(userId);

        if (!this.checkId(loginId)) {
            this.userInfModel.deleteData(userId);
            this.out.println(this.baseController.getResponse());
            return;
        }





        String activationToken = adminLoginModel.getActivationCodeByEmail(this.adminLoginModel.email);

        if(activationToken == null){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Internal server error while retiving activation token";
            this.out.println(this.baseController.getResponse());
            return;
        }
        if(!this.sendRegistrationMail(this.adminLoginModel.email,activationToken,this.baseController.getBaseUrl(req))){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Unable to send activation mail";
            this.out.println(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.status = true;
        this.baseController.serviceResponse.responseStat.msg = "Registration Successfully";
        this.out.println(this.baseController.getResponse());
        return;
    }

    private boolean checkParam(HttpServletRequest req, String paramName) {
        boolean status = true;

        if (req.getParameter(paramName) == null) {
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = paramName + " Required";
            status = false;
        }

        return status;
    }

    private boolean sendRegistrationMail(String email,String activationCode,String baseUrl){
        AdminLoginModel adminLoginModel = new AdminLoginModel();


        String to = email;
        String from = "cabguardpro@workspaceit.com";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);
        baseUrl+="registration/active/user?activation_code="+activationCode;

        String link = "<a href='"+baseUrl+"'>Click here</a>";
        try{

            MimeMessage message = new MimeMessage(session);

            message.setHeader("Content-Type", "text/html");
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            message.setSubject("Activate your account");
            message.setText("Hi,<br>   " + link + " to activate your account",null,"html");
            Transport.send(message);
            String title = "";
            String body = "";


        }catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }


        return true;
    }

    private boolean checkId(int id) {
        if (id > 0) {
            return true;
        }

        this.baseController.serviceResponse.responseStat.status = false;
        this.baseController.serviceResponse.responseStat.msg = "Registration Failed";

        return false;
    }
}