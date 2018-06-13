<%--
  Created by IntelliJ IDEA.
  User: WeiGuangWu
  Date: 2018/5/15
  Time: 15:16
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
                        <li class="active"><a href="#">首页</a></li>
                        <li><a href="dicomsearch">查询dicom</a></li>
                        <li><a href="patientsearch">查询患者</a></li>
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
                        <!-- <li><a href="#" data-url="html/demo01.html">锁定行和列</a></li> -->
                    </ul>
                </div>
            </div>
        </div>

        <div region="center" id="content">
            <div class="easyui-tabs" fit="true" id="tt">
                <div title="首页" iconCls="icon-ok">
                    <div class="easyui-accordion" data-options="fit:true">
                        <div title="关于雅森">
                            <div class="flow-panel">
                                <p>长期以来，由于医疗资源、各地区研究水平的差异，加之政策等因素带来的信息不对称，使得相同的疾病在不同的国家、地区获得的诊断和治疗效果大不相同。
雅森科技基于大数据分析技术、图像识别技术，加之计算机的深度学习能力，将海量数据转化为精准高效的诊断能力，这些能力可以通过人工智能的方式交付给社会。这些数据工具将帮助各地的人们获得平等的诊断机会，无论你是在旧金山还是在赞比亚。
                                </p>
                                <div class="aboutus-img">
                                    <img src="image/about_01.jpg" alt="关于雅森01">
                                    <img src="image/about_02.jpg" alt="关于雅森02">
                                    <img src="image/about_03.jpg" alt="关于雅森03">
                                </div>
                                <p>
                                    雅森科技成立于2006年，是国内最早专注于医学影像人工智能分析、核医学定量及CAD分析的高科技企业；长期致力于影像预处理、分析建模、大数据分析、深度学习辅助诊断等领域。产品覆盖脑、神经、甲状腺、血液、呼吸、病理等多模态分析技术。
同时也面向医疗机构提供业界领先的雅森天玑™智慧医疗平台，为医疗机构实现“AI赋能”。
                                </p>
                            </div>
                        </div>
                        <div title="系统公告">
                            <ul class="notice-list">
                                <li>
                                    <span>雅森科技助力厦门健康医疗大数据中心成果展示，AI+医疗引关注~</span>
                                    <span class="date">2018-05-31</span>
                                </li>
                                <li>
                                    <span>多病种深钻研，雅森天玑平台打造医疗数据应用链路</span>
                                    <span class="date">2018-05-31</span>
                                </li>
                                <li>
                                    <span>雅森科技携天玑™智能医疗平台亮相第十一届中国医院院长年会</span>
                                    <span class="date">2017-11-14</span>
                                </li>
                                <li>
                                    <span>对话雅森科技陈晖：医学影像+人工智能的深度商业化思考</span>
                                    <span class="date">2017-11-09</span>
                                </li>
                                <li>
                                    <span>对话宁波市第二医院副院长郑建军：医院引进医疗AI产品考虑的四大因素</span>
                                    <span class="date">2017-11-08</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

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
