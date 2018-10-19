<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>智能开关</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
<link rel="stylesheet" type="text/css"
	href="/weChat/lib/swiper4/swiper.min.css">
<link rel="stylesheet" type="text/css"
	href="/weChat/views/socket/css/socketSwiper.css">
<#include "/weChat/views/include/seajs.ftl">
</head>
<body style="background-color: #eff1f4">
	<!--房间切换-->
	<!--网关id -->
	<input type="hidden" name="sc" value="${gatewayId}" id="gatewayId">
	<input type="hidden" value="${currentSocketId}" id="lastSocketId">
	<input type="hidden" value="${index}" id="index">

	<div class="regbafixed_bg" id="switch_area" style="display: none;">
		<div class="regbafixed_box">
			<div class="title_box">
				<div class="title">房间切换</div>
				<div class="btncha" name="switch_area_btn"></div>
			</div>
			<div class="item_box">
				<#list intelligentSocket as socketInfo>
					<div class="item" name="socketName" data-id="${socketInfo.id}">${socketInfo.socketName}</div>
				</#list>
			</div>

		</div>
	</div>


	<div class="swiper-container gallery-top" style="transform:translate3d(0,0,0);overflow:hidden;">
		<div class="swiper-wrapper" id="swiper_top_area">
			<#list intelligentSocket as socketInfo>
			<div class="swiper-slide" name="slide" data-id="${socketInfo.id}">
				<div class="slide_content">
					<div class="fang_box">
						<div class="un" name="socketInfo" data-id="${socketInfo.id}">${socketInfo.socketName}</div>
						<div class="qieh" name="switch_btn">切换</div>
					</div>
					<div class="diany_box">
						<div class="dianti">
							<div class="diant">电源开关</div>
							<div class="diani">${electricityTime?string('MM/dd
								hh:mm:ss')}</div>
						</div>
						<div class="dianbtn" id="apple">
							<div
								class="appleswitch ${socketInfo.startState?string('switch-on','switch-off')}"
								name="appleswitch"></div>
						</div>
					</div>
				</div>
			</div>
			</#list>
		</div>
	</div>

	<div class="swiper-container gallery-thumbs" id="show_detail_area">
		<div class="swiper-wrapper">
			<div class="swiper-slide" style="width: 411px;margin-right: 10px;">
				<div class="list_box" name="realTimeMeter_btn">
					<div class="list_icon list_icon1"></div>
					<p>实时电表</p>
				</div>
				<div class="list_box" name="electricity_statistics_btn">
					<div class="list_icon list_icon2"></div>
					<p>电量统计</p>
				</div>
				<div class="list_box" name="human_detection_btn">
					<div class="list_icon list_icon3"></div>
					<p>人体探测</p>
				</div>
			</div>
		</div>
	</div>

</body>
<script type="text/javascript"
	src="/weChat/views/socket/js/socketDetail.js?0007"></script>
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
			window.location.href = "/wx/socket/findAllByGatewayId?id=${gatewayId}";
		}
	})
	</script>
</html>