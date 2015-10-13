<%--
  Created by IntelliJ IDEA.
  User: Abu Bakar Siddique
  Email: absiddique.live@gmail.com
  Date: 10/8/15
  Time: 12:28 PM
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
    ArrayList<StickerCategoryModel> stickerCategoryList = stickerCategoryModel.getStickerCategoryList();
%>
<!DOCTYPE html>
<html>

<%@include file="head.jsp" %>

<link href="/assets/filer/css/jquery.filer.css" type="text/css" rel="stylesheet"/>
<link href="/assets/filer/css/themes/jquery.filer-dragdropbox-theme.css" type="text/css" rel="stylesheet"/>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <%@include file="top_nav_bar.jsp" %>
    <%@include file="menu.jsp" %>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                Upload Image
            </h1>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="row shuffle clearfix">
                    <div class="col-lg-12">
                        <div class="box-body form-horizontal">
                            <div class="form-group">
                                <label for="sticker_category_id" class="col-sm-2 control-label">Category</label>

                                <div class="col-sm-10">
                                    <select name="sticker_category_id" class="form-control" id="sticker_category_id">
                                        <option value="">Select One</option>
                                        <% for (StickerCategoryModel stickerCategory : stickerCategoryList) { %>
                                        <option value="<%=stickerCategory.getId()%>"><%=stickerCategory.getName()%>
                                        </option>
                                        <% } %>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="is_paid" class="col-sm-2 control-label">Type</label>

                                <div class="col-sm-10">
                                    <select name="is_paid" class="form-control" id="is_paid">
                                        <option value="">Select One</option>
                                        <option value="0">Free</option>
                                        <option value="1">Paid</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-12">
                        <div style="margin: 0 auto;width: 98%;">
                            <input type="file" multiple="multiple" name="files[]" id="files">
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.row -->
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->
    <%@include file="footer.jsp" %>
    <div class="control-sidebar-bg"></div>
</div>
<!-- ./wrapper -->

