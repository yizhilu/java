angular
		.module('ylmf.nav', [])
		.controller(
				'navCtrl',
				function($scope, $state, $http, mainUrl, permissions, $cookies,
						$rootScope, $window, $uibModal, permission) {

					// 机构用户是否可以去完善资料
					$scope.toAudit = false;
					if ($window.sessionStorage['userStatus'] === 'STATUS_PENDING') {
						$scope.toAudit = true;
					}

					// 判断显示的二级菜单
					$scope.currentNav = "";
					$rootScope.uid = $cookies.get("UI");
					$scope.selectedNav = function(n) {
						if (n != '' || n != undefined) {
							if (n == $scope.currentNav) {
								$scope.currentNav = '';
							} else {
								$scope.currentNav = n;
							}
						}
						localStorage.clear();
					};
					// 根据地址栏的url设置当前链接的active
					var a = '';
					$scope.hasActive = function(a, b) {
						// console.log(a);
						// console.log(b);
						// console.log(window.location.href);
						// console.log('000000hasactive');
						// a 地址栏的链接地址
						// b 当前页面的url名称，跟state里面的url一致
						var cu_href = a.split('#!/')[1];
						/*
						 * console.log(cu_href); var hasUrl =
						 * cu_href.split('/')[1]; if (hasUrl.length != a) {
						 * console.log('清空'); a = hasUrl; localStorage.clear(); }
						 */
						// 判断当前地址栏是否包含b,这样在进入下一级页面还可以保证菜单栏选中的样式
						if (cu_href.indexOf(b) >= 0) {
							return 'active';
						} else {
							return '';
						}
					};

					/*
					 * window.onunload = function(){ console.log('地址栏地址栏'); };
					 */
					// console.log(window.location.href);
					// 登出
					$scope.loginOut = function() {
						$http({
							method : 'get',
							url : mainUrl + '/v1/security/logout'
						}).then(function(res) {
							console.log(res)
						}, function(err) {
							console.log(err);
						});
						/* 清除用户名 */
						$cookies.remove('UI', '', -1);
						$cookies.remove('UID', '', -1);
						$window.sessionStorage.removeItem('userPermissions');
						$window.sessionStorage.removeItem('nouserPermissions');
						/* 重置权限列表 */
						permissions.restPermissionList();
						$state.go('login');
						// $state.go('login');
					};
					// 验证是否有修改密码权限
					permission
							.verifyPermission("PATCH",
									'/v1/operator/updatePassword',
									'updatePassword', $scope);
					// 修改密码
					$scope.editPsw = function() {

						// $scope.selectedArea = function (s) {
						var meModal = $uibModal.open({
							animation : true, // 动画
							// appendTo:'body', //将模态框添加到某个元素
							ariaDescribedBy : '', // 描述
							ariaLabelledBy : '', // 描述
							backdrop : true, // 背景
							backdropClass : 'modal_bg', // 背景class
							windowClass : 'modal_container', // modal容器class
							templateUrl : 'views/login/editPsw.html',
							size : 'md',
							controller : 'editPswCtrl',
							resolve : {
								// 打开模态框时向模态框传递的数据
								items : function() {
									return '';
								}
							}
						});

						meModal.result.then(function(a) {
							// 保存调用该函数

							// layer.msg('ok');
						}, function(a) {
							// 关闭调用该函数
							// console.log(a)
							// layer.msg('玩命提示中');
						});
						// };

					}

				})
		.controller(
				"editPswCtrl",
				function($http, mainUrl, $uibModalInstance, items, $scope,
						$timeout, $cookies, $state) {

					var userId = $cookies.get("UID");
					var userType = $cookies.get("UIT");

					$scope.info = {
						id : userId,
						oldPassword : '',
						newPassword : ''
					};
					$scope.reset = function() {
						$uibModalInstance.close();
					};

					$scope.savePsw = function() {
						// 管理员修改密码
						$http(
								{
									method : 'PATCH',
									url : mainUrl + '/v1/operator/updatePassword',
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded'
									},
									transformRequest : function(obj) {
										var str = [];
										for ( var s in obj) {
											str
													.push(encodeURIComponent(s)
															+ "="
															+ encodeURIComponent(obj[s]));
										}
										return str.join("&");
									},
									data : $scope.info
								}).then(function(res) {
							console.log(res);
							if (res.data.responseCode == "_200") {
								layer.msg("修改成功，请重新登陆！");
								// $state.go("me.ministrators");
								$uibModalInstance.close();
								$state.go("login");
							} else {
								layer.msg(res.data.errorMsg);
							}
						}, function(err) {
							layer.msg(err.data.message);
						});
					}

				});
