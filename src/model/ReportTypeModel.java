package model;

import model.datamodel.app.ReportType;

import java.sql.SQLException;

/**
 * Created by mi on 11/4/16.
 */
public class ReportTypeModel extends ImageTalkBaseModel {
    public ReportTypeModel() {
        this.tableName = "report_type";
    }

    public ReportType getById(int id){
        ReportType reportType = new ReportType();
        String query = String.format("SELECT * FROM `"+this.tableName+"` WHERE id=%s limit 1",id);
        this.setQuery(query);
        this.getData();
        try{
            while(this.resultSet.next()){
                reportType.id = this.resultSet.getInt("id");
                reportType.name = this.resultSet.getString("name");
                reportType.createdDate = this.resultSet.getTimestamp("created_date");

            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return reportType;
    }
}
