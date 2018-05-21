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
        <div data-options="region:'center',title:'查询患者'">
            <div class="easyui-layout" style="width:auto;height:100%;">
                <div data-options="region:'north'" style="height:9%">
                    <div class="easyui-panel" title="" style="width:auto">
                        <div  style="margin:0px 0px 0px 35%">
                            <form id="ff" method="post">
                                <table cellpadding="5">
                                    <tr>
                                        <td>Name:</td>
                                        <td><input class="easyui-textbox" type="text" name="name" data-options="required:true"></input></td>
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
                    <div class="easyui-panel"  style="width:auto;height:100%;">
                        <div class="easyui-accordion" style="width:70%;height:auto;overflow:auto;">

                            <div title="Help" data-options="iconCls:'icon-help'" style="width:60%;height:300px;overflow:auto;padding:10px;">
                                <div style="height:100px"></div>
                                <%--<div class="easyui-accordion" style="height:auto">--%>
                                    <%--<div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:100px;padding:20px;">--%>
                                        <%--<h3 style="color:#0099FF;">Accordion for jQuery</h3>--%>
                                        <%--<p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>--%>
                                    <%--</div>--%>
                                    <%--<div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">--%>
                                        <%--<p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>--%>
                                    <%--</div>--%>
                                    <%--<div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>
                            <div title="Help" data-options="iconCls:'icon-help'" style="overflow:auto;padding:10px;">
                                <div class="easyui-accordion" style="width:100%;height:auto">
                                    <div title="A" data-options="iconCls:'icon-ok'" style="overflow:auto;height:auto;padding:20px;">
                                        <h3 style="color:#0099FF;">Accordion for jQuery</h3>
                                        <p>Accordion is a part of easyui framework for jQuery. It lets you define your accordion component on web page more easily.</p>
                                    </div>
                                    <div title="B" data-options="iconCls:'icon-help'" style="padding:10px;height:auto;">
                                        <p>The accordion allows you to provide multiple panels and display one or more at a time. Each panel has built-in support for expanding and collapsing. Clicking on a panel header to expand or collapse that panel body. The panel content can be loaded via ajax by specifying a 'href' property. Users can define a panel to be selected. If it is not specified, then the first panel is taken by default.</p>
                                    </div>
                                    <div title="C" data-options="iconCls:'icon-search'" style="padding:10px;">
                                    </div>
                                </div>
                            </div>



                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div data-options="region:'south',border:true" style="height:20px;background:#f1f8ff;">
            <a href="#" onclick="add()">点我</a>
        </div>
    </div>
</div>
</body>
</html>
