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
    $(function(){

        $("#patienttable").datagrid('hideColumn', "id");
        $("#datatypetable").datagrid('hideColumn', "patientname");

        $("#detailtable").datagrid('hideColumn', "id");
        $("#detailtable").datagrid('hideColumn', "datatype");
        $("#detailtable").datagrid('hideColumn', "patientname");
    });

    function simulationForm(url) {
        var form = $('<form method="post" action="'+url+'"></form>');
        form.appendTo("body").submit().remove();
        return;
    }
    function submitForm() {
        if(patientname.length==0){
            $.messager.confirm("友情提示","查询内容不能为空");
        }else{
            var obj = new Object()
            obj.patientname=patientname;
            var paramJsonStr = JSON.stringify(obj)
            $.ajax({
                type: "POST",
                url: "/es/getpatient",
                data: paramJsonStr,
                dataType: 'json',
                traditional:true,
                contentType: 'application/json;charset=utf-8',
                success: function (data) {
                    console.log(data);
                    if(data!=null){
                        $("#patienttable").datagrid("loadData",data);
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
    function showdatatype(index,record) {
        var patientname = record["PatientName"];
        alert(patientname);
        if(patientname.length==0){
            $.messager.confirm("友情提示","患者名不能为空");
        }else{
            var obj = new Object()
            obj.patientname=patientname;
            var paramJsonStr = JSON.stringify(obj)
            $.ajax({
                type: "POST",
                url: "/es/getpatientdatatype",
                data: paramJsonStr,
                dataType: 'json',
                traditional:true,
                contentType: 'application/json;charset=utf-8',
                success: function (data) {
                    console.log(data);
                    if(data!=null){
                        $("#datatypetable").datagrid("loadData",data);
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
    function showdetail(index,record) {
        var patientname = record.patientname;
        var datatype = record.datatype;
        var obj = new Object()
        obj.patientname=patientname;
        obj.datatype=datatype;
        var paramJsonStr = JSON.stringify(obj)
        $.ajax({
            type: "POST",
            url: "/es/getdetail",
            data: paramJsonStr,
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
                console.log(data);
                if(data!=null){
                    $("#detailtable").datagrid("loadData",data);
                }else{
                    $("#hint").html("查询失败");
                }
            },
            error: function () {
                $("#hint").html("程序运行出错！");
            }
        });
    }
    function downloadpatient() {
        var arr = new Array();
        var rows = $('#patienttable').datagrid('getSelections');
        for(var i=0;i<rows.length;i++){
            arr.push(rows[i].patientname);
        }
        if(arr.length == 0){
            $.messager.confirm("友情提示","您未选中任何人");
        }else{
            var param = "";
            for(var j=0; j<arr.length; j++){
                param += encodeURI(arr[j])+"#";
            }
            var url = "/es/downloadbypatient?patientname="+param;
            simulationForm(url);
        }

    }

    function downloadtype(){
        var patientname = "";
        var typearr = new Array();
        var rows = $('#datatypetable').datagrid('getSelections');
        for(var i=0;i<rows.length;i++){
            patientname = rows[i].patientname;
            typearr.push(rows[i].datatype);
        }

        if(typearr.length == 0){
            $.messager.confirm("提示","您未选择任何类别");
        }else{
            var typeStr = ""
            for(var j=0; j<typearr.length; j++){
                typeStr += typearr[j]+"#";
            }
            var paramStr = "patientname="+encodeURI(patientname)+"&datatype="+typeStr;
            var url = "/es/downloadbytype?"+paramStr;
            simulationForm(url);

        }

    }

    function downloaddetail(){
        var rows = $('#detailtable').datagrid('getSelections');
        var idarr = new Array();
        var patientname = "";
        var datatype = "";
        for(var i=0;i<rows.length;i++){
            patientname = rows[i].patientname;
            datatype = rows[i].datatype;
            idarr.push(rows[i].id);
        }
        if(idarr.length == 0){
            $.messager.confirm("提示","您未选择任何类别");
        }else{
            var idStr = ""
            for(var j=0; j<idarr.length; j++){
                idStr += idarr[j]+"#";
            }
            var paramStr = "patientname="+encodeURI(patientname)+"&datatype="+datatype+"&ids="+idStr;
            var url = "/es/downloaddetail?"+paramStr;
            simulationForm(url);

        }
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
                        <li class="active"><a href="#">查询患者</a></li>
                        <li><a href="signtag">打标签</a></li>
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
                 <div title="查询患者" iconCls="icon-ok">
                    <!-- <div class="easyui-accordion" data-options="fit:true"> -->
                        <div id="cc" class="easyui-layout" style="width:100%;height:100%;">
                            <div data-options="region:'north',title:'查询患者',split:true" style="min-height:80px;">
                                <div class="easyui-panel" title="" style="width:auto">
                                    <div  style="margin:0px 0px 0px 35%">
                                        <form id="ff" method="post">
                                            <table cellpadding="5">
                                                <tr>
                                                    <td>姓名:</td>
                                                    <td><input id="patientname" class="easyui-textbox" type="text" name="name" data-options="required:true"></input></td>
                                                    <td>
                                                        <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;" onclick="submitForm()">查询</a>
                                                    </td>
                                                </tr>
                                            </table>
                                        </form>
                                    </div>
                                </div>
                            </div>

                             <div data-options="region:'center',title:'查询结果',iconCls:'icon-ok'">
                                <div class="easyui-layout" style="width:100%;height:100%;">
                                     <div data-options="region:'west',title:'患者',split:true" style="width:30%;height:100%;">
                                         <table id="patienttable" class="easyui-datagrid"
                                               data-options="url:'',method:'get',border:false,singleSelect:true,fit:true,fitColumns:true,toolbar:[{ text: '下载', iconCls: 'icon-save', handler: downloadpatient}],onClickRow:showdatatype">
                                            <thead>
                                            <tr>
                                                <th field="ck" checkbox="true"></th>
                                                <th data-options="field:'id'" width="80"></th>
                                                <th data-options="field:'PatientName'" width="80">name</th>
                                                <th data-options="field:'PatientAge'" width="80">age</th>
                                                <th data-options="field:'PatientSex'" width="80">sex</th>
                                                <th data-options="field:'InstitutionName'" width="80">hospital</th>
                                            </tr>
                                            </thead>
                                        </table>
                                    </div>
                                     <div data-options="region:'center',title:'数据类别',split:true,url:'',method:'get',border:false,singleSelect:false,fit:true,fitColumns:true,onClickRow:showdetail,toolbar:[{text:'下载',iconCls:'icon-save',handler:downloadtype}]" style="width:15%;">
                                             <table id="datatypetable"class="easyui-datagrid"
                                                   data-options="url:'',method:'get',border:false,singleSelect:false,fit:true,fitColumns:true,onClickRow:showdetail,toolbar:[{text:'下载',iconCls:'icon-save',handler:downloadtype}]">
                                                <thead>
                                                <tr>
                                                    <th field="ck" checkbox="true"></th>
                                                    <th data-options="field:'patientname'" width="80"></th>
                                                    <th data-options="field:'datatype'" width="80">类别</th>
                                                </tr>
                                                </thead>
                                            </table>
                                     </div>
                                     <div data-options="region:'east',title:'数据详情',split:true" style="width:55%;">
                                        <table  id="detailtable" class="easyui-datagrid"
                                               data-options="url:'',method:'get',border:false,singleSelect:false,fit:true,fitColumns:true,toolbar:[{text:'下载',iconCls:'icon-save',handler:downloaddetail}]">
                                            <thead>
                                            <tr>
                                                <th field="ck" checkbox="true"></th>
                                                <th data-options="field:'id'" width="80"></th>
                                                <th data-options="field:'patientname'" width="80"></th>
                                                <th data-options="field:'datatype'" width="80"></th>
                                                <th data-options="field:'describe'" width="80">序列描述</th>
                                                <th data-options="field:'count',align:'right'" width="80">数量</th>
                                            </tr>
                                            </thead>
                                        </table>
                                     </div>
                                </div>
                            </div>

                        </div>

                    <!-- </div> -->
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