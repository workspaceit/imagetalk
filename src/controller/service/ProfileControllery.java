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
    /*ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;*/

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        res.setContentType("application/json");
        PrintWriter pw = res.getWriter();

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        switch (url) {
            case "/app/profile/change/status":
                pw.print(this.changeTextStatus(req));
                break;
            case "/app/profile/change/picture":
                pw.print(this.changePicture(req));
                break;
            case "/app/profile/change/phone/number":
                pw.print(this.changePhoneNumber(req));
                break;
            case "/app/profile/get/entities/count":
                pw.print(this.getCounts(req));
                break;
            default:
                break;
        }
        pw.close();
    }
    private String changeTextStatus(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        System.out.println("Inside status change " + baseController.appCredential.id);

        if(!baseController.checkParam("text_status", req, true)) {
            baseController.serviceResponse.responseStat.msg = "text_status is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }



        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setText_status(req.getParameter("text_status"));
        appLoginCredentialModel.setId(baseController.appCredential.id);

        HashMap<String,String> textStatusResponse = new HashMap<String,String>();

        if(!appLoginCredentialModel.updateUserTextStatus()){
            baseController.serviceResponse.responseStat.msg = "Internal server error on text status update";
            baseController.serviceResponse.responseStat.status = false;
            textStatusResponse.put("textStatus",appLoginCredentialModel.getUserTextStatusById());
            baseController.serviceResponse.responseData = textStatusResponse;
            return baseController.getResponse();
        }
        textStatusResponse.put("textStatus",appLoginCredentialModel.getUserTextStatusById());


        baseController.serviceResponse.responseStat.msg = "Text status Successfully updated";
        baseController.serviceResponse.responseData = textStatusResponse;
        return baseController.getResponse();
    }
    private String changePicture(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("photo",req,true)) {
            baseController.serviceResponse.responseStat.msg = "photo is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        UserInfModel userInfModel = new UserInfModel();

        userInfModel.setId(baseController.appCredential.user.id);
        userInfModel.startTransaction();

        String imgBase64 = req.getParameter("photo");
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
            userInfModel.rollBack();

            baseController.serviceResponse.responseStat.msg = "Internal server error on picture path update";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        userInfModel.commitTransaction();

        baseController.serviceResponse.responseStat.msg = "Picture Success fully updated";
        baseController.serviceResponse.responseData = userInfModel.getById();
        return baseController.getResponse();

    }
    private String changePhoneNumber(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if (!baseController.checkParam("phone_number", req, true)) {
            baseController.serviceResponse.responseStat.msg = "phone_number is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        appLoginCredentialModel.setId(baseController.appCredential.id);
        appLoginCredentialModel.setPhone_number(req.getParameter("phone_number"));

        if(appLoginCredentialModel.isPhoneNumberOthers()){
            baseController.serviceResponse.responseStat.msg = "phone_number is already been used";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        appLoginCredentialModel.startTransaction();
        if(!appLoginCredentialModel.updatePhoneNumber()){
            appLoginCredentialModel.rollBack();
            baseController.serviceResponse.responseStat.msg = "Data base error while updating Phone number";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        appLoginCredentialModel.commitTransaction();

        baseController.serviceResponse.responseStat.msg = "Phone number successfully update";
        baseController.serviceResponse.responseData =  appLoginCredentialModel.getAppCredentialById();
        return baseController.getResponse();

    }
    private String getCounts(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("count_params",req,true)) {
            baseController.serviceResponse.responseStat.msg = "count_params is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        int ownerId = 0;

        if(baseController.checkParam("user_credential_id",req,true)) {
            try{
                ownerId = Integer.parseInt(req.getParameter("user_credential_id"));
            }catch (Exception ex){
                ex.printStackTrace();
                baseController.serviceResponse.responseStat.msg = "user_credential_id is not integer";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }

        }else{
            ownerId = baseController.appCredential.id;
        }

        Gson gson = new Gson();
        ArrayList<String> countList = new ArrayList();
        try{
            String[] countParams = gson.fromJson(req.getParameter("count_params"), String[].class);

            for(String countParam : countParams){
                countList.add(countParam);
            }

        }catch(Exception ex){
            baseController.serviceResponse.responseStat.msg = "count_params is not in format";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
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

        baseController.serviceResponse.responseData = countResponse;
        return baseController.getResponse();

    }
}
