<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title></title>
<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
<!-- <#include "/weChat/views/include/seajs.ftl"> -->

<link rel="stylesheet" type="text/css"href="/weChat/newjs/swiper/swiper.min.css">
<link rel="stylesheet" type="text/css"href="/weChat/views/socketStatistics/css/statisticsSwiper.css">
<link rel="stylesheet" type="text/css"href="/weChat/css/style.css">


<style>
.echart {
	 height: 600px;
	 box-shadow: 0 3px 10px #cbd6ee;
	 box-sizing: border-box;
	 padding: 16px;
	 background-color: #fff; 
	 border-radius: 6px;
	 margin-top: 20px;
}
</style>
</head>
<body style="background-color: #eff1f4">
	<!-- 公共区域 -->
	<input type="hidden" value="${index}" id="index">
	<input type="hidden" value="${intelligentSocket.id}" id="socketId">
	<input type="hidden" value="" id="typeitem" data-temp='${config.status}'>
	<div class="swiper-container gallery-top">
		<div class="swiper-wrapper">
			<div class="swiper-slide" data-hash="slide1">
				<div class="slide_content">
					<div class="fang_box">
						<div class="un">${intelligentSocket.socketName}</div>
						<div class="qiehhh">实时电表</div>
					</div>
				</div>
			</div>
			<div class="swiper-slide" data-hash="slide2">
				<div class="slide_content">
					<div class="fang_box">
						<div class="un">${intelligentSocket.socketName}</div>
						<div class="qiehhh">电量统计</div>
					</div>
				</div>
			</div>
			<div class="swiper-slide" data-hash="slide3">
				<div class="slide_content">
					<div class="fang_box">
						<div class="un">${intelligentSocket.socketName}</div>
						<div class="qiehhh">人体探测</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="swiper-container gallery-thumbs" id="echart_area">
		<div class="swiper-wrapper">
			<div class="swiper-slide" data-hash="slide1">
			<#if page??>
				<div class="dlql_box" style="margin-top: 10px">
					<div class="left">
						<div class="tit">上次清零时间</div>
						<div class="time" name="clearTime">${page.clearTime!''}</div>
					</div>
					<div class="right">
						<div class="dl_btn" id="claerbtn" data-id="${page.id}">电量清零</div>
					</div>
				</div>
				<div class="ssdb_bg">
					<ul class="ssdb_box">
						<li class="item">
							<div class="ti">检测时间</div>
							<div class="con"  name="recordingTime">${page.recordingTime}</div>
						</li>
						<li class="item">
							<div class="ti">电器状态</div>
							<div class="con blue" name="startState">${page.intelligentSocket.startState?string('已关闭','已打开')}</div>
						</li>
						<li class="item">
							<div class="ti">电量</div>
							<div class="con"name="electricityValue">${page.electricityValue}(度)</div>
						</li>
						<li class="item">
							<div class="ti">功率</div>
							<div class="con" name="powerValue">${page.powerValue}(瓦)</div>
						</li>
						<li class="item">
							<div class="ti">电流</div>
							<div class="con"name="currentValue">${page.currentValue}(安)</div>
						</li>
						<li class="item">
							<div class="ti">电压</div>
							<div class="con"name="voltageValue">${page.voltageValue}(伏)</div>
						</li>
					</ul>
				</div>
				</#if>
				 <div class="tubiao_bg">
					<div class="title_box" name="real_time_electric" style="margin-top: 10px">
						<div class="ite relative" type="STATUS_ELECTRIC">电量</div>
						<div class="ite" type="STATUS_POWER">功率</div>
						<div class="ite" type="STATUS_CURRENT">电流</div>
						<div class="ite" type="STATUS_VOLTAGE">电压</div>
					</div>
					<div id="electricity_echart" name="electricity_echart" class="echart">
					</div>
				</div> 
			</div>
			<div class="swiper-slide" data-hash="slide2">
			 	<div class="dltz_bg">
					<div class="dltz_box" style="margin-top: 10px">
					<div class="ti" type='${config.status}' id="data-static">

					<#if config.status=='NOT_SENDNOTICE'>不发送通知			
				   <#elseif config.status=='DAY_SENDNOTICE'> 日报<span>(每天9:00发送通知)</span>
				   <#elseif config.status=='WEEK_SENDNOTICE'> 周报<span>(每周一9:00发送通知)</span>
					<#else>月报<span>(每月一号9:00发送通知)</span>
					</#if>
						</div>
						<div class="btn" id="noticebtn">电量通知选择</div>
					</div>
				</div> 
	 		<div class="tubiao_bg">
					<div class="title_box" name="electricity_statistics" style="margin-top: 10px">
						<div class="itee relative"  type="STATUS_DAY">日</div>
						<div class="itee" type="STATUS_WEEK">周</div>
						<div class="itee" type="STATUS_MONTH">月</div>
					</div>
					<div id="electricity_statistics_echart" name="electricity_statistics_echart" class="echart">
					</div>
				</div> 
			</div>
			<div class="swiper-slide" data-hash="slide3">
				<div class="tubiao_bg tubiao_bgg">
					<div class="title_box" name="human" style="margin-top: 10px">
						<div class="iteee relative" type="HEAT">热量图展示</div>
						<div class="iteee" type="TIME">时间轴展示</div>
					</div>
					<div name="human_timestamp_echart" id="human_timestamp_echart" class="echart"></div>
			<!-- 		时间轴展示 -->
					<div name="human_time_area" style="display: none">
						<div class="threetitle_box">
							<div class="ti1">日期</div>
							<div class="ti2">时间轴</div>
							<div class="ti3">热量</div>
						</div>
						<div id="human_time" name="timer"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 电量通知选择 -->
	<div class="regbafixed_bg" id="notice" style="display: none">
		<div class="regbafixed_box">
			<div class="title_box" style="margin-top: 10px">
				<div class="title">电量通知选择</div>
				<div class="btncha" id="btncha"></div>
			</div>
		<div class="item_box" id="notice_status" status="">
				<div class="item" name="not_item" type="NOT_SENDNOTICE">不发送通知</div>
				<div class="item" name="day_item" type="DAY_SENDNOTICE">日报</div>
				<div class="item " name="week_item" type="WEEK_SENDNOTICE">周报</div>
				<div class="item" name="month_item" type="MONTH_SENDNOTICE">月报</div>
			</div>
		</div>
	</div> 
	<div class="timezou_bg" id="template_div" style="display: none;">
		<div class="zou1">
			<div class="zouti" name="date"></div>
		</div>
		<div class="zou2">
			<div class="zoutime_box">
				<div class="zoutimeit">
					<div class="ztime ztimee" name="time"></div>
					<div class="zouzt" name="status">有人</div>
				</div>
			</div>
		</div>
		<div class="zou3">
			<div class="zourl" name="value"></div>
		</div>
	</div>	  
<!-- <div id="main" style="width: 600px;height:400px;"></div>  -->
<script src="/weChat/newjs/jquery/jquery-3.3.1.js"></script>
<script src="/weChat/newjs/layer/mobile/layer.js"></script>
<script src="/weChat/newjs/echarts/echarts.min.js"></script>
<script src="/weChat/newjs/swiper/swiper.min.js"></script>
<script src="/weChat/views/socketStatistics/js/newStatis.js"></script> 
<script>
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
			window.location.href = "/wx/socket/socketDetail?gatewayId=${intelligentSocket.intelligentGateway.id}&currentSocketId=${intelligentSocket.id}";
		}
</script>
</body>
</html>