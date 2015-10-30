package controller.thirdparty.google.geoapi;

import com.google.gson.*;
import model.datamodel.app.Location;
import model.datamodel.app.Places;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by mi on 10/5/15.
 */
public class GoogleGeoApi {

    private static String API_KEY = "AIzaSyAq4og4K5Wb6D38azyml00Ewc7J0rSKgEc";
    private static String BASE_URL ="https://maps.googleapis.com/maps/api/geocode/json";
    private static String BASE_PACE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private String params;

    public void setKeyWord(String keyWord) {
        try {
            this.keyWord = URLEncoder.encode(keyWord, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.keyWord ="";
            e.printStackTrace();
        }
    }
    public String pagetoken;
    private String keyWord;

    public GoogleGeoApi() {
        this.pagetoken = "";
    }
    public String generateGeoLocationUrlForKeyWord(){
        String url = null;
        try {
            url = BASE_URL+"?key="+API_KEY+"&address="+ URLEncoder.encode(this.keyWord, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
    private String generatePlacesUrlForLatLng(double lat,double lng){
        String url = null;
        String latLng = Double.toString(lat)+","+Double.toString(lng);

        url = BASE_PACE_URL+"?key="+API_KEY+"&location="+latLng+"&radius=500&pagetoken="+this.pagetoken+"&name="+this.keyWord;;


        return url;
    }
    private String generateGeoLocationUrlForLatLng(double lat,double lng){
        String url = null;
        String latLng = Double.toString(lat)+","+Double.toString(lng);

        url = BASE_URL+"?key="+API_KEY+"&latlng="+latLng+"&sensor=false";

        return url;
    }
    private String FireHttpsAction(String httpsURL){
        System.out.println(httpsURL);
        if(httpsURL==null || httpsURL== ""){
            return "";
        }
        String resposeStr = "";
        try {
            URL myurl = new URL(httpsURL);

            HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            String inputLine = "";
            while ((inputLine = in.readLine()) != null)
            {

                resposeStr+=inputLine.trim();
            }
             in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resposeStr;
    }

    public ArrayList<Location>  getLocationByKeyword(){
        String str =  this.FireHttpsAction(this.generateGeoLocationUrlForKeyWord());
        return parseLocationFromJson(str);
    }
    public ArrayList<Location>  getLocationByLatLng(double lat,double lng){

        String str =  this.FireHttpsAction(this.generateGeoLocationUrlForLatLng(lat, lng));
        return parseLocationFromJson(str);
    }
    public ArrayList<Places>  getPlacesByLatLng(double lat,double lng){

        String str =  this.FireHttpsAction(this.generatePlacesUrlForLatLng(lat, lng));

        return parsePlaceFromJson(str);
    }
    private ArrayList<Location> parseLocationFromJson(String str){

        ArrayList<Location> addressList = new ArrayList<Location>();
        if(str==null||str==""){
            return addressList;
        }

        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonObject response =  jsonParser.parse(str).getAsJsonObject();
        String status = jsonParser.parse(response.get("status").toString()).getAsString() ;


        if(status.equals("OK")){
            JsonArray result = jsonParser.parse(response.get("results").toString()).getAsJsonArray();
            if(result==null || result.size()==0){

                return addressList;
            }
            for(int i=0;i<result.size();i++){
                JsonObject location = gson.toJsonTree(result.get(i)).getAsJsonObject();
                Location address = new Location();
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
                addressList.add(address);
            }

        }
        return addressList;
    }
    private ArrayList<Places> parsePlaceFromJson(String str){

        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonObject response =  jsonParser.parse(str).getAsJsonObject();
        String status = jsonParser.parse(response.get("status").toString()).getAsString() ;
        ArrayList<Places> placesList = new ArrayList();

        if(status.equals("OK")){

            JsonArray result = jsonParser.parse(response.get("results").toString()).getAsJsonArray();
            if(response.has("next_page_token")){
                this.pagetoken = response.get("next_page_token").getAsString();
            }


            if(result==null || result.size()==0){

                return placesList;
            }
            //result.size()
            for(int i=0;i<result.size();i++){
                JsonObject location = gson.toJsonTree(result.get(i)).getAsJsonObject();
                Places places = new Places();
                //places.name ="sdf";//"Vía Paviso";

                places.placeId = location.get("id").getAsString();
                places.icon = location.get("icon").getAsString();

                try {
                    byte ptext[] = location.get("name").getAsString().getBytes("ISO-8859-1");
                    places.name = new String(ptext,"UTF-8");

                    byte pAddresstext[] = location.get("vicinity").getAsString().getBytes("ISO-8859-1");
                    places.formattedAddress = new String(pAddresstext,"UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                places.googlePlaceId = location.get("place_id").getAsString();
                places.lat = location.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                places.lng = location.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();

                placesList.add(places);
            }

        }
        return placesList;
    }
}
