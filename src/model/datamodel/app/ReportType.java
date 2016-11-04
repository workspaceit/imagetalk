package model.datamodel.app;

import java.sql.Timestamp;

/**
 * Created by mi on 11/4/16.
 */
public class ReportType {
    public int id;
    public String name;
    public Timestamp createdDate;

    public ReportType() {
        this.id = 0;
        this.name = "";
        this.createdDate = null;
    }

    @Override
    public String toString() {
        return "ReportType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
