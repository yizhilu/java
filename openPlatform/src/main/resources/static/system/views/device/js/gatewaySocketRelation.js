angular
		.module('ylmf.gateway', [])
		.controller(
				'gatewaySocketRelationCtrl',
				function($scope, $http, $uibModalInstance, mainUrl, permission,
						gatewayId, currentSocketId) {
					$scope.close = function() {
						$uibModalInstance.close();
					};
					$scope.echartOption = {
						title : {
							text : '网关关系图谱'
						},
						tooltip : {
							formatter : function(x) {
								return "绑定";
							}
						},
					};
					$scope.showEchart = false;
					$http({
						method : 'GET',
						url : mainUrl + '/v1/gateway/findSocketByGatewayId',
						params : {
							gatewayId : gatewayId,
							socketId : currentSocketId
						}
					})
							.then(
									function(res) {
										if (res.data.responseCode == '_200') {
											var gateway = res.data.data.gateway;
											var allSockets = res.data.data.allSockets;
											var data = [];
											var links = [];
											data.push({
												name : gateway.gatewayName,
												desc : '',
												symbolSize : 100,
												itemStyle : {
													normal : {
														color : 'red'
													}
												}
											});
											for (var i = 0; i < allSockets.length; i++) {
												var tempData = allSockets[i];
												if (tempData.id == currentSocketId) {
													data
															.push({
																name : tempData.socketName,
																desc : tempData.socketName
																		+ '于'
																		+ new Date(
																				tempData.bindTime)
																				.Format("yyyy-MM-dd hh:mm:ss")
																		+ '绑定'
																		+ gateway.gatewayName,
																symbolSize : 100,
															    itemStyle: {
															        normal: {
															            color: 'green'
															        }
															    }
															});
												} else {
													data
															.push({
																name : tempData.socketName,
																desc : tempData.socketName
																		+ '于'
																		+ new Date(
																				tempData.bindTime)
																				.Format("yyyy-MM-dd hh:mm:ss")
																		+ '绑定'
																		+ gateway.gatewayName,
																symbolSize : 100
															});
												}
												links
														.push({
															source : tempData.socketName,
															target : gateway.gatewayName,
															name : '绑定',
															desc : tempData.socketName
																	+ '于'
																	+ new Date(
																			tempData.bindTime)
																			.Format("yyyy-MM-dd hh:mm:ss")
																	+ '绑定'
																	+ gateway.gatewayName,
														});
											}
											$scope.echartOption = {
												title : {
													text : gateway.gatewayName
															+ '关系图谱'
												},
												tooltip : {
													formatter : function(x) {
														return x.data.desc;
													}
												},
												series : [ {
													type : 'graph',
													layout : 'force',
													symbolSize : 80,
													roam : true,
													edgeSymbol : [ 'circle',
															'arrow' ],
													edgeSymbolSize : [ 4, 10 ],
													edgeLabel : {
														normal : {
															textStyle : {
																fontSize : 20
															}
														}
													},
													force : {
														repulsion : 2500,
														edgeLength : [ 10, 50 ]
													},
													draggable : true,
													itemStyle : {
														normal : {
															color : '#4b565b'
														}
													},
													lineStyle : {
														normal : {
															width : 2,
															color : '#4b565b'

														}
													},
													edgeLabel : {
														normal : {
															show : true,
															formatter : function(
																	x) {
																return x.data.name;
															}
														}
													},
													label : {
														normal : {
															show : true,
															textStyle : {}
														}
													},
													data : data,
													links : links
												} ]
											};
											$scope.showEchart = true;
										} else {
											layer.msg(res.data.errorMsg);
										}
									}, function(err) {
										if (err.data) {
											layer.msg(err.data.message);
										}
									})
				})