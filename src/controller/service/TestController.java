package controller.service;

import com.google.gson.*;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import helper.ImageHelper;
import model.ContactModel;
import model.CountryModel;
import model.StickerCategoryModel;
import model.StickersModel;
import model.test.UserInfModel;
import model.test.AppLoginCredentialModel;

import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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




        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/app/test/dbmodel":
                this.test();
                break;
            case "/app/test/test":
                this.testSticker();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void testSticker(){
        ContactModel contactModel = new ContactModel();
        contactModel.getContactInStrArray();
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
        if(!this.baseController.checkParam("first_name", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Name required";
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

          userInfModel.startTransaction();
        userInfModel.insertData();
        if(userInfModel.getId()==0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error on userInfModel";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
          appLoginCredentialModel.startTransaction();
        appLoginCredentialModel.setU_id(userInfModel.getId());
        appLoginCredentialModel.setPhone_number(this.req.getParameter("phone_number"));
        appLoginCredentialModel.isNumberExist();
         /*  transaction started */


        appLoginCredentialModel.insert();

        if(appLoginCredentialModel.getId()==0){
            //       userInfModel.rollBack();
            this.baseController.serviceResponse.responseStat.msg = "Internal server error on appLoginCredentialModel";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
//        userInfModel.rollBack();
//        appLoginCredentialModel.rollBack();
        /* Commit database transaction */
        CountryModel countryModel = new CountryModel();
        countryModel.getAll();
        userInfModel.getAllByKeyword("a");
        appLoginCredentialModel.getAppCredentialById();


          userInfModel.commitTransaction();
         appLoginCredentialModel.commitTransaction();
//        userInfModel.rollBack();
//        appLoginCredentialModel.rollBack();

        this.baseController.serviceResponse.responseStat.msg = "Registration success";
        this.baseController.serviceResponse.responseData = appLoginCredentialModel.getAppCredentialById();
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
