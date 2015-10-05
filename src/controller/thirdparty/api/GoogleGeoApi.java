package controller.thirdparty.api;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
    public String FireHttpsAction(){
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
}
