<%--
  Created by IntelliJ IDEA.
  User: WeiGuangWu
  Date: 2018/5/15
  Time: 20:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>雅森大数据查询系统</title>
    <link rel="shortcut icon" type="image/x-icon" href="image/favicon.ico"  media="screen"/>
    <link rel="stylesheet" type="text/css" href="css/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/icon.css">
    <link rel="stylesheet" type="text/css" href="css/demo.css">
    <link rel="stylesheet" type="text/css" href="css/common.css" />
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
</head>
<script type="text/javascript">
    $.fn.serializeObject = function()
    {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

    function loadCSS(){
        var linkarr = new Array("css/default/easyui.css","css/icon.css","css/demo.css");
        for(var i=0;i<linkarr.length;i++){
            var link = document.createElement("link");
            link.rel = "stylesheet";
            link.type = "text/css";
            link.href = linkarr[i];
            document.getElementsByTagName("head")[0].appendChild(link);
        }
    }
    $(document).ready(function(){
        //加载页面的时候将id列隐藏
        $("#resulttable").datagrid('hideColumn', "id");

        $('#resulttable').datagrid('getPager').pagination({//分页栏下方文字显示
            displayMsg:'当前显示{from}-{to} 共{total}条记录',
        });

        //医院联想搜索
        $.ajax({
            type: "GET",
            url: "/es/associativeSearchHospital",
            data: null,
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
                console.log(JSON.stringify(data));
                console.log(data);
                if(data!=null){
                    $("#hospital").combobox({
                        data :data,//获取要显示的json数据
                        valueField: 'lable',
                        textField: 'text',
                        panelHeight: 'auto'
                    });
                }
            },
            error: function () {
            }
        });

        //器官联想搜索
        $.ajax({
            type: "GET",
            url: "/es/associativeSearchOrgan",
            data: null,
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
                console.log(JSON.stringify(data));
                console.log(data);
                if(data!=null){
                    $("#organ").combobox({
                        data :data,//获取要显示的json数据
                        valueField: 'lable',
                        textField: 'text',
                        panelHeight: 'auto'
                    });
                }
            },
            error: function () {
            }
        });

        $.extend($.fn.textbox.defaults.rules, {
            number: {
                validator: function (value, param) {
                    return /^[0-9]*$/.test(value);
                },
                message: "请输入数字"
            },
            chinese: {
                validator: function (value, param) {
                    var reg = /^[\u4e00-\u9fa5]+$/i;
                    return reg.test(value);
                },
                message: "请输入中文"
            },
            checkLength: {
                validator: function (value, param) {
                    return param[0] >= get_length(value);
                },
                message: '请输入最大{0}位字符'
            },
            specialCharacter: {
                validator: function (value, param) {
                    var reg = new RegExp("[`~!@#$^&*()=|{}':;'\\[\\]<>~！@#￥……&*（）——|{}【】‘；：”“'、？]");
                    return !reg.test(value);
                },
                message: '不允许输入特殊字符'
            }
        });
    });

    function dosigntag(){
        var tag = $("#tag").val();
        if(tag.length == 0){
            alert("tag不能为空")
        }else{
            var obj = new Object();
            obj.tag = tag;
            var paramJsonStr = JSON.stringify(obj);
            // $('#dlg').dialog('open');
            $.ajax({
                type: "POST",
                url: "/es/dosigntag",
                data: paramJsonStr,
                dataType: 'json',
                traditional:true,
                contentType: 'application/json;charset=utf-8',
                success: function (data) {
                    console.log(data);
                    if(data.result){
                        //打标签成功
                        $.messager.confirm('消息', '你成功为该批序列打上了'+data.tag+'的标签，数量'+data.total, function(r){});
                    }
                },
                error: function () {
                    $("#hint").html("程序运行出错！");
                }
            });
        }
    }

    function simulationForm(url) {
        var form = $('<form method="post" action="'+url+'"></form>');
        form.appendTo("body").submit().remove();
        return;
    }

    function submitForm(){
        var paramJsonStr = JSON.stringify($("#ff").serializeObject());
        $.ajax({
            type: "POST",
            url: "/es/ajaxSearch",
            data: paramJsonStr,
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
                console.log(data);
                if(data!=null){
                    $("#resulttable").datagrid("loadData",data);
                }else{
                    $("#hint").html("查询失败");
                }
            },
            error: function () {
                $("#hint").html("程序运行出错！");
            }
        });
    }
    function showthumbnail(index,record){
        var obj = new Object()
        obj.id=record["id"];
        var thumbnailtitle = "行号："+(index+1)+"的略缩图"
        $("#thumbnailtable").panel({title: thumbnailtitle});
        var paramJsonStr = JSON.stringify(obj)
        $.ajax({
            type: "POST",
            url: "/es/getdicomThumbnail",
            data: paramJsonStr,
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
                console.log(data);
                if(data!=null){
                    $("#thumbnailtable").datagrid("loadData",data);
                }else{
                    $("#hint").html("查询失败");
                }
            },
            error: function () {
                $("#hint").html("程序运行出错！");
            }
        });
    }
    function showPicture(value,row,index){
        return  '<img height="100" width="100" src=\''+value+'\'/>';
    }
