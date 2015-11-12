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
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by mi on 10/12/15.
 */
public class ContactController extends HttpServlet {
/*    ImageTalkBaseController baseController;
    PrintWriter pw;
    HttpServletRequest req;
    HttpServletResponse res;*/

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        res.setContentType("application/json");
        baseController = new ImageTalkBaseController();
        PrintWriter pw = res.getWriter();

        String url = req.getRequestURI().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(!baseController.isAppSessionValid(req)){
            pw.print(baseController.getResponse());
            pw.close();
            return;
        }

        switch (url) {
            case "/app/contact/findmatch":
                pw.print(this.findmatchContact(req));
                break;
            case "/app/contact/add":
                pw.print(this.addContacts(req));
                break;
            case "/app/contact/remove":
                pw.print(this.removeContacts(req));
                break;
            case "/app/contact/block":
                pw.print(this.blockContacts(req));
                break;
            case "/app/contact/unblock":
                pw.print(this.unBlockContacts(req));
                break;
            case "/app/contact/favorite":
                pw.print(this.favoritesContacts(req));
                break;
            case "/app/contact/unfavorite":
                pw.print(this.unFavoritesContacts(req));
                break;
            case "/app/contact/who/has/mine":
                pw.print(this.getWhoHasMyNumber(req));
                break;
            case "/app/contact/doesnot/have/mine":
                pw.print(this.getWhoDoesNotHasMyNumber(req));
                break;
            case "/app/contact/who/blocked/me":
                pw.print(this.getWhoBlockedMe(req));
                break;
            case "/app/contact/whom/i/blocked":
                pw.print(this.getWhomIBlocked(req));
                break;
            default:
                break;
        }
        pw.close();
    }
    private String findmatchContact(HttpServletRequest req){

        ImageTalkBaseController baseController = new ImageTalkBaseController();
        ArrayList<String> contacts = new ArrayList();
        Gson gson = new Gson();
        if(!baseController.checkParam("contacts", req, true)) {

            baseController.serviceResponse.responseStat.msg = "contacts required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{

                String [] contactsArray = gson.fromJson(req.getParameter("contacts").trim(),String[].class);
                for(String contact : contactsArray){
                    contacts.add(contact);
                }
            } catch (Exception ex){
                ex.printStackTrace();
                baseController.serviceResponse.responseStat.msg = "contacts is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        appLoginCredentialModel.setId(baseController.appCredential.id);
        appLoginCredentialModel.setContactList(contacts);

        ArrayList<AppCredential> respObj = appLoginCredentialModel.getMatchedPhoneNumber();
        baseController.serviceResponse.responseData = respObj ;

        System.out.println("respObj Size" + respObj.size());
        System.out.println("respObj str"+respObj.toString());
        return baseController.getResponse();

    }
    private String addContacts(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!baseController.checkParam("app_login_credential_id", req, true)) {

            baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.addContact()){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            return baseController.getResponse();
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        baseController.serviceResponse.responseStat.msg = "Contact"+suffix+"added successfully";
        return baseController.getResponse();

    }
    private String removeContacts(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!baseController.checkParam("app_login_credential_id", req, true)) {

            baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.removeContact()){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            return baseController.getResponse();
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        baseController.serviceResponse.responseStat.msg = "Contact"+suffix+"removed successfully";
        return baseController.getResponse();

    }
    private String blockContacts(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController();
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!baseController.checkParam("app_login_credential_id", req, true)) {

            baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.blockContact()){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            return baseController.getResponse();
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "blocked successfully";
        return baseController.getResponse();

    }
    private String unBlockContacts(HttpServletRequest req){
        ImageTalkBaseController baseController =  new ImageTalkBaseController(req);
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!baseController.checkParam("app_login_credential_id", req, true)) {

            baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.unBlockContact()){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            return baseController.getResponse();
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "unblocked successfully";
        return baseController.getResponse();

    }
    private String favoritesContacts(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!baseController.checkParam("app_login_credential_id", req, true)) {
            baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.favoriteContact()){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            return baseController.getResponse();
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "favorite successfully";
        return baseController.getResponse();

    }
    private String unFavoritesContacts(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ArrayList<Integer> contactIdList = new ArrayList();
        Gson gson = new Gson();
        if(!baseController.checkParam("app_login_credential_id", req, true)) {

            baseController.serviceResponse.responseStat.msg = "app_login_credential_id required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else{

            try{

                Integer [] contactsArray = gson.fromJson(req.getParameter("app_login_credential_id").trim(),Integer[].class);
                for(int contact : contactsArray){
                    contactIdList.add(contact);
                }
            } catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "app_login_credential_id is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        ContactModel contactModel = new ContactModel();
        contactModel.setOwner_id(baseController.appCredential.id);
        contactModel.setContactIdList(contactIdList);

        if(!contactModel.unFavoriteContact()){
            baseController.serviceResponse.responseStat.status = false;
            baseController.serviceResponse.responseStat.msg = contactModel.errorObj.msg;
            return baseController.getResponse();
        }
        String suffix = (contactIdList.size()>1)?"s are ":" is ";
        baseController.serviceResponse.responseStat.msg = "Contact"+suffix+ "unfavorite successfully";
        return baseController.getResponse();

    }
    private String getWhoHasMyNumber(HttpServletRequest req) {
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ContactModel contactModel = new ContactModel();

        String keyword="";
        if(baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(baseController.appCredential.id);




        if(baseController.checkParam("limit", req, true)) {
            try{
                contactModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            contactModel.limit = 10;
        }

        if(!baseController.checkParam("offset", req, true)){
            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                contactModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        ArrayList<Contact> contactList = contactModel.getWhoHasMyContactByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

        baseController.serviceResponse.responseStat.msg = respStr;
        baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        baseController.serviceResponse.responseData = contactList;
        return baseController.getResponse();

    }
    private String getWhoDoesNotHasMyNumber(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ContactModel contactModel = new ContactModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                contactModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            contactModel.limit = 10;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                contactModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        String keyword="";
        if(baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(baseController.appCredential.id);

        ArrayList<Contact> contactList = contactModel.getWhoDoesNotHasMyContactByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

        baseController.serviceResponse.responseStat.msg = respStr;
        baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        baseController.serviceResponse.responseData = contactList;
        return baseController.getResponse();

    }
    private String getWhoBlockedMe(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ContactModel contactModel = new ContactModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                contactModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            contactModel.limit = 10;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                contactModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }
        String keyword="";
        if(baseController.checkParam("keyword", req, true)) {
            keyword = req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(baseController.appCredential.id);


        ArrayList<Contact> contactList = contactModel.getWhoBlockedMeByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

        baseController.serviceResponse.responseStat.msg = respStr;
        baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        baseController.serviceResponse.responseData = contactList;
        return baseController.getResponse();

    }
    private String getWhomIBlocked(HttpServletRequest req){
        ImageTalkBaseController baseController = new ImageTalkBaseController(req);
        ContactModel contactModel = new ContactModel();

        if(baseController.checkParam("limit", req, true)) {
            try{
                contactModel.limit = Integer.parseInt(req.getParameter("limit").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "limit is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }else{
            contactModel.limit = 10;
        }

        if(!baseController.checkParam("offset", req, true)){

            baseController.serviceResponse.responseStat.msg = "offset required";
            baseController.serviceResponse.responseStat.status = false;
            return baseController.getResponse();
        }else {
            try{
                contactModel.offset = Integer.parseInt(req.getParameter("offset").trim());
            }catch (Exception ex){
                System.out.println(ex);
                baseController.serviceResponse.responseStat.msg = "offset is not in valid format";
                baseController.serviceResponse.responseStat.status = false;
                return baseController.getResponse();
            }
        }

        String keyword="";
        if(baseController.checkParam("keyword",req,true))
        {
            keyword = req.getParameter("keyword").trim();
        }

        contactModel.setKeyword(keyword);
        contactModel.setOwner_id(baseController.appCredential.id);


        ArrayList<Contact> contactList = contactModel.getWhomIBlockedByOwnerId();
        String respStr = (contactList.size()==0)?"No record found":"";

        baseController.serviceResponse.responseStat.msg = respStr;
        baseController.serviceResponse.responseStat.status = (contactList.size()>0);
        baseController.serviceResponse.responseData = contactList;
        return baseController.getResponse();

    }


}
