package controller.service;

import com.google.gson.*;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import helper.ImageHelper;
import model.*;
import model.datamodel.app.AppCredential;
import model.datamodel.app.Location;
import model.datamodel.app.PostComment;
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
import java.util.HashMap;

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
            case "/app/wallpost/get/recent":
                this.getRecentPost();
                break;
            case "/app/wallpost/get/others":
                this.getOthersPost();
                break;
            case "/app/wallpost/test":
                this.test();
                break;
            case "/app/wallpost/create/comment":
                this.creatComment();
                break;
            case "/app/wallpost/get/comment":
                this.getComments(true);
                break;
            case "/app/wallpost/get/comment/all":
                this.getComments(false);
                break;
            case "/app/wallpost/delete/comment":
                this.deleteComment();
                break;
            case "/app/wallpost/like":
                this.likePost();
                break;
            case "/app/wallpost/get/likes":
                this.getLikes();
                break;
            case "/app/wallpost/get/comment/count":
                this.getCommentCount();
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
                fileRelativePath= pictures.original.path;
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
    private void getRecentPost(){

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
        wallPostModel.setCurrentUserId(this.baseController.appCredential.id);
        ArrayList<WallPost> wallPostList =  wallPostModel.getAllRecent();

        this.baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        this.baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        this.baseController.serviceResponse.responseData =  wallPostList;
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
        wallPostModel.setCurrentUserId(this.baseController.appCredential.id);
        ArrayList<WallPost> wallPostList =  wallPostModel.getByOwner_id();
        this.baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        this.baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        this.baseController.serviceResponse.responseData =  wallPostList;
        this.pw.print(this.baseController.getResponse());
        return;
    }



    public void creatComment(){
        if(!this.baseController.checkParam("post_id", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "post_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!this.baseController.checkParam("comment", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "comment required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        PostCommentModel postCommentModel = new PostCommentModel();
        postCommentModel.setComment(this.req.getParameter("comment"));
        postCommentModel.setCommenter_id(this.baseController.appCredential.id);

        try{
            postCommentModel.setPost_id(Integer.parseInt(this.req.getParameter("post_id")));
        }catch(Exception ex){
            this.baseController.serviceResponse.responseStat.msg = "post_id int format required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(postCommentModel.getPost_id());

        if(!wallPostModel.isIdExist()){
            this.baseController.serviceResponse.responseStat.msg = "Post id does not exist";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(postCommentModel.insert()==0){
            this.baseController.serviceResponse.responseStat.msg = "Unable to comment on the post,database error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }



        this.baseController.serviceResponse.responseStat.msg = "Comment posted";
        this.baseController.serviceResponse.responseData = postCommentModel.getByPostId();
        this.pw.print(this.baseController.getResponse());
        return;

    }
    public void likePost(){
        if(!this.baseController.checkParam("post_id", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "post_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        PostLikeModel postLikeModel = new PostLikeModel();
        postLikeModel.setLiker_id(this.baseController.appCredential.id);
        try{

            postLikeModel.setPost_id(Integer.parseInt(this.req.getParameter("post_id")));
        }catch(Exception ex){
            this.baseController.serviceResponse.responseStat.msg = "post_id int format required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        String msg = "";
        boolean isLiked = false;
        if(postLikeModel.isAlreadyLiked()){
            if(postLikeModel.delete()==0){
                this.baseController.serviceResponse.responseStat.msg = "Database error on delete";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
            msg = "You have undo your like";
        }else{
            if(postLikeModel.insert()==0){
                this.baseController.serviceResponse.responseStat.msg = "Unable to like the post,database error";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }else{
                msg = "Successfully liked";
                isLiked = true;
            }
        }

        JsonObject respObj = new JsonObject();
        respObj.addProperty("likeCount", postLikeModel.getLikeCountByPostId());
        respObj.addProperty("isLiked",isLiked);

        this.baseController.serviceResponse.responseStat.msg = msg;
        this.baseController.serviceResponse.responseData = respObj;
        this.pw.print(this.baseController.getResponse());
        return;

    }
    public void getLikes(){

        if(!this.baseController.checkParam("post_id", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "post_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }


        PostLikeModel postLikeModel = new PostLikeModel();
        try{

            postLikeModel.setPost_id(Integer.parseInt(this.req.getParameter("post_id")));
        }catch(Exception ex){
            this.baseController.serviceResponse.responseStat.msg = "post_id int format required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseData = postLikeModel.getLikersByPostId();
        this.pw.print(this.baseController.getResponse());
        return;

    }
    public void getCommentCount(){



        PostCommentModel postCommentModel = new PostCommentModel();

        if(!this.baseController.checkParam("post_id", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "post_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }



        try{
            postCommentModel.setPost_id(Integer.parseInt(this.req.getParameter("post_id")));
        }catch(Exception ex){
            this.baseController.serviceResponse.responseStat.msg = "post_id int format required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        HashMap<String,Integer> commnetCountResponse = new HashMap();
        commnetCountResponse.put("likeCount",postCommentModel.getCountByPostId());

        this.baseController.serviceResponse.responseData = commnetCountResponse;
        this.pw.print(this.baseController.getResponse());
        return;

    }
    public void getComments(boolean pagination){




        if(!this.baseController.checkParam("post_id", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "post_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        PostCommentModel postCommentModel = new PostCommentModel();

        try{
            postCommentModel.setPost_id(Integer.parseInt(this.req.getParameter("post_id")));
        }catch(Exception ex){
            this.baseController.serviceResponse.responseStat.msg = "post_id int format required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        if(pagination){
            if(this.baseController.checkParam("limit", this.req, true)) {
                try{
                    postCommentModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }else{
                postCommentModel.limit = 3;
            }

            if(!this.baseController.checkParam("offset", this.req, true)){

                this.baseController.serviceResponse.responseStat.msg = "offset required";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }else {
                try{
                    postCommentModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                    this.baseController.serviceResponse.responseStat.status = false;
                    this.pw.print(this.baseController.getResponse());
                    return;
                }
            }
        }


        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(postCommentModel.getPost_id());

        if(!wallPostModel.isIdExist()){
            this.baseController.serviceResponse.responseStat.msg = "Post id does not exist";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        ArrayList<PostComment> postComments = new ArrayList();
        postComments  = postCommentModel.getByPostId();
        this.baseController.serviceResponse.responseStat.status = (postComments.size()>0);
        this.baseController.serviceResponse.responseStat.msg = (postComments.size()>0)?"":"No comment found";
        this.baseController.serviceResponse.responseData = postComments;
        this.pw.print(this.baseController.getResponse());
        return;

    }
    public void deleteComment(){




        if(!this.baseController.checkParam("comment_id", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "comment_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        PostCommentModel postCommentModel = new PostCommentModel();

        try{
            postCommentModel.setId(Integer.parseInt(this.req.getParameter("comment_id")));
        }catch(Exception ex){
            this.baseController.serviceResponse.responseStat.msg = "comment_id int format required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        postCommentModel.setCommenter_id(this.baseController.appCredential.id);

        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(postCommentModel.getPostIdById());
        wallPostModel.setOwner_id(this.baseController.appCredential.id);

        if(!wallPostModel.isIdExist()){
            this.baseController.serviceResponse.responseStat.msg = "Post id does not exist";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        boolean commenter = false;
        if(!postCommentModel.isCommenter()){
            this.baseController.serviceResponse.responseStat.msg = "You are not owner nor the commenter";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());

            return;
        }else{
            commenter = true;
        }

        if(!commenter && !wallPostModel.isWallPostOwner()){
            this.baseController.serviceResponse.responseStat.msg = "You are not owner nor the commenter";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        postCommentModel.startTransaction();

        if(postCommentModel.deleteById()==0){
            postCommentModel.rollBack();
            this.baseController.serviceResponse.responseStat.msg = "Internal server error";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        postCommentModel.commitTransaction();

        this.baseController.serviceResponse.responseStat.msg ="Comment deleted";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void test(){
        if(!this.baseController.checkParam("phone_number", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "Phone number required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!this.baseController.checkParam("token",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Token required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(!this.baseController.checkParam("first_name",this.req,true)) {
            this.baseController.serviceResponse.responseStat.msg = "Name required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        ActivationModel activationModel = new ActivationModel();

        if(!activationModel.setPhoneNumber(this.req.getParameter("phone_number"))){
            this.baseController.serviceResponse.responseStat.msg = "Phone number format miss matched";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        System.out.println("'" + this.req.getParameter("token")+"'");
        System.out.println("'"+this.req.getParameter("phone_number")+"'");
        activationModel.setActivationCode(this.req.getParameter("token"));

        if(!activationModel.isTokenValid()){
            this.baseController.serviceResponse.responseStat.msg = "Token miss matched";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        UserInfModel userInfModel = new UserInfModel();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        String imgBase64 = "";

        userInfModel.setF_name(this.req.getParameter("first_name"));
        userInfModel.setL_name(this.req.getParameter("last_name"));

         /*  transaction started */

        //  userInfModel.startTransaction();
        userInfModel.insertData();
        if(userInfModel.getId()==0){
            this.baseController.serviceResponse.responseStat.msg = "Internal server error on userInfModel";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        //  appLoginCredentialModel.startTransaction();
        appLoginCredentialModel.setU_id(userInfModel.getId());
        appLoginCredentialModel.setPhone_number(activationModel.getPhoneNumber());
        if(appLoginCredentialModel.isNumberExist()){
            this.baseController.serviceResponse.responseStat.msg = "Number already used";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
         /*  transaction started */


        appLoginCredentialModel.insert();
        System.out.println("02");
        if(appLoginCredentialModel.getId()==0){
            //       userInfModel.rollBack();
            this.baseController.serviceResponse.responseStat.msg = "Internal server error on appLoginCredentialModel";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        if(this.baseController.checkParam("photo",this.req,true)) {
            imgBase64 = this.req.getParameter("photo");
            Pictures pictures = ImageHelper.saveProfilePicture(imgBase64, userInfModel.getId());
            Gson gson = new Gson();
            String fileName =gson.toJson(pictures);
            if(fileName==null || fileName == ""){

                 /* All transaction rollback */

                //    userInfModel.rollBack();
                //    appLoginCredentialModel.rollBack();

                this.baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }

            userInfModel.setPicPath(fileName);
            if(!userInfModel.updatePicPath()){
                /* All transaction rollback */

                //    userInfModel.rollBack();
                //    appLoginCredentialModel.rollBack();

                this.baseController.serviceResponse.responseStat.msg = "Internal server error on picture path update";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        /* Commit database transaction */

        //  userInfModel.commitTransaction();
        // appLoginCredentialModel.commitTransaction();


        this.baseController.serviceResponse.responseStat.msg = "Registration success";
        this.baseController.serviceResponse.responseData = appLoginCredentialModel.getAppCredentialById();
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
