                                                       define(function(require, exports, module) {
	var $ = require("jquery"), common = require("common"), Sortable = require("Sortable"), commonDate = require("commonDate"), layer = require("layer");
	/**
	 * <pre>
	 * 解绑网关设备
	 * </pre>
	 */
	exports.unBindGateway = function(id,deleteObj) {
		layer.open({
			content : '是否解绑该设备？',
			btn : [ '确定', '取消' ],
			yes : function(index) {
				$.ajax({
					type : "POST",
					url : "/wx/gateway/removeGateway",
					data : {
						"gatewayId" : id
					},
					success : function(result) {
						layer.close(index);
						if (result.responseCode == "_200") {
							deleteObj.remove();
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
	 * <pre>
	 * 用户删除未绑定智能插座
	 * </pre>
	 */
	exports.deleteSocket = function(id,deleteObj) {
		layer.open({
			content : '是否确认删除该智能插座？',
			btn : [ '确定', '取消' ],
			yes : function(index) {
				$.ajax({
					type : "POST",
					url : "/wx/socket/deleteSocket",
					data : {
						"socketId" : id
					},
					success : function(result) {
						layer.close(index);
						if (result.responseCode == "_200") {
							deleteObj.remove();
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
	 * <pre>
	 * 置顶网关设备
	 * </pre>
	 */
	exports.setTopGateway = function(idArray,id) {
		var index = layer.open({
			type : 2,
			shade : false,
			shadeClose : false,
		});
		$.ajax({
			type : "POST",
			url : "/wx/gateway/setTopGateway",
			traditional : true,
			data : {
				ids : idArray
			},
			success : function(result) {
				layer.close(index);
				if (result.responseCode == "_200") {
					$("#dragslot").prepend($("div[id='dragslot'] div[itemId='"+id+"']"))
				} else {
					layer.open({
						content : result.errorMsg,
						time : 3
					});
				}
			}
		});
	}	
	
	/**
	 * 移动智能网关设备位置
	 */
	exports.dragGateway = function() {
		 $( "#dragslot" ).sortable({
			 handle: ".portlet-header" 
		 });
		 $( "#dragslot" ).disableSelection();
		 $("#dragslot").sortable({stop: function(event, ui) {
		  
		  }});
		    $("#dragslot").bind('sortstop', function(event, ui) {
			var item_idarr = '';
			$("div[id='dragslot'] > div").each(function(i) {
				var tempId = $(this).attr("itemId");
				item_idarr += tempId+',';
			});
			// 然后请求后台ajax 这样就完成了拖拽排序		
					$.ajax({
						type : "POST",
						url : "/wx/gateway/dragGateway",
						data : {
							"idstr" : item_idarr
						},
						success : function(result) {
							if (result.responseCode == "_200") {
							}
						}
					});
		  });
	}

	/**
	 * 拖动未绑定的智能插座到需要绑定的智能网关上完成绑定；
	 */
	exports.dragBindSocket = function(gatewayId, mac, unbindScoketElem) {
		$.post("/wx/gateway/scanBindSocket", {
			"gatewayId" : gatewayId,
			"mac" : mac
		}, function(result) {
			if (result.responseCode == "_200") {
				unbindScoketElem.remove();
				// window.location.reload();
			} else {
				unbindScoketElem.show();
				layer.open({
					content : result.errorMsg,
					time : 3
				});
			}
		}, "json");
	}
	/**
	 * 修改智能网关的名字
	 * 
	 */
	exports.setingGatewayName = function(id, gatewayName,oldGatewayName,setNameObj) {
		$.post("/wx/gateway/setingGatewayName",{
			"id" : id,
			"gatewayName" : gatewayName
		}, function(result) {
			if (result.responseCode == "_200") {
			} else {
				setNameObj.val(oldGatewayName);
				layer.open({
					content : result.errorMsg,
					time : 3
				});
			}
		}, "json");
	}
	
	
});
