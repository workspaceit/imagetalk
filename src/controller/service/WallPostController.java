package controller.service;

import com.google.gson.*;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import helper.ImageHelper;
import model.AppLoginCredentialModel;
import model.LocationModel;
import model.TagListModel;
import model.WallPostModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.Location;
import model.datamodel.app.WallPost;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mi on 10/2/15.
 */
public class WallPostController extends HttpServlet {
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
            case "/app/wallpost/create":
                this.create();
                break;
            case "/app/wallpost/get/own":
                this.getOwnPost();
                break;
            case "/app/wallpost/get/others":
                this.getOthersPost();
                break;
            case "/app/wallpost/test":
                this.test();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    public void create(){
        if(!this.baseController.checkParam("description", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "description required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        String imgBase64 = "";
        String fileRelativePath = "";
        ArrayList<Integer> taggedList = new ArrayList<Integer>();


        if(this.baseController.checkParam("photo", this.req, true)){
            if(this.baseController.checkParam("photo",this.req,true)) {
                imgBase64 = this.req.getParameter("photo");
                fileRelativePath = "";

                Pictures pictures = ImageHelper.saveWallPostPicture(imgBase64, this.baseController.appCredential.id);
                Gson gson = new Gson();
                System.out.println(gson.toJson(pictures));
                if (fileRelativePath == "") {

                    // Need roll back
                    this.baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }
        }


        if(this.baseController.checkParam("tagged_list", this.req, true)){

            String tagged_listStr = this.req.getParameter("tagged_list");

            Gson gson = new Gson();
            try{
                int[] tempTaggedList = gson.fromJson(tagged_listStr,int[].class);
                AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
                for(int tempTaggedId : tempTaggedList){
                    appLoginCredentialModel.setId(tempTaggedId);
                    if(appLoginCredentialModel.isIdExist()){
                        taggedList.add(tempTaggedId);
                    }else{
                        System.out.println("Trying to tag a non exit app_credential_id "+tempTaggedId);
                    }
                }
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "tagged_list not in format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        if(taggedList.contains(this.baseController.appCredential.id)){
            this.baseController.serviceResponse.responseStat.msg = "You are not allowed to tag you self";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        /*===============  Insert location here ==============*/
        LocationModel locationModel = new LocationModel();
        if(this.baseController.checkParam("location", this.req, true)){

            String loocationStr = this.req.getParameter("location");

            Gson gson = new Gson();
            try{
                Location location = gson.fromJson(loocationStr,Location.class);



                locationModel.setLat(location.lat);
                locationModel.setLng(location.lng);
                locationModel.setFormatted_address(location.formattedAddress);
                locationModel.setCountry(location.countryName);
                if(locationModel.insert()==0){
                    this.baseController.serviceResponse.responseStat.msg = "Internal server error";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;

                }
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "location is not in format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        /*======================================================*/


        System.out.println("fileName :" + fileRelativePath);

        WallPostModel wallPostModel = new WallPostModel();

        wallPostModel.setOwner_id(this.baseController.appCredential.id);
        wallPostModel.setDescrption(this.req.getParameter("description"));
        wallPostModel.setPicture_path(fileRelativePath);
        wallPostModel.setLocation_id(locationModel.getId());


        if(wallPostModel.insert()==0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        /* ============== Insert Tagged user =============== */

        TagListModel tagListModel = new TagListModel();
        for(Integer tagged :  taggedList){
            tagListModel.setTag_id(tagged.intValue());
            tagListModel.setPost_id(wallPostModel.getId());

            tagListModel.insert();
        }
        /*===================================================*/
        this.baseController.serviceResponse.responseStat.msg = "Wall post created";
        this.baseController.serviceResponse.responseData = wallPostModel.getById();
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getOwnPost(){

        WallPostModel wallPostModel = new WallPostModel();


        if(this.baseController.checkParam("limit", this.req, true)) {
            try{
                wallPostModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            wallPostModel.limit = 3;
        }

        if(!this.baseController.checkParam("offset", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "offset required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else {
            try{
                wallPostModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        wallPostModel.setOwner_id(this.baseController.appCredential.id);
        ArrayList<WallPost> wallPostList =  wallPostModel.getByOwner_id();

        this.baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        this.baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        this.baseController.serviceResponse.responseData =  wallPostList;
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getOthersPost(){

        WallPostModel wallPostModel = new WallPostModel();

        if(this.baseController.checkParam("limit", this.req, true)) {
            try{
                wallPostModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            wallPostModel.limit = 3;
        }

        if(!this.baseController.checkParam("offset", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "offset required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else {
            try{
                wallPostModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }


        if(!this.baseController.checkParam("other_app_credential_id", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "other_app_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                wallPostModel.setOwner_id(Integer.parseInt(this.req.getParameter("other_app_credential_id")));
            }catch (Exception ex){
                this.baseController.serviceResponse.responseStat.msg = "other_app_credential_id not integer required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        ArrayList<WallPost> wallPostList =  wallPostModel.getByOwner_id();
        this.baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        this.baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        this.baseController.serviceResponse.responseData =  wallPostList;
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void test(){
        GoogleGeoApi googleGeoApi = new GoogleGeoApi();
        googleGeoApi.setKeyWord(this.req.getParameter("location"));



        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setOwner_id(1);
//

        ArrayList<Location> addressList = googleGeoApi.getLocationByKeyword();

        this.baseController.serviceResponse.responseStat.msg =(addressList.size()<=0)?"No record found":"";
        this.baseController.serviceResponse.responseStat.status = (addressList.size()<=0)?false:true;
        this.baseController.serviceResponse.responseData =  addressList;
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
