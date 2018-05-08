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
            $("#tt").datagrid('hideColumn', "itemid");
            // $.ajax({
            //     type: "POST",
            //     url: "/milk/ajaxTest",
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
</script>
</head>
<body>
<div style="text-align: left;background-color: #E0ECFF;padding-left: 10px;padding-top: 5px;">
	<div id="searchField" style="width:250px">
        <input class="easyui-combobox" data-options="
		valueField: 'label',
		textField: 'value',
		data: [{
			label: 'java',
			value: 'Java'
		},{
			label: 'perl',
			value: 'Perl'
		},{
			label: 'ruby',
			value: 'Ruby'
		}]" />
    </div>

    <div><input id="hospital" type="button" value="点我" onclick="x()"/></div>

    <table id="tt" title="Checkbox Select" class="easyui-datagrid" style="width:550px;height:250px"
           url="/milk/ajaxTest"
           pageList="[10]"
           idField="itemid" pagination="true"
           iconCls="icon-save">
        <thead>
        <tr>
            <th field="ck" checkbox="true"></th>
            <th field="itemid" width="80">Item ID</th>
            <th field="productid" width="80">Product ID</th>
            <th field="listprice" width="80" align="right">List Price</th>
            <th field="unitcost" width="80" align="right">Unit Cost</th>
            <th field="attr1" width="100">Attribute</th>
            <th field="status" width="60" align="center">Status</th>
        </tr>
        </thead>
    </table>
</div>
</body>
</html>