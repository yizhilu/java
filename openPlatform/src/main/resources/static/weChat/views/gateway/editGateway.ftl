<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>民宿列表</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<#include "/weChat/views/include/seajs.ftl"/>
<style>
.focus-input input:focus {
    color: red !important;
}
.minlist_box4 .item .title .focus-input input:focus {
    color: red !important;
}
.ui-state-default{
border:0px !important;
}
</style>
</head>
<body style="background-color: #eff1f4">
	<div class="top_titlebox">
		<button class="left_btn"></button>
		<button class="right_btn" onclick="window.location.href='/wx/gateway/findAllGateway'">完成</button>
	</div>
	<div style="height: 61px"></div>
	<div class="minlist_box minlist_box3" id="dragslot">
	<#if userbindGateway??>
	
	<#list userbindGateway as item>
		<div name="gateway_item"  itemId="${item.id}" class="ui-state-default">
		    <ul class="item">
		        <i class="cha icon1 unbind" name="unbind_btn" data-id="${item.id}"></i>
		        <li class="title">
		            <div class="ti ti2 focus-input"><input type="text"  name="set_gateway" data-gateway-id="${item.id}"  value="${item.gatewayName}"></div>
		        </li>
		        <li class="unmber">
		            <div class="un">
		                <i class="lu"></i>
		                <div class="id">ID:<span>${item.id}</span></div>
		            </div>
		            <div class="btn_box">
		                <i class="gongnbtn icon1 gongnbtn1 my-handle  portlet-header"></i>
		                <i class="gongnbtn icon1 gongnbtn2" name="topping_btn" data-id="${item.id}"></i>
		            </div>
		        </li>
		    </ul>
	    </div>
    </#list>
	</#if>
</div>
	<div class="weibd">未绑定设备</div>
	<div class="minlist_box minlist_box2 minlist_box3 minlist_box4" id="unbindScoket">
	<#if unBindSocket??>
	<#list unBindSocket as item>
	<div class="slot-list"  itemId="${item.id}" data-serial-number="${item.thingId}" name="draggable_item">
    <ul class="item item2">
        <i class="cha icon1" name="btn" data-id="${item.id}"></i>
        <li class="title">
            <div class="ti ti2 focus-input"><input type="text" name="set_unBindSocket" data-id="${item.id}" value="${item.socketName}"></div>
        </li>
        <li class="unmber">
            <div class="un">
                <i class="lu"></i>
                <div class="id">ID:<span>${item.thingId}</span></div>
            </div>
            <div class="btn_box">
                <i class="gongnbtn icon1 gongnbtn3 handle" name="drag_socket_btn" ></i>
            </div>
        </li>
    </ul>
    </div>
    </#list>
	</#if>
</div>
	<script type="text/javascript"
	src="/weChat/views/gateway/js/editGateway.js"></script>
	<script>
	seajs.use([ "jquery" ], function($) {
		$(function() {
			if (window.history && window.history.pushState) {
				history.pushState(null, null, document.URL);
				window.addEventListener('popstate', forbidBack);
			}
		})
		/**
		 * 禁止回退按钮
		 */
		function forbidBack() {
			window.removeEventListener('popstate', forbidBack);
			window.location.href = "/wx/gateway/findAllGateway";
		}
	})
	</script>
	<!-- <script src="//cdn.bootcss.com/eruda/1.3.0/eruda.min.js"></script>
    <script>eruda.init();</script> -->
</body>
</html>