<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>扫码绑定设备</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<#include "/weChat/views/include/seajs.ftl"/>
</head>
<body style="background-color: #eff1f4">
	<div class="top_titlebox" id="jsSignature"
		data-appid="${jsSignature.appid}"
		data-noncestr="${jsSignature.noncestr}"
		data-timestamp="${jsSignature.timestamp}"
		data-signature="${jsSignature.signature}" data-gateway-id="${gatewayId!''}">
		<button class="left_btn" id="scan_button"></button>
	</div>
	<div class="wumin_bg">
		<div class="wumin_box">
			<div class="wumin_boxx">
				<div class="wumin_boxxx" id="scan_div">
					<i></i> <span>扫一扫</span>
				</div>
			</div>
		</div>
	</div>
</body>
<script src="/weChat/views/gateway/js/noGateway.js"></script>
</html>






























