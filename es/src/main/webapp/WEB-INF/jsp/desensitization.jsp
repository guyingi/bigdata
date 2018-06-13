<%--
  Created by IntelliJ IDEA.
  User: WeiGuangWu
  Date: 2018/5/18
  Time: 13:57
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
$(function(){
    //加载页面的时候将id列隐藏
    $("#resulttable").datagrid('hideColumn', "id");

    $('#resulttable').datagrid('getPager').pagination({//分页栏下方文字显示
        displayMsg:'当前显示{from}-{to} 共{total}条记录',
    });
});

function submit() {
    //根据tag查询属于这个标签的dicom 序列
    var tag = $("#tag").val();
    if(tag.length == 0){
        alert("tag为空");
    }else{
        var obj = new Object();
        obj.tag = tag;
        $("#tablediv").panel({title: tag});
        $.ajax({
            type: "POST",
            url: "/es/searchDicomByTag",
            data: JSON.stringify(obj),
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

}
function desensitize() {
    //请求做脱敏处理
    var tag = "";
    var rows = $('#tagtable').datagrid('getSelections');
    if(rows.length > 1){
        alert("只能同时对单个tag脱敏");
    }else{
        if(0 == rows.length){
            tag = $("#tablediv").panel("options").title;
        }else{
            tag = rows[0].tagname;
        }
        if(tag.length == 0){
            alert("提示，当前tag值为空");
        }else{
            $.messager.confirm('确认', '对tag为'+tag+"的序列做脱敏处理", function(r){
                if(r){
                    $("#hint").html("正在脱敏处理....稍等");
                    var obj = new Object()
                    obj.tag = tag;
                    $.ajax({
                        type: "POST",
                        url: "/es/desensitize",
                        data: JSON.stringify(obj),
                        dataType: 'json',
                        traditional:true,
                        contentType: 'application/json;charset=utf-8',
                        success: function (data) {
                            console.log(data);
                            if(data.result == 0){
                                $("#hint").html("");
                                $.messager.confirm('消息', '脱敏完成', function(r){});
                            }else if(data.result == 1){
                                $("#hint").html("");
                                alert("tag："+tag+"已经做过脱敏");
                            }else{
                                $("#hint").html("");
                                alert("脱敏操作失败");
                            }
                        },
                        error: function () {
                            $("#hint").html("程序运行出错！");
                        }
                    });
                }
            });
        }
    }
}

function listSeriesOfTag(index,record) {
    var tagname = record['tagname'];
    var obj = new Object();
    obj.tag = tagname;
    $("#tablediv").panel({title: tagname});
    $.ajax({
        type: "POST",
        url: "/es/searchDicomByTag",
        data: JSON.stringify(obj),
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
                        <li><a href="./navigation.html">首页</a></li>
                        <li><a href="./dicomsearch.html">查询dicom</a></li>
                        <li><a href="./patientsearch.html">查询患者</a></li>
                        <li><a href="./signtag.html">打标签</a></li>
                        <li class="active"><a href="#">数据脱敏</a></li>
                        <li><a href="./downloaddesensitization.html">下载脱敏数据</a></li>
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
                <div title="数据脱敏" iconCls="icon-ok">
                        <div class="easyui-layout" data-options="fit:true,border:false">
                            <div data-options="region:'north',border:false" style="min-height:50px;">
                                <div class="easyui-panel" title="" style="width:auto">
                                    <div  style="margin:0px 0px 0px 35%">
                                        <form id="ff" method="post">
                                            <table cellpadding="5">
                                                <tr>
                                                    <td>Tag:</td>
                                                    <td><input id="tag" class="easyui-textbox" type="text" name="tag" data-options="required:true"/></td>
                                                    <td>
                                                        <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;" onclick="submit()">查询</a>
                                                    </td>
                                                    <td>
                                                        <a style="width:30px"></a>
                                                    </td>
                                                    <td>
                                                        <a class="easyui-linkbutton" data-options="iconCls:'icon-reload'" style="width:80px;height:30px;" onclick="desensitize()">开始脱敏</a>
                                                    </td>
                                                    <td>
                                                        <p id="hint"></p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <div data-options="region:'center',border:false">
                                     <div class="easyui-layout" data-options="fit:true,border:false">
                                            <div data-options="region:'north'" data-options="fit:true,border:false" style="height:150px;">
                                                <div id="tagdiv" class="easyui-panel" title="" style="width:auto" data-options="fit:true,border:false">
                                                      <table id="tagtable" title="" class="easyui-datagrid" style="width:auto;height:480px"
                                                           url="/es/listtags"
                                                           pageList="[4]"
                                                           pageSize="4"
                                                           idField="id"
                                                           rownumbers="true"
                                                           pagination="true"
                                                           iconCls="icon-table"
                                                           data-options="onClickRow:listSeriesOfTag"
                                                    >
                                                        <thead>
                                                        <tr>
                                                            <th field="ck" checkbox="true"></th>
                                                            <th field="tagname" width="14%">tag</th>
                                                            <th field="count" width="14%" align="left">序列数量</th>
                                                            <th field="desensitize" width="20%" align="left">是否脱敏</th>
                                                        </tr>
                                                        </thead>
                                                    </table>
                                                </div>
                                            </div>
                                            <div data-options="region:'center',border:false" style="height:150px;">
                                                <div id="tablediv" class="easyui-panel" title="" data-options="fit:true,border:false" style="margin-top:5px;">
                                                    <table id="resulttable" title="" class="easyui-datagrid" style="width:auto;height:480px"
                                                           url="/es/ajaxPage"
                                                           pageList="[20]"
                                                           pageSize="20"
                                                           idField="id"
                                                           rownumbers="true"
                                                           pagination="true"
                                                           iconCls="icon-table"
                                                    >
                                                        <thead>
                                                        <tr>
                                                            <th field="ck" checkbox="true"></th>
                                                            <th field="id" width="0%">Item ID</th>
                                                            <th field="InstitutionName" width="14%">医院</th>
                                                            <th field="SeriesDescription" width="20%" align="left">序列描述</th>
                                                            <th field="PatientName" width="20%" align="left">名字</th>
                                                            <th field="SeriesDate" width="15%" align="left">检查日期</th>
                                                            <th field="NumberOfSlices" width="15%" align="center">张数</th>
                                                            <th field="tag" width="15%" align="center">tag</th>
                                                        </tr>
                                                        </thead>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
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