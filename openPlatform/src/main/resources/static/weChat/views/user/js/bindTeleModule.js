define(function(require, exports, module) {
	var $ = require("jquery"), common = require("common"), commonDate = require("commonDate"), layer = require("layer");
	/**
	 * 光标离开输入框
	 */
	exports.blur = function(obj) {

		if ($.trim(obj.val()).length <= 0) {
			$("#errorHide").html("请输入手机号码");
		} else {
			if (common.isPhone(obj.val())) {
				console.log(obj.val());
				$("#errorHide").html("");
				return true;
			} else {
				$("#errorHide").html("请输入正确的手机号码");
			}
		}
		return false;
	}

	/**
	 * 发送验证码
	 */
	exports.send = function(obj) {
		var phone = $("#blurphonenumber").val();
		console.log(phone);
		$.ajax({
			type : "get",
			url : "/wx/user/sendValidCodeValue?phoneNumber=" + phone,
			success : function(data) {

				$("#SendVerification").hide();
				$("#disableVerification").show();
				var countdown = 120;
				countDown(this, countdown);
			},
			error : function(err) {
				console.log("err:" + err);
			}
		});
	}

	function countDown(obj, second) {

		// 如果秒数还是大于0，则表示倒计时还没结束
		if (second >= 0) {
			console.log(second);
			// 按钮里的内容呈现倒计时状态
			$("#disableVerification").html(' (  ' + second + '   )');
			// 时间减一
			second--;
			// 一秒后重复执行
			setTimeout(function() {
				countDown(obj, second);
			}, 1000);
			// 否则，按钮重置为初始状态
		} else {
			// 按钮置未可点击状态
			$("#SendVerification").show();
			// 按钮里的内容恢复初始状态
			$("#disableVerification").html("").hide();
		}
	}

	/**
	 * 发送确认验证
	 */
	exports.notarizeVerify = function() {
		$("#btnVerification").hide();
		$("#disablebtnVerification").show();
		var phone = $("#blurphonenumber").val();
		var validCode = $("#verification").val();
		var target = $("#target").val();
		var mac = $("#mac").val();
		$.ajax({
			type : "post",
			url : "/wx/user/bindTelephone",
			data : {
				"phone" : phone,
				"validCode" : validCode,
				"target" : target,
				"mac" : mac
			},
			success : function(result) {
				if (result.responseCode == "_200") {
					$("#errorHide").html("");

					layer.open({
						content : "注册成功! ",
						time : 3
					})

					setTimeout(function() {
						window.location.href = result.data;
					}, 2900)

				} else {
					$("#errorHide").html(result.errorMsg);
					$("#btnVerification").show();
					$("#disablebtnVerification").hide();
				}
			},
			error : function(err) {

				/*
				 * layer.open({ content : "网络未连接", time : 3 })
				 */
			}
		});
	}
});
