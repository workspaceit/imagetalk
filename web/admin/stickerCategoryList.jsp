<%--
  Created by IntelliJ IDEA.
  User: Abu Bakar Siddique
  Email: absiddique.live@gmail.com
  Date: 10/27/15
  Time: 12:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="controller.service.ImageTalkBaseController" %>
<%@ page import="model.datamodel.app.Login" %>
<%@ page import="model.StickerCategoryModel" %>
<%@ page import="java.util.ArrayList" %>
<%
    ImageTalkBaseController imageTalkBaseController = new ImageTalkBaseController();
    Login login = new Login();
    if (imageTalkBaseController.isSessionValid(request)) {
        login = imageTalkBaseController.getUserLoginFromSession(request);
    } else {
        response.sendRedirect("/admin/login");
    }
    StickerCategoryModel stickerCategoryModel = new StickerCategoryModel();
    ArrayList<StickerCategoryModel> categoryModelArrayList = stickerCategoryModel.getStickerCategoryList();

%>
<!DOCTYPE html>
<html>

<%@include file="head.jsp" %>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <%@include file="top_nav_bar.jsp" %>
    <%@include file="menu.jsp" %>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                Sticker
                <small>Category</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="<%=imageTalkBaseController.getBaseUrl(request)+"admin"%>"><i class="fa fa-dashboard"></i>
                    Dashboard</a></li>
                <li>Sticker Category</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">
                                <h2>Sticker Category</h2>
                            </h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table class="table table-bordered table-striped" id="example1">
                                <thead>
                                <tr>
                                    <th>SL</th>
                                    <th>Name</th>
                                    <th>Type</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <% int i = 1; %>
                                <% for (StickerCategoryModel stickerCategory : categoryModelArrayList) { %>
                                <tr>
                                    <td>
                                        <%=i%>
                                    </td>
                                    <td>
                                        <%=stickerCategory.getName()%>
                                    </td>
                                    <td>
                                        <%=(stickerCategory.getIs_paid() == 1) ? "Paid" : "Free"%>
                                    </td>
                                    <td>
                                        <a href=""><i class="fa fa-edit"></i></a>
                                        <a href="javascript:void(0)"
                                           onclick="deleteCategory(this,<%=stickerCategory.getId()%>);"><i
                                                class="fa fa-trash-o"></i></a>
                                    </td>
                                    <% i++; %>
                                </tr>
                                <% } %>
                                </tbody>
                            </table>

                        </div>
                        <!-- /.box-body -->
                    </div>
                    <!-- /.box -->
                </div>
                <!-- /.col -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="small-box bg-red" id="errorMsgDiv">

                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@include file="footer.jsp" %>

    <!-- Control Sidebar -->

    <!-- Add the sidebar's background. This div must be placed
         immediately after the control sidebar -->
    <div class="control-sidebar-bg"></div>
</div>
<!-- ./wrapper -->

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

    function makeTeamLead(elem, login_id) {
        var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();

        var url = $("#base_url").val() + "admin/user/update/type/teamlead";
        $(loadingImg).show();
        $.ajax({
            url: url,
            method: "POST",
            data: {"login_id": login_id},
            success: function (data) {
                if (data.responseStat.status) {
                    $(loadingImg).hide();
                    $(elem).parents("tr").first().find(".UserType").first().html("Team lead");
                    $(elem).attr("onclick", "makeUser(this," + login_id + ")");
                    $(elem).html("Change to user");
                } else {
                }
            }
        });


        console.log("Trigger");
        return false;
    }

    function makeUser(elem, login_id) {
        var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();

        var url = $("#base_url").val() + "admin/user/update/type/user";
        $(loadingImg).show();
        $.ajax({
            url: url,
            method: "POST",
            data: {"login_id": login_id},
            success: function (data) {
                if (data.responseStat.status) {
                    $(loadingImg).hide();
                    $(elem).parents("tr").first().find(".UserType").first().html("Member");
                    $(elem).attr("onclick", "makeTeamLead(this," + login_id + ")");
                    $(elem).html("Make Team Lead");
                } else {

                }
            }
        });


        console.log("Trigger");
        return false;
    }

    function deleteAppUer(elem, user_id, login_id) {
        $("#errorMsgDiv").html("");
        var r = confirm("Are you sure ?");
        if (r) {
            var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();
            var url = $("#base_url").val() + "admin/operation/admin_user/delete";
            $(loadingImg).show();
            $.ajax({
                url: url,
                method: "POST",
                data: {
                    "u_id": user_id,
                    "login_id": login_id
                },
                success: function (data) {
                    $(loadingImg).hide();
                    if (data.responseStat.status) {
                        var row = $(elem).parents("tr").first();
                        if (!$(row).hasClass('selected')) {
                            $(row).addClass('selected');

                        }
                        var userTable = $("#example1").DataTable();
                        userTable.row('.selected').remove().draw(false);
                    } else {

                        $("#errorMsgDiv").html(data.responseStat.msg);
                        document.getElementById('errorMsgDiv').scrollIntoView();
                    }

                    console.log(data);
                }
            });
        } else {

        }
    }

    function deleteCategory(elem, catId) {
        var parentRow = $(elem).parents("tr").first();
        var url = $("#base_url").val() + "admin/sticker/operation/category/delete";

        $.ajax({
            url: url,
            dataType: "json",
            method: "POST",
            data: {"id": catId},
            success: function (data) {
                if (data.responseStat.status) {
                    parentRow.css({'display': 'none'});
                    alert(data.responseStat.msg)
                } else {
                    alert(data.responseStat.msg)
                }
            }
        });
    }
</script>
</body>
</html>