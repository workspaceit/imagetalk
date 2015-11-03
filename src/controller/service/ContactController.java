package controller.service;

import com.google.gson.Gson;
import model.AppLoginCredentialModel;
import model.ContactModel;
import model.datamodel.app.AppCredential;
import model.datamodel.app.Contact;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 10/12/15.
 */
public class ContactController extends HttpServlet {
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
            case "/app/contact/findmatch":
                this.findmatchContact();
                break;
            case "/app/contact/add":
                this.addContacts();
                break;
            case "/app/contact/remove":
                this.removeContacts();
                break;
            case "/app/contact/block":
                this.blockContacts();
                break;
            case "/app/contact/unblock":
                this.unBlockContacts();
                break;
            case "/app/contact/favorite":
                this.favoritesContacts();
                break;
            case "/app/contact/unfavorite":
                this.unFavoritesContacts();
                break;
            case "/app/contact/who/has/mine":
                this.getWhoHasMyNumber();
                break;
            case "/app/contact/doesnot/have/mine":
                this.getWhoDoesNotHasMyNumber();
                break;
            case "/app/contact/who/blocked/me":
                this.getWhoBlockedMe();
                break;
            case "/app/contact/whom/i/blocked":
                this.getWhomIBlocked();
                break;
            default:
                break;
        }
        this.pw.close();
    }
    private void findmatchContact(){
        ArrayList<String> contacts = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("contacts", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "contacts required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                String [] contactsArray = gson.fromJson(this.req.getParameter("contacts").trim(),String[].class);
                for(String contact : contactsArray){
                    contacts.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "contacts is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(this.baseController.appCredential.id);
        appLoginCredentialModel.setContactList(contacts);


        this.baseController.serviceResponse.responseData =  appLoginCredentialModel.getMatchedPhoneNumber();
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void addContacts(){
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("app_login_credential_id", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(this.req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }

        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.addContact()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        this.baseController.serviceResponse.responseStat.msg = "Contact"+suffix+"added successfully";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void removeContacts(){
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("app_login_credential_id", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(this.req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.removeContact()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        this.baseController.serviceResponse.responseStat.msg = "Contact"+suffix+"removed successfully";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void blockContacts(){
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("app_login_credential_id", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(this.req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.blockContact()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        this.baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "blocked successfully";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void unBlockContacts(){
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("app_login_credential_id", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(this.req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.unBlockContact()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        this.baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "unblocked successfully";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void favoritesContacts(){
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("app_login_credential_id", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(this.req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.favoriteContact()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        this.baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "favorite successfully";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void unFavoritesContacts(){
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!this.baseController.checkParam("app_login_credential_id", this.req, true)) {

            this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            this.baseController.serviceResponse.responseStat.status = false;
            this.pw.print(this.baseController.getResponse());
            return;
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(this.req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                this.baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                this.baseController.serviceResponse.responseStat.status = false;
                this.pw.print(this.baseController.getResponse());
                return;
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(this.baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.unFavoriteContact()){
            this.baseController.serviceResponse.responseStat.status = false;
            this.baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            this.pw.print(this.baseController.getResponse());
            return;
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        this.baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "unfavorite successfully";
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void getWhoHasMyNumber() {
        ContactModel contactModel = new ContactModel();

        String keyword="";
        if(this.baseController.checkParam("keyword", this.req, true)) {
            keyword = this.req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(this.baseController.appCredential.id);


        ArrayList<Contact> contactList = contactModel.getWhoHasMyContactByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

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


        this.baseController.serviceResponse.responseStat.msg = respStr;
        this.baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        this.baseController.serviceResponse.responseData = contactList;
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void getWhoDoesNotHasMyNumber(){
        ContactModel contactModel = new ContactModel();

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
        String keyword="";
        if(this.baseController.checkParam("keyword", this.req, true)) {
            keyword = this.req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(this.baseController.appCredential.id);

        ArrayList<Contact> contactList = contactModel.getWhoDoesNotHasMyContactByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

        this.baseController.serviceResponse.responseStat.msg = respStr;
        this.baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        this.baseController.serviceResponse.responseData = contactList;
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void getWhoBlockedMe(){
        ContactModel contactModel = new ContactModel();

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
        String keyword="";
        if(this.baseController.checkParam("keyword", this.req, true)) {
            keyword = this.req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(this.baseController.appCredential.id);


        ArrayList<Contact> contactList = contactModel.getWhoBlockedMeByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

        this.baseController.serviceResponse.responseStat.msg = respStr;
        this.baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        this.baseController.serviceResponse.responseData = contactList;
        this.pw.print(this.baseController.getResponse());
        return;

    }
    private void getWhomIBlocked(){
        ContactModel contactModel = new ContactModel();

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

        String keyword="";
        if(this.baseController.checkParam("keyword",this.req,true))
        {
            keyword = req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(this.baseController.appCredential.id);


        ArrayList<Contact> contactList = contactModel.getWhomIBlockedByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

        this.baseController.serviceResponse.responseStat.msg = respStr;
        this.baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        this.baseController.serviceResponse.responseData = contactList;
        this.pw.print(this.baseController.getResponse());
        return;

    }


}
