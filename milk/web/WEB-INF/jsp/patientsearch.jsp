<%--
  Created by IntelliJ IDEA.
  User: WeiGuangWu
  Date: 2018/5/15
  Time: 20:17
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

        $("#datatypetable").datagrid('hideColumn', "patientname");

        $("#detailtable").datagrid('hideColumn', "id");
        $("#detailtable").datagrid('hideColumn', "datatype");
        $("#detailtable").datagrid('hideColumn', "patientname");

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

    function simulationForm(url) {
        var form = $('<form method="post" action="'+url+'"></form>');
        form.appendTo("body").submit().remove();
        return;
    }
    function submitForm() {
        var patientname = $("#patientname").val();
        if(patientname.length==0){
            $.messager.confirm("友情提示","查询内容不能为空");
        }else{
            var obj = new Object()
            obj.patientname=patientname;
            var paramJsonStr = JSON.stringify(obj)
            $.ajax({
                type: "POST",
                url: "/milk/getpatient",
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
        var patientname = record.patientname;
        if(patientname.length==0){
            $.messager.confirm("友情提示","患者名不能为空");
        }else{
            var obj = new Object()
            obj.patientname=patientname;
            var paramJsonStr = JSON.stringify(obj)
            $.ajax({
                type: "POST",
                url: "/milk/getpatientdatatype",
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
            url: "/milk/getdetail",
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
                param += encodeURI(arr[j])+"-";
            }
            var url = "/milk/downloadbypatient?patientname="+param;
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
                typeStr += typearr[j]+"-";
            }
            var paramStr = "patientname="+encodeURI(patientname)+"&datatype="+typeStr;
            var url = "/milk/downloadbytype?"+paramStr;
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
        alert(idarr.length);
        if(idarr.length == 0){
            $.messager.confirm("提示","您未选择任何类别");
        }else{
            var idStr = ""
            for(var j=0; j<idarr.length; j++){
                idStr += idarr[j]+"-";
            }
            var paramStr = "patientname="+encodeURI(patientname)+"&datatype="+datatype+"&ids="+idStr;
            var url = "/milk/downloaddetail?"+paramStr;
            simulationForm(url);

        }
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

<div class="easyui-panel" title="" style="width:auto;height:95%;padding:10px;">
    <div class="easyui-layout" data-options="fit:true">
        <div data-options="region:'west',border:true" style="width:150px;height:auto;">
            <div class="easyui-panel" style="height:100%;padding:5px;">
                <ul id="tree" class="easyui-tree">
                    <li data-options="state:'open'">
                        <span>功能菜单</span>
                        <ul>
                            <li data-options="iconCls:'icon-search'">
                                <span>首页</span>
                            </li>
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
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <div data-options="region:'center',title:'查询患者'">
            <div class="easyui-layout" style="width:auto;height:100%;">
                <div data-options="region:'north'" style="height:9%">
                    <div class="easyui-panel" title="" style="width:auto">
                        <div  style="margin:0px 0px 0px 35%">
                            <form id="ff" method="post">
                                <table cellpadding="5">
                                    <tr>
                                        <td>Name:</td>
                                        <td><input id="patientname" class="easyui-textbox" type="text" name="name" data-options="required:true"></input></td>
                                        <td>
                                            <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;" onclick="submitForm()">Search</a>
                                        </td>
                                    </tr>
                                </table>
                            </form>
                        </div>
                    </div>
                </div>
                <div data-options="region:'center',title:'查询结果',iconCls:'icon-ok'">
                    <div class="easyui-layout" style="width:100%;height:100%;">
                        <div data-options="region:'west',split:true" title="患者" style="width:30%;height:100%">
                            <table id="patienttable" class="easyui-datagrid"
                                   data-options="url:'',method:'get',border:false,singleSelect:true,fit:true,fitColumns:true,toolbar:[{ text: '下载', iconCls: 'icon-save', handler: downloadpatient}],onClickRow:showdatatype">
                                <thead>
                                <tr>
                                    <th field="ck" checkbox="true"></th>
                                    <th data-options="field:'patientname'" width="80">name</th>
                                    <th data-options="field:'age'" width="80">age</th>
                                    <th data-options="field:'sex'" width="80">sex</th>
                                    <th data-options="field:'hospital'" width="80">hospital</th>
                                </tr>
                                </thead>
                            </table>

                        </div>
                        <div data-options="region:'center',title:'数据类别',iconCls:'icon-ok'" style="height:100%;">
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
                        <div data-options="region:'east',split:true" title="数据详情" style="width:60%;height:100%;">
                            <table  id="detailtable" class="easyui-datagrid"
                                   data-options="url:'',method:'get',border:false,singleSelect:false,fit:true,fitColumns:true,toolbar:[{text:'下载',iconCls:'icon-save',handler:downloaddetail}]">
                                <thead>
                                <tr>
                                    <th field="ck" checkbox="true"></th>
                                    <th data-options="field:'id'" width="80"></th>
                                    <th data-options="field:'patientname'" width="80"></th>
                                    <th data-options="field:'datatype'" width="80"></th>
                                    <th data-options="field:'describe'" width="80">序列描述</th>
                                    <th data-options="field:'organ'" width="100">器官</th>
                                    <th data-options="field:'count',align:'right'" width="80">数量</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div data-options="region:'south',border:true" style="height:20px;background:#f1f8ff;">
        </div>

    </div>
</div>
</body>
</html>
