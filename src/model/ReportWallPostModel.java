package model;

import helper.DateHelper;
import model.datamodel.app.ReportType;
import model.datamodel.app.ReportWallPost;
import model.datamodel.app.WallPost;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by mi on 11/4/16.
 */
public class ReportWallPostModel extends ImageTalkBaseModel{
    static final String tableName = "report_wallpost";
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
    public int updateActionTakenByWallPostId(int wallPostId) {

        String query = "UPDATE `"+tableName+"` SET `action_taken`=1,`action_taken_at`='%s'" +
                " where `wallpost_id` = %s and `action_taken`=0";
        String result = String.format(query,DateHelper.getUtcDateTime(),wallPostId);

        return super.insertData(result);
    }
    public static void main(String args[]){
        ReportWallPostModel reportWallPostModel = new ReportWallPostModel();
        ReportWallPost reportWallPost = new ReportWallPost();
        WallPostModel wallPostModel = new WallPostModel();
        ReportTypeModel reportTypeModel = new ReportTypeModel();
        ReportType reportType = new ReportType();
        reportType = reportTypeModel.getById(1);
        AppLoginCredentialModel appLoginCredentialModel = new AppLoginCredentialModel();
        AdminLoginModel adminLoginModel = new AdminLoginModel();
        wallPostModel.setId(1);
        appLoginCredentialModel.setId(1);
        reportWallPost.description ="Shut it down";
        reportWallPost.wallPost = wallPostModel.getById();
        reportWallPost.reporter = appLoginCredentialModel.getAppCredentialById();
        reportWallPost.actionTakenBy = adminLoginModel.getAdminCredentialById(1);
        reportWallPost.reportType = reportType;
        System.out.println(reportType);
        reportWallPostModel.insert(reportWallPost);
        reportWallPostModel.updateActionTakenByWallPostId(1);

    }
}
