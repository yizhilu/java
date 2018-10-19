angular
		.module('ylmf.users', [])
		.controller(
				'userListCtrl',
				function($scope, $http, $uibModal, mainUrl, permission) {
					var cpi = angular.fromJson(localStorage
							.getItem('currentPageInfo'));

					$scope.searchInfo = {
						nickName : '',
						status : '', // 状态
						page : 0,
						telephone : ''
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
							nickName : cpi.nickName,
							status : cpi.status,
							page : 1 + cpi.page,
							telephone : cpi.telephone
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
							url : mainUrl + '/v1/user/getByConditions',
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
							nickName : '',
							status : '', // 状态
							page : 0,
							telephone : ''
						};
						localStorage.removeItem("currentPageInfo");
						$scope.gatDataLists('0');
					}
				})