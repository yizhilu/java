seajs.use([ "jquery", "socketDetailModule", "common", "swiper4", "honeySwitch",
		"layer", "layercss" ], function($, socketDetailModule,
		swiper, layer) {
	var gatewayId = $("#gatewayId").val();// 获取智能网关的ID
	var i = $("#index").val();
	var galleryTop = new Swiper('.gallery-top', {
		initialSlide:Number(i), 
		spaceBetween : 10,
		loop : false,
		centeredSlides : true,
		slidesPerView : 1.2,
		coverflowEffect : {
			rotate : 0,
			stretch : 10,
			depth : 0,
			modifier : 2,
			slideShadows : true,
		},
		on: {
			    slideChangeTransitionEnd: function(){
			    	var socektId = $(".swiper-slide-active div[name='socketInfo']")
			    	.attr("data-id");
			    	$("#lastSocketId").val(socektId);
			}
		}
	});
	var galleryThumbs = new Swiper('.gallery-thumbs', {
		spaceBetween : 10,
		slidesPerView : 1,
		touchRatio : 0.2,
		loopedSlides : 10,
		slideToClickedSlide : true,
	});
	/* 禁止滑动 */
	galleryThumbs.detachEvents();
	/**
	 * 发送开启和关闭电源指令给对应智能网关
	 */
	$.switch({
		  target:'.appleswitch',
		  callback:function(that){
			var socketId = $("#lastSocketId").val();
			var gatewayId = $("#gatewayId").val();
			var className = that.attr('class');
		    if(that.attr('class').indexOf('on')!= -1 ){
		        // 开启
		    	socketDetailModule.sendCommod(socketId, gatewayId,true);
		    }else{
		       // 关闭
		    	socketDetailModule.sendCommod(socketId, gatewayId,false);
		    }
		  }
	 });



	// 点击打开切换层
	$("#swiper_top_area").on("click", "[name='switch_btn']", function() {
		$("#switch_area").show();

	})
	// 关闭切换层
	$("#switch_area").on("click", "[name='switch_area_btn']", function() {
		$("#switch_area").hide();
	})
	// 选中切换层的元素时先取消 里面的样式 再给选中的添加样式
	$("#switch_area").on("click", "[name='socketName']", function() {
		$("#switch_area").find("[name='socketName']").each(function(i) {
			$(this).removeClass("relative");
		});
		$(this).addClass("relative");
		return false;
	})

	// 点空白区域 关闭切换层
	$("#switch_area").on("click", function() {
		$("#switch_area").find("[name='socketName']").each(function(i) {
			if ($(this).hasClass("relative")) {
				// 当前选择元素的下标
				var i = $(this).index();
				// 获取智能设备的Id
				var socektId = $(this).attr("data-id");
				$("#lastSocketId").val(socektId);
				galleryTop.slideToLoop(Number(i),300,false);
				// galleryTop.slideTo(i, 1000, false);
				$("#switch_area").hide();

			}
		});
	})
	// 跳转到实时电表
	$("#show_detail_area").on(
			"click",
			'[name="realTimeMeter_btn"]',
			function() {
				var currentSocketId = $("#lastSocketId").val();
				window.location.href = '/wx/socketStatistics/?socketId='
						+ currentSocketId + '&index=0';

			})
	// 跳转到电量统计
	$("#show_detail_area").on(
			"click",
			'[name="electricity_statistics_btn"]',
			function() {
				var currentSocketId = $("#lastSocketId").val();
				window.location.href = '/wx/socketStatistics/?socketId='
						+ currentSocketId + '&index=1';
			})
	// 跳转到人体探测
	$("#show_detail_area").on(
			"click",
			'[name="human_detection_btn"]',
			function() {
				var currentSocketId = $("#lastSocketId").val();
				window.location.href = '/wx/socketStatistics/?socketId='
						+ currentSocketId + '&index=2';
			})
});
