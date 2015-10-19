package controller.service;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.AppLoginCredentialModel;
import model.UserInfModel;
import model.WallPostModel;
import model.datamodel.app.AppCredential;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mi on 10/15/15.
 */
public class ProfileControllery extends HttpServlet {
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

        if(!this.baseController.isAppSessionValid(this.req)){
            this.pw.print(this.baseController.getResponse());
            this.pw.close();
            return;
        }

        switch (url) {
            case "/app/profile/change/picture":
                this.changePicture();
            case "/app/profile/change/phone/number":
                this.changePhoneNumber();
            case "/app/profile/get/entities/count":
                this.getCounts();
            default:
                break;
        }
        this.pw.close();
    }
    private void changePicture(){
        if(!this.baseController.checkParam("photo",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "photo is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        UserInfModel userInfModel = new UserInfModel();

        userInfModel.setId(this.baseController.appCredential.user.id);
        userInfModel.startTransaction();

        String imgBase64 = this.req.getParameter("photo");
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
            userInfModel.rollBack();

            this.baseController.serviceResponse.responseStat.msg = "Internal server error on picture path update";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        userInfModel.commitTransaction();

        this.baseController.serviceResponse.responseStat.msg = "Picture Success fully updated";
        this.baseController.serviceResponse.responseData = userInfModel.getById();
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void changePhoneNumber() {
        if (!this.baseController.checkParam("phone_number", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "phone_number is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        appLoginCredentialModel.setId(this.baseController.appCredential.id);
        appLoginCredentialModel.setPhone_number(this.req.getParameter("phone_number"));

        if(appLoginCredentialModel.isPhoneNumberOthers()){
            this.baseController.serviceResponse.responseStat.msg = "phone_number is already been used";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        appLoginCredentialModel.startTransaction();
        if(!appLoginCredentialModel.updatePhoneNumber()){
            appLoginCredentialModel.rollBack();
            this.baseController.serviceResponse.responseStat.msg = "Data base error while updating Phone number";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        appLoginCredentialModel.commitTransaction();

        this.baseController.serviceResponse.responseStat.msg = "Phone number successfully update";
        this.baseController.serviceResponse.responseData =  appLoginCredentialModel.getAppCredentialById();
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void getCounts(){
        if(!this.baseController.checkParam("count_params",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "count_params is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        int ownerId = 0;

        if(this.baseController.checkParam("user_credential_id",this.req,true)) {
            try{
                ownerId = Integer.parseInt(this.req.getParameter("user_credential_id"));
            }catch (Exception ex){
                ex.printStackTrace();
                this.baseController.serviceResponse.responseStat.msg = "user_credential_id is not integer";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }

        }else{
            ownerId = this.baseController.appCredential.id;
        }

        Gson gson = new Gson();
        ArrayList<String> countList = new ArrayList();
        try{
            String[] countParams = gson.fromJson(this.req.getParameter("count_params"), String[].class);

            for(String countParam : countParams){
                countList.add(countParam);
            }

        }catch(Exception ex){
            this.baseController.serviceResponse.responseStat.msg = "count_params is not in format";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        HashMap<String,Integer> countResponse =  new HashMap();

        if(countList.contains("present")){
            countResponse.put("present",0);
        }
        if(countList.contains("wallPost")){
            WallPostModel  wallPostModel = new WallPostModel();
            wallPostModel.setOwner_id(ownerId);

            countResponse.put("wallPost",wallPostModel.getCountByOwnerId());
        }

        this.baseController.serviceResponse.responseData = countResponse;
        this.pw.print(this.baseController.getResponse());
        return;

    }
}
