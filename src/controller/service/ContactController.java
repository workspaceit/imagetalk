package controller.service;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 10/12/15.
 */
public class ContactController extends HttpServlet {
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

        if(!this.baseController.isAppSessionValid(this.req)){
            this.pw.print(this.baseController.getResponse());
            this.pw.close();
            return;
        }

        switch (url) {
            case "/app/contact/import":
                this.importContact();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void importContact(){
        ArrayList<String> contacts = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("contacts", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "contacts required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                String [] contactsArray = gson.fromJson(this.req.getParameter("contacts").trim(),String[].class);
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "contacts is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }



    }
}