<!-- jQuery 2.1.4 -->
<script src="/assets/plugins/jQuery/jQuery-2.1.4.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="/assets/bootstrap/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="/assets/plugins/fastclick/fastclick.min.js"></script>
<!-- AdminLTE App -->
<script src="/assets/dist/js/app.min.js"></script>
<!-- Sparkline -->
<script src="/assets/plugins/sparkline/jquery.sparkline.min.js"></script>
<!-- jvectormap -->
<script src="/assets/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script src="/assets/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
<!-- SlimScroll 1.3.0 -->
<script src="/assets/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- ChartJS 1.0.1 -->
<script src="/assets/plugins/chartjs/Chart.min.js"></script>
<!-- AdminLTE dashboard demo (This is only for demo purposes) -->
<script src="/assets/dist/js/pages/dashboard2.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/assets/dist/js/demo.js"></script>

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="/assets/filer/js/jquery.filer.min.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $('#files').filer({
            limit: null,
            maxSize: null,
            extensions: null,
            changeInput: '<div class="jFiler-input-dragDrop"><div class="jFiler-input-inner"><div class="jFiler-input-icon"><i class="icon-jfi-cloud-up-o"></i></div><div class="jFiler-input-text"><h3>Drag&Drop files here</h3> <span style="display:inline-block; margin: 15px 0">or</span></div><a class="jFiler-input-choose-btn blue">Browse Files</a></div></div>',
            showThumbs: true,
            appendTo: null,
            theme: "dragdropbox",
            templates: {
                box: '<ul class="jFiler-item-list"></ul>',
                item: '<li class="jFiler-item">\
                            <div class="jFiler-item-container">\
                                <div class="jFiler-item-inner">\
                                    <div class="jFiler-item-thumb">\
                                        <div class="jFiler-item-status"></div>\
                                        <div class="jFiler-item-info">\
                                            <span class="jFiler-item-title"><b title="{{fi-name}}">{{fi-name | limitTo: 25}}</b></span>\
                                        </div>\
                                        {{fi-image}}\
                                    </div>\
                                    <div class="jFiler-item-assets jFiler-row">\
                                        <ul class="list-inline pull-left">\
                                            <li>{{fi-progressBar}}</li>\
                                        </ul>\
                                        <ul class="list-inline pull-right">\
                                            <li><a class="icon-jfi-trash jFiler-item-trash-action"></a></li>\
                                        </ul>\
                                    </div>\
                                </div>\
                            </div>\
                        </li>',
                itemAppend: '<li class="jFiler-item">\
                            <div class="jFiler-item-container">\
                                <div class="jFiler-item-inner">\
                                    <div class="jFiler-item-thumb">\
                                        <div class="jFiler-item-status"></div>\
                                        <div class="jFiler-item-info">\
                                            <span class="jFiler-item-title"><b title="{{fi-name}}">{{fi-name | limitTo: 25}}</b></span>\
                                        </div>\
                                        {{fi-image}}\
                                    </div>\
                                    <div class="jFiler-item-assets jFiler-row">\
                                        <ul class="list-inline pull-left">\
                                            <span class="jFiler-item-others">{{fi-icon}} {{fi-size2}}</span>\
                                        </ul>\
                                        <ul class="list-inline pull-right">\
                                            <li><a class="icon-jfi-trash jFiler-item-trash-action"></a></li>\
                                        </ul>\
                                    </div>\
                                </div>\
                            </div>\
                        </li>',
                progressBar: '<div class="bar"></div>',
                itemAppendToEnd: false,
                removeConfirmation: false,
                _selectors: {
                    list: '.jFiler-item-list',
                    item: '.jFiler-item',
                    progressBar: '.bar',
                    remove: '.jFiler-item-trash-action',
                }
            },
            uploadFile: {
                url: $("#base_url").val() + "admin/operation/upload/image/ajax",
                data: {},
                type: 'POST',
                enctype: 'multipart/form-data',
                beforeSend: function () {
                },
                success: function (data, el) {
                    var category_id = $("#sticker_category_id").val();
                    var is_paid = $("#is_paid").val();
                    insertStickerInfo(data.path, category_id, is_paid);
                    var parent = el.find(".jFiler-jProgressBar").parent();
                    el.find(".jFiler-jProgressBar").fadeOut("slow", function () {
                        $("<div class=\"jFiler-item-others text-success\"><i class=\"icon-jfi-check-circle\"></i> Success</div>").hide().appendTo(parent).fadeIn("slow");
                    });
                },
                error: function (el) {
                    var parent = el.find(".jFiler-jProgressBar").parent();
                    el.find(".jFiler-jProgressBar").fadeOut("slow", function () {
                        $("<div class=\"jFiler-item-others text-error\"><i class=\"icon-jfi-minus-circle\"></i> Error</div>").hide().appendTo(parent).fadeIn("slow");
                    });
                },
                statusCode: {},
                onProgress: function () {
                },
            },
            dragDrop: {
                dragEnter: function () {
                },
                dragLeave: function () {
                },
                drop: function () {
                },
            },
            addMore: true,
            clipBoardPaste: true,
            excludeName: null,
            beforeShow: function () {
                return true
            },
            onSelect: function () {
            },
            afterShow: function () {
            },
            onRemove: function () {
            },
            onEmpty: function () {
            },
            captions: {
                button: "Choose Files",
                feedback: "Choose files To Upload",
                feedback2: "files were chosen",
                drop: "Drop file here to Upload",
                removeConfirmation: "Are you sure you want to remove this file?",
                errors: {
                    filesLimit: "Only {{fi-limit}} files are allowed to be uploaded.",
                    filesType: "Only Images are allowed to be uploaded.",
                    filesSize: "{{fi-name}} is too large! Please upload file up to {{fi-maxSize}} MB.",
                    filesSizeAll: "Files you've choosed are too large! Please upload files up to {{fi-maxSize}} MB."
                }
            }
        });

        function insertStickerInfo(path, category_id, is_paid) {
            var url = $("#base_url").val() + "admin/sticker/operation/create";
            $.ajax({
                url: url,
                method: "POST",
                dataType: JSON,
                data: {
                    "img_path": path,
                    "category_id": category_id,
                    "is_paid": is_paid
                },
                success: function (data) {
                    console.log(data);
                }
            });
        }
    });
</script>

<!--[if IE]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

</body>
</html>