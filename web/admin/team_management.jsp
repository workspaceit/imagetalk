<%@ page import="model.TeamModel" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.datamodel.TeamDetails" %>
<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.Login" %>
<%@ page import="model.datamodel.TeamMember" %>
<%
    ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
    Login login = new Login();
    if(imageTalkBaseController.isSessionValid(request)){
        login = imageTalkBaseController.getUserLoginFromSession(request);
    }else{
        response.sendRedirect("/admin/login");
    }
    TeamModel teamModel = new TeamModel();
    ArrayList<TeamDetails> teamDetailsList = teamModel.getAllTeamDetails();
%>
<!DOCTYPE html>
<html>

<%@include file="head.jsp"%>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <%@include file="top_nav_bar.jsp"%>
    <%@include file="menu.jsp"%>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                Team management
                <small>all team in list</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-dashboard"></i>Dashboard</a></li>
                <li>Team management</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">
                                <button class="btn btn-block btn-primary btn-lg" onclick="javascript:location.href ='<%=imageTalkBaseController.getBaseUrl(request)+"admin/team/add"%>';">Add new Team</button>
                            </h3>
                        </div><!-- /.box-header -->
                        <div class="box-body">
                            <table class="table table-bordered table-striped" id="example1" class="dynamicTable table" >

                                <!-- Table heading -->

                                <thead class="bg-gray">
                                <tr>

                                    <th>Name</th>
                                    <th>Team Lead</th>
                                    <th>Created By</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>

                                <%

                                    for(TeamDetails tempTeamDetails:teamDetailsList){
                                %>

                                <tr class="gradeA">
                                    <td><%=tempTeamDetails.name%></td>

                                    <%
                                        TeamMember teamLead =  new TeamMember();

                                        for(TeamMember tm:tempTeamDetails.members){
                                            if(tm.is_lead){
                                                teamLead = tm;
                                                break;
                                            }
                                        }
                                    %>
                                    <td><%=teamLead.f_name+" "+teamLead.l_name%></td>
                                    <td><%=tempTeamDetails.createdBy.f_name + " "+tempTeamDetails.createdBy.l_name%></td>
                                    <td>
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                                                Action
                                            </button>
                                            <ul class="dropdown-menu pull-left">
                                                <li><a href='<%=imageTalkBaseController.getBaseUrl(request)+"admin/team/update/"+tempTeamDetails.id%>' >Edit</a></li>
                                                <li><a href="javascript:void(0)" onclick="deleteTeam(this,<%=tempTeamDetails.id%>)">Delete</a></li>
                                            </ul>
                                        </div>
                                        <div class="btn-group" ><img class="loadingImg" src="/assets/img/loding.gif" style="display: none;"/></div>
                                    </td>
                                </tr>
                                <%	} %>
                                </tbody>
                            </table>
                        </div><!-- /.box-body -->
                    </div><!-- /.box -->
                </div><!-- /.col -->
            </div><!-- /.row -->

            <div class="row">
                <div class="small-box bg-red" id="errorMsgDiv">

                </div>
            </div>

        </section><!-- /.content -->
    </div><!-- /.content-wrapper -->
    <%@include file="footer.jsp"%>

    <!-- Control Sidebar -->

    <!-- Add the sidebar's background. This div must be placed
         immediately after the control sidebar -->
    <div class="control-sidebar-bg"></div>
</div><!-- ./wrapper -->

<!-- jQuery 2.1.4 -->
<script src="/assets/plugins/jQuery/jQuery-2.1.4.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="/assets/bootstrap/js/bootstrap.min.js"></script>
<!-- DataTables -->
<script src="/assets/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/assets/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- SlimScroll -->
<script src="/assets/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="/assets/plugins/fastclick/fastclick.min.js"></script>
<!-- AdminLTE App -->
<script src="/assets/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/assets/dist/js/demo.js"></script>
<!-- page script -->
<script>
    $(function () {
        $("#example1").DataTable();
    });


    function deleteTeam(elem,team_id){
        $("#errorMsgDiv").html("");
        var r = confirm("Are you sure ?");
        if (r) {
            var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();
            var url = $("#base_url").val()+"admin/operation/team/delete";
            $(loadingImg).show();
            $.ajax({
                url: url,
                method: "POST",
                data: {
                    "team_id": team_id
                },
                success: function (data) {
                    $(loadingImg).hide();
                    if(data.responseStat.status){
                        var row = $(elem).parents("tr").first();
                        if ( !$(row).hasClass('selected') ) {
                            $(row).addClass('selected');

                        }
                        var userTable = $("#example1").DataTable();
                        userTable.row('.selected').remove().draw( false );
                    }else{

                        $("#errorMsgDiv").html(data.responseStat.msg);
                        document.getElementById( 'errorMsgDiv' ).scrollIntoView();
                    }

                    console.log(data);
                }
            });
        } else {

        }
    }
</script>
</body>
</html>
