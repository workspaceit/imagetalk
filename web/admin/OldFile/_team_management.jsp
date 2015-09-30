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
<%@include file="head.jsp" %>
<body class="">

<!-- Main Container Fluid -->
<div class="container-fluid menu-hidden">

    <!-- Sidebar Menu -->
    <%@include file="menu.jsp" %>

    <!-- // Sidebar Menu END -->

    <!-- Content -->
    <div id="content">

        <%@include file="top_nav_bar.jsp" %>
        <!-- // END navbar -->



        <h1>User List</h1>
        <div class="innerLR">

            <!-- Widget -->
            <div class="widget widget-body-white widget-heading-simple">
                <div class="widget-body">

                    <div class="widget">
                        <div class="widget-body overflow-x">
                             <table class="dynamicTable table" >

                        <!-- Table heading -->

                        <thead class="bg-gray">
                        <tr>

                            <th>Name</th>
                            <th>Team Lead</th>
                            <th>Created By</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <!-- // Table heading END -->

                        <!-- Table body -->
                        <%

                            for(TeamDetails tempTeamDetails:teamDetailsList){
                        %>
                        <tbody>


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
                                        <li><a href="javascript:void(0)">Delete</a></li>
                                    </ul>
                                </div>
                                <div class="btn-group" ><img class="loadingImg" src="/assets/img/loding.gif" style="display: none;"/></div>
                            </td>
                        </tr>

                        </tbody>
                        <!-- // Table body END -->
                        <%	} %>
                    </table>
                    <!-- // Table END -->
                    </div>
                        <div>



                </div>
            </div>
            <!-- // Widget END -->




        </div>




    </div>
    <!-- // Content END -->

    <div class="clearfix"></div>
    <!-- // Sidebar menu & content wrapper END -->

    <%@include file="footer.jsp" %>

    <!-- // Footer END -->

</div>
<!-- // Main Container Fluid END -->
<script>
    function makeTeamLead(elem,login_id){
        var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();

        var url  = $("#base_url").val()+"admin/user/update/type/teamlead";
        $(loadingImg).show();
        $.ajax({
            url: url,
            method: "POST",
            data: {"login_id": login_id},
            success: function (data) {
                if(data.responseStat.status){
                    $(loadingImg).hide();
                    $(elem).parents("tr").first().find(".UserType").first().html("Team lead");
                    $(elem).attr("onclick","makeUser(this,"+login_id+")");
                    $(elem).html("Change to user");
                }else{
                }
            }
        });


        console.log("Trigger");
        return false;
    }
    function makeUser(elem,login_id){
        var loadingImg = $(elem).parents("td").first().find(".loadingImg").first();

        var url  = $("#base_url").val()+"admin/user/update/type/user";
        $(loadingImg).show();
        $.ajax({
            url: url,
            method: "POST",
            data: {"login_id": login_id},
            success: function (data) {
                if(data.responseStat.status){
                    $(loadingImg).hide();
                    $(elem).parents("tr").first().find(".UserType").first().html("Member");
                    $(elem).attr("onclick","makeTeamLead(this,"+login_id+")");
                    $(elem).html("Make Team Lead");
                }else{
                }
            }
        });


        console.log("Trigger");
        return false;
    }

</script>

<!-- Global -->
<script data-id="App.Config">
    var App = {};	var basePath = '',
            commonPath = '/assets/',
            rootPath = '../',
            DEV = false,
            componentsPath = '/assets/components/';

    var primaryColor = '#3695d5',
            dangerColor = '#b55151',
            successColor = '#609450',
            infoColor = '#4a8bc2',
            warningColor = '#ab7a4b',
            inverseColor = '#45484d';

    var themerPrimaryColor = primaryColor;

</script>

            <script src="/assets/components/library/bootstrap/js/bootstrap.min.js?v=v1.0.2&sv=v0.0.1"></script>
            <script src="/assets/components/plugins/nicescroll/jquery.nicescroll.min.js?v=v1.0.2&sv=v0.0.1"></script>
            <script src="/assets/components/plugins/breakpoints/breakpoints.js?v=v1.0.2&sv=v0.0.1"></script>
            <script src="/assets/components/plugins/preload/pace/pace.min.js?v=v1.0.2&sv=v0.0.1"></script>
            <script src="/assets/components/plugins/preload/pace/preload.pace.init.js?v=v1.0.2&sv=v0.0.1"></script>
            <script src="/assets/components/core/js/animations.init.js?v=v1.0.2"></script>
            <script src="/assets/components/common/tables/datatables/assets/lib/js/jquery.dataTables.min.js?v=v1.0.2&sv=v0.0.1"></script>
            <%--<script src="/assets/components/common/tables/datatables/assets/lib/extras/TableTools/media/js/TableTools.min.js?v=v1.0.2&sv=v0.0.1"></script>--%>
            <%--<script src="/assets/components/common/tables/datatables/assets/lib/extras/ColVis/media/js/ColVis.min.js?v=v1.0.2&sv=v0.0.1"></script>--%>
            <script src="/assets/components/common/tables/datatables/assets/custom/js/DT_bootstrap.js?v=v1.0.2&sv=v0.0.1"></script>
            <%--<script src="/assets/components/common/tables/datatables/assets/custom/js/datatables.init.js?v=v1.0.2&sv=v0.0.1"></script>--%>
            <script src="/assets/custom/table/js/datatables.init.js?v=v1.0.2&sv=v0.0.1"></script>

            <script src="/assets/components/common/forms/elements/fuelux-checkbox/fuelux-checkbox.js?v=v1.0.2&sv=v0.0.1"></script>
            <script src="/assets/components/common/forms/elements/bootstrap-select/assets/lib/js/bootstrap-select.js?v=v1.0.2&sv=v0.0.1"></script>
            <script src="/assets/components/common/forms/elements/bootstrap-select/assets/custom/js/bootstrap-select.init.js?v=v1.0.2&sv=v0.0.1"></script>
            <%--<script src="/assets/components/common/tables/datatables/assets/lib/extras/FixedHeader/FixedHeader.js?v=v1.0.2&sv=v0.0.1"></script>--%>
            <%--<script src="/assets/components/common/tables/datatables/assets/lib/extras/ColReorder/media/js/ColReorder.min.js?v=v1.0.2&sv=v0.0.1"></script>--%>
            <%--<script src="/assets/components/common/tables/classic/assets/js/tables-classic.init.js?v=v1.0.2&sv=v0.0.1"></script>--%>
            <script src="/assets/components/core/js/sidebar.main.init.js?v=v1.0.2"></script>
            <script src="/assets/components/core/js/sidebar.discover.init.js?v=v1.0.2"></script>
            <%--<script src="/assets/components/core/js/core.init.js?v=v1.0.2"></script>--%>
</body>
</html>