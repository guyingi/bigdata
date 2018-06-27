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
            url: "/searchTag",
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

    function exportSomeTag() {
        var arr = new Array();
        var rows = $('#tagtable').datagrid('getSelections');
        for(var i=0;i<rows.length;i++){
            arr.push(rows[i].tag);
        }
        if(arr.length==0){
            alert("未选中任何项")
        }else{
            var param="";
            for(var e in arr){
                param += arr[e]+"-";
            }
            simulationForm("/exportDesensitizeByTag?tags="+param);
        }

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
        }else if(arr.length > 1){
            alert("多个tag请用另一种方式下载")
        } else{
            simulationForm("/downloadDesensitizeByTag?tag="+arr[0]);
        }
    }
    function simulationForm(url) {
        var form = $('<form method="POST" action="'+url+'"></form>');
        form.appendTo("body").submit().remove();
        return;
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
                        <li><a href="signtag">打标签</a></li>
                        <li><a href="tagmanage">标签管理</a></li>
                        <li class="active"><a href="#">下载脱敏数据</a></li>
                    </ul>
                </div>
                <div title="雅森天机"></div>
                <div title="雅森数据"></div>
                <div title="脑科示例"></div>
                <div title="肺科示例">
                    <ul class="navmenu">
                        <!-- <li><a href="#" data-url="html/demo01.html">锁定行和列</a></li> -->
                    </ul>
                </div>
            </div>
        </div>

        <div region="center" id="content">
            <div class="easyui-tabs" fit="true" id="tt">
                <!-- 引入内容开始 -->
                <div title="下载脱敏数据" iconCls="icon-ok">
                        <div class="easyui-layout" data-options="fit:true,border:false">
                            <div data-options="region:'north'" style="min-height:48px;">
                                <div class="easyui-panel" title="" style="width:auto">
                                    <div  style="margin:0px 0px 0px 35%">
                                        <form id="ff" method="post">
                                            <table cellpadding="5">
                                                <tr>
                                                    <td>Tag:</td>
                                                    <td><input id="tag" class="easyui-textbox" type="text" name="name" data-options="required:true"/></td>
                                                    <td>
                                                        <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;" onclick="searchtag()">查询</a>
                                                    </td>
                                                </tr>
                                            </table>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <div data-options="region:'center',border:false">
                                     <div class="easyui-layout" data-options="fit:true,border:false">
                                            <div data-options="region:'north'" data-options="fit:true,border:false" style="min-height:30px;">
                                                <div class="easyui-panel" title="" style="width:auto" data-options="fit:true,border:false">
                                                       <div id="tool" style="margin:5px 5px 0px 5px;">
                                                            <a   class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="exportSomeTag();" style="width:100px;height:18px" align="right">导出选中</a>
                                                            <a   class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="downloadTagChecked();" style="width:100px;height:18px" align="right">下载选中</a>
                                                        </div>
                                                </div>
                                            </div>
                                            <div data-options="region:'center',title:'查询结果'">
                                                <div class="easyui-panel" data-options="fit:true,border:false">
                                                    <table id="tagtable" class="easyui-datagrid" title="" style="width:auto;height:100%"
                                                           data-options="url:'/searchTag',method:'POST'" >
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