</script>


 <body class="easyui-layout" id="layout" style="visibility:hidden;">
        <div region="north" id="header">
            <img src="image/yasenLogo.png" class="logo" />
            <div class="top-btns">
                <span>欢迎您，${username}</span>
                <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-lock'">修改密码</a>
                <a href="index.html" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-clear'">退出系统</a>
            </div>
        </div>

        <div region="west" split="true" title="导航菜单" id="naver">
            <div class="easyui-accordion" fit="true" id="navmenu">
                <div title="功能菜单">
                    <ul class="navmenu">
                        <li><a href="navigation">首页</a></li>
                        <li><a href="dicomsearch">查询dicom</a></li>
                        <li><a href="patientsearch">查询患者</a></li>
                        <li class="active"><a href="#">打标签</a></li>
                        <li><a href="desensitization">数据脱敏</a></li>
                        <li><a href="downloaddesensitization">下载脱敏数据</a></li>
                    </ul>
                </div>
                <div title="雅森天机"></div>
                <div title="雅森数据"></div>
                <div title="脑科示例"></div>
                <div title="肺科示例">
                    <ul class="navmenu">
                    </ul>
                </div>
            </div>
        </div>

        <div region="center" id="content">
            <div class="easyui-tabs" fit="true" id="tt">
                <!-- 引入内容开始 -->
                <!-- 复杂视图布局的嵌套 -->
                <div title="打标签" iconCls="icon-ok">
                        <div class="easyui-layout" data-options="fit:true,border:false">
                            <div data-options="region:'center'" style="padding:5px;">
                                <div class="easyui-layout" data-options="fit:true,border:false">
                                    <div data-options="region:'north',split:true" style="height:150px;">
                                        <!-- 查询和打标签部分开始 -->
                                        <div class="easyui-layout" data-options="fit:true,border:false">
                                            <div data-options="region:'east',split:true" style="width:260px;border:none;">
                                                    <div class="easyui-layout" data-options="region:'north',border:false" style="height:70%;background:#B3DFDA;padding:0">
                                                        <div data-options="region:'north',border:false" style="height:50%">
                                                            <p style="text-align: center;">为该批数据打个标签</p>
                                                        </div>
                                                        <div data-options="region:'center',border:false" style="text-align:center;">
                                                            <input id="tag" class="easyui-textbox" type="text" name="tag" data-options="required:false" style="width:200px;height:30px;"/>
                                                        </div>
                                                    </div>
                                                    <div data-options="region:'center',border:false">
                                                        <a class="easyui-linkbutton" style="width:80px;height:30px;position: absolute;left:80px;" onclick="dosigntag();">打标签</a>
                                                    </div>
                                            </div>
                                            <!-- 查询条件部分 -->
                                            <div data-options="region:'center',border:false">
                                                <form id="ff" method="post" action="" style="height:100%;">
                                                     <div class="easyui-layout" data-options="fit:true">
                                                        <div data-options="region:'center'" style="border: none;width:50%;">
                                                            <!-- 查询条件一 -->
                                                            <table cellpadding="2px">
                                                                <tr>
                                                                    <td>序列描述:</td>
                                                                    <td><input class="easyui-textbox" type="text" name="ManufacturerModelName" data-options="required:false"/></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>医院:</td>
                                                                    <td><input id="hospital" class="easyui-combobox" name="InstitutionName" type="text"/></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>性别:</td>
                                                                    <td>
                                                                        <select class="easyui-combobox" name="PatientSex" style="width:90%;"><option value="M">男</option><option value="F">女</option><option value="U">未知</option><option value="">不限</option></select>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td>年龄阶段:</td>
                                                                    <td>
                                                                        <table>
                                                                            <tr>
                                                                                <td>
                                                                                    <input id="age_start" class="easyui-textbox" name="PatientAge_start" data-options="validType:['number','length[0,3]']" style="width:60px;"/>
                                                                                </td>
                                                                                <td>
                                                                                    <p style="margin:0;">至</p>
                                                                                </td>
                                                                                <td>
                                                                                    <input class="easyui-textbox" name="PatientAge_end" data-options="validType:['number','length[0,3]']" style="width:70px;"/>
                                                                                </td>
                                                                                <td>
                                                                                    <p style="margin:0;">岁</p>
                                                                                </td>
                                                                            </tr>
                                                                        </table>
                                                                    </td>
                                                                </tr>
                                                        </table>

                                                        </div>
                                                        <div data-options="region:'east'" style="border: none;width:50%;">
                                                            <!-- 查询条件二 -->
                                                            <table cellpadding="0">
                                                            <tr>
                                                                <td>检查时间段：</td>
                                                                <td>
                                                                    <table>
                                                                        <tr>
                                                                            <td>
                                                                                <input class="easyui-datebox" name="StudyDate_start" data-options="sharedCalendar:'#cc'" style="width:100px;"/>
                                                                            </td>
                                                                            <td>
                                                                                <p style="margin:0;">至</p>
                                                                            </td>
                                                                            <td>
                                                                                <input class="easyui-datebox" name="StudyDate_end" data-options="sharedCalendar:'#cc'" style="width:100px;"/>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>录入时间段：</td>
                                                                <td>
                                                                    <table>
                                                                        <tr>
                                                                            <td>
                                                                                <input class="easyui-datebox" name="entrydate_start" data-options="sharedCalendar:'#cc'" style="width:100px;">
                                                                            </td>
                                                                            <td>
                                                                                <p style="margin:0;">至</p>
                                                                            </td>
                                                                            <td>
                                                                                <input class="easyui-datebox" name="entrydate_end" data-options="sharedCalendar:'#cc'" style="width:100px;">
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>图片张数：</td>
                                                                <td>
                                                                    <table>
                                                                        <tr>
                                                                            <td>
                                                                                <input class="easyui-textbox" name="NumberOfSlices_start" data-options="validType:['number','length[0,3]']" style="width:100px;">
                                                                            </td>
                                                                            <td>
                                                                                <p style="margin:0;">至</p>
                                                                            </td>
                                                                            <td>
                                                                                <input class="easyui-textbox" name="NumberOfSlices_end" data-options="validType:['number','length[0,3]']" style="width:100px;">
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:25px;" onclick="submitForm()">查询</a>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                        </div>

                                                     </div>
                                                </form>
                                            </div>
                                            <div id="cc" class="easyui-calendar"></div>  <!-- 这个是用在存放日历那个图标的，必须存在，放哪儿无所谓 -->
                                        </div>
                                        <!-- 查询和打标签部分结束 -->
                                    </div>
                                    <div id="tablediv" data-options="region:'center',title:'查询结果'">
                                         <table id="resulttable" title="" class="easyui-datagrid" style="width:auto;height:auto"
                                               url="/es/ajaxPage"
                                               pageList="[25]"
                                               pageSize="25"
                                               idField="id"
                                               rownumbers="true"
                                               pagination="true"
                                               iconCls="icon-table"
                                               data-options="onClickRow:showthumbnail"
                                        >
                                            <thead>
                                            <tr>
                                                <th field="id" width="0%">Item ID</th>
                                                <th field="InstitutionName" width="14%">医院</th>
                                                <th field="ManufacturerModelName" width="20%" align="left">序列描述</th>
                                                <th field="PatientName" width="20%" align="left">名字</th>
                                                <th field="SeriesDate" width="10%" align="left">检查日期</th>
                                                <th field="NumberOfSlices" width="10%" align="center">张数</th>
                                                <th field="tag" width="10%" align="center">Tag</th>
                                            </tr>
                                            </thead>
                                        </table>
                                    </div>
                                </div>
                            </div>
                            <div data-options="region:'east',title:'影像资料',split:true" style="width:310px;">
                                 <table id="thumbnailtable" class="easyui-datagrid" title=""
                                       data-options="singleSelect:true,collapsible:true,fit:true,border:false">
                                    <thead>
                                        <tr>
                                            <th data-options="field:'image0',width:100,heightalign:'center',formatter:showPicture"></th>
                                            <th data-options="field:'image1',width:100,heightalign:'center',formatter:showPicture"></th>
                                            <th data-options="field:'image2',width:100,heightalign:'center',formatter:showPicture"></th>
                                        </tr>
                                    </thead>
                                </table>
                            </div>
                        </div>
                </div>
                <!-- 引入内容结束 -->
            </div>
        </div>

        <div region="south" id="footer">雅森科技数据查询平台 V1.0</div>

        <script type="text/javascript">
            $(function() {
                //添加新的Tab页
                $("#navmenu").on("click", "a[data-url]", function(e) {
                    e.preventDefault();
                    var tabTitle = $(this).text();
                    var tabUrl = $(this).data("url");

                    if($("#tt").tabs("exists", tabTitle)) { //判断该Tab页是否已经存在
                        $("#tt").tabs("select", tabTitle);
                    }else {
                        $("#tt").tabs("add", {
                            title: tabTitle,
                            href: tabUrl,
                            closable: true
                        });
                    }
                    $("#navmenu .active").removeClass("active");
                    $(this).parent().addClass("active");
                });

                //解决闪屏的问题
                window.setTimeout(function() {
                    $("#layout").css("visibility", "visible");
                }, 80);
            });
        </script>
    </body>
</html>