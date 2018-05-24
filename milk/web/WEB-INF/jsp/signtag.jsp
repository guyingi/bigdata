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
            url: "/milk/associativeSearchHospital",
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
            url: "/milk/associativeSearchOrgan",
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
        var obj = new Object();
        obj.tag = tag;
        var paramJsonStr = JSON.stringify(obj);
        $.ajax({
            type: "POST",
            url: "/milk/dosigntag",
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

    function submitForm(){
        var paramJsonStr = JSON.stringify($("#ff").serializeObject());
        $.ajax({
            type: "POST",
            url: "/milk/ajaxSearch",
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
            url: "/milk/getdicomThumbnail",
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
<body style="padding:0;">
<div style="width:auto;height:4%;background: linear-gradient(to right, #0000C6, #FFFFFF);">
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

<div class="easyui-panel" title="" style="width:100%;height:96%;padding:2px;">
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

        <!--下面是dicom查询页面-->
        <div data-options="region:'center',title:'打标签'">
            <div class="easyui-panel" title="" style="width:auto;height:750px;padding:0px;">
                <div class="easyui-layout" data-options="fit:true">
                    <div data-options="region:'center'" style="padding:0px;height:auto">
                        <div title="" style="width:auto;height:auto;margin:auto;vertical-align:middle;padding:0px;">

                            <%--下面查询面板以及打标签的那一块--%>
                            <div title="" style="width:auto;height:auto;padding:0px;">
                                <div class="easyui-layout" style="width:auto;height:185px;">
                                    <div data-options="region:'west',border:false" style="width:1%"></div>
                                    <div data-options="region:'center',border:false" style="height:auto;">
                                        <div class="easyui-panel" data-options="border:false" title="" style="width:auto;height:100%;">
                                            <form id="ff" method="post" action="" >
                                                <div class="easyui-layout" style="width:100%;height:98%;">
                                                    <div data-options="region:'west',border:false" title="" style="width:45%;height:100%;">
                                                        <table cellpadding="2px">
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
                                                                    <select class="easyui-combobox" name="sex" style="width:100px"><option value="M">男</option><option value="F">女</option><option value="U">未知</option><option value="">不限</option></select>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>年龄阶段:</td>
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
                                                        </table>
                                                    </div>
                                                    <div data-options="region:'center',border:false" title="" style="width:55%;height:100%;">
                                                        <table cellpadding="0">
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
                                                                                <input class="easyui-numberspinner" name="slicethickness_min" value="0" data-options="precision:1,increment:0.1," style="width:100px;"></input>
                                                                            </td>
                                                                            <td>
                                                                                <p style="margin:0;">至</p>
                                                                            </td>
                                                                            <td>
                                                                                <input class="easyui-numberspinner" name="slicethickness_max" value="10" data-options="precision:1,increment:0.1," style="width:100px;"></input>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>
                                                                    <a class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px;height:30px;margin:0 0 0 60px" onclick="submitForm()">Search</a>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                    <div id="cc" class="easyui-calendar"></div>  <!-- 这个是用在存放日历那个图标的，必须存在，放哪儿无所谓 -->
                                                </div>

                                                    <div data-options="region:'east',border:false" style="width:10px;"></div>

                                            </form>
                                        </div>
                                    </div>
                                    <div class="easyui-layout" data-options="region:'east',border:false" title="" style="width:30%;height:auto;border-left:1px solid">
                                        <div class="easyui-layout" data-options="region:'north',border:false" style="height:70%;background:#B3DFDA;padding:0">
                                            <div data-options="region:'north',border:false" style="height:70%">
                                                <p style="margin:30px 0 0 30px">为该批数据打个标签</p>
                                            </div>
                                            <div data-options="region:'center',border:false">
                                                <input id="tag" class="easyui-textbox" type="text" name="tag" data-options="required:false" style="width:200px;height:30px;"/>
                                            </div>
                                        </div>
                                        <div data-options="region:'center',border:false">
                                            <a class="easyui-linkbutton" style="width:80px;height:30px;margin:10px 0 0 30px" onclick="dosigntag();">Sign Tag</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div style="margin:10px 0;"></div>

                            <%--下面是结果表格面板--%>
                            <div id="tablediv" class="easyui-panel" title="查询结果" style="width:auto;height:auto">
                                <table id="resulttable" title="" class="easyui-datagrid" style="width:auto;height:480px"
                                       url="/milk/ajaxPage"
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

                        </div>
                    </div>
                    <div data-options="region:'east'" style="width:330px;padding:10px">
                        <table id="thumbnailtable" class="easyui-datagrid" title="" style="width:auto;height:auto"
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
        </div>

    </div>
</div>
</body>
</html>
