package controller.service;

import com.google.gson.Gson;
import controller.thirdparty.google.geoapi.GoogleGeoApi;
import model.AppLoginCredentialModel;
import model.ContactModel;
import model.WallPostModel;
import model.datamodel.app.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mi on 10/2/15.
 */
public class SearchController extends HttpServlet {
   /* ImageTalkBaseController baseController;
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
        /*this.req = req;
        this.res = res;*/
        res.setContentType("application/json");
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        PrintWriter pw = res.getWriter();

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        switch (url) {
            case "/app/search/alluser/by/keyword":
                pw.print(this.getUserForTag(req));
                break;
            case "/app/search/location/by/keyword":
                pw.print(this.getLocationByKeyword(req));
                break;
            case "/app/search/location/by/latlng":
                pw.print(this.getLocationByLattLng(req));
                break;
            case "/app/search/places/by/latlng":
                pw.print(this.getPlacesByLatLng(req));
                break;
            case "/app/search/contact/by/keyword":
                pw.print(this.getContactByKeyword(req));
                break;
            default:
                break;
        }
        pw.close();
    }
    private String getLocationByLattLng(HttpServletRequest req) {
        double lat= 0;
        double lng= 0;
        ImageTalkBaseController baseController  = new ImageTalkBaseController(req);

        if(!baseController.checkParam("lat", req, true)) {

            baseController.serviceResponse.responseStat.msg = "lat required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{
                lat = Double.parseDouble(req.getParameter("lat").trim());
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "lat is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        if(!baseController.checkParam("lng", req, true)) {
            baseController.serviceResponse.responseStat.msg = "lng required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            try{
                lng = Double.parseDouble(req.getParameter("lng").trim());
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "lng is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }


        GoogleGeoApi googleGeoApi = new GoogleGeoApi();
        baseController.serviceResponse.responseStat.msg = "";
        baseController.serviceResponse.responseData =  googleGeoApi.getLocationByLatLng(lat,lng);
        return baseController.getResponse();
    }
    private String getAllUserByKeyword(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        String keyword="";
        if(baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                appLoginCredentialModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            appLoginCredentialModel.limit = 10;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                appLoginCredentialModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }


        appLoginCredentialModel.setId(baseController.appCredential.id);
        ArrayList<AppCredential> appCredentialsList =  appLoginCredentialModel.getAppCredentialByKeyword(keyword);

        if(appCredentialsList.size()==0){
            baseController.serviceResponse.responseStat.msg = "No record found";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseData = appCredentialsList;
        return baseController.getResponse();
    }
    private String getUserForTag(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        String keyword="";
        if(baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        ContactModel contactModel  = new ContactModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                contactModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            contactModel.limit = 10;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                contactModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }


        contactModel.setOwner_id(baseController.appCredential.id);
        ArrayList<Contact> appCredentialsList =  contactModel.getContactByKeyword(keyword);

        if(appCredentialsList.size()==0){
            baseController.serviceResponse.responseStat.msg = "No record found";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseData = appCredentialsList;
        return baseController.getResponse();
    }
    private String getContactByKeyword(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        String keyword="";
        if(baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }
        System.out.println("keyword :"+keyword);
        ContactModel contactModel  = new ContactModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                contactModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            contactModel.limit = 10;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                contactModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }


        contactModel.setOwner_id(baseController.appCredential.id);
        ArrayList<Contact> contactList =  contactModel.getContactByKeyword(keyword);

        if(contactList.size()==0){
            baseController.serviceResponse.responseStat.msg = "No record found";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }

        baseController.serviceResponse.responseData = contactList;
        return baseController.getResponse();
    }
    private String getLocationByKeyword(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);

        String keyword="";
        if(!baseController.checkParam("keyword", req, true)) {
            baseController.serviceResponse.responseStat.msg = "keyword required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            keyword = req.getParameter("keyword").trim();
        }


        GoogleGeoApi googleGeoApi = new GoogleGeoApi();
        googleGeoApi.setKeyWord(keyword);


        HashMap<String,Object> respObj =  new HashMap<>();
        HashMap<String,Object> extraObj =  new HashMap<>();
        if(baseController.checkParam("next_page_token", req, true)) {
            googleGeoApi.pagetoken = req.getParameter("next_page_token").trim();
        }
        ArrayList<Location> addressList = googleGeoApi.getLocationByKeyword();

        extraObj.put("nextPageToken", googleGeoApi.pagetoken);

        respObj.put("location", addressList);



        respObj.put("extra",extraObj);


        baseController.serviceResponse.responseStat.msg =(addressList.size()<=0)?"No record found":"";
        baseController.serviceResponse.responseStat.status = (addressList.size()<=0)?false:true;
        baseController.serviceResponse.responseData =  respObj;
        return baseController.getResponse();

    }
    private String getPlacesByLatLng(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        double lat= 0;
        double lng= 0;

        String keyword="";

        if(baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        if(!baseController.checkParam("lat", req, true)) {

            baseController.serviceResponse.responseStat.msg = "lat required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{
                lat = Double.parseDouble(req.getParameter("lat").trim());
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "lat is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        if(!baseController.checkParam("lng", req, true)) {
            baseController.serviceResponse.responseStat.msg = "lng required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{
            try{
                lng = Double.parseDouble(req.getParameter("lng").trim());
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "lng is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        GoogleGeoApi googleGeoApi = new GoogleGeoApi();
        googleGeoApi.setKeyWord(keyword);

        if(baseController.checkParam("next_page_token", req, true)) {
            googleGeoApi.pagetoken = req.getParameter("next_page_token").trim();
        }

        HashMap<String,Object> respObj =  new HashMap<>();
        HashMap<String,Object> extraObj =  new HashMap<>();
        ArrayList<Places> places = googleGeoApi.getPlacesByLatLng(lat, lng);

        extraObj.put("nextPageToken",googleGeoApi.pagetoken);

        respObj.put("places", places);

        Gson gson = new Gson();
        System.out.println("Length : " + gson.toJson(places).toString().length());
        System.out.println(gson.toJson(places).toString());

        respObj.put("extra",extraObj);

        baseController.serviceResponse.responseStat.msg = "";
        baseController.serviceResponse.responseData = respObj ;
        return baseController.getResponse();
    }

}
