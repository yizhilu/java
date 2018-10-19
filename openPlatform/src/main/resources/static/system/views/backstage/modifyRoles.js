angular.module('ylmf.modifyRoles', []).controller(
		'modifyRolesCtrl',
		function($scope, $http, mainUrl, $state, $stateParams) {
			// 所有角色
			$scope.roles = {};
			// 已绑定的角色
			$scope.bindRoles = {};
			// 获取用户已绑定角色
			$scope.getBindRoles = function() {
				$http({
					method : 'GET',
					url : '/v1/roles/getRolesByOperator',
					params : {
						operatorId : $stateParams.id
					}
				}).then(
						function(res) {
							if (res.data.responseCode === '_200'
									&& res.data.data.length) {
								res.data.data.forEach(function(item, idnex) {
									if ($scope.roles[item.id]) {
										$scope.roles[item.id].isChecked = true;
										$scope.bindRoles[item.id] = item.id;
									}
								})
							}
							if (res.data.responseCode !== '_200') {
								layer.msg(res.data.errorMsg);
							}

						}, function(err) {
							layer.msg(err.message);
						})
			}
			// 只能分配自己有的角色给别人
			$scope.getMyRoles = function() {
				// 获取自己拥有的所有角色
				$http({
					method : 'GET',
					url : mainUrl + '/v1/roles/getRolesByOperator',
					params : {}
				}).then(
						function(res) {
							if (res.data.responseCode == "_200"
									&& res.data.data.length) {
								DATA = res.data.data;
								DATA.forEach(function(item, index) {
									$scope.roles[item.id] = item;
									$scope.roles[item.id].isChecked = false;
								});
								$scope.getBindRoles();
							} else {
								layer.msg(res.data.errorMsg);
							}
						}, function(err) {
							layer.msg(err.data.message);
						});
			}

			$scope.getMyRoles();
			$scope.modifyRoles = function() {
				var bindRoles = [];
				var unbindRoles = [];
				var roles = $scope.roles;
				for ( const key in roles) {
					// 拒绝重复绑定
					if (roles.hasOwnProperty(key) && roles[key].isChecked
							&& !$scope.bindRoles.hasOwnProperty(key)) {
						bindRoles.push(roles[key].id);
					}

					// 解绑角色
					if ($scope.bindRoles.hasOwnProperty(key)
							&& !roles[key].isChecked) {
						unbindRoles.push(roles[key].id);
					}

				}
				$http({
					method : 'POST',
					url : '/v1/security/bindRolesForOperator',
					params : {
						roleIds : bindRoles,
						userId : $stateParams.id
					}
				}).then(function(res) {
					console.log(res);
					if (res.status == 200) {
						layer.msg("修改角色成功！");
						$state.go("me.ministrators");
					} else {
						layer.msg(res.data.errorMsg);
					}
				}, function(err) {
					layer.msg(err.data.message);
				})
				$http({
					method : 'POST',
					url : '/v1/security/unbindRolesForOperator',
					params : {
						roleIds : unbindRoles,
						userId : $stateParams.id
					}
				}).then(function(res) {
					console.log(res);
					if (res.status == 200) {
						if (res.data.responseCode != "_200") {
							layer.msg(res.data.errorMsg)
						} else {
							layer.msg("修改角色成功！");
							$state.go("me.ministrators");
						}
					} else {
						layer.msg(res.data.errorMsg);
					}
				}, function(err) {
					layer.msg(err.data.message);
				})
			}
		});