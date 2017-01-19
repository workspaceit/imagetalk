package model;

import model.datamodel.app.ReportType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mi on 11/4/16.
 */
public class ReportTypeModel extends ImageTalkBaseModel {
    public ReportTypeModel() {
        this.tableName = "report_type";
    }

    public List<ReportType> getAll(){
        List<ReportType>  reportTypeList =  new ArrayList<>();
        String query = "SELECT * FROM "+this.tableName;
        this.setQuery(query);
        this.getData();
        try{
            while(this.resultSet.next()){

                ReportType reportType = new ReportType();
                reportType.id = this.resultSet.getInt("id");
                reportType.name = this.resultSet.getString("name");
                reportType.createdDate = this.resultSet.getTimestamp("created_date");
                reportTypeList.add(reportType);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return reportTypeList;
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
