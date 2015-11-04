package model.datamodel.app.socket;

/**
 * Created by mi on 10/29/15.
 */

public class SocketResponse {

    public ResponseStat responseStat = null;
    public Object       responseData = null;

    public SocketResponse() {
        this.responseStat = new ResponseStat();
        this.responseData = new Object();
    }

    public class ResponseStat {
        public boolean status;
        public String tag;
        public String chatId;
        public String  msg;

        public ResponseStat() {
            this.status = true;
            this.tag = "";
            this.msg = "";
            this.chatId = "";
        }
    }
}