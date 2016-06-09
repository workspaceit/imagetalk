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
  /*  ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;
*/

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        PrintWriter pw = res.getWriter();

        String url = req.getRequestURI();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        switch (url) {
            case "/app/sticker/user_sticker":
                pw.print(this.userStickerEligibleAdd(req));
                break;
            case "/app/sticker/update_usage":
                pw.print(this.stickerUsageUpdate(req));
                break;
            default:
                break;
        }
        baseController.closeDbConnection();
        pw.close();

    }

    private String stickerUsageUpdate(HttpServletRequest req) {
        int id;
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "id is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else
        {
            try {
                id = Integer.parseInt(req.getParameter("id"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "id must be int";
                baseController.serviceResponse.responseStat.status = false;
                e.printStackTrace();
                return baseController.getResponse();
            }

        }

        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setUsage(1);
        stickerUsageEligibleModel.setId(id);
        if(stickerUsageEligibleModel.updateStickerUsage())
        {
            baseController.serviceResponse.responseStat.msg = "sticker usage updated successfully";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg = "sticker usage not updated";
        baseController.serviceResponse.responseStat.status = false;
        return baseController.getResponse();

    }

    private String userStickerEligibleAdd(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        int sticker_category_id;

        if(!baseController.checkParam("sticker_category_id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "sticker_category_id is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else {
            try {
                sticker_category_id = Integer.parseInt(req.getParameter("sticker_category_id"));
            }
            catch (Exception e)
            {
                baseController.serviceResponse.responseStat.msg = "sticker_category_id must be int";
                baseController.serviceResponse.responseStat.status = false;
                e.printStackTrace();
                return baseController.getResponse();
            }
        }


        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setSticker_category_id(sticker_category_id);
        stickerUsageEligibleModel.setApp_login_credential_id(baseController.appCredential.id);

        if(stickerUsageEligibleModel.insert()==0)
        {
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.status = true;
        baseController.serviceResponse.responseStat.msg = "user sticker eligibility added successfully";
        return baseController.getResponse();

    }
}
