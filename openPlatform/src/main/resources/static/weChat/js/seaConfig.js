
seajs.config({
	alias : {
		// css
		'style' : '/weChat/css/style.css',
		'layercss' : '/weChat/lib/mobile/need/layer.css',
		'jquery-uicss' : '/weChat/lib/jquery_ui/jquery-ui.min.css',
		'swiper3css' : '/weChat/lib/swiper3/swiper.min.css',
		'swiper4css' : '/weChat/lib/swiper4/swiper.min.css',
		'honeySwitchcss': '/weChat/views/scoketInfo/css/honeySwitch.css',
		// js包
		'layer' : '/weChat/lib/mobile/layer.js',
		'jquery' : '/weChat/lib/jquery/jquery-3.3.1-seajs.min.js',
		'jquery-ui':'/weChat/lib/jquery_ui/jquery-ui.sea.min.js',
		'jquery-ui-touch':'/weChat/lib/jquery_ui/jquery.ui.touch-punch.min.js',
		'wx':'http://res.wx.qq.com/open/js/jweixin-1.0.0.js',
		'jqueryLazyLoad' : '/weChat/js/lib/jquery.lazyload.js',
		'swiper3' : '/weChat/lib/swiper3/swiper-3.3.1.min.sea.js',
		'swiper4' : '/weChat/lib/swiper4/swiper.min.sea.js',
		'common' : '/weChat/js/common.js',
		'echarts' : '/weChat/js/echarts.min.js',
		'commonDate' : '/weChat/js/commonDate.js',
		'cookies' : '/weChat/js/cookies.js',
		'Sortable':'/weChat/lib/Sortable.js',
		'iosSelect':'/weChat/lib/iosSelect.js',
		'data':'/weChat/lib/data.js',
		'honeySwitch':'/weChat/lib/jquery.switch.js',
		// 路径配置文件
		'config' : '/weChat/js/config.js',
		//绑定定电话号码
		'bindTeleModule':'/weChat/views/user/js/bindTeleModule.js',
		//更换电话号码
		'updateTeleModule':'/weChat/views/user/js/updateTeleModule.js',
		//智能网关列表
		'gateWayListModule':'/weChat/views/gateway/js/gatewayList.js',
		//编辑智能网关
		'editGatewayModule':'/weChat/views/gateway/js/editGatewayModule.js',
		//没有设备扫码
		'noGatewayModule':'/weChat/views/gateway/js/noGatewayModule.js',
		//智能插座列表
		'socketListModule':'/weChat/views/socket/js/socketListModule.js',
		//编辑智能插座
		'editSocketListModule':'/weChat/views/socket/js/editSocketListModule.js',
		//智能插座详情
		'socketDetailModule':'/weChat/views/socket/js/socketDetailModule.js',
		//智能插座统计信息
		'statisticsModule':'/weChat/views/socketStatistics/js/statisticsModule.js',
		
		'settingInfoModule':'/weChat/views/setting/js/settingInfoModule.js'
	},  
	preload : [ "jquery", "style", "layercss" ,'swiper3css','swiper4css','swiper3','swiper4'],
	map : [ [ /^(.*\.(?:css|js))(.*)$/i, '$1?v=0005' ] ], // map,批量更新时间戳
	charset : 'utf-8',
	timeout : 20000,
	debug : false
});




