package model.datamodel.app;

import model.datamodel.admin.AdminCredential;

import java.sql.Timestamp;

/**
 * Created by mi on 11/4/16.
 */
public class ReportWallPost {
    public int id;
    public ReportType reportType;
    public WallPost wallPost;
    public AppCredential reporter;
    public AdminCredential actionTakenBy;
    public boolean actionTaken;
    public String description;
    public Timestamp actionTakenAt;
    public Timestamp createdDate;
    public ReportWallPost() {
        this.id = 0;
        this.wallPost = new WallPost();
        this.reporter = new AppCredential();
        this.actionTakenBy = new AdminCredential();
        this.actionTaken = false;
        this.reportType = new ReportType();
        this.description = "";
        this.actionTakenAt = null;
        this.createdDate = null;
    }

    @Override
    public String toString() {
        return "ReportWallPost{" +
                "id=" + id +
                ", reportType=" + reportType +
                ", wallPost=" + wallPost +
                ", reporter=" + reporter +
                ", actionTakenBy=" + actionTakenBy +
                ", actionTaken=" + actionTaken +
                ", description='" + description + '\'' +
                ", actionTakenAt=" + actionTakenAt +
                ", createdDate=" + createdDate +
                '}';
    }
}
