package controller.service;

import model.AppLoginCredentialModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 10/2/15.
 */
public class SearchController extends HttpServlet {
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
            case "/app/search/user/fortag":
                this.getUserForTag();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void getUserForTag(){


        String keyword="";
        if(this.baseController.checkParam("keyword", this.req, true)) {
            keyword = this.req.getParameter("keyword").trim();
            return;
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        ArrayList<AuthCredential> appCredentialsList =  appLoginCredentialModel.getAppCredentialByKeyword(keyword);

        if(this.baseController.checkParam("limit", this.req, true)) {
            try{
                appLoginCredentialModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }

            return;
        }
        if(this.baseController.checkParam("offset", this.req, true)) {
            try{
                appLoginCredentialModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }


            return;
        }
        if(appCredentialsList.size()==0){
            this.baseController.serviceResponse.responseStat.msg = "No record found";
            this.baseController.serviceResponse.responseStat.status = false;
        }

        this.baseController.serviceResponse.responseData = appCredentialsList;
        this.pw.print(this.baseController.getResponse());
    }
}
