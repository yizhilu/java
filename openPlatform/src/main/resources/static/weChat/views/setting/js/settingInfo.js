seajs
		.use(
				[ "jquery", "settingInfoModule", "common", 
						 "layer","honeySwitch","iosSelect" ,"data","layercss" ],
				function($, settingInfoModule,honeySwitch,iosSelect,data, common,layer) {
					
					
					
					     /**
							 * 房间开关
							 */
					     	$.switch({
								  target:'#roomSwitch',
								  callback:function(that){
									  settingInfoModule.updateRoomSwitch();
								  }
								});
					     	
					     	
					     	/**
							 * 夜间模式开关
							 */
							$.switch({
								  target:'#nightSwitch',
								  callback:function(that){
									  settingInfoModule.updateNightSwitch();
								  }
								})


							$('#oneInform').unbind('click').click(function () {

                               $("#multipleInform").removeClass('relative');
								$(this).addClass('relative');
								settingInfoModule.updateRoomInformNumber();
							});
							
							$('#multipleInform').unbind('click').click(function () {

	                               $("#oneInform").removeClass('relative');
									$(this).addClass('relative');
									settingInfoModule.updateRoomInformNumber();
							});
							
							
							
			        // 触摸滑动选择电量
				// batteryInfoModule.battery();
					// 切换开关状态

							settingInfoModule.slides();
					
					// 切换勿扰开关状态
							settingInfoModule.nodisswitch();

				});

			