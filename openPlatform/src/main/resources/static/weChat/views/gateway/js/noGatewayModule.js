define(function(require, exports, module) {
	var $ = require("jquery"), wx = require("wx"), common = require("common"), commonDate = require("commonDate"), layer = require("layer");
	/**
	 * <pre>
	 * 扫描二维码进行设备绑定
	 * </pre>
	 */
	exports.scanCode = function() {
		wx.scanQRCode({
					needResult : 1,
					scanType : [ "qrCode" ],
					success : function(res) {
						var url = res.resultStr
						if (common.isNull(url)) {
							layer.open({
								content : "未扫描到内容",
								time : 3
							});
						} else if (common.isInclude(url,
								'/wx/wxsso/scan/bindGateway')) {
							// 如果扫描的结果包含'/wx/wxsso/scan/bindGateway' 那么视为是去绑定
							// 智能网关
							var param = common.getUrlParam(url);
							exports.bindGateway(param.mac);

						} else if (common.isInclude(url,
								'/wx/wxsso/scan/bindSocket')) {
							// 如果扫描的结果包含'/wx/wxsso/scan/bindSocket' 那么视为是去绑定
							// 智能插座
							var param = common.getUrlParam(url);
							var gatewayId = $("#jsSignature").attr(
									"data-gateway-id");
							exports.bindSocket(param.mac, gatewayId);

						}else{
							layer.open({
								content : "二维码内容不合法,请扫描指定二维码",
								time : 3
							});
						}
					}
				});
	};
	/** 绑定 智能网关 */
	exports.bindGateway = function(mac) {
		$.post("/wx/gateway/scanBindGateway", {
			"mac" : mac
		}, function(result) {
			if (result.responseCode == "_200") {
				layer.open({
					content : "添加成功",
					time : 3,
					end : function() {
						window.location.href = "/wx/gateway/findAllGateway";
					}
				});
			} else {
				layer.open({
					content : result.errorMsg,
					time : 3
				});
			}
		}, "json");
	}
	/** 绑定 智能插座 */
	exports.bindSocket = function(mac, gatewayId) {
		$.post("/wx/gateway/scanBindSocket", {
			"mac" : mac,
			"gatewayId" : gatewayId
		}, function(result) {
			if (result.responseCode == "_200") {
				layer.open({
					content : "添加成功",
					time : 3,
					end : function() {
						if(common.isNull(gatewayId)){
							window.location.href = "/wx/gateway/findAllGateway";							
						}else{
							window.location.href = "/wx/socket/findAllByGatewayId?id="+gatewayId;							
						}
					}
				});
			} else {
				layer.open({
					content : result.errorMsg,
					time : 3
				});
			}
		}, "json");
	}
})