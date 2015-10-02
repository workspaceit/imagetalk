package controller.service;

import model.CountryModel;
import model.datamodel.app.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by mi on 10/1/15.
 */
public class UtilityController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;
    @Override
    public void init() throws ServletException {
        super.init();
    }

    public class LoginRespose {
        Login login;

        public LoginRespose() {
            this.login = new Login();
        }
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
           case "/utility/get/countries":
                this.getCountries();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void getCountries(){
        CountryModel countryModel = new CountryModel();
        this.baseController.serviceResponse.responseData = countryModel.getAll();
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
