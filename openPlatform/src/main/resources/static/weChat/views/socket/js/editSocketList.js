
seajs.use([ "jquery", "editSocketListModule", "common","layer","jquery-ui","jquery-ui-touch","jquery-uicss", "layercss" ],
		function($, socket, common, layer) {
			/**
			 * 修改智能设备的名称
			 */
	
		$("#socket_area").on(
					"click",
					"[name='set_name']",
					function() {
						var oldSocketName = $(this).val();
						var scoketId = $(this).attr("data-name-id");
						var gatewayId = $(this).attr("data-gateway-id");

						$(this).blur(
								function() {
									var newSocketName = socket
											.removeStringSpaceCharacter($(this)
													.val());
									if (common.isNull(newSocketName)
											|| newSocketName == oldSocketName
											|| newSocketName.length <= 0) {
										$(this).val(oldSocketName);
										return;
									} else {

										if (newSocketName.length > 64) {
											layer.open({
												content : "输入名称长度为1-64个字符 ",
												time : 1.5
											});
										} else {
											$(this).val(newSocketName);
											socket.setSockeName(scoketId,newSocketName,oldSocketName,$(this));
										}
									}
								});
					});

			/**
			 * 解绑智能插座
			 */
			$("div[id='socket_area']").on(
					"click",
					"i[name='unbind_btn']",
					function() {
						var currentId = $(this).attr("data-id");
						socket.unBindSocket(currentId,
								$("div[id='socket_area'] div[itemId='"
										+ currentId + "']"));
					});

			/**
			 * 点击置顶
			 * 
			 * @returns
			 */

			$("#socket_area").on("click", "[name='top_btn']", function() {
				var currentId = $(this).attr("data-id");
				var gatewayId = $(this).attr("data-gateway-id");
				if (common.isNull(currentId) || common.isNull(gatewayId)) {
					return;
				} else {
					socket.setTop(currentId, gatewayId);
				}
			});
			//
			socket.dragSocket();

		});
