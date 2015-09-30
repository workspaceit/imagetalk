package controller.service;

/**
 * Created by mi on 8/20/15.
 */
public class ServiceResponse {

    public ResponseStat responseStat = null;
    public Object       responseData = null;

    public ServiceResponse() {
        this.responseStat = new ResponseStat();
        this.responseData = new Object();
    }

    public class ResponseStat {
        public boolean status;
        public String  msg;

        public ResponseStat() {
            this.status = true;
            this.msg = "";
        }
    }
}