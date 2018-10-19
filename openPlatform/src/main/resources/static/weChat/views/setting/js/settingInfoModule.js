define(function(require, exports, module) {
	var $ = require("jquery"), common = require("common"),honeySwitch=require("honeySwitch"),iosSelect=require("iosSelect"),data=require("data"),commonDate = require("commonDate"), layer = require("layer");

	

	/**
	 * 触摸滑动选择电量
	 */
		exports.battery = function(){		
		    var showGeneralDom = document.querySelector('#showGeneral');
		    var suIdDom = document.querySelector('#suId');
		    var weiIdDom = document.querySelector('#weiId');
		    var heIdDom = document.querySelector('#heId');
		    var xeiIdDom = document.querySelector('#xeiId');
		    showGeneralDom.addEventListener('click', function () {
		        var suId = showGeneralDom.dataset['su_id'];
		        var suValue = showGeneralDom.dataset['su_value'];
		        var weiId = showGeneralDom.dataset['wei_id'];
		        var weiValue = showGeneralDom.dataset['wei_value'];
		        var heId = showGeneralDom.dataset['he_id'];
		        var heValue = showGeneralDom.dataset['he_value'];
		        var xeiId = showGeneralDom.dataset['xei_id'];
		        var xeiValue = showGeneralDom.dataset['xei_value'];
		        var sanguoSelect = new IosSelect(4, 
		            [suData, weiData,heData,xeiData],
		            {
		                title: '电量选择',
		                itemHeight: 35,
		                oneLevelId: suId,
		                twoLevelId: weiId,
		                threeLevelId: heId,
		                fourLevelId: xeiId,
		                callback: function (selectOneObj, selectTwoObj,selectThreeObj, selectFourObj) {
		                    suIdDom.value = selectOneObj.id;
		                    weiIdDom.value = selectTwoObj.id;
		                    heIdDom.value = selectThreeObj.id;
		                    xeiIdDom.value = selectFourObj.id;
		                    showGeneralDom.innerHTML = '' + selectOneObj.value + '' + selectTwoObj.value+'' + selectThreeObj.value + '' + selectFourObj.value;
		                    var value= showGeneralDom.innerHTML;
		                    console.log(value);
		                    
		                    var appleswitch=  $("#pagestatus").html();
		                    console.log(appleswitch);
		                    if(appleswitch =="false"){  
		                    console.log(666);
		         	       // 开启
							$.post("/wx/setting/modifyBatteryInfo", {
								"whetherOpen" : true,
								"electricityThreshold" : value
							}, function(result) {
								if (result.responseCode == "_200") {								
											
								} else {
									layer.open({
										content : result.errorMsg,
										time : 3
									})
								}
							}, "json");
		                    }else {
		                    }
		                }
		            });
		    });
		}
		    
		/**
		 * 切换开关状态
		 */
		exports.slides = function(){
			$.switch({
				  target:'#switch',
				  callback:function(that){
				    // return false 返回原始状态
					  var values =$("#showGeneral").html();
					  var mywhetherOpen=false;
					  if($("#switch").attr('class').indexOf('on')!= -1 ){
							mywhetherOpen=true;
						}
					  $.post("/wx/setting/modifyBatteryInfo", {
							"whetherOpen" : mywhetherOpen,
							"electricityThreshold" : values
						}, function(result) {
							if (result.responseCode == "_200") {
								console.log("成功");
							} else {
								layer.open({
									content : result.errorMsg,
									time : 3
								})
							}
						}, "json");
				  }
				});
		}
		
		/**
		 * 触摸滑动选择用户设置勿扰模式
		 */
			exports.doNotDisturb = function(){		
			    var showGeneralDom = document.querySelector('#General');
			    var suIdDom = document.querySelector('#suId');
			    var weiIdDom = document.querySelector('#weiId');
			    var heIdDom = document.querySelector('#heId');
			    var xeiIdDom = document.querySelector('#xeiId');
			    showGeneralDom.addEventListener('click', function () {
			        var suId = showGeneralDom.dataset['su_id'];
			        var suValue = showGeneralDom.dataset['su_value'];
			        var weiId = showGeneralDom.dataset['wei_id'];
			        var weiValue = showGeneralDom.dataset['wei_value'];
			        var heId = showGeneralDom.dataset['he_id'];
			        var heValue = showGeneralDom.dataset['he_value'];
			        var xeiId = showGeneralDom.dataset['xei_id'];
			        var xeiValue = showGeneralDom.dataset['xei_value'];
			        var sanguoSelect = new IosSelect(4, 
			            [aData, bData,cData,dData],
			            {
			                title: '至',
			                itemHeight: 35,
			                oneLevelId: suId,
			                twoLevelId: weiId,
			                threeLevelId: heId,
			                fourLevelId: xeiId,
			                callback: function (selectOneObj, selectTwoObj,selectThreeObj, selectFourObj) {
			                    suIdDom.value = selectOneObj.id;
			                    weiIdDom.value = selectTwoObj.id;
			                    heIdDom.value = selectThreeObj.id;
			                    xeiIdDom.value = selectFourObj.id;
			                    showGeneralDom.innerHTML = '' + selectOneObj.value + '' + selectTwoObj.value+'' + selectThreeObj.value + '' + selectFourObj.value;
			                    var value= showGeneralDom.innerHTML;
			                    var startTime = '' + selectOneObj.value + ':' + selectTwoObj.value;
			                    var endTime=''+ selectThreeObj.value + ':' + selectFourObj.value;
			                    console.log(startTime);
			                    console.log(endTime);
			                    
			                    var appleswitch=  $("#status").html();
			                    console.log(appleswitch);
			                    if(appleswitch =="false"){  
			                    console.log(666);
			         	       // 开启
								$.post("/wx/setting/modifyDoNotDisturbMode", {
									"whetherOpen" : true,
									"startTime" : startTime,
									"endTime":endTime
								}, function(result) {
									if (result.responseCode == "_200") {								
											window.location.reload();	
									} else {
										layer.open({
											content : result.errorMsg,
											time : 3
										})
									}
								}, "json");
			                    }
			                }
			
			       
			            });
			    });
			}
			
			
			/**
			 * 切换勿扰开关状态
			 */
			exports.nodisswitch = function(){	
				$.switch({
					  target:'#switches',
					  callback:function(that){
							$.get("/wx/setting/updateNotDistrubSwitch",function(result) {
								if (result.responseCode == "_200") {								
                                        console.log("成功");
								} else {
									layer.open({
										content : result.errorMsg,
										time : 3
									})
								}
							}, "json");
				
					  }
					});
			}

	
	
	
	/**
	 * 房间开关
	 */
	
	exports.updateRoomSwitch = function(){
		
		$.get("/wx/setting/updateRoomSwitch", function(result) {
			if (result.responseCode == "_200") {
                console.log("成功");
			} 
		}, "json");
		
	}
	
	/**
	 * 房间通知次数
	 */
	
	exports.updateRoomInformNumber = function(){

		if($('#oneInformdiv').hasClass('switch-on')){
            console.log("Y");
		}
		$.get("/wx/setting/updateRoomInformNumber", function(result) {
			console.log("load");
			if (result.responseCode == "_200") {
				console.log("成功");
			}else{
				/*
				 * if($('#oneInformdiv').hasClass('switch-on')){
				 * console.log("Y"); }
				 */
				
			}
		}, "json");
		
	}
	
	/**
	 * 修改通知时间段
	 */
	exports.lupdateNightTime = function(obj,obj1){
		
		$.post("/wx/setting/updateNightTime",{
			"startTime":obj,
			"endTime":obj1
		} ,function(result) {
			if (result.responseCode == "_200") {
                console.log("成功");
			} 
		}, "json");
	
	}
	/**
	 * 修改夜间模式开关
	 */
	exports.updateNightSwitch = function(){
		
		$.get("/wx/setting/updateNightSwitch", function(result) {
			if (result.responseCode == "_200") {
                console.log("成功");
			} 
		}, "json");
		
	}
	
	
	exports.updateBothered = function(obj1,obj2,whetherOpen){
		$.post("/wx/setting/modifyDoNotDisturbMode", {
			"startTime" : obj1,
			"endTime":obj2,
		}, function(result) {
			if (result.responseCode == "_200") {
				console.log("成功");
			} else {
				layer.open({
					content : result.errorMsg,
					time : 3
				})
			}
		}, "json");
	}

	/**
	 * 修改电量值
	 */
	exports.updateElectricQuantity=function(whetherOpen,electric){
		// "whetherOpen" : whetherOpen,
		$.post("/wx/setting/modifyBatteryInfo", {
			"whetherOpen":whetherOpen,
			"electricityThreshold" : electric
		}, function(result) {
			if (result.responseCode == "_200") {								
						console.log("成功");
			} else {
				layer.open({
					content : result.errorMsg,
					time : 3
				})
			}
		}, "json");
	}
	

});