<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
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
</head>
<style type="text/css">
    .transparent-div{ opacity:0}
</style>
<script type="text/javascript">
    // window.location="/milk/search"
    function submitForm(){
        // $('#ff').form('submit');
        $('hint').html("");
        $.ajax({
            type: "GET",
            url: "/milk/login",
            data: $('#ff').serialize(),
            dataType: 'json',
            traditional:true,
            contentType: 'application/json;charset=utf-8',
            success: function (data) {
                console.log(data)
                if(data!=null){
                    if(data.result){
                        window.location="/milk/navigation?"+$('#ff').serialize();
                    }else{
                        $('hint').html("密码错误");
                    }
                }
            },
            error: function () {
                $('hint').html("登陆失败");
            }
        });
    }

</script>
<body background="/milk/image/首页.jpg" class="easyui-layout">
    <div class="transparent-div" data-options="region:'north',border:false" style="height:180px;padding:10px"></div>
    <div class="transparent-div" data-options="region:'west',border:false" style="width:36%;padding:10px;"></div>
    <div class="transparent-div" data-options="region:'east',border:false" style="width:36%;padding:10px;"></div>
    <div class="transparent-div" data-options="region:'south',border:false" style="width:auto;height:250px;"></div>
    <div class="easyui-layout" data-options="region:'center'" style="vertical-align: center;horiz-align: center; width:200px;height:300px;background:url(image/loginbg.jpg)" >
        <div data-options="region:'north',border:false" style="height:100px;">
            <p style="font-size:28px;font-weight:bold;text-align:center;text-space:10px;color:#1b3c66;text-shadow:0.5px 0.5px #37587a;">雅森大数据查询系统</p>
        </div>
        <div data-options="region:'west',border:false" style="width:18%;"></div>
        <div data-options="region:'east',border:false" style="width:18%;"></div>
        <div data-options="region:'south',border:false" style="height:10px;"></div>
        <div class="easyui-panel" data-options="region:'center',border:false">
            <form id="ff" method="post" action="/milk/login">
                <div style="margin-bottom:20px">
                    <input class="easyui-textbox" name="username" data-options="prompt:'用户名或手机号'" style="width:100%;height:32px">
                </div>
                <div >
                    <input class="easyui-textbox" name="password" data-options="prompt:'密码'" style="width:100%;height:32px">
                </div>
                <div >
                    <p id="hint" style="color:red"></p>
                </div>
                <div style="margin-bottom:20px">
                    <input class="easyui-checkbox" type="checkbox" name="" value="0" style="horiz-align: left">下次自动登陆</input>
                    <a>&nbsp&nbsp&nbsp&nbsp</a>
                    <a href="#" style="horiz-align: right">忘记密码</a>
                </div>

                <div>
                    <div>
                        <a href="" class="easyui-linkbutton" style="width:100%;height:32px;background: #37587a;color:#FFFFFF" onclick="submitForm()">登陆</a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
<%--<img src="/milk/image/首页.jpg"/>--%>
<%--<a type="button" href="http://localhost:8080/navigation">跳转</a>--%>