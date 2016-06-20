package controller.service;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.ActivationModel;
import model.AppLoginCredentialModel;
import model.ContactModel;
import model.UserInfModel;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.Contact;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 10/1/15.
 */
public class AppUserRegistration extends HttpServlet {


    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
       
        res.setContentType("application/json");
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        PrintWriter pw = res.getWriter();
        baseController.printRequestDetails(req);

        String url = req.getRequestURI();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        String jsonResponseStr = "";
        switch (url) {
            case "/app/register/number":
                jsonResponseStr = this.initializePhoneNumber(req, res);
                break;
            case "/app/register/user":
                jsonResponseStr = this.registerUser(req, res);
                break;
            case "/app/register/verifytoken":
                jsonResponseStr = this.verifyToken(req, res);
                break;
            case "/app/register/test":
                jsonResponseStr = this.test(req, res);
                break;
            default:
                break;
        }
        baseController.printResponsetDetails(req,res,jsonResponseStr);
        baseController.closeDbConnection();
        pw.print(jsonResponseStr);
        pw.close();
    }
    public boolean sendTokenViaSms(String phoneNumber,String token){
        // Have to implement after sms api received
        return true;
    }
    public String initializePhoneNumber(HttpServletRequest req, HttpServletResponse res){

        ActivationModel activationModel = new ActivationModel();
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        if (!baseController.checkParam("phone_number",req,true)) {
            baseController.serviceResponse.responseStat.msg = "Phone number required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        if(!activationModel.setPhoneNumber(req.getParameter("phone_number"))){
            baseController.serviceResponse.responseStat.msg = "Phone number format incorrect";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setPhone_number(activationModel.getPhoneNumber());
        if(appLoginCredentialModel.isNumberExist()){
            baseController.serviceResponse.responseStat.msg = "Number already used";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        // For testing inside assignToken function token is set to '1234'
        if(!activationModel.assignToken()){
            baseController.serviceResponse.responseStat.msg = "Error on database operation";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        boolean tokenSent = this.sendTokenViaSms(activationModel.getPhoneNumber(),activationModel.getActivationCode());

        if(!tokenSent){
            baseController.serviceResponse.responseStat.msg = "Unable to send the token in given phone number";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }



        baseController.serviceResponse.responseStat.msg = "Token is sent";

        return baseController.getResponse();
    }
    private String verifyToken(HttpServletRequest req, HttpServletResponse res){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        ActivationModel activationModel = new ActivationModel();
        if(!baseController.checkParam("phone_number", req, true)) {
            baseController.serviceResponse.responseStat.msg = "Phone number required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(!baseController.checkParam("token", req, true)) {
            baseController.serviceResponse.responseStat.msg = "Token required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(!activationModel.setPhoneNumber(req.getParameter("phone_number"))){
            baseController.serviceResponse.responseStat.msg = "Phone number format miss matched";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        activationModel.setActivationCode(req.getParameter("token"));

        if(!activationModel.isTokenValid()){
            baseController.serviceResponse.responseStat.msg = "Token miss matched";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        baseController.serviceResponse.responseStat.msg = "Phone number is verified";
        return baseController.getResponse();
    }
    private String test(HttpServletRequest req, HttpServletResponse res){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();


        baseController.serviceResponse.responseStat.msg = "";
        baseController.serviceResponse.responseData =  appLoginCredentialModel.getAppCredentialByKeyword("");
        return baseController.getResponse();
    }
    private String registerUser(HttpServletRequest req, HttpServletResponse res){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        System.out.println("At registration ");
        if(!baseController.checkParam("phone_number", req, true)) {
            baseController.serviceResponse.responseStat.msg = "Phone number required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(!baseController.checkParam("token",req,true)) {
            baseController.serviceResponse.responseStat.msg = "Token required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(!baseController.checkParam("first_name",req,true)) {
            baseController.serviceResponse.responseStat.msg = "Name required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }


        ActivationModel activationModel = new ActivationModel();

        if(!activationModel.setPhoneNumber(req.getParameter("phone_number"))){
            baseController.serviceResponse.responseStat.msg = "Phone number format miss matched";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        System.out.println("'" + req.getParameter("token")+"'");
        System.out.println("'"+req.getParameter("phone_number")+"'");
        activationModel.setActivationCode(req.getParameter("token"));

        if(!activationModel.isTokenValid()){
            baseController.serviceResponse.responseStat.msg = "Token miss matched";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        UserInfModel userInfModel = new UserInfModel();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        String imgBase64 = "";

        userInfModel.setF_name(req.getParameter("first_name"));
        userInfModel.setL_name(req.getParameter("last_name"));

        if (baseController.checkParam("device_id",req,true)) {
            userInfModel.setDeviceId(req.getParameter("device_id"));
        }

         /*  transaction started */

      //  userInfModel.startTransaction();
        userInfModel.insertData();
        if(userInfModel.getId()==0){
            baseController.serviceResponse.responseStat.msg = "Internal server error on userInfModel";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
      //  appLoginCredentialModel.startTransaction();
        appLoginCredentialModel.setU_id(userInfModel.getId());
        appLoginCredentialModel.setPhone_number(activationModel.getPhoneNumber());
        if(appLoginCredentialModel.isNumberExist()){
            baseController.serviceResponse.responseStat.msg = "Number already used";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
         /*  transaction started */


        appLoginCredentialModel.insert();
        System.out.println("02");
        if(appLoginCredentialModel.getId()==0){
     //       userInfModel.rollBack();
            baseController.serviceResponse.responseStat.msg = "Internal server error on appLoginCredentialModel";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(baseController.checkParam("photo",req,true)) {
            imgBase64 = req.getParameter("photo");
            Pictures pictures = ImageHelper.saveProfilePicture(imgBase64, userInfModel.getId());
            Gson gson = new Gson();
            String fileName =gson.toJson(pictures);
            if(fileName==null || fileName == ""){

                 /* All transaction rollback */

            //    userInfModel.rollBack();
            //    appLoginCredentialModel.rollBack();

                baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }

            userInfModel.setPicPath(fileName);
            if(!userInfModel.updatePicPath()){
                /* All transaction rollback */

            //    userInfModel.rollBack();
            //    appLoginCredentialModel.rollBack();

                baseController.serviceResponse.responseStat.msg = "Internal server error on picture path update";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        /* Commit database transaction */

      //  userInfModel.commitTransaction();
       // appLoginCredentialModel.commitTransaction();
        AuthCredential authCredential = new AuthCredential();
        authCredential = appLoginCredentialModel.getAppCredentialById();

        String keyword = "";
        ContactModel contactModel = new ContactModel();
        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(authCredential.id);
        contactModel.setLimit(50);
        contactModel.setOffset(0);

        System.out.println("user app cred id after registration :" + authCredential.id);

        ArrayList<Contact> contacts = new ArrayList<Contact>();

        contacts = contactModel.getWhoHasMyContactByOwnerId();

        /*for(int i=0;i<contacts.size();i++)
        {
           contacts.get(i).id; // id of the owner of the contact list
        }*/


        baseController.setAppSession(req, authCredential);

        baseController.serviceResponse.responseStat.msg = "Registration success";
        baseController.serviceResponse.responseData =authCredential;
        return baseController.getResponse();

    }

}