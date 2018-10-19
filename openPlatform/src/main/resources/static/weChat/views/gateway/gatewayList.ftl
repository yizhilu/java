<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>民宿列表</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<#include "/weChat/views/include/seajs.ftl"/>
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
			onclick="window.location.href='/wx/gateway/findAllGateway?type=edit'">编辑</button>
	</div>
	<div style="height: 61px"></div>
	<div class="minlist_box">
		<#if userbindGateway??> <#list userbindGateway as
			item>
		<ul class="item">
			<li class="title">
				<div class="ti">${item.gatewayName}</div>
			</li>
			<li class="unmber"
				onclick="window.location.href='/wx/socket/findAllByGatewayId?id=${item.id}'">
				<div class="un">
					<i class="lu"></i>
					<div class="id">
						ID:<span>${item.id}</span>
					</div>
				</div>
				<div class="btn_box">
					<i class="jian"></i>
				</div>
			</li>
		</ul>
		</#list> </#if>
	</div>
	<div class="weibd">未绑定设备</div>
	<div class="minlist_box minlist_box2">
		<#if unBindSocket??> <#list unBindSocket as item>
		<ul class="item item2">
			<li class="title">
				<div class="ti">${item.socketName}</div>
			</li>
			<li class="unmber">
				<div class="un">
					<i class="lu"></i>
					<div class="id">
						ID:<span>${item.thingId}</span>
					</div>
				</div>
				<div class="btn_box"></div>
			</li>
		</ul>
		</#list> </#if>
	</div>
</body>
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
			window.location.href = "/wx/gateway/findAllGateway";
		}
	})
</script>
</html>






























