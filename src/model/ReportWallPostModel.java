package model;

import com.google.gson.Gson;
import helper.DateHelper;
import model.datamodel.app.*;
import model.datamodel.photo.Pictures;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mi on 11/4/16.
 */
public class ReportWallPostModel extends ImageTalkBaseModel{
    static final String tableName = "report_wallpost";
    private Gson gson;
    public ReportWallPostModel() {
        super.tableName = "report_wallpost";
        this.gson = new Gson();
    }

    public int insert(ReportWallPost reportWallPost) {

        String query = "INSERT INTO `"+tableName+"`" +
                " (`wallpost_id`, `reporter_id`, `report_type`, `description`, `action_taken`,`action_taken_by`)" +
                " VALUES (%s,%s,%s,'%s',%s,%s)";
        String result = String.format(query,
                reportWallPost.wallPost.id,
                reportWallPost.reporter.id,
                reportWallPost.reportType.id,
                StringEscapeUtils.escapeEcmaScript(reportWallPost.description),
                reportWallPost.actionTaken?1:0,
                reportWallPost.actionTakenBy.id);
        System.out.println(result);
        return super.insertData(result);
    }
    public int updateByWalpostId(ReportWallPost reportWallPost) {

        String query = "UPDATE `"+tableName+"` set " +
                "   `action_taken`=%s,`action_taken_by`=%s,`action_taken_at`='%s'  " +
                " where wallpost_id = "+reportWallPost.wallPost.id;
        String result = String.format(query,
                reportWallPost.actionTaken?1:0,
                reportWallPost.actionTakenBy.id,
                reportWallPost.actionTakenAt);
        System.out.println(result);
        return super.insertData(result);
    }
    public int updateActionTakenByWallPostId(int wallPostId) {

        String query = "UPDATE `"+tableName+"` SET `action_taken`=1,`action_taken_at`='%s'" +
                " where `wallpost_id` = %s and `action_taken`=0";
        String result = String.format(query,DateHelper.getUtcDateTime(),wallPostId);

        return super.insertData(result);
    }
    public ReportWallPost getById(int id){

        ReportWallPost reportWallPost = new ReportWallPost();


        String query = "select * from "+super.tableName+" rw " +
                "JOIN wall_post wp on wp.id = rw.wallpost_id " +
                "JOIN app_login_credential reporter on reporter.id = rw.reporter_id " +
                "LEFT join admin_login actionTaker on actionTaker.id = rw.action_taken_by " +
                "LEFT join user_inf reporter_user on reporter_user.id = reporter.u_id " +
                "LEFT join user_inf actionTaker_user on reporter_user.id = actionTaker.u_id " +
                "where rw.id = "+id+" limit 1";
        this.setQuery(query);
        this.getData();
        try {
            while(this.resultSet.next()){

                reportWallPost.id = this.resultSet.getInt("rw.id");
                reportWallPost.description = this.resultSet.getString("rw.description");
                reportWallPost.createdDate = this.resultSet.getTimestamp("rw.created_date");
                reportWallPost.wallPost.id = this.resultSet.getInt("wp.id");
                reportWallPost.wallPost.description = this.resultSet.getString("wp.description");
                reportWallPost.wallPost.isBlocked = this.resultSet.getInt("wp.is_blocked")==1?true:false;
                reportWallPost.wallPost.wallPostMood = this.resultSet.getString("wp.wall_post_mood");
                reportWallPost.wallPost.picPath = this.resultSet.getString("wp.picture_path");
                reportWallPost.wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wp.created_date")); //Long.toString(this.resultSet.getString("wall_postCdate").getTime());


                reportWallPost.wallPost.owner.id = this.resultSet.getInt("wp.owner_id");


                reportWallPost.reporter.id = resultSet.getInt("reporter.id");
                reportWallPost.reporter.textStatus = resultSet.getString("reporter.text_status");
                reportWallPost.reporter.phoneNumber = resultSet.getString("reporter.phone_number");
                reportWallPost.reporter.user.firstName = resultSet.getString("reporter_user.f_name");
                reportWallPost.reporter.user.lastName = resultSet.getString("reporter_user.l_name");
                try {
                    reportWallPost.reporter.user.picPath = (this.resultSet.getObject("reporter_user.pic_path")==null)?new Pictures():this.gson.fromJson(this.resultSet.getString("reporter_user.pic_path"), Pictures.class);
                }catch (Exception ex){
                    reportWallPost.reporter.user.picPath.original.path =(this.resultSet.getObject("reporter_user.pic_path")==null)?"":this.resultSet.getString("reporter_user.pic_path");
                    ex.printStackTrace();
                }

                reportWallPost.reporter.user.createdDate = resultSet.getString("reporter_user.created_date");

                System.out.println(reportWallPost.reporter);

                reportWallPost.actionTakenBy.user.firstName = resultSet.getString("actionTaker_user.f_name");
                reportWallPost.actionTakenBy.user.lastName =  resultSet.getString("actionTaker_user.l_name");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return reportWallPost;
    }
    public List<ReportWallPost> getAllPending(){
        List<ReportWallPost> reportWallPostList = new ArrayList();

        String query = "select * from "+super.tableName+" rw " +
                "JOIN wall_post wp on wp.id = rw.wallpost_id " +
                "JOIN app_login_credential reporter on reporter.id = rw.reporter_id " +
                "LEFT join admin_login actionTaker on actionTaker.id = rw.action_taken_by " +
                "LEFT join user_inf reporter_user on reporter_user.id = reporter.u_id " +
                "LEFT join user_inf actionTaker_user on reporter_user.id = actionTaker.u_id " +
                "LEFT join report_type rt on rt.id = rw.report_type " +
                " where " +
                " rw.action_taken = 0 " +
                " order by rw.id desc ";
        this.setQuery(query);
        this.getData();
        try {
            while(this.resultSet.next()){
                ReportWallPost reportWallPost = new ReportWallPost();
                reportWallPost.description = this.resultSet.getString("rw.description");
                reportWallPost.createdDate = this.resultSet.getTimestamp("rw.created_date");

                reportWallPost.id = this.resultSet.getInt("rw.id");
                reportWallPost.wallPost.id = this.resultSet.getInt("wp.id");
                reportWallPost.wallPost.description = this.resultSet.getString("wp.description");
                reportWallPost.wallPost.isBlocked = this.resultSet.getInt("wp.is_blocked")==1?true:false;
                reportWallPost.wallPost.wallPostMood = this.resultSet.getString("wp.wall_post_mood");
                reportWallPost.wallPost.picPath = this.resultSet.getString("wp.picture_path");
                reportWallPost.wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wp.created_date")); //Long.toString(this.resultSet.getString("wall_postCdate").getTime());

                reportWallPost.reportType.id = this.resultSet.getInt("rt.id");
                reportWallPost.reportType.name  = this.resultSet.getString("rt.name");


                reportWallPost.reporter.id = resultSet.getInt("reporter.id");
                reportWallPost.reporter.textStatus = resultSet.getString("reporter.text_status");
                reportWallPost.reporter.phoneNumber = resultSet.getString("reporter.phone_number");
                reportWallPost.reporter.user.firstName = resultSet.getString("reporter_user.f_name");
                reportWallPost.reporter.user.lastName = resultSet.getString("reporter_user.l_name");
                try {
                    reportWallPost.reporter.user.picPath = (this.resultSet.getObject("reporter_user.pic_path")==null)?new Pictures():this.gson.fromJson(this.resultSet.getString("reporter_user.pic_path"), Pictures.class);
                }catch (Exception ex){
                    reportWallPost.reporter.user.picPath.original.path =(this.resultSet.getObject("reporter_user.pic_path")==null)?"":this.resultSet.getString("reporter_user.pic_path");
                    ex.printStackTrace();
                }

                reportWallPost.reporter.user.createdDate = resultSet.getString("reporter_user.created_date");

                System.out.println(reportWallPost.reporter);

                reportWallPost.actionTakenBy.user.firstName = resultSet.getString("actionTaker_user.f_name");
                reportWallPost.actionTakenBy.user.lastName =  resultSet.getString("actionTaker_user.l_name");



                reportWallPostList.add(reportWallPost);
                // wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                //  wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                //  wallPost.owner.user.lastName = this.resultSet.getString("l_name");

                //            try{
                //                wallPost.owner.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                //            }catch (Exception ex){
                //                wallPost.owner.user.picPath.original.path = this.resultSet.getString("proPic");
                //
                //                ex.printStackTrace();
                //            }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return reportWallPostList;
    }
    public List<ReportWallPost> getAll(){
        List<ReportWallPost> reportWallPostList = new ArrayList();

        String query = "select * from "+super.tableName+" rw " +
                "JOIN wall_post wp on wp.id = rw.wallpost_id " +
                "JOIN app_login_credential reporter on reporter.id = rw.reporter_id " +
                "LEFT join admin_login actionTaker on actionTaker.id = rw.action_taken_by " +
                "LEFT join user_inf reporter_user on reporter_user.id = reporter.u_id " +
                "LEFT join user_inf actionTaker_user on reporter_user.id = actionTaker.u_id " +
                "LEFT join report_type rt on rt.id = rw.report_type " +
                " order by rw.id desc ";
        this.setQuery(query);
        this.getData();
        try {
            while(this.resultSet.next()){
                ReportWallPost reportWallPost = new ReportWallPost();
                reportWallPost.description = this.resultSet.getString("rw.description");
                reportWallPost.createdDate = this.resultSet.getTimestamp("rw.created_date");

                reportWallPost.id = this.resultSet.getInt("rw.id");
                reportWallPost.wallPost.id = this.resultSet.getInt("wp.id");
                reportWallPost.wallPost.description = this.resultSet.getString("wp.description");
                reportWallPost.wallPost.isBlocked = this.resultSet.getInt("wp.is_blocked")==1?true:false;
                reportWallPost.wallPost.wallPostMood = this.resultSet.getString("wp.wall_post_mood");
                reportWallPost.wallPost.picPath = this.resultSet.getString("wp.picture_path");
                reportWallPost.wallPost.createdDate = this.getProcessedDateTime(this.resultSet.getString("wp.created_date")); //Long.toString(this.resultSet.getString("wall_postCdate").getTime());

                reportWallPost.reportType.id = this.resultSet.getInt("rt.id");
                reportWallPost.reportType.name  = this.resultSet.getString("rt.name");

                reportWallPost.wallPost.owner.id = this.resultSet.getInt("wp.owner_id");


                reportWallPost.reporter.id = resultSet.getInt("reporter.id");
                reportWallPost.reporter.textStatus = resultSet.getString("reporter.text_status");
                reportWallPost.reporter.phoneNumber = resultSet.getString("reporter.phone_number");
                reportWallPost.reporter.user.firstName = resultSet.getString("reporter_user.f_name");
                reportWallPost.reporter.user.lastName = resultSet.getString("reporter_user.l_name");
                try {
                    reportWallPost.reporter.user.picPath = (this.resultSet.getObject("reporter_user.pic_path")==null)?new Pictures():this.gson.fromJson(this.resultSet.getString("reporter_user.pic_path"), Pictures.class);
                }catch (Exception ex){
                    reportWallPost.reporter.user.picPath.original.path =(this.resultSet.getObject("reporter_user.pic_path")==null)?"":this.resultSet.getString("reporter_user.pic_path");
                    ex.printStackTrace();
                }

                reportWallPost.reporter.user.createdDate = resultSet.getString("reporter_user.created_date");

                System.out.println(reportWallPost.reporter);

                reportWallPost.actionTakenBy.user.firstName = resultSet.getString("actionTaker_user.f_name");
                reportWallPost.actionTakenBy.user.lastName =  resultSet.getString("actionTaker_user.l_name");



                reportWallPostList.add(reportWallPost);
                // wallPost.owner.user.id = this.resultSet.getInt("user_infId");
                //  wallPost.owner.user.firstName = this.resultSet.getString("f_name");
                //  wallPost.owner.user.lastName = this.resultSet.getString("l_name");

                //            try{
                //                wallPost.owner.user.picPath  = this.gson.fromJson(this.resultSet.getString("proPic"), Pictures.class);
                //            }catch (Exception ex){
                //                wallPost.owner.user.picPath.original.path = this.resultSet.getString("proPic");
                //
                //                ex.printStackTrace();
                //            }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return reportWallPostList;
    }
    public static void main(String args[]){
        ReportWallPostModel reportWallPostModel = new ReportWallPostModel();
        List<ReportWallPost> reportWallPost =  reportWallPostModel.getAll();
        System.out.print(reportWallPost);
    }
}
