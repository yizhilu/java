define(function(require, exports, module) {
	var $ = require("jquery"), common = require("common"), Sortable = require("Sortable"), commonDate = require("commonDate"), layer = require("layer");
	/**
	 * <pre>
	 * 获取所有智能插座@param showListDivId 克隆后加入区域的id
	 * @param options ajax 请求的参数
	 * </pre>
	 */

	exports.setTop = function(id, gatewayId) {
		$.post("/wx/socket/setTopSocket", {
			"id" : id,
			"gatewayId" : gatewayId
		}, function(result) {
			if (result.responseCode == "_200") {
//				window.location.href = "/wx/socket/findAllByGatewayId?type=edit&id="
//						+ gatewayId;
				$("#socket_area").prepend($("div[id='socket_area'] div[itemId='"+id+"']"))
			} else {
				layer.open({
					content : result.errorMsg,
					time : 3
				})
			}
		}, "json");
	}

	exports.setSockeName = function(id, newSocketName,oldSocketName,setNameObj) {
		$.post("/wx/socket/setSocketName", {
			id : id,
			name : newSocketName,
		}, function(result) {
			if (result.responseCode == "_200") {
				
			} else {
				setNameObj.val(oldSocketName);
				layer.open({
					content : result.errorMsg,
					time : 3
				})
			}
		}, "json");

	}
	exports.removeStringSpaceCharacter = function(str) {
		return $.trim(str);
	}
	
	
	/**
	 * <pre>
	 * 解绑智能插座
	 * </pre>
	 */
	exports.unBindSocket = function(id,deleteObj) {
		layer.open({
			content : '是否解绑该智能插座?',
			btn : [ '确定', '取消' ],
			yes : function(index) {
				$.ajax({
					type : "POST",
					url : "/wx/socket/unBindSocket",
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
	 * 移动智能插座位置
	 */
	exports.dragSocket = function() {
		 $( "#socket_area" ).sortable({
			 handle: ".portlet-header" 
		 });
		 $( "#socket_area" ).disableSelection();
		 $("#socket_area").sortable({stop: function(event, ui) {
		  
		  }});
		    $("#socket_area").bind('sortstop', function(event, ui) {
			var item_idarr = '';
			$("div[id='socket_area'] > div").each(function(i) {
				var tempId = $(this).attr("itemId");
				item_idarr += tempId+',';
			});
			// 然后请求后台ajax 这样就完成了拖拽排序		
					$.ajax({
						type : "POST",
						url : "/wx/socket/dragSocket",
						data : {
							"idstr" : item_idarr
						},
						success : function(result) {
							if (result.responseCode == "_200") {
								console.log(99);
							}
						}
					});
		  });	
	}
	
	
	
});
