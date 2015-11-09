package controller.service;


import model.StickerUsageEligibleModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 11/9/15
 * Project Name:ImageTalk
 */
public class StickerController extends HttpServlet{
    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;


    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.req = req;
        this.res = resp;
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
            case "/app/sticker/user_sticker":
                this.userStickerEligibleAdd();
                break;
            case "/app/sticker/update_usage":
                this.stickerUsageUpdate();
                break;
            default:
                break;
        }
        this.pw.close();


    }

    private void stickerUsageUpdate() {
        int id;

        if(!this.baseController.checkParam("id",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "id is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else
        {
            try {
                id = Integer.parseInt(req.getParameter("id"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "id must be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }

        }

        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setUsage(1);
        stickerUsageEligibleModel.setId(id);
        if(stickerUsageEligibleModel.updateStickerUsage())
        {
            this.baseController.serviceResponse.responseStat.msg = "sticker usage updated successfully";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.msg = "sticker usage not updated";
        this.baseController.serviceResponse.responseStat.status = false;
        this.pw.print(this.baseController.getResponse());
        return;

    }

    private void userStickerEligibleAdd() {

        int sticker_category_id;

        if(!this.baseController.checkParam("sticker_category_id",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "sticker_category_id is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else {
            try {
                sticker_category_id = Integer.parseInt(req.getParameter("sticker_category_id"));
            }
            catch (Exception e)
            {
                this.baseController.serviceResponse.responseStat.msg = "sticker_category_id must be int";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                e.printStackTrace();
                return;
            }
        }


        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setSticker_category_id(sticker_category_id);
        stickerUsageEligibleModel.setApp_login_credential_id(this.baseController.appCredential.id);

        if(stickerUsageEligibleModel.insert()==0)
        {
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.status = true;
        this.baseController.serviceResponse.responseStat.msg = "user sticker eligibility added successfully";
        this.pw.print(this.baseController.getResponse());
        return;

    }
}
