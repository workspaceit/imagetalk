package controller.service;

import helper.ImageHelper;
import model.WallPostModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mi on 10/2/15.
 */
public class WallPostController extends HttpServlet {
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
            case "/app/wallpost/create":
                this.create();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    public void create(){
        if(!this.baseController.checkParam("description", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "description required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        String imgBase64 = "";
        String fileRelativePath = "";
        int locationId = 0;
        if(this.baseController.checkParam("photo", this.req, true)){
            if(this.baseController.checkParam("photo",this.req,true)) {
                imgBase64 = this.req.getParameter("photo");
                fileRelativePath = ImageHelper.saveFile(imgBase64, null, this.baseController.appCredential.id);
                if (fileRelativePath == "") {

                    // Need roll back
                    this.baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }
        }

        /*===============  Insert location here ==============*/

        /*======================================================*/


        System.out.println("fileName :" + fileRelativePath);

        WallPostModel wallPostModel = new WallPostModel();

        wallPostModel.setOwner_id(this.baseController.appCredential.id);
        wallPostModel.setDescrption(this.req.getParameter("description"));
        wallPostModel.setPicture_path(fileRelativePath);
        wallPostModel.setLocation_id(locationId);


        if(wallPostModel.insert()==0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Wall post created";
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
