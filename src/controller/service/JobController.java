package controller.service;

import com.google.gson.Gson;
import helper.ImageHelper;
import model.JobModel;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

/**
 * Created by IntelliJ IDEA.
 * User: rajib
 * Email: rajibcse2k10@gmail.com
 * Date: 10/29/15
 * Project Name:ImageTalk
 */
public class JobController extends HttpServlet {
/*    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;*/
    Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {

        ImageTalkBaseController baseController = new ImageTalkBaseController();
        res.setContentType("application/json");
        baseController = new ImageTalkBaseController();
        PrintWriter pw = res.getWriter();

        this.gson = new Gson();

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }
        switch (url)
        {
            case "app/user/job/findAllJobs":
                pw.print(this.findAllJobs(req));
                break;
            case "/app/user/job/add":
                pw.print(this.addJobs(req));
                break;
            case "app/user/job/remove":
                pw.print(this.removeJobs(req));
                break;
            case "/app/user/job/update":
                pw.print(this.updateJobs(req));
                break;
            default:
                break;
        }


    }

    private String updateJobs(HttpServletRequest req) {
        ImageTalkBaseController baseController  = new ImageTalkBaseController(req);
        Enumeration<String> parameterNames = req.getParameterNames();
        HashMap<String, String> paramWithValues = new HashMap<String, String>();

        while (parameterNames.hasMoreElements())
        {
            String paramName = parameterNames.nextElement();
            if(paramName.equals("title"))
            {
                if(!baseController.checkParam(paramName,req,true)) {
                    baseController.serviceResponse.responseStat.msg = "title is required";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }else{
                    paramWithValues.put(paramName,req.getParameter("title"));
                }

            }
            else if(paramName.equals("description"))
            {
                if(req.getParameter("description")==null)
                    paramWithValues.put(paramName,"");
                else
                    paramWithValues.put(paramName,req.getParameter("description"));
            }
            else if(paramName.equals("price"))
            {
                int price;
                if(!baseController.checkParam("price",req,true)) {
                    baseController.serviceResponse.responseStat.msg = "price is required";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }else{
                    try{
                        price =  parseInt(req.getParameter("price"));
                    }catch (Exception ex){

                        baseController.serviceResponse.responseStat.msg = "price is float required";
                        baseController.serviceResponse.responseStat.status = false;
                        ex.printStackTrace();
                        return baseController.getResponse();

                    }

                }
                if(price<1)
                {
                    baseController.serviceResponse.responseStat.msg = "price can't be Zero or negative ";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
                paramWithValues.put(paramName,Integer.toString(price));

            }
            else
            {
                baseController.serviceResponse.responseStat.msg = "Please provide valid parameter name to update";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        if (paramWithValues.isEmpty())
        {
            baseController.serviceResponse.responseStat.msg = "Please provide at least one parameter to update";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        JobModel jobModel = new JobModel();
        jobModel.setApp_login_credential_id(baseController.appCredential.id);
        if(jobModel.update(paramWithValues)==false)
        {
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Update Not done.";
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.status = false;
        baseController.serviceResponse.responseStat.msg = "Job Table update successfully.";
        return baseController.getResponse();

    }

    private String removeJobs(HttpServletRequest req) {

    return "";
    }

    private String addJobs(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        String imgBase64 = "";
        String fileRelativePath = "";
        String title= "";
        String description = "";
        float price = 0;

        if(!baseController.checkParam("title",req,true)) {
            baseController.serviceResponse.responseStat.msg = "title is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            title = req.getParameter("title");
        }

        if(baseController.checkParam("description",req,true)) {
            description = req.getParameter("description");
        }



        if(!baseController.checkParam("price",req,true)) {
            baseController.serviceResponse.responseStat.msg = "price is required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            try{
                price =  parseInt(req.getParameter("price"));
            }catch (Exception ex){

                baseController.serviceResponse.responseStat.msg = "price is float required";
                baseController.serviceResponse.responseStat.status = false;
                ex.printStackTrace();
                return baseController.getResponse();

            }

        }


        Pictures pictures = new Pictures();

        if(baseController.checkParam("icon",req,true)) {
            imgBase64 = req.getParameter("icon");
            fileRelativePath = "";
            System.out.println("photo received");
            pictures = ImageHelper.saveJobIcon(imgBase64, baseController.appCredential.id);
            System.out.println("photo Saved");



            if (pictures.original.path == "") {
                System.out.println("Unable to save the Image : "+fileRelativePath);

                baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        JobModel jobModel = new JobModel();

        jobModel.setApp_login_credential_id(baseController.appCredential.id);
        jobModel.setTitle(title);
        jobModel.setDescription(description);
        jobModel.setPrice(price);
        jobModel.setIcon(this.gson.toJson(pictures));

        if(jobModel.isExist()){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Job already added";
            return baseController.getResponse();
        }
        if(jobModel.insert()==0){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg = "Job added successfully";
        baseController.serviceResponse.responseData = jobModel.getAllById();
        return baseController.getResponse();
    }

    private String findAllJobs(HttpServletRequest req) {

        return "";
    }
}
