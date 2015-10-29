package socket;

/**
 * Created by mi on 10/29/15.
 */

public class SocketReponse {

    public ResponseStat responseStat = null;
    public Object       responseData = null;

    public SocketReponse() {
        this.responseStat = new ResponseStat();
        this.responseData = new Object();
    }

    public class ResponseStat {
        public boolean status;
        public String incommingTag;
        public String  msg;

        public ResponseStat() {
            this.status = true;
            this.incommingTag = "";
            this.msg = "";
        }
    }
}