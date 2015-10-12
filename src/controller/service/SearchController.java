package controller.service;

import controller.thirdparty.google.geoapi.GoogleGeoApi;
import model.AppLoginCredentialModel;
import model.ContactModel;
import model.WallPostModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.AuthCredential;
import model.datamodel.app.Location;
import model.datamodel.app.Login;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 10/2/15.
 */
public class SearchController extends HttpServlet {
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
            case "/app/search/user/fortag":
                this.getUserForTag();
                break;
            case "/app/search/location/by/keyword":
                this.getLocationByKeyword();
                break;
            case "/app/search/location/by/latlng":
                this.getLocationByLattLng();
            default:
                break;
        }
        this.pw.close();
    }
    private void getLocationByLattLng(){
        double lat= 0;
        double lng= 0;


        if(!this.baseController.checkParam("lat", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "lat required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{
                lat = Double.parseDouble(this.req.getParameter("lat").trim());
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "lat is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        if(!this.baseController.checkParam("lng", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "lng required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            try{
                lng = Double.parseDouble(this.req.getParameter("lng").trim());
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "lng is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }


        GoogleGeoApi googleGeoApi = new GoogleGeoApi();
        this.baseController.serviceResponse.responseStat.msg = "";
        this.baseController.serviceResponse.responseData =  googleGeoApi.getLocationByLatLng(lat,lng);
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getAllUserByKeyword(){

        String keyword="";
        if(this.baseController.checkParam("keyword", this.req, true)) {
            keyword = this.req.getParameter("keyword").trim();
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();

        if(this.baseController.checkParam("limit", this.req, true)) {
            try{
                appLoginCredentialModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            appLoginCredentialModel.limit = 10;
        }

        if(!this.baseController.checkParam("offset", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "offset required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else {
            try{
                appLoginCredentialModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }


        appLoginCredentialModel.setId(this.baseController.appCredential.id);
        ArrayList<AppCredential> appCredentialsList =  appLoginCredentialModel.getAppCredentialByKeyword(keyword);

        if(appCredentialsList.size()==0){
            this.baseController.serviceResponse.responseStat.msg = "No record found";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseData = appCredentialsList;
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getUserForTag(){

        String keyword="";
        if(this.baseController.checkParam("keyword", this.req, true)) {
            keyword = this.req.getParameter("keyword").trim();
        }

        ContactModel contactModel  = new ContactModel();

        if(this.baseController.checkParam("limit", this.req, true)) {
            try{
                contactModel.limit = Integer.parseInt(this.req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }else{
            contactModel.limit = 10;
        }

        if(!this.baseController.checkParam("offset", this.req, true)){

            this.baseController.serviceResponse.responseStat.msg = "offset required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else {
            try{
                contactModel.offset = Integer.parseInt(this.req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }


        contactModel.setOwner_id(this.baseController.appCredential.id);
        ArrayList<AppCredential> appCredentialsList =  contactModel.getContactByKeyword(keyword);

        if(appCredentialsList.size()==0){
            this.baseController.serviceResponse.responseStat.msg = "No record found";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseData = appCredentialsList;
        this.pw.print(this.baseController.getResponse());
        return;
    }
    private void getLocationByKeyword(){
        String keyword="";
        if(!this.baseController.checkParam("keyword", this.req, true)) {
            this.baseController.serviceResponse.responseStat.msg = "keyword required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{
            keyword = this.req.getParameter("keyword").trim();
        }


        GoogleGeoApi googleGeoApi = new GoogleGeoApi();
        googleGeoApi.setKeyWord(keyword);

        ArrayList<Location> addressList = googleGeoApi.getLocationByKeyword();

        this.baseController.serviceResponse.responseStat.msg =(addressList.size()<=0)?"No record found":"";
        this.baseController.serviceResponse.responseStat.status = (addressList.size()<=0)?false:true;
        this.baseController.serviceResponse.responseData =  addressList;
        this.pw.print(this.baseController.getResponse());
        return;

    }
}
