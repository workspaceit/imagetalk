package controller.admin;

import controller.service.ImageTalkBaseController;
import model.StickerCategoryModel;
import model.StickersModel;
import model.datamodel.app.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Application Name : ImageTalk
 * Package Name     : model
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 10/9/15
 */

public class StickerController extends HttpServlet {
    private PrintWriter             pw;
    private String                  url;
    private Login                   login;
    private ImageTalkBaseController baseController;

    public StickerController() {
        this.login = new Login();
        this.baseController = new ImageTalkBaseController();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.pw = resp.getWriter();
        this.url = req.getRequestURI();

        if (!this.baseController.isSessionValid(req)) {
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if (!this.baseController.isAdmin(req)) {
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.login = this.baseController.getUserLoginFromSession(req);

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (this.url) {
            case "/admin/sticker/operation/category/new":
                this.insertCategory(req);
                break;
            case "/admin/sticker/operation/category/delete":
                this.getCategoryDelete(req);
                break;
            case "/admin/sticker/operation/create":
                this.insertSticker(req);
                break;
            default:
                this.getSticker();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private void insertCategory(HttpServletRequest req) {
        StickerCategoryModel sCategoryModel = new StickerCategoryModel();
        String               categoryName   = req.getParameter("name");

        System.out.print(categoryName);

        sCategoryModel.setName(categoryName);
        sCategoryModel.setIs_paid(Integer.parseInt(req.getParameter("is_paid")));
        sCategoryModel.setCreated_by(this.login.u_id);

        if (sCategoryModel.insert() > 0) {
            this.baseController.serviceResponse.responseStat.msg = "Category \"" + categoryName + "\" Store Successfully";
            this.baseController.serviceResponse.responseStat.status = true;
            this.pw.print(this.baseController.getResponse());
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Category \"" + categoryName + "\" Store Successfully";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
        }
    }

    private void getCategoryDelete(HttpServletRequest req) {
        int categoryId = Integer.parseInt(req.getParameter("id"));

        if (categoryId > 0) {
            StickerCategoryModel stickerCategoryModel = new StickerCategoryModel();
            if (stickerCategoryModel.deleteCategory(categoryId)) {
                this.baseController.serviceResponse.responseStat.msg = "Category delete successfully";
                this.baseController.serviceResponse.responseStat.status = true;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        this.baseController.serviceResponse.responseStat.msg = "Category delete failed";
        this.baseController.serviceResponse.responseStat.status = false;
        this.pw.print(this.baseController.getResponse());
    }

    private void insertSticker(HttpServletRequest req) {
        String        imgPath       = req.getParameter("img_path");
        int           category_id   = Integer.parseInt(req.getParameter("category_id"));
        int           isPaid        = Integer.parseInt(req.getParameter("is_paid"));
        int           userId        = this.login.id;
        StickersModel stickersModel = new StickersModel();

        stickersModel.setPath(imgPath);
        stickersModel.setSticker_category_id(category_id);
        stickersModel.setIs_paid(isPaid);
        stickersModel.setCreated_by(userId);

        int stickerId = stickersModel.insert();

        if (stickerId > 0) {
            this.baseController.serviceResponse.responseStat.status = true;
        } else {
            this.baseController.serviceResponse.responseStat.status = false;
        }
        this.pw.print(this.baseController.serviceResponse.responseStat.status);
        this.pw.print("test");
    }

    private void getSticker() {

    }
}
