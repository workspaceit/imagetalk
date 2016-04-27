package controller.service;

import com.google.gson.*;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import helper.ImageHelper;
import model.*;
import model.datamodel.app.*;
import model.datamodel.photo.Pictures;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mi on 10/2/15.
 */
public class WallPostController extends HttpServlet {

/*    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;*/

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setCharacterEncoding("utf8");

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        PrintWriter pw = res.getWriter();

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (url) {
            case "/app/wallpost/create":
                pw.print(this.create(req));
                break;
            case "/app/wallpost/delete":
                pw.print(this.deleteWallPost(req));
                break;

            case "/app/wallpost/get/own":
                pw.print(this.getOwnPost(req));
                break;
            case "/app/wallpost/get/favorite":
                this.getFavoritePost(req);
                break;
            case "/app/wallpost/get/recent":
                pw.print(this.getRecentPost(req));
                break;
            case "/app/wallpost/get/others":
                pw.print(this.getOthersPost(req));
                break;
            case "/app/wallpost/test":
                pw.print(this.test(req));
                break;
            case "/app/wallpost/create/comment":
                pw.print(this.creatComment(req));
                break;
            case "/app/wallpost/get/comment":
                pw.print(this.getComments(req, true));
                break;
            case "/app/wallpost/get/comment/all":
                pw.print(this.getComments(req, false));
                break;
            case "/app/wallpost/delete/comment":
                pw.print(this.deleteComment(req));
                break;
            case "/app/wallpost/like":
                pw.print(this.likePost(req));
                break;
            case "/app/wallpost/get/likes":
                pw.print(this.getLikes(req));
                break;
            case "/app/wallpost/get/comment/count":
                pw.print(this.getCommentCount(req));
                break;
            case "/app/wallpost/add/remove/favourite":
                pw.print(this.favourWallPost(req));
                break;
            case "/app/wallpost/hide":
                pw.print(this.hidePost(req));
                break;
            case "/app/wallpost/count/byownerid":
                pw.print(this.wallpostCountByOwnerId(req));
                break;

            default:
                break;
        }
        pw.close();
    }

    public String create(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        String imgBase64 = "";
        String fileRelativePath = "";
        ArrayList<Integer> taggedList = new ArrayList<Integer>();

        if(!baseController.checkParam("type", req, true)){
            baseController.serviceResponse.responseStat.msg = "type required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

            if(baseController.checkParam("photo",req,true)) {
                imgBase64 = req.getParameter("photo");
                fileRelativePath = "";
                System.out.println("photo received");
                Pictures pictures = ImageHelper.saveWallPostPicture(imgBase64, baseController.appCredential.id);
                System.out.println("photo Saved");
                fileRelativePath= pictures.original.path;
                Gson gson = new Gson();
                System.out.println("fileRelativePath : "+fileRelativePath);
                if (fileRelativePath == "") {
                    System.out.println("Unable to save the Image : "+fileRelativePath);
                    // Need roll back
                    baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();
                }
            }



        if(baseController.checkParam("tagged_list", req, true)){

            String tagged_listStr = req.getParameter("tagged_list");
            System.out.println("At tagged_list  ");
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
                baseController.serviceResponse.responseStat.msg = "tagged_list not in format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        if(taggedList.contains(baseController.appCredential.id)){
            baseController.serviceResponse.responseStat.msg = "You are not allowed to tag you self";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        /*===============  Insert location here ==============*/
        LocationModel locationModel = new LocationModel();

        if(baseController.checkParam("places", req, true)){
            System.out.println("At location  ");
            String locationStr = req.getParameter("places");

            Gson gson = new Gson();
            try{
                Places places = gson.fromJson(locationStr,Places.class);


                locationModel.setPlace_id(places.placeId);
                locationModel.setIcon(places.icon);
                locationModel.setName(places.name);
                locationModel.setGoogle_place_id(places.googlePlaceId);
                locationModel.setLat(places.lat);
                locationModel.setLng(places.lng);
                locationModel.setFormatted_address(places.formattedAddress);
                locationModel.setCountry(places.countryName);
                if(locationModel.insert()==0){
                    baseController.serviceResponse.responseStat.msg = "Internal server error";
                    baseController.serviceResponse.responseStat.status = false;
                    return baseController.getResponse();

                }
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "location is not in format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        /*======================================================*/




        WallPostModel wallPostModel = new WallPostModel();

        wallPostModel.setOwner_id(baseController.appCredential.id);

        if(baseController.checkParam("description", req, true)){
            try {
                wallPostModel.setDescrption(URLDecoder.decode(new String(req.getParameter("description").getBytes("UTF-8"),"UTF-8"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                baseController.serviceResponse.responseStat.msg = "Unable to handel Description text";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        if(baseController.checkParam("wall_post_mood", req, true)){
            try {
                wallPostModel.setWallPostMood(URLDecoder.decode(new String(req.getParameter("wall_post_mood").getBytes("UTF-8"),"UTF-8"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                baseController.serviceResponse.responseStat.msg = "Unable to handel Wall Post Mood text";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        try{
            wallPostModel.setType(Integer.parseInt(req.getParameter("type")));
        }catch (Exception ex){
            ex.printStackTrace();
            baseController.serviceResponse.responseStat.msg = "type int required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();

        }



        wallPostModel.setPicture_path(fileRelativePath);
        wallPostModel.setLocation_id(locationModel.getId());


        if(wallPostModel.insert()==0){
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        /* ============== Insert Tagged user =============== */

        TagListModel tagListModel = new TagListModel();
        for(Integer tagged :  taggedList){
            tagListModel.setTag_id(tagged.intValue());
            tagListModel.setPost_id(wallPostModel.getId());

            tagListModel.insert();
        }
        /*===================================================*/
        baseController.serviceResponse.responseStat.msg = "Wall post created";
        baseController.serviceResponse.responseData = wallPostModel.getById();

        System.out.println("Response Sent");
        return baseController.getResponse();
    }
    private String getRecentPost(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController();
        WallPostModel wallPostModel = new WallPostModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                wallPostModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            wallPostModel.limit = 3;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                wallPostModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        wallPostModel.setCurrentUserId(baseController.appCredential.id);
        ArrayList<WallPost> wallPostList =  wallPostModel.getAllRecent();

        baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        baseController.serviceResponse.responseData =  wallPostList;
        return baseController.getResponse();
    }

    private String getOwnPost(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        WallPostModel wallPostModel = new WallPostModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                wallPostModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            wallPostModel.limit = 3;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                wallPostModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        wallPostModel.setOwner_id(baseController.appCredential.id);
        ArrayList<WallPost> wallPostList =  wallPostModel.getByOwner_id();

        baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        baseController.serviceResponse.responseData =  wallPostList;
        return baseController.getResponse();
    }

    private String getFavoritePost(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        WallPostModel wallPostModel = new WallPostModel();


        if(baseController.checkParam("limit", req, true)) {
            try{
                wallPostModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;

                return baseController.getResponse();
            }
        }else{
            wallPostModel.limit = 3;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                wallPostModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        wallPostModel.setOwner_id(baseController.appCredential.id);
        ArrayList<WallPost> wallPostList =  wallPostModel.getAllFavoriteByOwnerId();

        baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        baseController.serviceResponse.responseData =  wallPostList;
        return baseController.getResponse();
    }
    private String getOthersPost(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();

        WallPostModel wallPostModel = new WallPostModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                wallPostModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            wallPostModel.limit = 3;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                wallPostModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }


        if(!baseController.checkParam("other_app_credential_id", req, true)){

            baseController.serviceResponse.responseStat.msg = "other_app_credential_id required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }else{
            try{
                wallPostModel.setOwner_id(Integer.parseInt(req.getParameter("other_app_credential_id")));
            }catch (Exception ex){
                baseController.serviceResponse.responseStat.msg = "other_app_credential_id not integer required";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }
        wallPostModel.setCurrentUserId(baseController.appCredential.id);
        ArrayList<WallPost> wallPostList =  wallPostModel.getByOwner_id();
        baseController.serviceResponse.responseStat.msg =(wallPostList.size()<=0)?"No record found":"";
        baseController.serviceResponse.responseStat.status = (wallPostList.size()<=0)?false:true;
        baseController.serviceResponse.responseData =  wallPostList;
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();
    }



    public String creatComment(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        if(!baseController.checkParam("post_id", req, true)){

            baseController.serviceResponse.responseStat.msg = "post_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(!baseController.checkParam("comment", req, true)){

            baseController.serviceResponse.responseStat.msg = "comment required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        PostCommentModel postCommentModel = new PostCommentModel();
        try {
            postCommentModel.setComment(URLDecoder.decode(new String(req.getParameter("comment").getBytes("UTF-8"), "UTF-8"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            baseController.serviceResponse.responseStat.msg = "Unable to encode comment";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        postCommentModel.setCommenter_id(baseController.appCredential.id);

        try{
            postCommentModel.setPost_id(Integer.parseInt(req.getParameter("post_id")));
        }catch(Exception ex){
            baseController.serviceResponse.responseStat.msg = "post_id int format required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(postCommentModel.getPost_id());

        if(!wallPostModel.isIdExist()){
            baseController.serviceResponse.responseStat.msg = "Post id does not exist";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        if(postCommentModel.insert()==0){
            baseController.serviceResponse.responseStat.msg = "Unable to comment on the post,database error";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }



        baseController.serviceResponse.responseStat.msg = "Comment posted";
        baseController.serviceResponse.responseData = postCommentModel.getByPostId();
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();

    }
    public String likePost(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("post_id",req, true)){

            baseController.serviceResponse.responseStat.msg = "post_id required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        PostLikeModel postLikeModel = new PostLikeModel();
        postLikeModel.setLiker_id(baseController.appCredential.id);
        try{

            postLikeModel.setPost_id(Integer.parseInt(req.getParameter("post_id")));
        }catch(Exception ex){
            baseController.serviceResponse.responseStat.msg = "post_id int format required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        String msg = "";
        boolean isLiked = false;
        if(postLikeModel.isAlreadyLiked()){
            if(postLikeModel.delete()==0){
                baseController.serviceResponse.responseStat.msg = "Database error on delete";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
            msg = "You have undo your like";
        }else{
            if(postLikeModel.insert()==0){
                baseController.serviceResponse.responseStat.msg = "Unable to like the post,database error";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }else{
                msg = "Successfully liked";
                isLiked = true;
            }
        }

        JsonObject respObj = new JsonObject();
        respObj.addProperty("likeCount", postLikeModel.getLikeCountByPostId());
        respObj.addProperty("isLiked",isLiked);

        baseController.serviceResponse.responseStat.msg = msg;
        baseController.serviceResponse.responseData = respObj;
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();

    }
    public String getLikes(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        if(!baseController.checkParam("post_id", req, true)){

            baseController.serviceResponse.responseStat.msg = "post_id required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        PostLikeModel postLikeModel = new PostLikeModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                postLikeModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }else{
            postLikeModel.limit = 10;
        }

        if(!baseController.checkParam("offset",req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }else {
            try{
                postLikeModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }
        }

        try{

            postLikeModel.setPost_id(Integer.parseInt(req.getParameter("post_id")));
        }catch(Exception ex){
            baseController.serviceResponse.responseStat.msg = "post_id int format required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseData = postLikeModel.getLikersByPostId();
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();

    }
    public String getCommentCount(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        PostCommentModel postCommentModel = new PostCommentModel();

        if(!baseController.checkParam("post_id", req, true)){
            baseController.serviceResponse.responseStat.msg = "post_id required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        try{
            postCommentModel.setPost_id(Integer.parseInt(req.getParameter("post_id")));
        }catch(Exception ex){
            baseController.serviceResponse.responseStat.msg = "post_id int format required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        HashMap<String,Integer> commnetCountResponse = new HashMap();
        commnetCountResponse.put("likeCount",postCommentModel.getCountByPostId());

        baseController.serviceResponse.responseData = commnetCountResponse;
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();

    }
    public String getComments(HttpServletRequest req, boolean pagination){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        if(!baseController.checkParam("post_id", req, true)){

            baseController.serviceResponse.responseStat.msg = "post_id required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        PostCommentModel postCommentModel = new PostCommentModel();

        try{
            postCommentModel.setPost_id(Integer.parseInt(req.getParameter("post_id")));
        }catch(Exception ex){
            baseController.serviceResponse.responseStat.msg = "post_id int format required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        if(pagination){
            if(baseController.checkParam("limit", req, true)) {
                try{
                    postCommentModel.limit = Integer.parseInt(req.getParameter("limit").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    //this.pw.print(this.baseController.getResponse());
                    return baseController.getResponse();
                }
            }else{
                postCommentModel.limit = 3;
            }

            if(!baseController.checkParam("offset", req, true)){

                baseController.serviceResponse.responseStat.msg = "offset required";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.baseController.getResponse());
                return baseController.getResponse();
            }else {
                try{
                    postCommentModel.offset = Integer.parseInt(req.getParameter("offset").trim());
                }catch (Exception ex){
                    System.out.println(ex);
                    baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                    baseController.serviceResponse.responseStat.status = false;
                    //this.pw.print(this.baseController.getResponse());
                    return baseController.getResponse();
                }
            }
        }


        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(postCommentModel.getPost_id());

        if(!wallPostModel.isIdExist()){
            baseController.serviceResponse.responseStat.msg = "Post id does not exist";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        ArrayList<PostComment> postComments = new ArrayList();
        postComments  = postCommentModel.getByPostId();
        baseController.serviceResponse.responseStat.status = (postComments.size()>0);
        baseController.serviceResponse.responseStat.msg = (postComments.size()>0)?"":"No comment found";
        baseController.serviceResponse.responseData = postComments;
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();

    }
    public String deleteComment(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        if(!baseController.checkParam("comment_id", req, true)){

            baseController.serviceResponse.responseStat.msg = "comment_id required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        PostCommentModel postCommentModel = new PostCommentModel();

        try{
            postCommentModel.setId(Integer.parseInt(req.getParameter("comment_id")));
        }catch(Exception ex){
            baseController.serviceResponse.responseStat.msg = "comment_id int format required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        postCommentModel.setCommenter_id(baseController.appCredential.id);

        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setId(postCommentModel.getPostIdById());
        wallPostModel.setOwner_id(baseController.appCredential.id);

        if(!wallPostModel.isIdExist()){
            baseController.serviceResponse.responseStat.msg = "Post id does not exist";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        boolean commenter = false;
        if(!postCommentModel.isCommenter()){
            baseController.serviceResponse.responseStat.msg = "You are not owner nor the commenter";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            commenter = true;
        }

        if(!commenter && !wallPostModel.isWallPostOwner()){
            baseController.serviceResponse.responseStat.msg = "You are not owner nor the commenter";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        postCommentModel.startTransaction();

        if(postCommentModel.deleteById()==0){
            postCommentModel.rollBack();
            baseController.serviceResponse.responseStat.msg = "Internal server error";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }

        postCommentModel.commitTransaction();

        baseController.serviceResponse.responseStat.msg ="Comment deleted";
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();

    }
    private String test(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("phone_number", req, true)) {
            baseController.serviceResponse.responseStat.msg = "Phone number required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(!baseController.checkParam("token",req,true)) {
            baseController.serviceResponse.responseStat.msg = "Token required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(!baseController.checkParam("first_name",req,true)) {
            baseController.serviceResponse.responseStat.msg = "Name required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        ActivationModel activationModel = new ActivationModel();

        if(!activationModel.setPhoneNumber(req.getParameter("phone_number"))){
            baseController.serviceResponse.responseStat.msg = "Phone number format miss matched";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        System.out.println("'" + req.getParameter("token")+"'");
        System.out.println("'"+req.getParameter("phone_number")+"'");
        activationModel.setActivationCode(req.getParameter("token"));

        if(!activationModel.isTokenValid()){
            baseController.serviceResponse.responseStat.msg = "Token miss matched";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        UserInfModel userInfModel = new UserInfModel();
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        String imgBase64 = "";

        userInfModel.setF_name(req.getParameter("first_name"));
        userInfModel.setL_name(req.getParameter("last_name"));

         /*  transaction started */

        //  userInfModel.startTransaction();
        userInfModel.insertData();
        if(userInfModel.getId()==0){
            baseController.serviceResponse.responseStat.msg = "Internal server error on userInfModel";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        //  appLoginCredentialModel.startTransaction();
        appLoginCredentialModel.setU_id(userInfModel.getId());
        appLoginCredentialModel.setPhone_number(activationModel.getPhoneNumber());
        if(appLoginCredentialModel.isNumberExist()){
            baseController.serviceResponse.responseStat.msg = "Number already used";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
         /*  transaction started */


        appLoginCredentialModel.insert();
        System.out.println("02");
        if(appLoginCredentialModel.getId()==0){
            //       userInfModel.rollBack();
            baseController.serviceResponse.responseStat.msg = "Internal server error on appLoginCredentialModel";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        if(baseController.checkParam("photo",req,true)) {
            imgBase64 = req.getParameter("photo");
            Pictures pictures = ImageHelper.saveProfilePicture(imgBase64, userInfModel.getId());
            Gson gson = new Gson();
            String fileName =gson.toJson(pictures);
            if(fileName==null || fileName == ""){

                 /* All transaction rollback */

                //    userInfModel.rollBack();
                //    appLoginCredentialModel.rollBack();

                baseController.serviceResponse.responseStat.msg = "Unable to save the Image";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.this.ba.getResponse());
                return baseController.getResponse();
            }

            userInfModel.setPicPath(fileName);
            if(!userInfModel.updatePicPath()){
                /* All transaction rollback */

                //    userInfModel.rollBack();
                //    appLoginCredentialModel.rollBack();

                baseController.serviceResponse.responseStat.msg = "Internal server error on picture path update";
                baseController.serviceResponse.responseStat.status = false;
                //this.pw.print(this.this.ba.getResponse());
                return baseController.getResponse();
            }
        }
        /* Commit database transaction */

        //  userInfModel.commitTransaction();
        // appLoginCredentialModel.commitTransaction();


        baseController.serviceResponse.responseStat.msg = "Registration success";
        baseController.serviceResponse.responseData = appLoginCredentialModel.getAppCredentialById();
        //this.pw.print(this.this.ba.getResponse());
        return baseController.getResponse();
    }
    private String favourWallPost(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("wall_post_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "wall_post_id required";
            baseController.serviceResponse.responseStat.status = false;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }



        WallPostFavoriteModel wallPostFavoriteModel = new WallPostFavoriteModel();

        wallPostFavoriteModel.setOwner_id(baseController.appCredential.id);

        try{
            wallPostFavoriteModel.setWall_post_id(Integer.parseInt(req.getParameter("wall_post_id")));
        }catch (Exception ex){
            ex.printStackTrace();
            baseController.serviceResponse.responseStat.msg = "wall_post_id int required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        wallPostFavoriteModel.changeFavoriteState();
        if(wallPostFavoriteModel.errorObj.errStatus){
            baseController.serviceResponse.responseStat.msg = wallPostFavoriteModel.errorObj.msg;
            baseController.serviceResponse.responseStat.status =  !wallPostFavoriteModel.errorObj.errStatus;
            //this.pw.print(this.baseController.getResponse());
            return baseController.getResponse();
        }
        HashMap<String,Object> respObj = new HashMap<>();

        respObj.put("isFavorite", wallPostFavoriteModel.isFavorite);
        baseController.serviceResponse.responseStat.msg = wallPostFavoriteModel.operationStatus.msg;
        baseController.serviceResponse.responseData = respObj;
        //this.pw.print(this.baseController.getResponse());
        return baseController.getResponse();
    }
    private String deleteWallPost(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("wall_post_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "wall_post_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        int wallPostId = 0;
        try{
            wallPostId = Integer.parseInt(req.getParameter("wall_post_id"));
        }catch(Exception ex){

        }

        WallPostModel wallPostModel  = new WallPostModel();

        wallPostModel.setId(wallPostId);
        wallPostModel.setOwner_id(baseController.appCredential.id);

        if(!wallPostModel.delete()) {
            baseController.serviceResponse.responseStat.msg = wallPostModel.errorObj.msg;
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg ="Wall Post deleted";
        return baseController.getResponse();
    }
    private String deleteWallPostTemp(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        if(!baseController.checkParam("wall_post_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "wall_post_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        int wallPostId = 0;
        try{
            wallPostId = Integer.parseInt(req.getParameter("wall_post_id"));
        }catch(Exception ex){

        }

        WallPostModel wallPostModel  = new WallPostModel();

        wallPostModel.setId(wallPostId);
        wallPostModel.setOwner_id(baseController.appCredential.id);

        if(!wallPostModel.delete()) {
            baseController.serviceResponse.responseStat.msg = wallPostModel.errorObj.msg;
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg ="Wall Post deleted";
        return baseController.getResponse();
    }

    private String hidePost(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        if(!baseController.checkParam("wall_post_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "wall_post_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }
        /*if(!baseController.checkParam("owner_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "wall_post_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }*/

        int wallPostId = 0;
        try{
            wallPostId = Integer.parseInt(req.getParameter("wall_post_id"));
        }catch(Exception ex){

        }

        WallPostStatusModel wallPostStatusModel  = new WallPostStatusModel();

        wallPostStatusModel.setOwner_id(baseController.appCredential.id);
        wallPostStatusModel.setWall_post_id(wallPostId);

        if(wallPostStatusModel.hide() <= 0) {
            baseController.serviceResponse.responseStat.msg = wallPostStatusModel.errorObj.msg;
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseStat.msg ="Successfully Hidden Wall Post";
        return baseController.getResponse();
    }

    private String wallpostCountByOwnerId(HttpServletRequest req)
    {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        if(!baseController.checkParam("owner_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "owner_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        int ownerId = 0;
        try{
            ownerId = Integer.parseInt(req.getParameter("owner_id"));
        }catch(Exception ex){

        }

        WallPostModel wallPostModel  = new WallPostModel();


        wallPostModel.setOwner_id(ownerId);
        int wallPostCount = wallPostModel.getCountByOwnerId();


        baseController.serviceResponse.responseStat.status=true;
        baseController.serviceResponse.responseStat.msg ="Data Found";
        baseController.serviceResponse.responseData= wallPostCount;
        return baseController.getResponse();
    }
}
