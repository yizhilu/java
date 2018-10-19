/**
 * 存取本地cookie
 * 2013-06-24
 * LiYong
 */
define(function(require, exports, module) {
	var $ = require("jquery");
	var hostname=window.location.hostname;
	var domain="";
	if(hostname.indexOf("vanda.cn") != -1){
		domain = ";vanda.com";
	}
	/*
	 * 设置有会话周期cookie
	 */
	exports.setSessionCookie = function(name, value) {
		var Days = 356;
		var exp = new Date();
		exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
		document.cookie =name + "=" + encodeURIComponent(value) + ";expires=" + exp.toGMTString() + "; path=/"+domain;
	}
	
		/*
	 * 设置有会话周期cookie(不编码)
	 */
	exports.setSessionCookie2 = function(name, value) {
		var Days = 356;
		var exp = new Date();
		exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
		document.cookie =name + "=" + value + ";expires=" + exp.toGMTString() + "; path=/"+domain;
	}
	
	/*
	 * 设置无会话周期cookie，浏览器关闭cookie就失效
	 */
	exports.setSessionlessCookie = function(name, value) {
		document.cookie = name + "=" + encodeURIComponent(value) + "; path=/"+domain;
	}	
	/*
	 * 设置无会话周期cookie，浏览器关闭cookie就失效 (不编码)
	 */
	exports.setSessionlessCookie2 = function(name, value) {
		document.cookie =  name + "=" + value + "; path=/"+domain;
	}	
	
	/*
	 * 读取cookie
	 */
	exports.getCookie = function(name) {
		var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
		if( arr = document.cookie.match(reg))
			return decodeURIComponent(arr[2]);
		else
			return "";
	}
}); 