﻿<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>设置</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<#include "/weChat/views/include/seajs.ftl"/>
<link rel="stylesheet" type="text/css"
	href="/weChat/views/setting/css/setting.css">
<link rel="stylesheet" type="text/css"
	href="/weChat/views/setting/css/iosSelect.css">
<style type="text/css">
body {
	padding-top: 14px;
	margin-top: -14px;
}
</style>
</head>
<body style="background-color: #eff1f4">
	<div class="setup_bg">
		<div class="item_bg">
			<div class="title">电量告警</div>
			<div class="item_box">
				<div class="item">
					<div class="ti">电量警告</div>
					<div
						class="switch  ${electricSetting.whetherOpen?string('switch-on','switch-off')}"
						id="switch">${electricSetting.whetherOpen?string('开', '关')}</div>
				</div>
				<div class="item">
					<div class="ti timin">电量</div>
					<div class="con" id="conpage">
						<div class="du">
							<span id="showGeneral">${electricSetting.electricityThreshold?string('###')}</span>度
						</div>
						<p hidden id="pagestatus">${electricSetting.whetherOpen?string('true',
							'false')}</p>
					</div>
				</div>
			</div>
		</div>
		<div class="item_bg">
			<div class="title">人体探测</div>
			<div class="item_box">
				<div class="item">
					<div class="ti">房间无人时通知我</div>
					<div
						class="appleswitch ${humanSetting.whetherOpen?string('switch-on','switch-off')}"
						id="roomSwitch">${humanSetting.whetherOpen?string('开', '关')}</div>
				</div>
				<!-- ONLY_ONCE  MANY_A_TIME -->
				<div class="item">
					<div class="two ${humanSetting.strategy?contains('ONLY_ONCE')?string('relative','')}" id="oneInform">通知一次</div>
					<div class="two ${humanSetting.strategy?contains('MANY_A_TIME')?string('relative','')}" id="multipleInform">持续通知</div>
				</div>
				<div class="item">
					<div class="ti">夜间时段</div>
					<div
						class="appleswitch ${humanSetting.openNightMode?string('switch-on','switch-off')}"
						id="nightSwitch">${humanSetting.openNightMode?string('开','关')}</div>
				</div>
				<div class="item2" id="switchss">
					<div class="it">
						<div class="tii">从</div>
						<div class="time" id="humanStartTimeQuantum">${humanSetting.nightModeStartTime?string('HH:mm')}</div>
					</div>
					<div class="it">
						<div class="tii">到</div>
						<div class="time" id="humanEndTimeQuantum">${humanSetting.nightModeEndTime?string('HH:mm')}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="item_bg">
			<div class="title">消息通知</div>
			<div class="item_box">
				<div class="item">
					<div class="ti">勿扰模式</div>
					<div
						class="switches ${notDisturbSetting.whetherOpen?string('switch-on','switch-off')}"
						id="switches">${notDisturbSetting.whetherOpen?string('开', '关')}</div>
					<p hidden id="status">${notDisturbSetting.whetherOpen?string('true','false')}</p>
				</div>
				<div class="item2" id="general">
					<div class="it">
						<div class="tii">从</div>
						<div class="time" id="satrtBotheredTime">${notDisturbSetting.startTime?string('HH:mm')}</div>
					</div>
					<div class="it">
						<div class="tii">到</div>
						<div class="time" id="endBotheredTime">${notDisturbSetting.endTime?string('HH:mm')}</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="regbafixed_bg" id="switchTimes" style="display: none;">
		<div class="regbafixedtm_box">
			<div class="number_box">
				<div class="un1">
					<div class="swiper-container swiper-container3" id="start-hours">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">00</div>
						</div>
					</div>
				</div>
				<div class="mao">:</div>
				<div class="un1">
					<div class="swiper-container swiper-container3" id="start-minute">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">00</div>
						</div>
					</div>
				</div>
				<div class="dao">到</div>
				<div class="un1">
					<div class="swiper-container swiper-container3" id="end-hours">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">00</div>
						</div>
					</div>
				</div>
				<div class="mao">:</div>
				<div class="un1">
					<div class="swiper-container swiper-container3" id="end-minute">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">00</div>
						</div>
					</div>
				</div>
			</div>
			<button id="btn_time">确认</button>
		</div>
	</div>
	<!-- 第二个时间插件层  用于显示电量-->
	<div class="regbafixed_bg" id="powerSettings" style="display: none;">
		<div class="regbafixedtm_box">
			<div class="number_box">
				<div class="un1">
					<div class="swiper-container swiper-container5" id="one">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">0</div>
						</div>
					</div>
				</div>
				<div class="mao"></div>
				<div class="un1">
					<div class="swiper-container swiper-container5" id="tow">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">0</div>
						</div>
					</div>
				</div>
				<div class="mao"></div>
				<div class="un1">
					<div class="swiper-container swiper-container5" id="three">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">0</div>
						</div>
					</div>
				</div>
				<div class="mao"></div>
				<div class="un1">
					<div class="swiper-container swiper-container5" id="hour">
						<div class="cloth"></div>
						<div class="swiper-wrapper">
							<div class="swiper-slide">0</div>
						</div>
					</div>
				</div>
			</div>
			<button id="btn_powerSettings">确认</button>
		</div>
	</div>
	<!--  -->
	<div class="form-item item-line">
		<div class="pc-box">
			<input type="hidden" name="su_id" id="suId" value=""> <input
				type="hidden" name="wei_id" id="weiId" value=""> <input
				type="hidden" name="he_id" id="heId" value=""> <input
				type="hidden" name="xei_id" id="xeiId" value="">
		</div>
	</div>

	<script type="text/javascript"
		src="/weChat/views/setting/js/settingInfo.js"></script>
	<script type="text/javascript"
		src="/weChat/views/setting/js/time.js"></script>
	<script type="text/javascript"
		src="/weChat/views/setting/js/powerSettings.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
			var hm = document.createElement("script");
			hm.src = "//hm.baidu.com/hm.js?b25bf95dd99f58452db28b1e99a1a46f";
			var s = document.getElementsByTagName("script")[0];
			s.parentNode.insertBefore(hm, s);
		})();
	</script>
</body>
</html>

