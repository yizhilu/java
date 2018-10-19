angular
		.module('ylmf.gateway', [])
		.controller(
				'gatewayListCtrl',
				function($scope, $http, $uibModal, mainUrl, permission) {
					var cpi = angular.fromJson(localStorage
							.getItem('currentPageInfo'));

					$scope.searchInfo = {
						mac : '',
						status : '', // 状态
						page : 0,
						gatewayName : ''
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
							mac : cpi.mac,
							status : cpi.status,
							page : 1 + cpi.page,
							gatewayName : cpi.gatewayName
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
							url : mainUrl + '/v1/gateway/getByConditions',
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

					$scope.resetSearchInfo = function() {
						$scope.searchInfo = {
							mac : '',
							status : '', // 状态
							page : 0,
							gatewayName : ''
						};
						localStorage.removeItem("currentPageInfo");
						$scope.gatDataLists('0');
					}
					// 修改密码
					$scope.openRelation = function(gatewayId, currentSocketId) {
						var meModal = $uibModal
								.open({
									animation : true, // 动画
									// appendTo:'body', //将模态框添加到某个元素
									ariaDescribedBy : '', // 描述
									ariaLabelledBy : '', // 描述
									backdrop : true, // 背景
									backdropClass : 'modal_bg', // 背景class
									windowClass : 'modal_container', // modal容器class
									templateUrl : 'views/device/gatewaySocketRelation.html',
									size : 'md',
									controller : 'gatewaySocketRelationCtrl',
									resolve : {
										// 打开模态框时向模态框传递的数据
										gatewayId : function() {
											return gatewayId;
										},
										currentSocketId : function() {
											return currentSocketId;
										},
										loadPlugIn : [
												'$ocLazyLoad',
												function($ocLazyLoad) {
													return $ocLazyLoad
															.load([ 'views/device/js/gatewaySocketRelation.js' ])
												} ]
									}
								});
						meModal.result.then(function(a) {
						}, function(a) {
						});

					}
					$scope.openQrcode = function(mac) {
						var meModal = $uibModal
								.open({
									animation : true, // 动画
									// appendTo:'body', //将模态框添加到某个元素
									ariaDescribedBy : '', // 描述
									ariaLabelledBy : '', // 描述
									backdrop : true, // 背景
									backdropClass : 'modal_bg', // 背景class
									windowClass : 'modal_container', // modal容器class
									templateUrl : 'views/device/gatewaySocketQrcode.html',
									size : 'md',
									controller : 'gatewaySocketQrcodeCtrl',
									resolve : {
										// 打开模态框时向模态框传递的数据
										mac : function() {
											return mac;
										},
										type : function() {
											return "gateway";
										},
										loadPlugIn : [
												'$ocLazyLoad',
												function($ocLazyLoad) {
													return $ocLazyLoad
															.load([ 'views/device/js/gatewaySocketQrcode.js' ])
												} ]
									}
								});
						meModal.result.then(function(a) {
						}, function(a) {
						});

					}
				})