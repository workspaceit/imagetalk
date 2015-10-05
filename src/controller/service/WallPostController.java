package controller.service;

import com.google.gson.*;
import controller.thirdparty.api.GoogleGeoApi;
import helper.ImageHelper;
import model.TagListModel;
import model.WallPostModel;
import model.datamodel.app.Location;

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
        int locationId = 0;
        ArrayList<Integer> taggedList = new ArrayList<Integer>();


        if(this.baseController.checkParam("photo", this.req, true)){
            if(this.baseController.checkParam("photo",this.req,true)) {
                imgBase64 = this.req.getParameter("photo");
                fileRelativePath = ImageHelper.saveFile(imgBase64, null, this.baseController.appCredential.id);
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
                for(int tempTaggedId : tempTaggedList){
                    taggedList.add(tempTaggedId);
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

        /*======================================================*/


        System.out.println("fileName :" + fileRelativePath);

        WallPostModel wallPostModel = new WallPostModel();

        wallPostModel.setOwner_id(this.baseController.appCredential.id);
        wallPostModel.setDescrption(this.req.getParameter("description"));
        wallPostModel.setPicture_path(fileRelativePath);
        wallPostModel.setLocation_id(locationId);


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
    private void test(){
        GoogleGeoApi googleGeoApi = new GoogleGeoApi();
        googleGeoApi.setKeyWord(this.req.getParameter("location"));



        WallPostModel wallPostModel = new WallPostModel();
        wallPostModel.setOwner_id(1);
//        this.baseController.serviceResponse.responseData = wallPostModel.getByOwner_id();

        String str =  googleGeoApi.FireHttpsAction();

        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonObject response =  jsonParser.parse(str).getAsJsonObject();
        String status = jsonParser.parse(response.get("status").toString()).getAsString() ;
        Location address = new Location();
        if(status.equals("OK")){
            JsonArray result = jsonParser.parse(response.get("results").toString()).getAsJsonArray();
            if(result==null || result.size()==0){
                this.baseController.serviceResponse.responseStat.msg ="No record found";
                this.pw.print(this.baseController.getResponse());
                return;
            }
            JsonObject location = gson.toJsonTree(result.get(0)).getAsJsonObject();

            JsonArray addressComponents = location.getAsJsonArray("address_components");
            for(JsonElement addressElement : addressComponents){
                JsonObject addressObject = addressElement.getAsJsonObject();
                if(addressObject.getAsJsonArray("types").get(0).getAsString().equals("country")){
                    address.countryName = addressObject.get("long_name").getAsString();
                }
            }
            address.formattedAddress = location.get("formatted_address").getAsString();
            address.lat = location.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
            address.lng = location.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();

        }else{
            this.baseController.serviceResponse.responseStat.msg ="No record found";
            this.pw.print(this.baseController.getResponse());
            return;
        }

        this.baseController.serviceResponse.responseData =  address;
        this.pw.print(this.baseController.getResponse());
        return;
    }
}
