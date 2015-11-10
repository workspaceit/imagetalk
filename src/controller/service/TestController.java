package controller.service;

import com.google.gson.*;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import helper.ImageHelper;
import model.*;
import model.datamodel.app.Places;
import model.datamodel.app.StickerCategory;

import model.datamodel.app.Stickers;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
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
            case "/app/test/test":
                this.test();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void testSticker(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//Here you say to java the initial timezone. This is the secret
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//Will print in UTC
        System.out.println(dateFormat.format(new Date()));

//Here you set to your timezone
        dateFormat.setTimeZone(TimeZone.getDefault());
//Will print on your default Timezone
        System.out.println(dateFormat.format(new Date()));
        return;
    }
    private void test(){

        StickersModel stickersModel = new StickersModel();



        if(this.baseController.checkParam("limit", this.req, true)) {
            try{
                stickersModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            stickersModel.limit = 30;
        }

        if(!this.baseController.checkParam("offset", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "offset required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else {
            try{
                stickersModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        ArrayList<Stickers> stickers = stickersModel.getAll();

        this.baseController.serviceResponse.responseData = stickers;
        this.baseController.serviceResponse.responseStat.status =  (stickers.size()>0);
        this.baseController.serviceResponse.responseStat.msg = (stickers.size()==0)?"No record found":"";
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
