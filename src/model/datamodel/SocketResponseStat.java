package model.datamodel;

/**
 * Created by mi on 8/20/15.
 */
 public class SocketResponseStat {
        public boolean status;
        public String  command;
        public boolean onlyReceiverMember;
        public SocketResponseStat() {
            this.status = true;
            this.command = "Default";
            this.onlyReceiverMember = false;
        }
 }