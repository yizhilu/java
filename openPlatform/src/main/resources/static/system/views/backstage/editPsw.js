angular.module("editPsw", []).controller("editPswCtrl",
		function($scope, $http, mainUrl, $stateParams, $state) {
			$scope.note = $stateParams.note;
			$scope.info = {
				id : $stateParams.id,
				oldPassword : $stateParams.oldPassword,
				newPassword : $stateParams.newPassword
			};
			console.log($scope.note + "0.0");
			$scope.savePsw = function() {
				// 管理员修改密码
				if ($stateParams.note == 'minis') {
					console.log($scope.info + "0.0");
					$http({
						method : 'PATCH',
						url : mainUrl + '/v1/user/updatePassword',
						params : $scope.info
					}).then(function(res) {
						console.log(res);
						if (res.data.responseCode == "_200") {
							layer.msg("修改成功！");
							$state.go("me.ministrators");
						} else {
							layer.msg(res.data.errorMsg);
						}
					}, function(err) {
						layer.msg(err.data.message);
					});
				} else {// 机构管理员修改密码
					console.log($scope.info + "0.0");
					$http({
						method : 'PATCH',
						url : mainUrl + '/v1/user/updatePassword',
						params : $scope.info
					}).then(function(res) {
						console.log(res);
						if (res.data.responseCode == "_200") {
							layer.msg("修改成功！");
							$state.go("me.ministrators");
						} else {
							layer.msg(res.data.errorMsg);
						}
					}, function(err) {
						layer.msg(err.data.message);
					});
				}
			}
		});