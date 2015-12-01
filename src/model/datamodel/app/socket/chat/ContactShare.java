package model.datamodel.app.socket.chat;

import model.datamodel.app.Contact;

/**
 * Created by mi on 12/1/15.
 */
public class ContactShare extends BaseChat {
    public Contact contact;

    public ContactShare() {
        this.contact = new Contact();
    }
}