seajs.use([ "jquery", "settingInfoModule", 'swiper4', "common", "layer",
		"honeySwitch", "iosSelect", "data", "layercss", 'swiper4css' ],
		function($, settingInfoModule, swiper, honeySwitch, iosSelect, data,
				common, layer) {

			/**
			 * 开始小时
			 */
			$(function() {

				// 开始时的 小时
				var one = 0;

				// 开始时的 分钟
				var tow = 0;

				// 结束时的 时间
				var three = 0;

				// 结束时的 分钟
				var hour = 0;

				var mywhetherOpen=false;
				/**
				 * 初始化前，需要拿到时间
				 * 
				 */
				init();
				var mySwiper;
				var mySwiperStartMinute;
				var mySwiperEndHours;
				var mySwiperEndMinute;

				function init() {
					myOne = new Swiper('#one', {
						width : 50,
						height : 150,
						direction : 'vertical',
						slidesPerView : 5,
						freeMode : false,
						freeModeSticky : true,
						centeredSlides : true,
						loop : true,
						mousewheelControl : true,
						spaceBetween : 15,
						on : {
							init : function() {
								console.log(this)
								addSwiperSlide(this, 10);
								switchSlideToLoop(this, parseInt(0, 10));
							},
							slideChangeTransitionEnd : function() {
								console.log("电量设置第一个已改变");
							},
						}
					});

					// 开始第二个
					myTow = new Swiper('#tow', {
						width : 50,
						height : 150,
						direction : 'vertical',
						slidesPerView : 5,
						freeMode : false,
						freeModeSticky : true,
						centeredSlides : true,
						loop : true,
						mousewheelControl : true,
						spaceBetween : 15,// 两个swiper之间的距离
						on : {
							init : function() {
								addSwiperSlide(this, 10);
								switchSlideToLoop(this, parseInt(0, 10));
							},
							slideChangeTransitionEnd : function() {
								console.log("dian第二个已改变");
							},
						}
					});

					// 开始第三个

					myThree = new Swiper('#three', {
						width : 50,
						height : 150,
						direction : 'vertical',
						slidesPerView : 5,
						freeMode : false,
						freeModeSticky : true,
						centeredSlides : true,
						loop : true,
						mousewheelControl : true,
						spaceBetween : 15,// 两个swiper之间的距离
						on : {
							init : function() {
								console.log("初始化结束小时");
								addSwiperSlide(this, 10);
								switchSlideToLoop(this, parseInt(0, 10));
							},
							slideChangeTransitionEnd : function() {
								console.log("dian第三个已改变");
							},
						}
					});

					// 开始第四个
					myHour = new Swiper('#hour', {
						width : 50,
						height : 150,
						direction : 'vertical',
						slidesPerView : 5,
						freeMode : false,
						freeModeSticky : true,
						centeredSlides : true,
						loop : true,
						mousewheelControl : true,
						spaceBetween : 15,// 两个swiper之间的距离
						on : {
							init : function() {
								console.log("初始化结束分钟");
								addSwiperSlide(this, 10);
								switchSlideToLoop(this, parseInt(0, 10));
							},
							slideChangeTransitionEnd : function() {
								console.log("第四个已改变");
							},
						}
					});

				}
				;

				// 打开时间选择插件
				$("#showGeneral").click(function() {
					// 获取页面时间
					var temp = $("#showGeneral").text();
					console.log(temp + "它的长度为：" + temp.length);

					// 获取页面值
					for (var i = temp.length; i < 4; i++) {
						temp = '0' + temp;
					}
					one = temp.substring(0, 1);
					tow = temp.substring(1, 2);
					three = temp.substring(2, 3);
					hour = temp.substring(3, 4);

					// 时间插件滚动到指定位置
					switchSlideToLoop(myOne, parseInt(one, 10));
					switchSlideToLoop(myTow, parseInt(tow, 10));
					switchSlideToLoop(myThree, parseInt(three, 10));
					switchSlideToLoop(myHour, parseInt(hour, 10));
					$("#powerSettings").show(500);

				});

				// 关闭时间选择插件
				$("#btn_powerSettings").click(function() {
					// 拼接时间，并且渲染到指定页面位置
					jointTimeAndRenderer();
					$("#powerSettings").hide();
				});

				/**
				 * 拼接时间，渲染到指定页面
				 */
				function jointTimeAndRenderer() {

					// 获取当前活动区域的内容
					var electric = $(
							"#powerSettings div[class*=swiper-slide-active]")
							.text();

					// 正则匹配前面不为0
					electric = electric.replace(/\b(0+)/gi, "");
					// 拼接开始时间
					if(electric<=0){
						alert("电量阀值最小值为1");
						electric=1;
					}
					
					$("#showGeneral").html(electric);
//					var whetherOpen=$("#pagestatus").text();
					console.log("当前设置的电量信息为:" + electric);
					
					if($("#switch").attr('class').indexOf('on')!= -1 ){
						mywhetherOpen=true;
					}
					settingInfoModule.updateElectricQuantity(mywhetherOpen,electric);
				}
				/**
				 * 跳转到指定页面
				 */
				function switchSlideToLoop(swiper, number) {

					console.log("获取指定的位置" + number);
					swiper.slideToLoop(number, 1000);
				}

				function addSwiperSlide(obj, type) {
					console.log("动态添加div");
					console.log(obj);

					obj.removeSlide(0);
					var s = new Array();
					var i;
					for (i = 0; i < type; i++) {
						if (i.toString().length == 1) {
							var t = i
						} else {
							t = i;
						}
						;
						s[i] = '<div class="swiper-slide">' + t + '</div>';
					}
					obj.appendSlide(s);
					obj.update();
				}

			});
		});
