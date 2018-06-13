<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="css/default/easyui.css">
<link rel="stylesheet" type="text/css" href="css/icon.css">
<link rel="stylesheet" type="text/css" href="css/demo.css">
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
<script>
        $(document).ready(function() {
            // $('#grid').datagrid('load',"/es/image/datagrid_data.json");
            // $("#tt").datagrid('hideColumn', "itemid");
            // $.ajax({
            //     type: "POST",
            //     url: "/es/ajaxTest",
            //     data: null,
            //     dataType: 'json',
            //     traditional:true,
            //     contentType: 'application/json;charset=utf-8',
            //     success: function (data) {
            //         console.log(JSON.stringify(data));
            //         console.log(data);
            //         if(data!=null){
            //             $("#tt").datagrid("loadData",data);
            //         }
            //     },
            //     error: function () {
            //     }
            // });
        });

        function x() {
            var rows = $('#tt').datagrid('getSelections');
            for(var i=0;i<rows.length;i++){
                alert(rows[i].itemid);
            }

        }
        function A(value,row,index){ return  '<img height="100" width="100" src=\''+value+'\'/>'}

</script>
</head>
<body>
<table id="grid" class="easyui-datagrid" title="Basic DataGrid" style="width:auto;height:auto"
       data-options="singleSelect:true,collapsible:true,method:'get',url:'/milk/image/datagrid_data.json'">
    <thead>
    <tr>
        <th data-options="field:'image1',width:100,heightalign:'center',formatter:A">image1</th>
        <th data-options="field:'image2',width:100,heightalign:'center',formatter:A">image2</th>
        <th data-options="field:'image3',width:100,heightalign:'center',formatter:A">image3</th>
    </tr>
    </thead>
</table>
<img height="100" width="100" src='/milk/temp/0001.jpg'>
</body>
</html>