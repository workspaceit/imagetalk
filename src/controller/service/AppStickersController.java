package controller.service;

import model.StickerCategoryModel;
import model.StickerUsageEligibleModel;
import model.StickersModel;
import model.datamodel.app.Stickers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 10/13/15.
 */
public class AppStickersController extends HttpServlet {
/*    ImageTalkBaseController baseController;
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

        res.setContentType("application/json");
        ImageTalkBaseController baseController = new ImageTalkBaseController();

        String resp = "";
        PrintWriter pw = res.getWriter();

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/app/stickers/get/free":
                pw.print(this.getAllFree(req, true));
                break;
            case "/app/stickers/get/all/free":
                pw.print(this.getAllFree(req, false));
                break;
            case "/app/stickers/get/paid/category":
                pw.print(this.getAllPaid(req, false));
                break;
            case "/app/stickers/get/for/post":
                pw.print(this.getForPost(req, false));
                break;
            case "/app/stickers/add/stickerUsability":
                pw.print(this.addStickerUsability(req));
                break;
            case "/app/stickers/update/stickerUsability":
                pw.print(this.updateStickerUsability(req));
                break;
            default:
                break;
        }
        baseController.closeDbConnection();
        pw.close();
    }

    private String updateStickerUsability(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        int sticker_category_id;

        if (!baseController.checkParam("sticker_category_id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "sticker_category_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else
        {
            try
            {
                sticker_category_id = Integer.parseInt(req.getParameter("sticker_category_id"));
            }
            catch (Exception e)
            {
                System.out.println(e);
                baseController.serviceResponse.responseStat.msg = "sticker_category_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setSticker_category_id(sticker_category_id);
        stickerUsageEligibleModel.setApp_login_credential_id(baseController.appCredential.id);

        int id = stickerUsageEligibleModel.isExist();
        if(id==0)
        {
            stickerUsageEligibleModel.setUsage(1);
            if(stickerUsageEligibleModel.insert()==0)
            {
                baseController.serviceResponse.responseStat.msg = "sticker usability couldn't insert";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
            else
            {
                baseController.serviceResponse.responseStat.msg = "sticker usability inserted successfully with usage=1";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        else if(id==1)
        {
            baseController.serviceResponse.responseStat.msg = "sticker usability already updated";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        stickerUsageEligibleModel.setUsage(1);
        if(stickerUsageEligibleModel.updateStickerUsage())
        {
            baseController.serviceResponse.responseStat.msg = "sticker usability Updated Successfully";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else {
            baseController.serviceResponse.responseStat.msg = "sticker usability not updated";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

    }

    private String addStickerUsability(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        int sticker_category_id;

        if (!baseController.checkParam("sticker_category_id",req,true))
        {
            baseController.serviceResponse.responseStat.msg = "sticker_category_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        else
        {
            try
            {
                sticker_category_id = Integer.parseInt(req.getParameter("sticker_category_id"));
            }
            catch (Exception e)
            {
                System.out.println(e);
                baseController.serviceResponse.responseStat.msg = "sticker_category_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setSticker_category_id(sticker_category_id);
        stickerUsageEligibleModel.setApp_login_credential_id(baseController.appCredential.id);
        if(stickerUsageEligibleModel.insert()==0)
        {
            baseController.serviceResponse.responseStat.msg = "sticker usability couldn't insert";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg = "sticker usability inserted successfully";
        return baseController.getResponse();

    }
    private String getAllPaid(HttpServletRequest req,boolean pagination){
        ImageTalkBaseController baseController = new ImageTalkBaseController();

        StickerCategoryModel stickerCategoryModel = new StickerCategoryModel();


        if(pagination){
            if(baseController.checkParam("limit", req, true)) {
                try{
                    stickerCategoryModel.limit = Integer.parseInt(req.getParameter("limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
            }else{
                stickerCategoryModel.limit = 3;
            }

            if(baseController.checkParam("sticker_limit", req, true)) {
                try{
                    stickerCategoryModel.stickersLimit = Integer.parseInt(req.getParameter("sticker_limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "sticker_limit is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
            }else{
                stickerCategoryModel.limit = 6;
            }

            if(!baseController.checkParam("offset", req, true)){

                baseController.serviceResponse.responseStat.msg = "offset required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }else {
                try{
                    stickerCategoryModel.offset = Integer.parseInt(req.getParameter("offset").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
            }
        }

        stickerCategoryModel.setIs_paid(1);
        baseController.serviceResponse.responseData = stickerCategoryModel.getAll(true);
        return baseController.getResponse();
    }
    private String getAllFree(HttpServletRequest req,boolean pagination){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        StickerCategoryModel stickerCategoryModel = new StickerCategoryModel();


        if(pagination){
            if(baseController.checkParam("limit", req, true)) {
                try{
                    stickerCategoryModel.limit = Integer.parseInt(req.getParameter("limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
            }else{
                stickerCategoryModel.limit = 3;
            }

            if(baseController.checkParam("sticker_limit", req, true)) {
                try{
                    stickerCategoryModel.stickersLimit = Integer.parseInt(req.getParameter("sticker_limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "sticker_limit is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
            }else{
                stickerCategoryModel.limit = 6;
            }

            if(!baseController.checkParam("offset", req, true)){

                baseController.serviceResponse.responseStat.msg = "offset required";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }else {
                try{
                    stickerCategoryModel.offset = Integer.parseInt(req.getParameter("offset").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
            }
        }

        stickerCategoryModel.setIs_paid(0);
        baseController.serviceResponse.responseData = stickerCategoryModel.getAll(true);
        return baseController.getResponse();
    }


    private String getForPost(HttpServletRequest req,boolean pagination){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        StickersModel stickersModel = new StickersModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                stickersModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            stickersModel.limit = 30;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                stickersModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        ArrayList<Stickers> stickers = stickersModel.getAll();

        baseController.serviceResponse.responseData = stickers;
        baseController.serviceResponse.responseStat.status =  (stickers.size()>0);
        baseController.serviceResponse.responseStat.msg = (stickers.size()==0)?"No record found":"";
        return baseController.getResponse();
    }



}
