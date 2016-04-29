package model.datamodel.app;

/**
 * Application Name : ImageTalk
 * Package Name     : model.datamodel.app
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 4/27/16
 */
public class ReportAppIssue {

    public int    id;
    public User reporterId;
    public String reportText;
    public String createdDate;

    public ReportAppIssue() {
        this.id = 0;
        this.reporterId = new User();
        this.reportText = "";
        this.createdDate = "";
    }
}
