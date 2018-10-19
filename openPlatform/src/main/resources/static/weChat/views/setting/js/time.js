seajs.use([ "jquery", "settingInfoModule", 'swiper4', "common", "layer",
		"honeySwitch", "iosSelect", "data", "layercss", 'swiper4css' ],
		function($, settingInfoModule, swiper, honeySwitch, iosSelect, data,
				common, layer) {

			/**
			 * 开始小时
			 */
			$(function() {

				// 开始时的 小时
				var startHours = 0;

				// 开始时的 分钟
				var startMinute = 0;

				// 结束时的 时间
				var endHours = 0;

				// 结束时的 分钟
				var endMinute = 0;

				var type;

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
					mySwiper = new Swiper('#start-hours', {
						slideActiveClass : 'swiper-slide-active',// 设置当前活动的类名
						slidePrevClass : 'swiper-slide-prev',// 设置前一个活动的类名
						slideClass : 'swiper-slide',// 设置slide类名
						slideNextClass : 'swiper-slide-next',// 设置下一个块的类名
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
								// Swiper初始化了
								console.log("初始化开始小时");
								console.log(this)
								addSwiperSlide(this, 24);
								switchSlideToLoop(this, parseInt(0, 10));
								// this.emit('transitionEnd');//在初始化时触发一次transitionEnd事件
							},
							slideChangeTransitionEnd : function() {
								console.log("第一个已改变");
							},
						}
					});

					// 开始第二个
					mySwiperStartMinute = new Swiper('#start-minute', {
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
								// Swiper初始化了
								console.log("初始化开始分钟");
								addSwiperSlide(this, 60);
								switchSlideToLoop(this, parseInt(0, 10));
								// this.emit('transitionEnd');//在初始化时触发一次transitionEnd事件
							},
							slideChangeTransitionEnd : function() {
								console.log("第二个已改变");
							},
						}
					});

					// 开始第三个

					mySwiperEndHours = new Swiper('#end-hours', {
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
								addSwiperSlide(this, 24);
								switchSlideToLoop(this, parseInt(0, 10));
								// this.emit('transitionEnd');//在初始化时触发一次transitionEnd事件
							},
							slideChangeTransitionEnd : function() {
								console.log("第三个已改变");
							},
						}
					});

					// 开始第四个
					mySwiperEndMinute = new Swiper('#end-minute', {
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
								// Swiper初始化了
								console.log("初始化结束分钟");
								addSwiperSlide(this, 60);
								switchSlideToLoop(this, parseInt(0, 10));
								// this.emit('transitionEnd');//在初始化时触发一次transitionEnd事件
							},
							slideChangeTransitionEnd : function() {
								console.log("第四个已改变");
							},
						}
					});

				}
				;

				// 打开夜间时间选择插件
				$("#switchss").click(
						function() {
							type = "switchss";
							console.log("type" + type);

							// 获取页面时间
							getTime($("#humanStartTimeQuantum"),
									$("#humanEndTimeQuantum"));

							// 时间插件滚动到指定位置
							switchSlideToLoop(mySwiper,
									parseInt(startHours, 10));
							switchSlideToLoop(mySwiperStartMinute, parseInt(
									startMinute, 10));
							switchSlideToLoop(mySwiperEndHours, parseInt(
									endHours, 10));
							switchSlideToLoop(mySwiperEndMinute, parseInt(
									endMinute, 10));

							$("#switchTimes").show(500);

						});

				// 打开勿扰时间选择插件
				$("#general").click(
						function() {
							type = "general";
							// 获取页面时间
							console.log("type" + type);
							getTime($("#satrtBotheredTime"),
									$("#endBotheredTime"));

							// 时间插件滚动到指定位置
							switchSlideToLoop(mySwiper,
									parseInt(startHours, 10));
							switchSlideToLoop(mySwiperStartMinute, parseInt(
									startMinute, 10));
							switchSlideToLoop(mySwiperEndHours, parseInt(
									endHours, 10));
							switchSlideToLoop(mySwiperEndMinute, parseInt(
									endMinute, 10));

							$("#switchTimes").show(500);

						});

				// 关闭夜间时间选择插件
				$("#btn_time").click(function() {

					// 拼接时间，并且渲染到指定页面位置
					jointTimeAndRenderer();
					$("#switchTimes").hide();
				});

				/**
				 * 获取页面时间，
				 */
				function getTime(obj1, obj2) {
					startHours = parseInt(obj1.text().substring(0, 2), 10);
					startMinute = parseInt(obj1.text().substring(3, 5), 10);
					endHours = parseInt(obj2.text().substring(0, 2), 10);
					endMinute = parseInt(obj2.text().substring(3, 5), 10);
					console.log("获取页面时间：" + startHours);
				}

				/**
				 * 拼接时间，渲染到指定页面
				 */
				function jointTimeAndRenderer() {

					// 获取当前活动区域的内容
					var tempTime = $(
							"#switchTimes div[class*=swiper-slide-active]")
							.text();
					// 拼接开始时间
					startTime = tempTime.substring(0, 2) + ":"
							+ tempTime.substring(2, 4);
					// 拼接结束时间
					endTime = tempTime.substring(4, 6) + ":"
							+ tempTime.substring(6, 8);

					// 将拼接后的时间渲染到页面
					console.log("type：" + type);
					if (type == 'switchss') {
						console.log("测试:" + startTime + ": " + endTime);
						$("#humanStartTimeQuantum").html(startTime);
						$("#humanEndTimeQuantum").html(endTime);
						settingInfoModule.lupdateNightTime(startTime, endTime);
					} else if (type == 'general') {
						$("#satrtBotheredTime").html(startTime);
						$("#endBotheredTime").html(endTime);
						settingInfoModule.updateBothered(startTime, endTime);
					} else {
					}
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
							var t = '0' + i
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
