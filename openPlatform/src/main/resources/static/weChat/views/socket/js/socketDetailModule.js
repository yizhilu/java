define(function(require, exports, module) {
	var $ = require("jquery"), common = require("common"), commonDate = require("commonDate"), layer = require("layer");

	// 发送开启和关闭电源指令给对应智能网关
	exports.sendCommod = function(socketId, gatewayId, startState) {
		$.post("/wx/socket/sendCommand", {
			"socketId" : socketId,
			"gatewayId" : gatewayId,
			"startState" : startState
		}, function(result) {
			if (result.responseCode == "_200") {
			} else {
				layer.open({
					content : result.errorMsg,
					time : 3
				})
			}
		}, "json");
	}
});
