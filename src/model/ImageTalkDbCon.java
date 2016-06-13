package model;

/**
 * Created by mi on 6/9/16.
 */
public class ImageTalkDbCon {
    public static void closeConnection(){
        ImageTalkBaseModel.dbConnectionClose();
    }
}
