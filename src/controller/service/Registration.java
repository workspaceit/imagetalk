package controller.service;

import model.ActivationModel;
import model.datamodel.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mi on 10/1/15.
 */
public class Registration extends HttpServlet {
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
            case "/app/register/number":
                this.initializePhoneNumber();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    public void initializePhoneNumber(){

        ActivationModel activationModel = new ActivationModel();
        if (this.baseController.checkParam("phone_number",this.req,true)) {
            activationModel.setPhoneNumber(this.req.getParameter("phone_number"));
        } else {
            this.baseController.serviceResponse.responseStat.msg = "Phone number required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        // For testing inside assignToken function token is set to '1234'
        if(!activationModel.assignToken()){
            this.baseController.serviceResponse.responseStat.msg = "Error on database operation";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        this.baseController.serviceResponse.responseStat.msg = "Token is sent";
        this.pw.print(this.baseController.getResponse());
        return;
    }
    public void sendTokenViaSms(){

    }
}
