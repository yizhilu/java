
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>设备列表</title>
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
			onclick="window.location.href='/wx/socket/findAllByGatewayId?id=${gatewayBindSocket[0].intelligentGateway.id}&type=edit'">编辑</button>
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
	<div class="minlist_box minlist_box2 minlist_box5" id="scoketinfo">
		<#list gatewayBindSocket as item>
		<ul class="item item2" name="scoket_info" data-name-id="${item.thingId}">

			<li class="title"
				onclick="window.location.href='/wx/socket/socketDetail?gatewayId=${gatewayBindSocket[0].intelligentGateway.id}&currentSocketId=${item.id}'">
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
			<li class="zhuangt_box">
				<div class="zhuangt" type='${typeMode}'>
				<#if  typeMode?index_of('daymodel')!=-1>				
					<i class="zt_i ${statuslist[item_index]?string('zt_i3','zt_i1')} icon2"></i> <span class="word">${statuslist[item_index]?string("有人","无人")}</span>					
					<#else>
					<i class="zt_i zt_i5 icon2"></i> <span class="word">夜间</span>	
					</#if>
				</div>					
				<div class="zhuangt">
					<i class="zt_i ${item.startState?string('zt_i4 ','zt_i2')} icon2"></i> <span class="word">${item.startState?string
						("已开","关闭")}</span>
				</div>		
			</li>
			
		</ul>
		</#list>
			
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




























