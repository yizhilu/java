define(function (require, exports, module) {
	var $ = require('jquery');
	
	/**
	 * 页面倒计时
	 * 
	 * @param elementId
	 *            页面显示元素id
	 * @param endTimestamp
	 *            结束时间戳
	 * @param callback
	 *            时间结束后回调
	 * @param callback
	 *            回调时候的参数
	 */
	exports.countDownTime = function (elementId,endTimestamp,callback,callbackParam) {
		// ====检查参数
		if(elementId==null||elementId==""){
			alert("页面显示元素id为空");
		}
		if(endTimestamp==null||endTimestamp==""){
			alert("结束时间戳为空");
		}
		// ====元素对象
		var secondObj=$("#"+elementId).find("[id^='second']");// 秒元素
		var minuteObj=$("#"+elementId).find("[id^='minute']");// 分元素
		var hourObj=$("#"+elementId).find("[id^='hour']");// 时元素
		var dayObj=$("#"+elementId).find("[id^='day']");// 天元素
		// ====设置心跳时间
		var millisec=1000;// 心跳时间,初始为1000ms
		if(secondObj.length==0){// 如果秒元素不存在，心跳时间以分计算
			millisec=millisec*60;
			if(minuteObj.length==0){// 如果分元素不存在，心跳时间以时计算
				millisec=millisec*60;
				if(hourObj.length==0){// 如果小时元素不存在，心跳时间以天计算
					millisec=millisec*24;
					if(dayObj.length==0){// 如果天元素不存在，没有元素标签弹出提示框
						alert("没有显示时间的标签");
						return;
					}
				}
			}
		}
		
		// 字段前加0
		function fullField(field){
			if (field < 10) {
				field = "0" + field;
			}
			return field;
		}
		
		// 递归执行
		var secondFlush=0;// 秒刷新时间计数，如果秒元素存在，每60秒刷新一次,开始需要刷新初始值为0,主要用于防止时元素不存在的时候秒数过多
		function recursion(){
			// 如果秒元素存在且秒元素值不为0，则直接秒元素减1，减少加载所有元素时间
			if(secondObj.length>0&&secondFlush>0&&parseInt(secondObj.html())>0){
				secondObj.html(fullField(parseInt(secondObj.html())-1));
				setTimeout(recursion,millisec)
				secondFlush--;
				return;
			}
			secondFlush=60;// 秒刷新重新赋值
			// 获取原始数据
			var nowDate = new Date();// 当前时间
			var timeDifference =parseInt(""+endTimestamp)-new Date().getTime();// 当前时间到结束时间的时间差(毫秒)
			timeDifference=Math.floor(timeDifference/1000);// 时间差毫秒转秒
			if(timeDifference<=0){
				timeDifference=0;
			}
			if(dayObj.length>0){// 天
				dayObj.html(fullField(Math.floor(timeDifference/(60*60*24))));
				timeDifference=timeDifference%(60*60*24);
			}
			if(hourObj.length>0){// 时
				hourObj.html(fullField(Math.floor(timeDifference/(60*60))));
				timeDifference=timeDifference%(60*60);
			}
			if(minuteObj.length>0){// 分
				minuteObj.html(fullField(Math.floor(timeDifference/(60))));
				timeDifference=timeDifference%(60);
			}
			if(secondObj.length>0){// 秒
				secondObj.html(fullField(timeDifference));
			}
			// 判定是否需要倒计时
			if(timeDifference<=0){
				if(callback!=null){
					if(callbackParam==null){
						callback();
					}else{
						callback(callbackParam);
					}
				}
				return;
			}
			setTimeout(recursion,millisec)
		}
		// 启动递归执行
		recursion();
	}
	exports.dateFormat=function(date,fmt){
		Date.prototype.format = function(fmt) { 
		     var o = { 
		        "M+" : this.getMonth()+1,                 // 月份
		        "d+" : this.getDate(),                    // 日
		        "h+" : this.getHours(),                   // 小时
		        "m+" : this.getMinutes(),                 // 分
		        "s+" : this.getSeconds(),                 // 秒
		        "q+" : Math.floor((this.getMonth()+3)/3), // 季度
		        "S"  : this.getMilliseconds()             // 毫秒
		    }; 
		    if(/(y+)/.test(fmt)) {
		            fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		    }
		     for(var k in o) {
		        if(new RegExp("("+ k +")").test(fmt)){
		             fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
		         }
		     }
		    return fmt; 
		}
		return date.format(fmt);
	}
});
