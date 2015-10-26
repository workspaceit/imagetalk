package controller.service;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.ActivationModel;
import model.AppLoginCredentialModel;
import model.UserInfModel;
import model.datamodel.app.AuthCredential;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mi on 10/1/15.
 */
public class AppUserRegistration extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        this.req = req;
        this.res = res;
        res.setContentType("application/json");
        this.baseController = new ImageTalkBaseController();
        this.pw = res.getWriter();

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/app/register/number":
                this.initializePhoneNumber();
                break;
            case "/app/register/user":
                this.registerUser();
                break;
            case "/app/register/verifytoken":
                this.verifyToken();
                break;
            case "/app/register/test":
                this.test();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    public boolean sendTokenViaSms(String phoneNumber,String token){
        // Have to implement after sms api received
        return true;
    }
    public void initializePhoneNumber(){

        ActivationModel activationModel = new ActivationModel();
        if (!this.baseController.checkParam("phone_number",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Phone number required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(!activationModel.setPhoneNumber(this.req.getParameter("phone_number"))){
            this.baseController.serviceResponse.responseStat.msg = "Phone number format incorrect";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setPhone_number(activationModel.getPhoneNumber());
        if(appLoginCredentialModel.isNumberExist()){
            this.baseController.serviceResponse.responseStat.msg = "Number already used";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        // For testing inside assignToken function token is set to '1234'
        if(!activationModel.assignToken()){
            this.baseController.serviceResponse.responseStat.msg = "Error on database operation";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        boolean tokenSent = this.sendTokenViaSms(activationModel.getPhoneNumber(),activationModel.getActivationCode());

        if(!tokenSent){
            this.baseController.serviceResponse.responseStat.msg = "Unable to send the token in given phone number";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }



        this.baseController.serviceResponse.responseStat.msg = "Token is sent";
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void verifyToken(){
        ActivationModel activationModel = new ActivationModel();
        if(!this.baseController.checkParam("phone_number", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Phone number required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!this.baseController.checkParam("token", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Token required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!activationModel.setPhoneNumber(this.req.getParameter("phone_number"))){
            this.baseController.serviceResponse.responseStat.msg = "Phone number format miss matched";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        activationModel.setActivationCode(this.req.getParameter("token"));

        if(!activationModel.isTokenValid()){
            this.baseController.serviceResponse.responseStat.msg = "Token miss matched";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Phone number is verified";
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void test(){
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();


        this.baseController.serviceResponse.responseStat.msg = "";
        this.baseController.serviceResponse.responseData =  appLoginCredentialModel.getAppCredentialByKeyword("");
        this.pw.print(this.baseController.getResponse());
    }
    private void registerUser(){

        System.out.println("At registration ");
        if(!this.baseController.checkParam("phone_number", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Phone number required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!this.baseController.checkParam("token",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Token required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!this.baseController.checkParam("first_name",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        ActivationModel activationModel = new ActivationModel();

        if(!activationModel.setPhoneNumber(this.req.getParameter("phone_number"))){
            this.baseController.serviceResponse.responseStat.msg = "Phone number format miss matched";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        System.out.println("'" + this.req.getParameter("token")+"'");
        System.out.println("'"+this.req.getParameter("phone_number")+"'");
        activationModel.setActivationCode(this.req.getParameter("token"));

        if(!activationModel.isTokenValid()){
            this.baseController.serviceResponse.responseStat.msg = "Token miss matched";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        UserInfModel userInfModel = new UserInfModel();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        String imgBase64 = "";

        userInfModel.setF_name(this.req.getParameter("first_name"));
        userInfModel.setL_name(this.req.getParameter("last_name"));

         /*  transaction started */

      //  userInfModel.startTransaction();
        userInfModel.insertData();
        if(userInfModel.getId()==0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error on userInfModel";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
      //  appLoginCredentialModel.startTransaction();
        appLoginCredentialModel.setU_id(userInfModel.getId());
        appLoginCredentialModel.setPhone_number(activationModel.getPhoneNumber());
        if(appLoginCredentialModel.isNumberExist()){
            this.baseController.serviceResponse.responseStat.msg = "Number already used";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
         /*  transaction started */


        appLoginCredentialModel.insert();
        System.out.println("02");
        if(appLoginCredentialModel.getId()==0){
     //       userInfModel.rollBack();
            this.baseController.serviceResponse.responseStat.msg = "Internal server error on appLoginCredentialModel";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(this.baseController.checkParam("photo",this.req,true)) {
            imgBase64 = this.req.getParameter("photo");
            Pictures pictures = ImageHelper.saveProfilePicture(imgBase64, userInfModel.getId());
            Gson gson = new Gson();
            String fileName =gson.toJson(pictures);
            if(fileName==null || fileName == ""){

                 /* All transaction rollback */

            //    userInfModel.rollBack();
            //    appLoginCredentialModel.rollBack();

                this.baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }

            userInfModel.setPicPath(fileName);
            if(!userInfModel.updatePicPath()){
                /* All transaction rollback */

            //    userInfModel.rollBack();
            //    appLoginCredentialModel.rollBack();

                this.baseController.serviceResponse.responseStat.msg = "Internal server error on picture path update";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        /* Commit database transaction */

      //  userInfModel.commitTransaction();
       // appLoginCredentialModel.commitTransaction();
        AuthCredential authCredential = new AuthCredential();
        authCredential = appLoginCredentialModel.getAppCredentialById();

        this.baseController.setAppSession(req, authCredential);

        this.baseController.serviceResponse.responseStat.msg = "Registration success";
        this.baseController.serviceResponse.responseData =authCredential;
        this.pw.print(this.baseController.getResponse());
        return;

    }

}