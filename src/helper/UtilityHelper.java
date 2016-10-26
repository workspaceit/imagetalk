package helper;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Created by omar on 9/3/16.
 */
public class UtilityHelper {
    public static String getRandomNumber(){
        Random rnd = new Random();
        int n = 1000 + rnd.nextInt(900000);
        return Integer.toString(n);
    }
    public static String getRandomNumber(int n){
        if(n>100){
            return null;
        }
        String randomText = "";
        Random rnd = new Random();
        int i=0;
        while(i<n){
            randomText +=Integer.toString(rnd.nextInt(9));
            i++;
        }
        return randomText;
    }

    public static double round(double value, int places) {

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
