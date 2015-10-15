package controller.service;

import com.google.gson.*;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import helper.ImageHelper;
import model.ContactModel;
import model.CountryModel;
import model.StickerCategoryModel;
import model.StickersModel;
import model.datamodel.app.StickerCategory;

import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                this.testSticker();
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


    }
}
