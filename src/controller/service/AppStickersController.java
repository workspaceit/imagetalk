package controller.service;

import model.StickerCategoryModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
            default:
                break;
        }
        this.pw.close();
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
        this.baseController.serviceResponse.responseData = stickerCategoryModel.getAll();
        this.pw.print(this.baseController.getResponse());
        return;
    }




}
