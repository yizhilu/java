angular
		.module('ylmf.ministrators', [])
		.controller(
				'ministratorsCtrl',
				function($scope, $http, $uibModal, mainUrl, permission) {
					// 验证是否有禁用启用权限
					permission.verifyPermission("PATCH",
							'/v1/operator/disableOrUsable', 'disableOrUsable',
							$scope);
					// 验证是否有修改权限
					permission.verifyPermission("PATCH", '/v1/operator/',
							'modifyUser', $scope);
					// 验证是否有绑定角色权限
					permission.verifyPermission("POST",
							'/v1/security/bindRoleForUser', 'bindRoleForUser',
							$scope);
					// 验证是否有重置密码权限
					permission.verifyPermission("PATCH",
							'/v1/operator/resetPassword', 'resetPassword',
							$scope);
					var cpi = angular.fromJson(localStorage
							.getItem('currentPageInfo'));
					$scope.datepickerOptions = {
						maxDate : $scope.DateDayEnd,
						minMode : 'day'
					}

					$scope.resetPassword = function(resetId) {
						// if ($scope.hasResetPasswordPermission) {
						var text = '重置密码'
						layer.confirm('您确定要' + text + '?', {
							icon : 3,
							title : '提示'
						}, function(index) {

							$http({
								method : 'PATCH',
								url : '/v1/operator/resetPassword',
								params : {
									id : resetId
								}
							}).then(function(res) {
								console.log(res);
								layer.closeAll();
								if (res.data.responseCode == '_200') {
									layer.msg(text + '成功！');
								} else {
									layer.msg(text + '失败！');
								}
							}, function(err) {
								layer.msg(text + '失败！');
							});
						});
						// } else {
						// layer.msg('没有权限重置密码');
						// }
					}

					$scope.searchInfo = {
						account : '', // 账号
						name : '',
						status : '', // 状态
						page : 0,
						phone : ''
					};
					// 分页组件的参数
					$scope.pageParams = {
						maxSize : 5,
						totalItems : 0,
						currentPage : 1,
						itemsPerPage : 15
					};

					if (cpi != null) {
						$scope.searchInfo = {
							account : cpi.account,
							status : cpi.status,
							name : cpi.name,
							page : 1 + cpi.page,
							phone : cpi.phone
						};
						$scope.pageParams.currentPage = 1 + cpi.page;
					}

					// 获取数据
					$scope.gatDataLists = function(a) {
						if (a == 0) {
							$scope.pageParams.currentPage = 1;
						}
						$scope.searchInfo.page = $scope.pageParams.currentPage - 1;
						localStorage.setItem('currentPageInfo', angular
								.toJson($scope.searchInfo));
						$http({
							url : mainUrl + '/v1/operator/getByCondition',
							method : 'GET',
							params : $scope.searchInfo
						})
								.then(
										function(res) {
											if (res.data.responseCode == '_200') {
												$scope.dataLists = res.data.data.content;
												$scope.pageParams.totalItems = res.data.data.totalElements;
												$scope.pageParams.itemsPerPage = res.data.data.size;
												$scope.pageParams.totalPages = res.data.data.totalPages
											} else {
												layer.msg(res.data.errorMsg);
											}
										}, function(err) {
											layer.msg(err.data.message);
										});
					};
					$scope.gatDataLists();

					// 启用禁用
					$scope.hasDisable = function(item, isDis) {
						// isDis:false:启用；true：禁用
						var text = isDis ? '禁用' : '恢复';
						layer
								.confirm(
										'是否' + text + '该用户登录?',
										{
											icon : 3,
											title : '提示'
										},
										function(index) {
											$http(
													{
														url : mainUrl
																+ '/v1/operator/disableOrUsable',
														method : 'PATCH',
														params : {
															id : item.id,
															flag : !isDis
														}
													})
													.then(
															function(res) {
																console
																		.log(res);
																layer
																		.closeAll();
																if (res.data.responseCode == '_200') {
																	layer
																			.msg(text
																					+ '成功！');
																	isDis ? item.useStatus = "禁用"
																			: item.useStatus = "STATUS_NORMAL";
																} else {
																	layer
																			.msg(text
																					+ '失败！');
																}
															},
															function(err) {
																layer
																		.msg(text
																				+ '失败！');
															});
										})
					};

					$scope.resetSearchInfo = function() {
						$scope.searchInfo = {
							account : '', // 账号
							name : '',
							status : '', // 状态
							page : 0,
							phone : ''
						};
						localStorage.removeItem("currentPageInfo");
						$scope.gatDataLists('0');
					}
				})