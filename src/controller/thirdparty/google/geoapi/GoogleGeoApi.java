package controller.thirdparty.google.geoapi;

import com.google.gson.*;
import model.datamodel.app.Location;

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
    private String params;

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    private String keyWord;

    public GoogleGeoApi() {

    }
    public String generateGeoLocationUrlByKeyWord(){
        String url = null;
        try {
            url = BASE_URL+"?key="+API_KEY+"&address="+ URLEncoder.encode(this.keyWord, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
    private String FireHttpsAction(){
        String httpsURL = this.generateGeoLocationUrlByKeyWord();
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
                System.out.println(inputLine);
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
        String str =  this.FireHttpsAction();

        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonObject response =  jsonParser.parse(str).getAsJsonObject();
        String status = jsonParser.parse(response.get("status").toString()).getAsString() ;
        ArrayList<Location> addressList = new ArrayList<Location>();

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
}
