seajs.use([ "jquery","wx", "noGatewayModule", "common", "layer", "layercss" ],
		function($,wx, noGatewayModule, common, layer) {
			var appid = $("#jsSignature").attr("data-appid");
			var timestamp = $("#jsSignature").attr("data-timestamp");
			var noncestr = $("#jsSignature").attr("data-noncestr");
			var signature = $("#jsSignature").attr("data-signature");
			wx.config({
				debug : false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
				appId : appid, // 必填，公众号的唯一标识
				timestamp : timestamp, // 必填，生成签名的时间戳
				nonceStr : noncestr, // 必填，生成签名的随机串
				signature : signature,// 必填，签名，见附录1
				jsApiList : [ "scanQRCode" ]
			// 必填，需要使用的JS接口列表，所有JS接口列表
			});
			wx.error(function(res) {

			});
			wx.ready(function() {
				// 1 判断当前版本是否支持指定 JS 接口，支持批量判断
				wx.checkJsApi({
					jsApiList : [ 'scanQRCode' ],
					success : function(res) {
						if(res.errMsg!="checkJsApi:ok"){
							layer.open({
								content : "checkJsApi failure",
								time : 3
							});
						}
					}
				});
				/**
				 * 点击右上角扫一扫触发
				 */
				$("#scan_button").on("click", function() {
					noGatewayModule.scanCode();
				})
				/**
				 * 点击中间区域扫一扫触发
				 */
				$("#scan_div").on("click", function() {
					noGatewayModule.scanCode();
				})
			});
		})