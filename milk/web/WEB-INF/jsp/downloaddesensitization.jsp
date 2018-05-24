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

        searchtag();
    });

    function add() {
        $('#acc').accordion('add', {
            title: 'A',
            content: 'New Content',
            selected: false
        });
        // $('#acc').accordion('add', {
        //     title : "A",
        //     iconCls : 'icon-ok',
        //     selected : true,
        //     content : '<div style="padding:10px"><ul name="'+dd+'">操</ul></div>',
        // });
    }

    function searchtag() {
        var tag = $("#tag").val();
        var obj = new Object();
        obj.tag = tag;
        $.ajax({
            type: "POST",
            url: "/milk/searchTag",
            data: JSON.stringify(obj),
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
                console.log(data);
                if(data!=null){
                    $("#tagtable").datagrid("loadData",data);
                }else{
                    $("#hint").html("查询失败");
                }
            },
            error: function () {
                $("#hint").html("程序运行出错！");
            }
        });
    }

    function downloadTagChecked(){
        //下载选中项的dicom文件
        var arr = new Array();
        var rows = $('#tagtable').datagrid('getSelections');
        for(var i=0;i<rows.length;i++){
            arr.push(rows[i].tag);
        }
        if(arr.length==0){
            alert("未选中任何项")
        }else{
            var obj = new Object()
            obj.tags=arr;
            var paramJsonStr = JSON.stringify(obj)
            // $.ajax({
            //     type: "POST",
            //     url: "/milk/downloaddicomhelp",
            //     data: paramJsonStr,
            //     dataType: 'json',
            //     traditional:true,
            //     contentType: 'application/json;charset=utf-8',
            //     success: function (data) {
            //     },
            //     error: function () {
            //         $("#hint").html("程序运行出错！");
            //     }
            // });
            simulationForm("/milk/downloadDesensitizeByTag?tag=SB");
        }
    }
    function simulationForm(url) {
        var form = $('<form method="post" action="'+url+'"></form>');
        form.appendTo("body").submit().remove();
        return;
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
        <div data-options="region:'center',title:'下载脱敏数据'">
            <div class="easyui-layout" style="width:auto;height:100%;">
                <div data-options="region:'north'" style="height:9%">
                    <div class="easyui-panel" title="" style="width:auto">
                        <div  style="margin:0px 0px 0px 35%">
                            <form id="ff" method="post">
                                <table cellpadding="5">
                                    <tr>
                                        <td>Tag:</td>
                                        <td><input id="tag" class="easyui-textbox" type="text" name="name" data-options="required:true"/></td>
                                        <td>
                                            <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;" onclick="searchtag()">Search</a>
                                        </td>
                                    </tr>
                                </table>
                            </form>
                        </div>
                    </div>
                </div>
                <div data-options="region:'center',title:'查询结果',iconCls:'',tools:'#tt'">
                    <div class="easyui-panel"  style="width:auto;height:100%;">
                        <table id="tagtable" class="easyui-datagrid" title="" style="width:auto;height:100%"
                               data-options="url:'/milk/searchTag',method:'POST'">
                            <thead>
                            <tr>
                                <th field="ck" checkbox="true"></th>
                                <th data-options="field:'tag',width:80">tag</th>
                                <th data-options="field:'count',width:100">count</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div id="tt">
                    <a   class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="exportSomeTag();" style="width:100px;height:18px" align="right">导出选中</a>
                    <a   class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="downloadTagChecked();" style="width:100px;height:18px" align="right">下载选中</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
