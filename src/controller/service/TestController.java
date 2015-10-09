package controller.service;

import com.google.gson.*;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import helper.ImageHelper;
import model.*;
import model.datamodel.app.AppCredential;
import model.datamodel.app.Location;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mi on 10/2/15.
 */

/**
 * Created by mi on 10/8/15.
 */
public class TestController extends  HttpServlet{

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


        if(!this.baseController.isAppSessionValid(this.req)){
            this.pw.print(this.baseController.getResponse());
            this.pw.close();
            return;
        }

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/app/test/dbmodel":
                this.test();
                break;

            default:
                break;
        }
        this.pw.close();
    }

    private void test(){
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


        this.baseController.serviceResponse.responseStat.msg = "Registration success";
        this.baseController.serviceResponse.responseData = appLoginCredentialModel.getAppCredentialById();
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
