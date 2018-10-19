
                $(function() {
                    var index = $("#index").val();
					var socketId = $("#socketId").val();
					var topSwiper;
					var myEcharts;
					console.log("index= "+index+"  socketId= "+socketId);
					// 低部swiper区域
					var bottomSwiper = new Swiper('.gallery-thumbs', {
						initialSlide : Number(index),// 设定初始化时slide的索
						spaceBetween : 10,
						loop : true,
						centeredSlides : false,
						slidesPerView : 1,
						touchRatio : 0.2,
						slideToClickedSlide : true,
						on : {
							init : function() {
								topSwiper = initTopSwiper();
							},	
							slideChangeTransitionEnd : function() {
								var realIndex = this.realIndex;
								if (realIndex == 0) {
									$("title").html("实时电表");
								} else if (realIndex == 1) {
									$("title").html("电量统计"); 
									
								} else if (realIndex == 2) {
									$("title").html("人体探测");
								}
							}
						}
					});
					function initTopSwiper() {
						var galleryTop = new Swiper('.gallery-top', {
							initialSlide : Number(index),// 设定初始化时slide的索引。
							spaceBetween : 10, // slide 之间的距离
							loop : true,
							centeredSlides : true, // 设定为true时，active 活动快居中
							slidesPerView : 1.2, // 设置slider容器能够同时显示的slides数量(carousel模式)。
							coverflowEffect : {
								rotate : 0,
								stretch : 10,
								depth : 0,
								modifier : 2,
								slideShadows : true
							},
							on : {
								init : function() {
								},	
								slideChangeTransitionEnd : function() {
									var realIndex = this.realIndex;
									console.log("human:"+realIndex);
									if (realIndex == 0) {
										loadRealTimeElectric();
									} else if (realIndex == 1) {
									loadElectricityStatistics();
									} else if (realIndex == 2) {
										loadHumanHeatmap();
									}
								}
							}
						});
						return galleryTop;
					}
					// 设置为另外一个Swiper实例开始控制该Swiper。
					topSwiper.controller.control = bottomSwiper;
					bottomSwiper.controller.control = topSwiper;
// -----------------------------公共--js-------------------------------------------------------------------------------------
				function selectlab(document) {
						var labArr = document.parent();
						var list = $(labArr).children("div");
						var type = document.attr("type");
						for (var i = 0; i < list.length; i++) {
							var tempType = $(list[i]).attr("type");
							if (tempType == type) {
								$(list[i]).addClass("relative");
							} else {
								$(list[i]).removeClass("relative");
							}
						}
				}


				 function dateFormat(timestamp, formats) {
					// formats格式包括
					// 1. Y-m-d
					// 2. Y-m-d H:i:s
					// 3. Y年m月d日
					// 4. Y年m月d日 H时i分
					formats = formats || 'Y-m-d';

					var zero = function(value) {
						if (value < 10) {
							return '0' + value;
						}
						return value;
					};

					var myDate = timestamp ? new Date(timestamp) : new Date();

					var year = myDate.getFullYear();
					var month = zero(myDate.getMonth() + 1);
					var day = zero(myDate.getDate());

					var hour = zero(myDate.getHours());
					var minite = zero(myDate.getMinutes());
					var second = zero(myDate.getSeconds());

					return formats.replace(/Y|m|d|H|i|s/ig, function(matches) {
						return ({
							Y : year,
							m : month,
							d : day,
							H : hour,
							i : minite,
							s : second
						})[matches];
					});
				};
				
				function getDateRange(datevalue) {
					var dateValue = datevalue;
					var arr = dateValue.split("/");
					// 月份-1 因为月份从0开始 构造一个Date对象
					var date = new Date(arr[0], arr[1] - 1, arr[2]);

					var dateOfWeek = date.getDay();// 返回当前日期的在当前周的某一天（0～6--周日到周一）

					var dateOfWeekInt = parseInt(dateOfWeek, 10);// 转换为整型

					if (dateOfWeekInt == 0) {// 如果是周日
						dateOfWeekInt = 7;
					}
					var aa = 7 - dateOfWeekInt;// 当前于周末相差的天数
					var temp2 = parseInt(arr[2], 10);// 按10进制转换，以免遇到08和09的时候转换成0
					var sunDay = temp2 + aa;// 当前日期的周日的日期
					var monDay = sunDay - 6// 当前日期的周一的日期
					var startDate = new Date(arr[0], arr[1] - 1, monDay);
					var endDate = new Date(arr[0], arr[1] - 1, sunDay);
					var sm = parseInt(startDate.getMonth()) + 1;// 月份+1 因为月份从0开始
					var em = parseInt(endDate.getMonth()) + 1;
					var start = (sm < 10 ? "0" + sm : sm)
							+ "/"
							+ (startDate.getDate() < 10 ? "0" + startDate.getDate()
									: startDate.getDate());
					var end = (em < 10 ? "0" + em : em)
							+ "/"
							+ (endDate.getDate() < 10 ? "0" + endDate.getDate() : endDate
									.getDate());
					var str = start + "-" + end;
					return str;
				}
				/**
				 * 获取传入日期在当月属于第几周
				 */
				function getMonthWeek(a, b, c) {
					var date = new Date(a, parseInt(b) - 1, c), w = date.getDay(), d = date
							.getDate();
					return Math.ceil((d + 6 - w) / 7);
				}
				
				/**
				 * 私有方法，处理时间
				 */
				function formOneDate(date){
					var arrayDate=date.split("/");
					var month= arrayDate[0];
					var day= arrayDate[1];
					
                    var tempmonth=parseInt(month);
                    var tempday=parseInt(day);
					
					if(tempday-1<1){
						month=tempmonth-1;
                     if(tempmonth==1||tempmonth==3||tempmonth==5||tempmonth==7||tempmonth==8||tempmonth==10||tempmonth==12){
                    	 day='31';
                      }else if(tempmonth==4||tempmonth==6||tempmonth==9||tempmonth==11){
                    	  day='30';
                      }else if(tempmonth==2){
                    	  day='28';
                      }
					}else{
						day=tempday-1;
					}
					return month+"/"+day;
				}

				/*
				 * 传入年，周数，获取周数对应的日期范围
				 */
				function getDayEveryDay(year, index) {
				    var d = new Date(year, 0, 1);
				    while (d.getDay() != 1) {
				        d.setDate(d.getDate() + 1);
				    }
				    var to = new Date(year + 1, 0, 1);
				    var i = 1;
				    var arr = [];
				    for (var from = d; from < to;) {
				        if (i == index) {
				            arr.push(from.getFullYear() + "-" + (from.getMonth() + 1) + "-" + from.getDate());
				        }
				        var j = 6;
				        while (j > 0) {
				            from.setDate(from.getDate() + 1);
				            if (i == index) {
				                arr.push(from.getFullYear() + "-" + (from.getMonth() + 1) + "-" + from.getDate());
				            }
				            j--;
				        }
				        if (i == index) {
				            return arr;
				        }
				        from.setDate(from.getDate() + 1);
				        i++;
				    }
				}
				
				// -----------------------------------------------------------公共js-----------------------------------------------------
				
					// 加载实时电表数据
				
					// #############################实施电表##################################################################
					function loadRealTimeElectric() {
						console.log(11);
                    var electricityEchart = $("#echart_area div[class*='swiper-slide-active']").find("div[name='electricity_echart']");
	                myEcharts = initRealTimeElectric(electricityEchart[0]);
	                var type = $("#echart_area div[name='real_time_electric']").find("div[class*='relative']").attr("type");
	                loadRealTimeElectricDate(type, myEcharts,socketId);
				
					}
				
					 function initRealTimeElectric(dom) {
						var width = document.body.clientWidth;
						dom.style.width = width - 32 + 'px';
						 myEcharts = echarts.init(dom);
						 
					
						var option = {
							title : {
								text : '实时电表',
							},
							tooltip : {
								trigger : 'axis',
								axisPointer : {
									type : 'cross'
								}
							},
							grid:{
								containLabel:true
							},
							xAxis : {
								type : 'category',
								boundaryGap : false,
								data : []
							},
							yAxis : {
								type : 'value',
								axisPointer : {
									snap : true
								}
							},
							series : [ {
								name : '电量',
								type : 'line',
								smooth : true,
								data : [],
							} ]
						};
						myEcharts.setOption(option);
						return myEcharts;
					}

					// 加载电表数据
					function loadRealTimeElectricDate(type, myEcharts, socketId) {
						$.getJSON("/wx/socketStatistics/realTimeMeter?socketId=" + socketId,
								function(json) {
									if (json.responseCode == "_200") {
										// 获取波形图所需要数据
										var responsedata = json.data;
										var electricity = {};
										var otherinfo={};
										var time = new Array();
										var times = new Array();
										var electricityValue = new Array();// 电量值
										var powerValue = new Array();// 功率值
										var currentValue = new Array();
										var voltageValue = new Array();
										electricity = responsedata.electricitydata;
										otherinfo = responsedata.info; 
										for (var i = 0; i < electricity.length; i++) {
											time[i] = (electricity[i][0]).substr(4,2)+"/"+
											(electricity[i][0]).substr(6,2)+" "+(electricity[i][0]).substr(8,2)
													+ '点';
											electricityValue[i] = electricity[i][1];
										}
										for (var i = 0; i < otherinfo.length; i++) {
											times[i] = (otherinfo[i][3]).substr(4,2)+"/"+
											(otherinfo[i][3]).substr(6,2)+" "+(otherinfo[i][3]).substr(8,2)
													+ '点';
											currentValue[i] = otherinfo[i][0];
											powerValue[i] = otherinfo[i][1];
											voltageValue[i] = otherinfo[i][2];
										}
										var option = {};
										if (type == "STATUS_ELECTRIC") {
											option = {
												xAxis : {
													type : 'category',
													boundaryGap : false,
													data : time
												},
												series : [ {
													name : '电量',
													type : 'line',
													smooth : true,
													data : electricityValue,
												} ]
											}
										} else if (type == "STATUS_POWER") {
											option = {
												xAxis : {
													type : 'category',
													boundaryGap : false,
													data : times
												},
												series : [ {
													name : '功率',
													type : 'line',
													smooth : true,
													data : powerValue,
												} ]
											}
										} else if (type == "STATUS_CURRENT") {
											option = {
												xAxis : {
													type : 'category',
													boundaryGap : false,
													data : times
												},
												series : [ {
													name : '电流',
													type : 'line',
													smooth : true,
													data : currentValue,
												} ]
											}
										} else if (type == "STATUS_VOLTAGE") {
											option = {
												xAxis : {
													type : 'category',
													boundaryGap : false,
													data : times
												},
												series : [ {
													name : '电压',
													type : 'line',
													smooth : true,
													data : voltageValue,
												} ]
											}
										}
										// 填入数据
										console.log(myEcharts);
										myEcharts.setOption(option);
									} else {
										layer.open({
											content : json.errorMsg,
											time : 3
										});
									}
								});
					}
					// ###########################################################################################################
		
		// *******************************************电量信息*******************************************************************
		
		// 加载电量统计数据
		function loadElectricityStatistics() {
			var electricityStatisticsEchart = $("#echart_area div[class*='swiper-slide-active']").find("div[name='electricity_statistics_echart']");
			myEcharts =initElectricityStatistics(electricityStatisticsEchart[0]);
			var type = $("#echart_area div[name='electricity_statistics']").find("div[class*='relative']").attr("type");
			loadElectricityStatisticsDate(type,myEcharts, socketId);
			
		}
		
		// 初始化电量统计数据
		 function initElectricityStatistics(dom) {
			var width = document.body.clientWidth;
			dom.style.width = width - 32 + 'px';
			var myEcharts = echarts.init(dom);
			var labelOption = {
				normal : {
					show : true,
					rotate : -90,
					fontSize : 16,
					rich : {
						name : {
							textBorderColor : '#fff'
						}
					}
				}
			};
			option = {
				title : {
					text : '电量统计',
						},
				grid:{
						containLabel:true
			    },
				xAxis : {
					type : 'category',
					data : []
				},
				yAxis : {
					type : 'value'
				},
				series : [ {
					type : 'bar',
					label : labelOption,
					data : []
				} ]
			};
			myEcharts.setOption(option);
			return myEcharts;
		}
		// 加载电量统计数据
	 function loadElectricityStatisticsDate(type, myEcharts, socketId) {
			$.getJSON("/wx/socketStatistics/electricityStatistics?socketId="+ socketId,
							function(json) {
								if (json.responseCode == "_200") {								
									// 获取柱形图所需要数据
									var responsedata = json.data;
									console.log(responsedata);
									var electricityday = {};
									var electricityweek = {};
									var electricitymonth = {};
									var electricitydayValue = new Array();
									var electricityweekValue = new Array();
									var electricitymonthValue = new Array();
									var daysValue = new Array();
									var day = new Array();
									var days = new Array();
									var daysValues = new Array();
									var weekValue = new Array();
									var weekValues = new Array();
									var weekValuesyear = new Array();
									var weekValuesmonth = new Array();
									var weekValuesday = new Array();
									var weekValueses = new Array();
									var week = new Array();
									var weeks = new Array();
									var weekes = new Array();
									var weekDate = new Array();
									var monthValue = new Array();
									var monthValues = new Array();
									var monthValuess = new Array();
									var monthValueses = new Array();
									var month = new Array();
									var months = new Array();
									var str = new Array();
									var weekArray = [];	
									var monthArray = [];
									electricityday = responsedata.dayelectricity;
									electricityweek = responsedata.weekelectricity;
									electricitymonth = responsedata.monthelectricity;
									for (var i = 0; i < electricityday.length; i++) {
										daysValue[i] = electricityday[i][0];
										day[i] = electricityday[i][1];				
										daysValues[i] = daysValue[i].substr(5,2)+"/"+daysValue[i].substr(8,2);										
										electricitydayValue[i] = [ daysValues[i],
												day[i] ];
									}		
									
									if (electricityweek.length > 4) {
																	for (var i = electricityweek.length - 4; i < electricityweek.length; i++) {
																		weekArray.push(electricityweek[i]);
																	}
																	for (var j = 0; j < weekArray.length; j++) {
																		weekValue[j] = weekArray[j][0];
																		weekes[j] = getDayEveryDay(weekArray[j][2],
																				weekValue[j]);
																		weekDate[j] = weekes[j][0].substr(5, 2)
																				+ weekes[j][0].substr(7, 3) + "/"
																				+ weekes[j][6].substr(5, 2)
																				+ weekes[j][6].substr(7, 3);
																		weeks[j] = weekArray[j][1];
																	}
																} else {
																	for (var i = 0; i < electricityweek.length; i++) {
																		weekValue[i] = electricityweek[i][0];
																		weekes[i] = getDayEveryDay(
																				electricityweek[i][2], weekValue[i]);
																		weekDate[i] = weekes[i][0].substr(5, 2)
																				+ weekes[i][0].substr(7, 3) + "/"
																				+ weekes[i][6].substr(5, 2)
																				+ weekes[i][6].substr(7, 3);
																		weeks[i] = electricityweek[i][1];
																	}
																}
									
									if(electricitymonth.length>3){
										for (var i = electricitymonth.length - 3; i < electricitymonth.length; i++) {
											monthArray.push(electricitymonth[i]);
										}
										for (var j = 0; j < monthArray.length; j++) {
											month[j] = monthArray[j][1];			
											monthValue[j] = monthArray[j][0];
											monthValueses[j] = monthValue[j] + '月';
											electricitymonthValue[j] = month[j];
										}
									}else{
									for (var i = 0; i < electricitymonth.length; i++) {
										month[i] = electricitymonth[i][1];			
										monthValue[i] = electricitymonth[i][0];
										monthValueses[i] = monthValue[i] + '月';
										electricitymonthValue[i] = month[i];
									}
									}
								
									if (type == 'STATUS_DAY') {
										var option = {
											xAxis : {
												type : 'category',
											
												data : daysValues
											},
											series : [ {
												type : 'bar',
												data : electricitydayValue,
											} ]
										};
									} else if (type == "STATUS_WEEK") {
										var option = {
											xAxis : {
												type : 'category',
												data : weekDate
											},
											series : [ {
												type : 'bar',
												data : weeks,
											} ]
										};
									} else if (type == "STATUS_MONTH") {
										var option = {
											xAxis : {
												type : 'category',
												data : monthValueses
											},
											series : [ {
												type : 'bar',
												data : electricitymonthValue,
											} ]
										};
									}
									myEcharts.setOption(option);
								}
							})
		}

		// --------------------------------------------------------------------------------------------------------------

// **********************************电量信息**********************************************************************
		
		
		
// *********************************************************************************************************
		// 加载人体探测热力图数据
		function loadHumanHeatmap() {
			console.log(222);
			var humanTimestampEchart = $("#echart_area div[class*='swiper-slide-active'] div[name='human_timestamp_echart']");
			myEcharts = initHumanHeatmap(humanTimestampEchart[0]);
			loadHumanHeatmapDate(myEcharts, socketId);
		}
		 function initHumanHeatmap(dom) {
			var width = document.body.clientWidth;
			dom.style.width = width - 32 + 'px';
			var myEcharts = echarts.init(dom);
		
			var hours = ['24','23','22','21','20','19','18','17','16','15','14','13','12','11','10','09','08','07','06','05','04','03','02','01'];
			
			
			var days = [];
			
			var mdate = new Date();
			var ss=mdate.getDate();

			for (var i = 4; i>=0; i--) {
				var da=mdate.setDate(ss-i);
				// 获取四天之前的日期；-----------start----------------------
				var newdate=dateFormat(da,'m/d');
				// 获取四天之前的日期；-------------end --------------------
				console.log(newdate);
				days.push(newdate);
			}	
			
			var option = {
				
				tooltip : {
					position : 'top'
				},
				animation : false,
				grid : {
					left : '20px',
					right : '20px',
					containLabel : true
				},
				xAxis : {
					type : 'category',
					data : days,
					splitArea : {
						show : true
					}
				},
				yAxis : {
					name:'时间(点)',
					type : 'category',
					axisLabel : {
						formatter : '{value}:00'
					},
					data : hours,
					splitArea : {
						show : true
					}
				},
				visualMap : {
					min : 0,
					max : 1100,
					calculable : true,
					orient : 'horizontal',
					left : 'center',
					bottom : '150%'
				},
				series : [ {
					name : '人体探测热量图',
					type : 'heatmap',
					data : [],
					label : {
						normal : {
							show : true
						}
					},

					itemStyle : {
						emphasis : {
							shadowBlur : 10,
							shadowColor : 'rgba(0, 0, 0, 0.5)'
						}
					}
				} ]
			};
			myEcharts.setOption(option);
			return myEcharts;
		}
		// 加载人体探测热力图数据
	 function loadHumanHeatmapDate(myEcharts, socketId) {
			$.get('/wx/socketStatistics/humanHeatmap?socketId=' + socketId).done(
					function(data) {
						var days = [];
						var temp = data.data;

						var isEmpty = $.isEmptyObject(temp);
						if (isEmpty) {
							temp = [];
						}
						var array = $.extend(true, [], temp);
						
						for (var i = 0; i < array.length; i++) {
							
							var arraytemp=array[i][1];
							if(arraytemp=='00'){
							var newtempTime=formOneDate(array[i][0]);
							console.log(11);	
							array.splice(i,1);
							array.splice(i,0,[newtempTime,"24",temp[i][2]]);
                            console.log();
							// var dadad=new Date().getTime();
							    // var dataValue = new Date(new Date().getTime()
								// - (1000 * 60 * 60 * 24))
							}
							
						}
						
						
					
						// 填入数据
						myEcharts.setOption({
							series : [ {
								// 根据名字对应到相应的系列
								name : '人体探测热力图',
								type : 'heatmap',
								data : array,
							} ]
						});
					});
		}
		// *******************************************************************************************************
					
					
		// 切换标签并显示对应的实时电表
		$("#echart_area")
				.find("div[name='real_time_electric']")
				.on(
						"click",
						'div[type]',
						function() {
							selectlab($(this));
							if ($(this).attr("type") == 'STATUS_POWER') {
								loadRealTimeElectric("STATUS_POWER",myEcharts, socketId);
							} else if ($(this).attr("type") == 'STATUS_CURRENT') {
							loadRealTimeElectric("STATUS_CURRENT",myEcharts, socketId);
							} else if ($(this).attr("type") == 'STATUS_VOLTAGE') {
								loadRealTimeElectric("STATUS_VOLTAGE",myEcharts, socketId);
							} else {
								loadRealTimeElectric("STATUS_ELECTRIC",myEcharts, socketId);
							}
						});
		// 切换标签并显示对应参数的电量统计
		$("#echart_area")
				.find("div[name='electricity_statistics']")
				.on(
						"click",
						'div[type]',
						function() {
							selectlab($(this));
							if ($(this).attr("type") == 'STATUS_DAY') {
								loadElectricityStatistics('STATUS_DAY',	myEcharts, socketId);
							} else if ($(this).attr("type") == 'STATUS_WEEK') {
								loadElectricityStatistics('STATUS_WEEK',myEcharts, socketId);
							} else if ($(this).attr("type") == 'STATUS_MONTH') {
								loadElectricityStatistics('STATUS_MONTH',myEcharts, socketId);
							}
						});
		// 切换标签并显示对应的人体探测图像
		$("#echart_area").find("div[name='human']").on("click",'div[type]',function() {
							selectlab($(this));
							var socketId = $("#socketId").val();
							if ($(this).attr("type") == 'HEAT') {
								$("#echart_area div[name='human_timestamp_echart']").show();
								$("#echart_area div[name='human_time_area']").hide();
								loadHumanHeatmap();
							} else {	
								$("#echart_area div[name='human_timestamp_echart']").hide();
								$("#echart_area div[name='human_time_area']").show();
						       	$.get("/wx/socketStatistics/humanSomeone",{"socketId" : socketId},function(result) {
										if (result.responseCode == "_200") {
															var temp = result.data;
															$("#echart_area div[name='human_time_area'] div[name='timer']").empty();
															var lastTime = "";
															var isEmpty = $.isEmptyObject(temp);
															if (isEmpty) {
															temp = [];
															}
										for (var i = 0; i < temp.length; i++) {
																var cloneElem = $("#template_div").clone(true);
																cloneElem.attr("id");
														if (jsFormatTime(temp[i][1],false,'Y/m/d') == lastTime) {
								cloneElem.find("[name='date']").html(jsFormatTime(temp[i][1],false,'Y/m/d')).css("visibility","hidden");
																} else {
																	lastTime = jsFormatTime(temp[i][1],false,'Y/m/d');
																	cloneElem.find(	"[name='date']").html(lastTime);
																}
							cloneElem.find(	"[name='time']").html(jsFormatTime(temp[i][1],false,'H:i')+ "-"+ jsFormatTime(temp[i][1],true,'H:i'));
												cloneElem.find("[name='status']").html("有人");
																cloneElem.find("[name='value']").html(temp[i][0]);
																cloneElem.show();
																$("#echart_area div[name='human_time_area'] div[name='timer']").append(cloneElem);
															}
														} else {
															layer.open({
															content : result.errorMsg,
															time : 3
														})
														}
													}, "json");
								
		                        
			                        
							}
						})
		function jsFormatTime(time, istrue, format) {
			if (istrue) {
				var time = new Date(time.replace("-", "/"));
				time.setMinutes(time.getMinutes() + 30, time.getSeconds(), 0);
			}
			return dateFormat(time, format);
		}

		// 切换电量通知选项标签
		$("div[id='notice_status']").on("click",'div[type]',function() {
				var status = $(this).attr("type");
				selectlab($(this));
							$.post("/wx/electricStatisticsConfig/modifyNoticeStatus",
								{"status" : status,"socketId" : socketId},
								function(result) {
								if (result.responseCode == "_200") {
								$("#typeitem").attr("data-temp",status);
								//status
								
								console.log(status);
								
								if(status=='NOT_SENDNOTICE'){
									$("#data-static").html("不发送通知");
								}else if(status=='DAY_SENDNOTICE'){
									$("#data-static").html("日报(每天9:00发送通知)");
								}else if(status=='WEEK_SENDNOTICE'){
									$("#data-static").html("周报(每天9:00发送通知)");
								}else{
									$("#data-static").html("月报(每天9:00发送通知)");
								}
								
								setTimeout(() => {
									$("#notice").hide();
								}, 500);
								} else {layer.open({content : result.errorMsg,
													time : 3
										})
												}
											}, "json");
						});

		// 显示电量通知选项窗口
		$("div[id='noticebtn']").on("click", function() {
			$("#notice").show();
			
			var type=	$("#typeitem").attr("data-temp");
			console.log("type"+type);
			
			if(type.length<=0){
				type="WEEK_SENDNOTICE";
			}
			if(type=='NOT_SENDNOTICE'){
				console.log("不发送");
            $("div[name=not_item").addClass("relative");
			}else if(type=='DAY_SENDNOTICE'){
				console.log("日报");
		    $("div[name=day_item").addClass("relative");
			}else if(type=='WEEK_SENDNOTICE'){
				console.log("周报");
			$("div[name=week_item").addClass("relative");
			}else{
				console.log("月报");
		    $("div[name=month_item").addClass("relative");
		    
			}
		});
		// 关闭电量通知选项窗口
		$("div[id='btncha']").on("click", function() {
			$("#notice").hide();
		});

		// 
		$("div[id='claerbtn']").on("click", function() {
			var id = $(this).attr("data-id");
			
			clearDate(id);
		});

		function clearDate(id) {
			console.log(11+"id:"+id);
		    layer.open({
		      content : '是否确认清零电量？',
		      btn : [ '确定', '取消' ],
		      yes : function(index) {
		        $.ajax({
		          type : "POST",
		          url : "/wx/socketStatistics/modifyStandardValue",
		          data : { id:id},
		          success : function(result) {
		            layer.close(index);
		            if (result.responseCode == "_200") {
		              window.location.reload();
		            } else {
		              layer.open({
		                content : result.errorMsg,
		                time : 3
		              });
		            }
		          }
		        });
		      }
		    });

		  }
		
		/**
		 * 定時刷新 10s
		 */
		
		setInterval(() => {
		
			var  indexs=	$("#echart_area div[class*='swiper-slide-active']").index();
		    if(indexs==1){
		    	console.log("顶层活动块:"+indexs);
		    	
				$.get("/wx/socketStatistics/cactualElectricity",{
					"socketId" : socketId
							},
							function(result) {
								if (result.responseCode == "_200") {
									var temp=result.data.data.data;
									var startState=result.data.startState;
									if(startState=='CONNECTED'){
										startState="已打开";
									}else{
										startState="已关闭";
									}
									if(temp!=null&&temp!=undefined){
// $("#echart_area div[class$='swiper-slide-active']
// div[name='electrictyinfo']").show();
										$("#echart_area div[class$='swiper-slide-active'] div[name='clearTime']").html(temp.clearTimes);
										$("#echart_area div[class$='swiper-slide-active'] div[name='recordingTime']").html(jsFormatTime(temp.recordingTime,false,"Y-m-d H:i:s"));
										$("#echart_area div[class$='swiper-slide-active'] div[name='startState']").html(startState);
										$("#echart_area div[class$='swiper-slide-active'] div[name='electricityValue']").html(temp.electricityValue+'(度)');
										$("#echart_area div[class$='swiper-slide-active'] div[name='powerValue']").html(temp.powerValue+'(瓦)');
										$("#echart_area div[class$='swiper-slide-active'] div[name='currentValue']").html(temp.currentValue+'(安)');
										$("#echart_area div[class$='swiper-slide-active'] div[name='voltageValue']").html(temp.voltageValue+'(伏)');
									}
									// clearTime recordingTime electricityValue
									// powerValue currentValue voltageValue
									// clearTimes
								} 
							}, "json");		
		    	
		    var type1=	$("#echart_area div[class$='swiper-slide-active'] div[name='real_time_electric'] div[class='ite relative']").attr('type');
		    console.log("第一个活动块下子类型："+type1);					    
		    if(type1=='STATUS_ELECTRIC'){
		    	 statisticsModule.loadRealTimeElectric("STATUS_ELECTRIC",myEcharts, socketId);
		    }else if(type1=='STATUS_POWER'){
		    	statisticsModule.loadRealTimeElectric("STATUS_POWER",myEcharts, socketId);					    	
		    }else if(type1=='STATUS_CURRENT'){
		    	statisticsModule.loadRealTimeElectric("STATUS_CURRENT",myEcharts, socketId);
		    }else if(type1=='STATUS_VOLTAGE'){
		    	statisticsModule.loadRealTimeElectric("STATUS_VOLTAGE",myEcharts, socketId);					
		    }
		    	
		    }else if(indexs==2){
		     var type2=$("#echart_area div[class$='swiper-slide-active'] div[name='electricity_statistics'] div[class='itee relative']").attr('type');
		     console.log("第二个活动块下子类型："+type2);
		    	if(type2=='STATUS_DAY'){				    		
		    		statisticsModule.loadElectricityStatistics('STATUS_DAY',myEcharts, socketId);
		    	}else if(type2=='STATUS_WEEK'){
		    		statisticsModule.loadElectricityStatistics('STATUS_WEEK',myEcharts, socketId);
		    	}else if(type2=='STATUS_MONTH'){
		    		statisticsModule.loadElectricityStatistics('STATUS_MONTH',myEcharts, socketId);					
		    	}
		    }else if(indexs==3){
		        var type3=$("#echart_area div[class$='swiper-slide-active'] div[name='human'] div[class='iteee relative']").attr('type');
		        console.log("第三个活动块下子类型："+type3);
		        if(type3=='HEAT'){
                	// 加载人体探测热力图
                	loadHumanHeatmap();
                }else if(type3=='TIME'){
	       	$.get("/wx/socketStatistics/humanSomeone",{"socketId" : socketId},function(result) {
									if (result.responseCode == "_200") {
										var temp = result.data;
										$(
												"#echart_area div[name='human_time_area'] div[name='timer']")
												.empty();
										var lastTime = "";

										var isEmpty = $
												.isEmptyObject(temp);
										if (isEmpty) {
											temp = [];
										}
										for (var i = 0; i < temp.length; i++) {
											var cloneElem = $(
													"#template_div")
													.clone(
															true);
											cloneElem
													.attr("id");
											if (jsFormatTime(
													temp[i][1],
													false,
													'Y/m/d') == lastTime) {
												cloneElem
														.find(
																"[name='date']")
														.html(
																jsFormatTime(
																		temp[i][1],
																		false,
																		'Y/m/d'))
														.css(
																"visibility",
																"hidden");
											} else {
												lastTime = jsFormatTime(
														temp[i][1],
														false,
														'Y/m/d');
												cloneElem
														.find(
																"[name='date']")
														.html(
																lastTime);
											}
											cloneElem
													.find(
															"[name='time']")
													.html(
															jsFormatTime(
																	temp[i][1],
																	false,
																	'H:i')
																	+ "-"
																	+ jsFormatTime(
																			temp[i][1],
																			true,
																			'H:i'));
											cloneElem
													.find(
															"[name='status']")
													.html(
															"有人");
											cloneElem
													.find(
															"[name='value']")
													.html(
															temp[i][0]);
											cloneElem
													.show();
											$(
													"#echart_area div[name='human_time_area'] div[name='timer']")
													.append(
															cloneElem);
										}
									} else {
										layer
												.open({
													content : result.errorMsg,
													time : 3
												})
									}
								}, "json");
			
            
                }
                
		    }
		}, 3000000);					

		
		
				})
					
					
					
					