package helper;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by mi on 10/1/15.
 */
public class ImageHelper {
    private  final static String GLOBAL_PATH = "/home/mi/Projects/j2ee/imagetalk_picture/";
    public static String getGlobalPath(){
        return GLOBAL_PATH;
    }
    public static void createDirIfNotExist(String path){
        File theDir = new File(path);

// if the directory does not exist, create it
        if (!theDir.exists()) {
            try{
                theDir.mkdir();
                System.out.println("DIR created");
            }
            catch(SecurityException se){
                //handle it
            }
        }
    }
   public static String saveFile(Object imgObj,String path,int uId){
       if(path==null){
           path = "/home/mi/pic/";
       }
       String fileName ="";
       try{
           fileName = +System.nanoTime()+".jpg";
           createDirIfNotExist(path+uId);
           path += uId+"/"+fileName;
           File file = new File(path);

           long startTime = System.nanoTime();
           if(imgObj.getClass().equals(BufferedImage.class) ){
               ImageIO.write((BufferedImage)imgObj,"jpg",file);
           }else if(imgObj.getClass().equals(String.class)){
               ImageIO.write(decodeToImage((String)imgObj),"jpg",file);
           }

       }catch (Exception ex){
           System.out.println(ex);
           return fileName;
       }
       return fileName;
   }
    public static String saveProfilePicture(Object imgObj,int uId){
        String path = GLOBAL_PATH;
        String fileName ="";
        try{
            fileName = +System.nanoTime()+".jpg";
            path +=uId;
            createDirIfNotExist(path);
            path +="/profile";
            createDirIfNotExist(path);
            path +="/"+fileName;
            System.out.println(path);
            File file = new File(path);

            long startTime = System.nanoTime();
            if(imgObj.getClass().equals(BufferedImage.class) ){
                ImageIO.write((BufferedImage)imgObj,"jpg",file);
            }else if(imgObj.getClass().equals(String.class)){
                ImageIO.write(decodeToImage((String)imgObj),"jpg",file);
            }

            fileName = uId+"/profile/"+fileName;

        }catch (Exception ex){
            System.out.println(ex);
            return fileName;
        }
        return fileName;
    }

    public static BufferedImage decodeToImage(String imageString)
    {
        BufferedImage image = null;
        byte[] imageByte;
        try
        {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return image;
    }

    public static String encodeToString(BufferedImage image, String type)
    {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return imageString;
    }
}


