angular.module('ylmf.addMinistrators', []).controller(
		'addMinistratorsCtrl',
		function($scope, $http, mainUrl, $state) {

			// 所有角色
			$scope.roles = {};

			$scope.ministrators = {
				account : '', // 账号
				name : '', // 姓名
			};

			$scope.canCreate = true;
			$scope.create = function() {
				if (!$scope.canCreate) {
					return false;
				}
				var roles = $scope.roles;
				var rolesStr = '';

				for ( const key in roles) {
					if (roles[key].isChecked) {
						if ('' != rolesStr) {
							rolesStr = rolesStr + ",";
						}
						rolesStr = rolesStr + roles[key].name;
					}
				}
				$scope.canCreate = false;

				$http({
					method : 'POST',
					url : mainUrl + '/v1/operator/createAndSetRole?roleName=' + rolesStr,
					data : {
						account : $scope.ministrators.account,
						name : $scope.ministrators.userName
					}
				}).then(function(res) {
					if (res.data.responseCode == "_200") {
						layer.msg("创建成功！");
						$state.go("me.ministrators");
					} else {
						layer.msg(res.data.errorMsg);
					}
					$scope.canCreate = true;
				}, function(err) {
					layer.msg(err.data.message);
					$scope.canCreate = true;
				});
			}
			// 只能分配自己有的角色给别人
			$scope.getMyRoles = function() {
				// 获取自己拥有的所有角色
				$http({
					method : 'GET',
					url : mainUrl + '/v1/roles/getRolesByOperator',
				}).then(
						function(res) {
							if (res.data.responseCode == "_200"
									&& res.data.data.length) {
								DATA = res.data.data;
								DATA.forEach(function(item, index) {
									// 页面赋值
									$scope.roles[item.id] = item;
									$scope.roles[item.id].isChecked = false;
								});
							} else {
								layer.msg(res.data.errorMsg);
							}
						}, function(err) {
							layer.msg(err.data.message);
						});
			}
			$scope.getMyRoles();
		});