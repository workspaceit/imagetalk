<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.app.Login" %>
<%@ page import="model.AdminLoginModel" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.datamodel.app.ReportWallPost" %>
<%@ page import="java.util.List" %>
<%@ page import="model.ReportWallPostModel" %>
<%@ page import="org.omg.CosNaming.NamingContextExtPackage.StringNameHelper" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%
  ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
  Login login = new Login();
  if(imageTalkBaseController.isSessionValid(request)){
    login = imageTalkBaseController.getUserLoginFromSession(request);
  }else{
    response.sendRedirect("/admin/login");
  }
  ReportWallPostModel reportWallPostModel = new ReportWallPostModel();
  List<ReportWallPost> reportWallPostList = reportWallPostModel.getAllPending();

%>
<!DOCTYPE html>
<html>

<%@include file="head.jsp"%>
<link rel="stylesheet" href="/assets/css/bootstrap-toggle.min.css">

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

  <%@include file="top_nav_bar.jsp"%>
  <%@include file="menu.jsp"%>
  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
      <h1>
        App user
        <small>all admin user list</small>
      </h1>
      <ol class="breadcrumb">
        <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-dashboard"></i> Dashboard</a></li>
        <li>Admin user management</li>
      </ol>
    </section>

    <!-- Main content -->
    <section class="content">
      <div class="row">
        <div class="col-xs-12">
          <div class="box">
            <div class="box-header">

            </div><!-- /.box-header -->
            <div class="box-body">
              <table class="table table-bordered table-striped" id="example1">
                <thead>
                <tr>
                  <th>No</th>
                  <th>Wall post</th>
                  <th>Wall post desc</th>
                  <th>Report type</th>
                  <th>Report desc</th>
                  <th>Reporter</th>
                  <th>Date</th>
                  <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <%
                  int count = 0;
                  for(ReportWallPost reportWallPost:reportWallPostList){
                %>

                <tr>
                  <td width="2%">
                    <%=++count%>
                  </td>
                  <td width="20%" >
                    <img src="<%=imageTalkBaseController.getBaseUrl(request)%>app/media/access/pictures?p=<%=reportWallPost.wallPost.picPath%>" style="width: 70%;height: 40%" />
                  </td>
                  <td width="20%" ><span><%=StringEscapeUtils.escapeHtml3(reportWallPost.wallPost.description)%></span></td>
                  <td>
                    <%=reportWallPost.reportType.name%>
                  </td>
                  <td>
                    <%=StringEscapeUtils.escapeHtml3(reportWallPost.description)%>
                  </td>
                  <td  width="10%" >
                    <span>
                     <b>Name : </b>
                    <%=reportWallPost.reporter.user.firstName+" "+reportWallPost.reporter.user.lastName%>
                   </span>
                    <span>
                    <img src="<%=imageTalkBaseController.getBaseUrl(request)%>app/media/access/pictures?p=<%=reportWallPost.reporter.user.picPath.original.path%>" style="width: 100%;height: 30%" />
                    </span>

                  </td>
                  <td  width="10%" ><%=reportWallPost.createdDate%></td>
                  <td  width="10%" >
                    <input type="checkbox" class="developerToggle" <%=(reportWallPost.wallPost.isBlocked)?"checked":""%> data-toggle="toggle" reportId="<%=reportWallPost.id%>" >
                  </td>

                </tr>
                <%

                  }
                %>
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
//    $('#example2').DataTable({
//      "paging": true,
//      "lengthChange": false,
//      "searching": false,
//      "ordering": true,
//      "info": true,
//      "autoWidth": false
//    });
  });

  function blockWallPost(id,actionType){
    var url  = $("#base_url").val()+"admin/report/wall-post/take-action";

    $.ajax({
      url: url,
      method: "POST",
      data: {"id": id,"action_type":actionType},
      success: function (data) {
        if(data.responseStat.status){
          alert("Wall post is "+(actionType==="_blocked")?"blocked":"allowed");
        }
      },error:function(){
        alert("Error occurred");
      }
    });


    console.log("Trigger");
    return false;
  }


</script>
<script src="/assets/dist/js/bootstrap-toggle.min.js"></script>
<script>
  $('.developerToggle').bootstrapToggle({
    on: 'Blocked',
    off: 'Allow',

  });
  $('.developerToggle').change(function(){
    var actionType = "";
    if($(this).prop('checked')){
      actionType = "_block";
    }else{
      actionType = "_allow";
    }
    blockWallPost( $(this).attr("reportId"),actionType);

  });
</script>
</body>
</html>
