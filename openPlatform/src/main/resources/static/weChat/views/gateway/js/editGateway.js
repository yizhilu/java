seajs.use([ "jquery", "editGatewayModule", "common", "layer", "editSocketListModule","Sortable",
		"jquery-ui", "jquery-ui-touch", "jquery-uicss", "layercss" ], function(
		$, editGatewayModule, common, layer,editSocketListModule) {

	editGatewayModule.dragGateway();
	/**
	 * 
	 */

	// 解绑智能开关
	$("div[id='dragslot']").on("click", "i[name='unbind_btn']", function() {
		var currentId = $(this).attr("data-id");
		editGatewayModule.unBindGateway(currentId,$("div[id='dragslot'] div[itemId='"
				+ currentId + "']"));
	});
	
	// 删除未绑定的智能插座
	$("div[id='unbindScoket']").on("click", "i[name='btn']", function() {
		var currentId = $(this).attr("data-id");
		editGatewayModule.deleteSocket(currentId,$("div[id='unbindScoket'] div[itemId='"
				+ currentId + "']"));
	});
	// 点击置顶
	$("div[id='dragslot']").on("click", "i[name='topping_btn']", function() {
		// 先获取以前的 排序顺序
		// 然后判断以前的元素是否是大于1个的否则 不进行置顶
		// 再移除选中置顶的元素 把它加入到头部。
		var idArray = [];
		$("div[id='dragslot'] > div").each(function(i) {
			var tempId = $(this).attr("itemId");
			idArray.push(tempId);
		});
		if (idArray.length <= 1) {
			return;
		}
		var currentId = $(this).attr("data-id");
		var index = idArray.indexOf(currentId);
		if (index > -1) {
			idArray.splice(index, 1);
		} else {
			return;
		}
		idArray.unshift(currentId);
		editGatewayModule.setTopGateway(idArray,currentId);
	});

	/**
	 * 修改智能设备的名字
	 */


	$("#dragslot").on("click","[name='set_gateway']",function() {
				var oldGatewayName = $(this).val();
				//$(this).val("").focus().val(oldGatewayName);
				var gatewayId = $(this).attr("data-gateway-id");
				$(this).blur(
						function() {
							var newGatewayName = $.trim($(this).val());
							if (common.isNull(newGatewayName)|| newGatewayName == oldGatewayName|| newGatewayName.length <= 0) {
								$(this).val(oldGatewayName);
								return;
							} else {
								if (newGatewayName.length > 64) {
									layer.open({
										content : "输入名称长度为1-64个字符 ",
										time : 1.5
									});
								} else {
									$(this).val(newGatewayName);
									editGatewayModule.setingGatewayName(gatewayId,newGatewayName,oldGatewayName,$(this));
								}
							}
						});
			        });
	
	/**
	 * 修改未绑定智能插座名字
	 * 
	 */
	

	$("#unbindScoket").on("click","input[name='set_unBindSocket']",function(){
		var oldSocketName=$(this).val();
//		//$(this).val("").focus().val(oldSocketName);
 	    var  socketId=$(this).attr("data-id");
	console.log(oldSocketName+" : "+socketId);
	     $(this).blur(
			function() {
				var newSocketName = $.trim($(this).val());
		
				console.log(newSocketName);
				if (common.isNull(newSocketName)|| newSocketName == oldSocketName|| newSocketName.length <= 0) {
					$(this).val(oldSocketName);
					return false;
				} else {
					if (newSocketName.length > 64) {
						layer.open({
							content : "输入名称长度为1-64个字符 ",
							time : 1.5
						});
					} else {
						$(this).val(newSocketName);
						editSocketListModule.setSockeName(socketId,newSocketName,oldSocketName,$(this));
					}
					return false;
				}
			});
	     return false;
	    });
	// 绑定未绑定的智能插座
	$("#unbindScoket").find("[name='draggable_item']").draggable({
		revert : "invalid",handle: ".handle"
	});
	$("#dragslot").find("[name='gateway_item']").droppable({
		drop : function(event, ui) {
			var gatewayElem = $(this);
			var unbindScoketElem = ui.draggable;
			var gatewayId = gatewayElem.attr("itemId");
			var scoketMac = unbindScoketElem.attr("data-serial-number");
			if(!common.isNull(gatewayId)&&!common.isNull(scoketMac)){
				unbindScoketElem.hide();
				editGatewayModule.dragBindSocket(gatewayId,scoketMac,unbindScoketElem);				
			}
		}
	});
	});