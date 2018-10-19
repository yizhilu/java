<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>设备列表</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<#include "/weChat/views/include/seajs.ftl">
<style>
.minlist_box4 .item .title .focus-input input:focus {
    color: red;
}
.ui-state-default{
border:0px !important;
}
</style>
</head>
<body style="background-color: #eff1f4">
	<div class="top_titlebox" id="jsSignature"
		data-appid="${jsSignature.appid}"
		data-noncestr="${jsSignature.noncestr}"
		data-timestamp="${jsSignature.timestamp}"
		data-signature="${jsSignature.signature}"
		data-gateway-id="${gatewayId!''}">
		<button class="left_btn" id="scan_button"></button>
		<button class="right_btn"
			onclick="window.location.href='/wx/socket/findAllByGatewayId?id=${gatewayBindSocket[0].intelligentGateway.id}'">完成</button>
	</div>
	<div style="height: 61px"></div>
	<div class="bgbg_box">
		<img src="/weChat/images/bgbg.jpg" alt="">
		<div class="minsname_box">
			<div class="minsname">
				<div class="ti">${gatewayBindSocket[0].intelligentGateway.gatewayName}</div>
				<div class="date icon1">${.now?string("yyyy/MM/dd")}</div>
			</div>
		</div>
	</div>
	<div
		class="minlist_box minlist_box2 minlist_box3 minlist_box4 minlist_box6"
		id="socket_area">
		<#list gatewayBindSocket as item>
		<div  itemId="${item.id}" class="ui-state-default">
			<ul class="item item2">
				<i class="cha icon1" name="unbind_btn" data-id="${item.id}"></i>
				<li class="title">
					<div class="ti ti2 focus-input">
						<input name="set_name" data-name-id="${item.id}"
							data-gateway-id="${item.intelligentGateway.id}" type="text"
							value="${item.socketName}">
					</div>
				</li>
				<li class="unmber">
					<div class="un">
						<i class="lu"></i>
						<div class="id">
							ID:<span>${item.thingId}</span>
						</div>
					</div>
					<div class="btn_box">
						<i class="gongnbtn icon1 gongnbtn1  portlet-header"></i> <i
							class="gongnbtn icon1 gongnbtn2" name="top_btn"
							data-id="${item.id}"
							data-gateway-id="${item.intelligentGateway.id}"></i>
					</div>
				</li>
			</ul>
		</div>
		</#list>
	</div>
	<script type="text/javascript"
		src="/weChat/views/socket/js/editSocketList.js"></script>
	<script src="/weChat/views/gateway/js/noGateway.js"></script>
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
			window.location.href = "/wx/socket/findAllByGatewayId?id=${gatewayBindSocket[0].intelligentGateway.id}";
		}
	})
	</script>
</body>
</html>