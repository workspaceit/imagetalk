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
            case "/app/stickers/get/free":
                this.getAllFree(true);
                break;
            case "/app/stickers/get/all/free":
                this.getAllFree(false);
                break;
            case "/app/stickers/get/paid/category":
                this.getAllPaid(false);
                break;
            case "/app/stickers/get/for/post":
                this.getForPost(false);
                break;
            case "/app/stickers/add/stickerUsability":
                this.addStickerUsability();
                break;
            case "/app/stickers/update/stickerUsability":
                this.updateStickerUsability();
                break;
            default:
                break;
        }
        this.pw.close();
    }

    private void updateStickerUsability() {

        int sticker_category_id;

        if (!this.baseController.checkParam("sticker_category_id",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "sticker_category_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
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
                this.baseController.serviceResponse.responseStat.msg = "sticker_category_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setSticker_category_id(sticker_category_id);
        stickerUsageEligibleModel.setApp_login_credential_id(this.baseController.appCredential.id);

        int id = stickerUsageEligibleModel.isExist();
        if(id==0)
        {
            stickerUsageEligibleModel.setUsage(1);
            if(stickerUsageEligibleModel.insert()==0)
            {
                this.baseController.serviceResponse.responseStat.msg = "sticker usability couldn't insert";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
            else
            {
                this.baseController.serviceResponse.responseStat.msg = "sticker usability inserted successfully with usage=1";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        else if(id==1)
        {
            this.baseController.serviceResponse.responseStat.msg = "sticker usability already updated";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        stickerUsageEligibleModel.setUsage(1);
        if(stickerUsageEligibleModel.updateStickerUsage())
        {
            this.baseController.serviceResponse.responseStat.msg = "sticker usability Updated Successfully";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        else {
            this.baseController.serviceResponse.responseStat.msg = "sticker usability not updated";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

    }

    private void addStickerUsability() {

        int sticker_category_id;

        if (!this.baseController.checkParam("sticker_category_id",this.req,true))
        {
            this.baseController.serviceResponse.responseStat.msg = "sticker_category_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
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
                this.baseController.serviceResponse.responseStat.msg = "sticker_category_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        StickerUsageEligibleModel stickerUsageEligibleModel = new StickerUsageEligibleModel();
        stickerUsageEligibleModel.setSticker_category_id(sticker_category_id);
        stickerUsageEligibleModel.setApp_login_credential_id(this.baseController.appCredential.id);
        if(stickerUsageEligibleModel.insert()==0)
        {
            this.baseController.serviceResponse.responseStat.msg = "sticker usability couldn't insert";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "sticker usability inserted successfully";
        this.baseController.serviceResponse.responseStat.status = false;
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void getAllPaid(boolean pagination){
        StickerCategoryModel stickerCategoryModel = new StickerCategoryModel();


        if(pagination){
            if(this.baseController.checkParam("limit", this.req, true)) {
                try{
                    stickerCategoryModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }else{
                stickerCategoryModel.limit = 3;
            }

            if(this.baseController.checkParam("sticker_limit", this.req, true)) {
                try{
                    stickerCategoryModel.stickersLimit = Integer.parseInt(this.req.getParameter("sticker_limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "sticker_limit is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }else{
                stickerCategoryModel.limit = 6;
            }

            if(!this.baseController.checkParam("offset", this.req, true)){

                this.baseController.serviceResponse.responseStat.msg = "offset required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }else {
                try{
                    stickerCategoryModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }
        }

        stickerCategoryModel.setIs_paid(1);
        this.baseController.serviceResponse.responseData = stickerCategoryModel.getAll(true);
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getAllFree(boolean pagination){
        StickerCategoryModel stickerCategoryModel = new StickerCategoryModel();


        if(pagination){
            if(this.baseController.checkParam("limit", this.req, true)) {
                try{
                    stickerCategoryModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }else{
                stickerCategoryModel.limit = 3;
            }

            if(this.baseController.checkParam("sticker_limit", this.req, true)) {
                try{
                    stickerCategoryModel.stickersLimit = Integer.parseInt(this.req.getParameter("sticker_limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "sticker_limit is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }else{
                stickerCategoryModel.limit = 6;
            }

            if(!this.baseController.checkParam("offset", this.req, true)){

                this.baseController.serviceResponse.responseStat.msg = "offset required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }else {
                try{
                    stickerCategoryModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }
        }

        stickerCategoryModel.setIs_paid(0);
        this.baseController.serviceResponse.responseData = stickerCategoryModel.getAll(true);
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getForPost(boolean pagination){
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
