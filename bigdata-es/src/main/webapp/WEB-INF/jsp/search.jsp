<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String rootpath = request.getContextPath();%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>雅森大数据查询系统</title>
<link rel="shortcut icon" type="image/x-icon" href="favicon.ico"  media="screen"/>
<link rel="stylesheet" type="text/css" href="css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="css/icon.css">
<link rel="stylesheet" type="text/css" href="css/demo.css">
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
<script src="https://cdn.bootcss.com/react/15.4.2/react.min.js"></script>
<script src="https://cdn.bootcss.com/react/15.4.2/react-dom.min.js"></script>
<script src="https://cdn.bootcss.com/babel-standalone/6.22.1/babel.min.js"></script>

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

function exportSomePath(){
    //导出选中项hdfs路径
    var arr = new Array();
    var rows = $('#resulttable').datagrid('getSelections');
    for(var i=0;i<rows.length;i++){
        arr.push(rows[i].id);
    }
    if(arr.length==0){
        alert("未选中任何项")
    }else{
        var obj = new Object()
        obj.type="some";
        obj.ids=arr;
        var paramJsonStr = JSON.stringify(obj)
        $.ajax({
            type: "POST",
            url: "/es/exportdsomepathhelp",
            data: paramJsonStr,
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
            },
            error: function () {
                $("#hint").html("程序运行出错！");
            }
        });

        simulationForm("/es/exportdsomepath");
    }
}
function simulationForm(url) {
    var form = $('<form method="post" action="'+url+'"></form>');
    form.appendTo("body").submit().remove();
    return;
}
function checkall() {
    var a = 0;
    if (!$("#ckbid").is(":checked")) {
        $("input[name='ckb']").each(function () {
            if ($(this).is(":checked")) {
                $(this).removeAttr("checked");
            }
        });
    } else {
        $("input[name='ckb']").each(function () {
            if (!$(this).is(":checked")) {
                $(this).prop("checked", true);
            }
        });
    }
}
function downloadDicomChecked() {
    //下载选中项的dicom文件
    var arr = new Array();
    var rows = $('#resulttable').datagrid('getSelections');
    for(var i=0;i<rows.length;i++){
        arr.push(rows[i].id);
    }
    if(arr.length==0){
        alert("未选中任何项")
    }else{
        var obj = new Object()
        obj.ids=arr;
        var paramJsonStr = JSON.stringify(obj)
        $.ajax({
            type: "POST",
            url: "/es/downloaddicomhelp",
            data: paramJsonStr,
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
            },
            error: function () {
                $("#hint").html("程序运行出错！");
            }
        });
        simulationForm("/es/downloaddicom");
    }
}
function downloadDicomSingle(event) {
    //下载dicom文件
    event = event ? event : window.event;
    var obj = event.srcElement ? event.srcElement : event.target;
    var $obj = $(obj);
    var id = $obj.attr("value")
    //这时obj就是触发事件的对象，可以使用它的各个属性
    //还可以将obj转换成jque
    var arr = new Array();
    arr.push(id);
    var obj = new Object()
    obj.ids=arr;
    var paramJsonStr = JSON.stringify(obj)
    $.ajax({
        type: "POST",
        url: "/es/downloadDicomHelp",
        data: paramJsonStr,
        dataType: 'json',
        traditional:true,
        contentType: 'application/json;charset=utf-8',
        success: function (data) {
        },
        error: function () {
            $("#hint").html("程序运行出错！");
        }
    });

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
</head>
<style type="text/css">
.div-inline{ display:inline} 
</style>
<body style="padding:0">
<div style="width:auto;height:30px;background: linear-gradient(to right, #0000C6, #FFFFFF);">
	<p style="font-family:STXinwei;color:white;"><font size="6">欢迎使用</font></p>
</div>

<div class="easyui-panel" title="" style="width:auto;height:750px;padding:10px;">
    <div class="easyui-layout" data-options="fit:true">
        <div data-options="region:'center'" style="padding:0px">
            <div title="" style="width:auto;height:auto;margin:auto;vertical-align:middle;padding:0px;">

                <div title="" style="width:auto;height:auto;padding:0px;">
                    <div class="easyui-panel" title="请输入查询条件" style="width:auto;height:auto;">
                        <form id="ff" method="post" action="">
                            <div class="easyui-layout" style="width:auto;height:170px;">
                                <div data-options="region:'west',border:false" style="width:100px;"></div>
                                <div data-options="region:'center',border:false" style="width:500px;">
                                    <div class="easyui-layout" style="width:auto;height:165px;">
                                        <div data-options="region:'west',border:false" title="" style="width:280px;height:auto;">
                                            <table cellpadding="2px">
                                                <tr>
                                                    <td>设备:</td>
                                                    <td><input class="easyui-textbox" type="text" name="device" data-options="required:false" /></td>
                                                </tr>
                                                <tr>
                                                    <td>器官:</td>
                                                    <td><input id="organ" class="easyui-combobox" name="organ" type="text"/></td>
                                                </tr>
                                                <tr>
                                                    <td>序列描述:</td>
                                                    <td><input class="easyui-textbox" type="text" name="seriesdescription" data-options="required:false"/></td>
                                                </tr>
                                                <tr>
                                                    <td>医院:</td>
                                                    <td><input id="hospital" class="easyui-combobox" name="institution" type="text"/></td>
                                                </tr>
                                                <tr>
                                                    <td>性别:</td>
                                                    <td>
                                                        <select class="easyui-combobox" name="sex"><option value="M">男</option><option value="F">女</option><option value="U">未知</option><option value="">不限</option></select>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div data-options="region:'center',border:false" title="" style="width:auto;height:auto;">
                                            <table cellpadding="0">
                                                <tr>
                                                    <td>年龄阶段：</td>
                                                    <td>
                                                        <table>
                                                            <tr>
                                                                <td>
                                                                    <input id="age_start" class="easyui-textbox" name="age_start" data-options="validType:['number','length[0,3]']" style="width:50px;"/>
                                                                </td>
                                                                <td>
                                                                    <p style="margin:0;">至</p>
                                                                </td>
                                                                <td>
                                                                    <input class="easyui-textbox" name="age_end" data-options="validType:['number','length[0,3]']" style="width:50px;"/>
                                                                </td>
                                                                <td>
                                                                    <p style="margin:0;">岁</p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>检查时间段：</td>
                                                    <td>
                                                        <table>
                                                            <tr>
                                                                <td>
                                                                    <input class="easyui-datebox" name="studydate_start" data-options="sharedCalendar:'#cc'" style="width:100px;"/>
                                                                </td>
                                                                <td>
                                                                    <p style="margin:0;">至</p>
                                                                </td>
                                                                <td>
                                                                    <input class="easyui-datebox" name="studydate_end" data-options="sharedCalendar:'#cc'" style="width:100px;"/>
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
                                                                    <input class="easyui-textbox" name="imagecount_min" data-options="validType:['number','length[0,3]']" style="width:100px;">
                                                                </td>
                                                                <td>
                                                                    <p style="margin:0;">至</p>
                                                                </td>
                                                                <td>
                                                                    <input class="easyui-textbox" name="imagecount_max" data-options="validType:['number','length[0,3]']" style="width:100px;">
                                                                </td>
                                                                <td>
                                                                    <p style="margin:0;">张</p>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>SlickThickness：</td>
                                                    <td>
                                                        <table>
                                                            <tr>
                                                                <td>
                                                                    <input class="easyui-numberspinner" name="slicethickness_min" value="0" data-options="precision:1,increment:0.1," style="width:120px;"></input>
                                                                </td>
                                                                <td>
                                                                    <p style="margin:0;">至</p>
                                                                </td>
                                                                <td>
                                                                    <input class="easyui-numberspinner" name="slicethickness_min" value="10" data-options="precision:1,increment:0.1," style="width:120px;"></input>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div data-options="region:'east',border:false" title="" style="width:200px;height:auto;">
                                            <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;margin:120px 0 0 50px" onclick="submitForm()">Search</a>
                                            <%--<input class="easyui-linkbutton" data-options="iconCls:'icon-search'" type="button" style="width:80px;height:30px;margin:120px 0 0 50px" onclick="submitForm()"/>--%>
                                        </div>
                                        <div id="cc" class="easyui-calendar"></div>  <!-- 这个是用在存放日历那个图标的，必须存在，放哪儿无所谓 -->
                                    </div>
                                </div>
                                <div data-options="region:'east',border:false" style="width:100px;"></div>
                            </div>
                        </form>
                    </div>
                </div>
                <div style="margin:10px 0;"></div>

                <%--下面是结果表格面板--%>
                <div id="tablediv" class="easyui-panel" title="查询结果" style="width:auto;height:auto" data-options="iconCls:'icon-ok',tools:'#tt'">
                    <table id="resulttable" title="" class="easyui-datagrid" style="width:auto;height:480px"
                           url="/es/ajaxPage"
                           pageList="[20]"
                           pageSize="20"
                           idField="id"
                           rownumbers="true"
                           pagination="true"
                           iconCls="icon-table"
                           data-options="onClickRow:showthumbnail"
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
                <div id="tt">
                    <a href="/es/exportallpath"  class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="exportAllPath();" style="width: 100px;height:18px" align="right">导出全部</a>
                    <a   class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="exportSomePath();" style="width:100px;height:18px" align="right">导出选中</a>
                    <a href="/es/exportexcel"  class="easyui-linkbutton" data-options="iconCls:'icon-save'"  style="width:100px;height:18px" align="right">导出excel</a>
                    <a   class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="downloadDicomChecked();" style="width:100px;height:18px" align="right">下载选中</a>
                </div>

            </div>
        </div>
        <div data-options="region:'east'" style="width:330px;padding:10px">
            <table id="thumbnailtable" class="easyui-datagrid" title="缩略图" style="width:auto;height:auto"
                   data-options="singleSelect:true,collapsible:true">
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

</body>
</html>