package model;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Application Name : ImageTalk
 * Package Name     : model
 * Author           : Abu Bakar Siddique
 * Email            : absiddique.live@gmail.com
 * Created Date     : 4/27/16
 */
public class ReportAppIssueModel extends ImageTalkBaseModel{

    private int id;
    private int reporter_id;
    private String report_text;
    private String created_date;

    public ReportAppIssueModel(){
        super();
        super.tableName = "report_app_issue";


        this.id =0;
        this.reporter_id=0;
        this.report_text = "";
        this.created_date = "";

    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        this.id = id;
        return true;
    }

    public int getReporterId() {
        return reporter_id;
    }

    public boolean setReporterId(int reporter_id){
        this.reporter_id=reporter_id;
        return true;
    }

    public String getReportText() {
        return report_text;
    }

    public boolean setReportText(String report_text){
        this.report_text=report_text;
        return true;
    }

    public String getCreated_date() {
        return created_date;
    }

    public boolean setCreated_date(String created_date) {
        this.created_date = created_date;
        return true;
    }

    public int insert(){

        String query1 = "INSERT INTO "+this.tableName+" (`reporter_id`, `report_text`,`created_date`) " +
                        " VALUES (?,?,?)";

        PreparedStatement ps = null;
        try {
            ps = this.con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            ps.setInt((int) 1, this.reporter_id);
            ps.setString((int) 2, this.report_text);
            ps.setString((int)3, this.getUtcDateTime());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                this.id = rs.getInt(1);
            }
            rs.close();
            ps.close();
            this.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.id;
    }




}
