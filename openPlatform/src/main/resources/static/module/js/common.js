/**
 * 通用js方法文件 2013-06-24 LiYong
 */
define(function(require, exports, module) {
	var $ = require('jquery');

	/*
	 * 禁止<a href>标签元素的跳转
	 */
	exports.aclick = function(event) {
		var e = window.event || event;
		if (e.preventDefault)
			e.preventDefault();
		else
			window.event.returnValue = false;
		return false;
	}

	/** 判读是否传入的字符串为空 */
	exports.isNull = function(str) {
		if (str == '' || str == undefined || !str || $.trim(str).length == 0) {
			return true;
		}
		return false;
	}

	/* 判断是否是汉字 */
	var isChinese = function(str) {
		if (/.*[\u4e00-\u9fa5]+.*$/.test(str)) {
			return true;
		}
		return false;
	}

	// 取得赠送评论输入框的字符长度
	exports.strlen = function(str, flag) {
		var strlength = 0;
		for (i = 0; i < str.length; i++) {
			if (isChinese(str.charAt(i)) == true)
				strlength = strlength + 2;
			else
				strlength = strlength + 1;
		}
		// flag判断是按字计数还是按字符计数 flag是true 字计数 反之则按字符
		if (flag) {
			return Math.round(strlength / 2);
		} else {
			return strlength;
		}

	}

	/**
	 * 判断是否还有多少个字符的输入的方法 divid（需要判断的文本域id） redivid（结果输出的div层）
	 */
	exports.spnums = function(divid, redivid, inputnum) {
		var str = divid.val();
		var len = this.strlen(str);
		if (len > inputnum) {
			redivid.html('已超出<font color="red">' + (len - inputnum)
					+ '</font>字符');

		} else {
			redivid.html('还可以输入' + (inputnum - len) + '字符&nbsp;');
		}
	}

	/**
	 * 判断输入是否为大于零的正整数 return boolean
	 */
	exports.isNum = function(num) {
		var reg = /^[0-9]*[1-9][0-9]*$/;
		return reg.test(num);
	}

	/**
	 * 判断输入是否大小写英文字符加数字，且符号长度限制 str 效验的字符内容 min 最小字符数 max 最大字符数 return boolean
	 */
	exports.checkStr = function(str, min, max) {
		var type = "^[a-zA-Z0-9]{" + min + "," + max + "}$";
		var reg = new RegExp(type);
		if (str.match(reg) == null)
			return false;
		else
			return true;
	}

	/** 判断邮件地址是否正确 */
	exports.isEmail = function(mail) {
		var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
		return reg.test(mail);
	}
	/** 判断手机号码是否正确 */
	exports.isPhone = function(phone) {
		var reg = /^1[3-9][0-9]{9}$/;
		return reg.test(phone);
	}
	/** 判断固话是否正确 */
	exports.isTelephone = function(telephone) {
		var reg = /^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/;
		return reg.test(telephone);
	}

	/** 判断银行卡号是否正确 */
	exports.isBankCode = function(bankCode) {
		var reg = /^\d{19}$/;
		return reg.test(bankCode);
	}

	/** 判断证件号码是否正确 */
	exports.isCertificatesCode = function(certificatesCode) {
		var reg = /^\d{13}$/;
		return reg.test(certificatesCode);
	}
	/**
	 * 截取指定长度的字符串，然后加上。。。一起显示，。。。占一位 divid（需要判断的div层） redivid（结果输出的div层）
	 */
	exports.substr = function(divid, redivid, slen) {
		var str = divid.html();
		if (this.strlen(str) > slen) {
			var strlength = 0;
			for (i = 0; i < str.length; i++) {
				if (isChinese(str.charAt(i)) == true) {
					strlength = strlength + 2;
				} else {
					strlength = strlength + 1;
				}
				if (Math.round(strlength / 2) == slen) {
					redivid.html(str.substring(0, i - 1) + "...");
					return;
				}
				;
			}
		} else {
			redivid.html(str);
		}
	}
	/**
	 * 截取指定长度的字符串，多余的用...显示 str 需截取的字符串 slen 截取长度
	 */
	exports.substr1 = function(str, slen) {
		if (str.length > slen) {
			str = str.substring(0, slen) + "...";
		}
		return str;
	}
	/**
	 * 在指定地方显示用红色字体显示信息，指定秒数后恢复原先信息 divobj 在该层区域显示 msg 显示的内容 setcolor 显示字体的颜色
	 * times 显示多少秒后恢复原先信息
	 */
	exports.showETip = function(divobj, msg, setcolor, times) {
		var tmp_text = divobj.html();
		divobj.html('<font color=' + setcolor + '>' + msg + '</font>');
		setTimeout(function() {
			divobj.html(tmp_text)
		}, 2000);
	}
	/**
	 * 格式化日期 yyyy-MM-dd HH:mm:ss
	 */
	exports.formatDate = function(nowDate) {
		var nowStr = null;
		if (nowDate != undefined && nowDate != null) {
			nowStr = nowDate.format("yyyy-MM-dd HH:mm:ss");
		}
		return nowStr;
	}

	/**
	 * 根据前缀获取实际图片文件 图片文件 需要加的前缀
	 */
	exports.getVkeImg = function(imgfile, prefix, suffix) {
		if (imgfile == null || imgfile.length == 0 || prefix == null
				|| prefix.length == 0)
			return "";
		var start = imgfile.lastIndexOf('/') + 1;
		imgfile = imgfile.substr(0, start) + prefix + imgfile.substr(start);
		// 说明：2014年5月7日商定所有分离（大中小）图片后缀名为jpg
		var startPoint = imgfile.lastIndexOf('.') + 1;
		if (suffix == null)
			suffix = "jpg";
		imgfile = imgfile.substr(0, startPoint) + suffix;
		return imgfile;
	}

	/**
	 * 获取请求头参数
	 */
	exports.getUrlParam = function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null)
			return unescape(r[2]);
		return null;
	}

	/**
	 * 格式化日期
	 */
	Date.prototype.format = function(format) {
		var o = {
			"M+" : this.getMonth() + 1,
			"d+" : this.getDate(),
			"h+" : this.getHours(),
			"m+" : this.getMinutes(),
			"s+" : this.getSeconds(),
			"q+" : Math.floor((this.getMonth() + 3) / 3),
			"S" : this.getMilliseconds()
		};
		if (/(y+)/.test(format)) {
			format = format.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
		}
		;
		for ( var k in o) {
			if (new RegExp("(" + k + ")").test(format)) {
				format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
						: ("00" + o[k]).substr(("" + o[k]).length));
			}
			;
		}
		;
		return format;
	};

	/**
	 * 检测浏览器是否支持打开PC录制客户端
	 */
	exports.checkIsSupport = function() {
		var isSupport = false;
		try {
			window.navigate('#showme');
			isSupport = true;
		} catch (e) {
			return isSupport;
		}
		return isSupport;
	}

	/**
	 * 检测浏览器是否支持ActiveX
	 */
	exports.checkActiveX = function(str) {
		try {
			var obj;
			obj = new ActiveXObject(str);
			return true;
		} catch (e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断浏览器是否支持canvas
	 */
	exports.checkCanvasSupport = function() {
		try {
			document.createElement('canvas').getContext('2d');
			return true;
		} catch (e) {
			return false;
		}
	}

	exports.secondToDate = function(s, isformat) {
		var t = "";
		if (s > -1) {
			hour = Math.floor(s / 3600);
			min = Math.floor(s / 60) % 60;
			sec = s % 60;

			if (hour > 0) {
				t += hour + "小时";
			}

			if (min < 10 && min > 0) {
				t += "0";
			}

			if (min > 0) {
				t += Math.floor(min) + "分钟";
			}

			if (sec < 10 && sec > 0) {
				t += "0";
			}

			if (sec > 0) {
				t += Math.floor(sec) + "秒";
			}

			if (!t) {
				t = "0秒";
			}
		}
		return t;
	}

	/**
	 * 计算输入字符的长度
	 */
	exports.inputLen = function getByteLen(val) {
		var len = 0;
		for (var i = 0; i < val.length; i++) {
			if (val[i].match(/[^\x00-\xff]/ig) != null) // 全角
				len += 2;
			else
				len += 1;
		}
		return len;
	}
	/**
	 * 计算输入字符的长度
	 */
	exports.getBrowserType = function() {
		var browser = {
			appname : 'unknown',
			version : 0
		}, userAgent = window.navigator.userAgent.toLowerCase(); // 使用navigator.userAgent来判断浏览器类型
		// msie,firefox,opera,chrome,netscape
		if (/(msie|firefox|opera|chrome|netscape)\D+(\d[\d.]*)/.test(userAgent)) {
			browser.appname = RegExp.$1;
			browser.version = RegExp.$2;
		} else if (/version\D+(\d[\d.]*).*safari/.test(userAgent)) { // safari
			browser.appname = 'safari';
			browser.version = RegExp.$2;
		}
		return browser;
	}
});
