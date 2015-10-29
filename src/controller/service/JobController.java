package controller.service;

import com.google.gson.Gson;
import model.ContactModel;
import model.JobModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by rajib on 10/29/15.
 */
public class JobController extends HttpServlet {
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
            throws ServletException, IOException
    {

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
        switch (url)
        {
            case "/job/findAllJobs":
                this.findAllJobs();
                break;
            case "/job/add":
                this.addJobs();
                break;
            case "/job/remove":
                this.removeJobs();
                break;
            default:
                break;
        }


    }

    private void removeJobs() {

    }

    private void addJobs() {

        if(!this.baseController.checkParam("app_login_credential_id", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        JobModel jobModel = new JobModel();
        jobModel.setApp_login_credential_id(this.baseController.appCredential.id);
        jobModel.setDescription(req.getParameter("description"));
        jobModel.setPrice(parseInt(req.getParameter("price")));
        jobModel.setIcon("Picture.jpg");

        if(jobModel.insert()==0){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = jobModel.errorObj.msg;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.msg = "Job added successfully";
        this.pw.print(this.baseController.getResponse());
        return;


    }

    private void findAllJobs() {

    }
}
