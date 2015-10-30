package controller.service;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.JobModel;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

/**
 * Created by rajib on 10/29/15.
 */
public class JobController extends HttpServlet {
    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;
    Gson gson;

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

        this.gson = new Gson();

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
            case "/app/user/job/add":
                this.addJobs();
                break;
            case "/job/remove":
                this.removeJobs();
                break;
            case "/app/user/job/update":
                this.updateJobs();
                break;
            default:
                break;
        }


    }

    private void updateJobs() {

        Enumeration<String> parameterNames = req.getParameterNames();
        HashMap<String, String> paramWithValues = new HashMap<String, String>();

        while (parameterNames.hasMoreElements())
        {
            String paramName = parameterNames.nextElement();
            if(paramName.equals("title"))
            {
                if(!this.baseController.checkParam(paramName,this.req,true)) {
                    this.baseController.serviceResponse.responseStat.msg = "title is required";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }else{
                    paramWithValues.put(paramName,req.getParameter("title"));
                }

            }
            else if(paramName.equals("description"))
            {
                paramWithValues.put(paramName,req.getParameter("description"));
            }
            else if(paramName.equals("price"))
            {
                int price;
                if(!this.baseController.checkParam("price",this.req,true)) {
                    this.baseController.serviceResponse.responseStat.msg = "price is required";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }else{
                    try{
                        price =  parseInt(req.getParameter("price"));
                    }catch (Exception ex){

                        this.baseController.serviceResponse.responseStat.msg = "price is float required";
                        this.baseController.serviceResponse.responseStat.status = false;
                        this.pw.print(this.baseController.getResponse());
                        ex.printStackTrace();
                        return;

                    }

                }
                paramWithValues.put(paramName,Integer.toString(price));

            }
            else
            {
                this.baseController.serviceResponse.responseStat.msg = "Please provide valid parameter name to update";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if (paramWithValues.isEmpty())
        {
            this.baseController.serviceResponse.responseStat.msg = "Please provide at least one parameter to update";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        JobModel jobModel = new JobModel();
        jobModel.setApp_login_credential_id(this.baseController.appCredential.id);
        if(jobModel.update(paramWithValues)==false)
        {
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Update Not done.";
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.status = false;
        this.baseController.serviceResponse.responseStat.msg = "Job Table update successfully.";
        this.pw.print(this.baseController.getResponse());
        return;

    }

    private void removeJobs() {

    }

    private void addJobs() {

        String imgBase64 = "";
        String fileRelativePath = "";
        String title= "";
        String description = "";
        float price = 0;

        if(!this.baseController.checkParam("title",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "title is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            title = req.getParameter("title");
        }

        if(this.baseController.checkParam("description",this.req,true)) {
            description = req.getParameter("description");
        }



        if(!this.baseController.checkParam("price",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "price is required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                price =  parseInt(req.getParameter("price"));
            }catch (Exception ex){

                this.baseController.serviceResponse.responseStat.msg = "price is float required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                ex.printStackTrace();
                return;

            }

        }


        Pictures pictures = new Pictures();

        if(this.baseController.checkParam("icon",this.req,true)) {
            imgBase64 = this.req.getParameter("icon");
            fileRelativePath = "";
            System.out.println("photo received");
            pictures = ImageHelper.saveJobIcon(imgBase64, this.baseController.appCredential.id);
            System.out.println("photo Saved");



            if (pictures.original.path == "") {
                System.out.println("Unable to save the Image : "+fileRelativePath);

                this.baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        JobModel jobModel = new JobModel();

        jobModel.setApp_login_credential_id(this.baseController.appCredential.id);
        jobModel.setTitle(title);
        jobModel.setDescription(description);
        jobModel.setPrice(price);
        jobModel.setIcon(this.gson.toJson(pictures));

        if(jobModel.isExist()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Job already added";
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(jobModel.insert()==0){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseStat.msg = "Job added successfully";
        this.baseController.serviceResponse.responseData = jobModel.getAllById();
        this.pw.print(this.baseController.getResponse());
        return;
    }

    private void findAllJobs() {

    }
}
