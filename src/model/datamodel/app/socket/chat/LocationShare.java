package model.datamodel.app.socket.chat;

import model.datamodel.app.Places;

/**
 * Created by mi on 11/30/15.
 */
public class LocationShare extends ChatPhoto{
    public Places places;

    public LocationShare() {
        super();
        this.places = new Places();
    }
}
