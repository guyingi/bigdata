<%--
  Created by IntelliJ IDEA.
  User: WeiGuangWu
  Date: 2018/5/18
  Time: 13:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
    <title>雅森大数据查询系统</title>
    <link rel="shortcut icon" type="image/x-icon" href="favicon.ico"  media="screen"/>
    <link rel="stylesheet" type="text/css" href="css/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/icon.css">
    <link rel="stylesheet" type="text/css" href="css/demo.css">
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/jquery.easyui.min.js"></script>
</head>
<script type="text/javascript">
$(function(){
    $('#tree').tree({
        onClick: function(node){
            if("查询dicom" == node.text){
                window.location="/milk/dicomsearch";
            }else if("查询患者" == node.text){
                window.location="/milk/patientsearch";
            }else if("打标签" == node.text){
                window.location="/milk/signtag";
            }else if("数据脱敏" == node.text){
                window.location="/milk/desensitization";
            }else if("下载脱敏数据" == node.text){
                window.location="/milk/downloaddesensitization";
            }else{
                // alert("other");
                window.location="/milk/navigation";
            }
        }
    });
});

function submit() {
    //根据tag查询属于这个标签的dicom 序列
    var tag = $("#tag").val()
    var obj = new Object()
    obj.tag = tag;
    $("#tablediv").panel({title: tag});
    $.ajax({
        type: "POST",
        url: "/milk/searchDicomByTag",
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
function desensitize() {
    //请求做脱敏处理
    var tag = $("#tablediv").panel("options").title;
    var obj = new Object()
    obj.tag = tag;
    $.ajax({
        type: "POST",
        url: "/milk/desensitize",
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
<body style="padding:0;">
<div style="width:auto;height:30px;background: linear-gradient(to right, #0000C6, #FFFFFF);">
    <div class="easyui-layout" style="width:auto;height:30px;background-color:rgba(255, 255, 255, 0);">
        <div data-options="region:'west',border:false" style="width:90%;background-color:rgba(255, 255, 255, 0);"></div>
        <div data-options="region:'center',border:false" style="background-color:rgba(255, 255, 255, 0);display:flex;flex-direction:row;justify-content:center;align-items:center;">
            <a style="flex:1">${username}</a>
        </div>
        <div data-options="region:'east',border:false" style="width:5%;background-color:rgba(255, 255, 255, 0);display:flex;flex-direction:row;justify-content:center;align-items:center;">
            <a href="/milk" style="flex:1">退出</a>
        </div>
    </div>
</div>

<div class="easyui-panel" title="" style="width:auto;height:750px;padding:10px;">
    <div class="easyui-layout" data-options="fit:true">
        <div data-options="region:'west',border:true" style="width:150px;height:auto;">
            <div class="easyui-panel" style="height:100%;padding:5px;">
                <ul id="tree" class="easyui-tree">
                    <li data-options="state:'open'">
                        <span>功能菜单</span>
                        <ul>
                            <li data-options="iconCls:'icon-search'">
                                <span>查询dicom</span>
                            </li>
                            <li data-options="iconCls:'icon-search'">
                                <span>查询患者</span>
                            </li>
                            <li data-options="iconCls:'icon-search'">
                                <span>打标签</span>
                            </li>
                            <li data-options="iconCls:'icon-search'">
                                <span>数据脱敏</span>
                            </li>
                            <li data-options="iconCls:'icon-search'">
                                <span>下载脱敏数据</span>
                            </li>
                            <li data-options="iconCls:'icon-search'">
                                <span>首页</span>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <div data-options="region:'center',title:'数据脱敏'">
            <div class="easyui-layout" style="width:auto;height:100%;">
                <div data-options="region:'north'" style="height:9%">
                    <div class="easyui-panel" title="" style="width:auto">
                        <div  style="margin:0px 0px 0px 35%">
                            <form id="ff" method="post">
                                <table cellpadding="5">
                                    <tr>
                                        <td>Tag:</td>
                                        <td><input id="tag" class="easyui-textbox" type="text" name="tag" data-options="required:true"/></td>
                                        <td>
                                            <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;" onclick="submit()">Search</a>
                                        </td>
                                        <td>
                                            <a style="width:30px"></a>
                                        </td>
                                        <td>
                                            <a class="easyui-linkbutton" data-options="iconCls:'icon-reload'" style="width:80px;height:30px;" onclick="desensitize()">开始脱敏</a>
                                        </td>
                                        <td>
                                            <p>正在脱敏处理....</p>
                                        </td>
                                    </tr>
                                </table>
                            </form>
                        </div>
                    </div>
                </div>
                <div data-options="region:'center',title:'',iconCls:'icon-ok'">
                    <%--下面是结果表格面板--%>
                    <div id="tablediv" class="easyui-panel" title="" style="width:auto;height:auto">
                        <table id="resulttable" title="" class="easyui-datagrid" style="width:auto;height:480px"
                               url="/milk/ajaxPage"
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
                                <th field="institutionName" width="14%">医院</th>
                                <th field="organ" width="14%" align="left">器官</th>
                                <th field="seriesDescription" width="20%" align="left">序列描述</th>
                                <th field="patientName" width="20%" align="left">名字</th>
                                <th field="seriesDate" width="15%" align="left">检查日期</th>
                                <th field="numberOfSlices" width="15%" align="center">张数</th>
                            </tr>
                            </thead>
                        </table>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>