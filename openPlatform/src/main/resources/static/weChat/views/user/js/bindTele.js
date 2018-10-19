seajs.use([ "jquery", "bindTeleModule", "common", "layer", "layercss" ],
		function($, bindTeleModule, common, layer) {

			var isnull = false;
			$("#blurphonenumber").blur(function() {
				isnull = bindTeleModule.blur($(this));
			});
			$("#SendVerification").click(function() {
				/**
				 * 如果手机号码输入正确
				 */
				if (isnull) {
					bindTeleModule.send($(this));
				}
			});

			$("#btnVerification").click(function() {
				if (isnull) {
					var btn = $("#verification").val();
					if ($.trim(btn).length <= 0) {
						$("#errorHide").html("请输入正确的短信验证码");
					} else {
						$("#errorHide").html("");
						bindTeleModule.notarizeVerify();
					}
				}
			})
		});
