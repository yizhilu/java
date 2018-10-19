angular
		.module('ylmf.editMinistrators', [])
		.controller(
				'editMinistratorsCtrl',
				function($scope, $http, mainUrl, $state, $stateParams) {
					$scope.operatortory = {
						id : $stateParams.id,
						personType : 'ORDINARY_POLICE_OFFICER',
						personTypeName : '',
						password : '',
						userName : '',
						policeId : '',
						password2:'',
						policeSpecies : {
							id : '',
							name : ''
						},
						agency : {
							name : ''
						},
						name : '' // 姓名
					// nickName: '' //昵称
					};
					$scope.id = $stateParams.id;
					// 查询管理员基本信息
					$scope.operatorFind = function() {
						$http({
							method : 'GET',
							url : mainUrl + '/v1/user/getById',
							params : {
								id : $stateParams.id
							},
							async : false
						})
								.then(
										function(res) {
											if (res.data.responseCode == "_200") {
												$scope.operatortory = res.data.data;
												$scope.operatortory.personType = $scope.operatortory.personType
														+ "";
												// 回显警种
												$scope.jstree.id = $scope.operatortory.policeSpecies.id;
												$scope.jstree.name = $scope.operatortory.policeSpecies.name;
												// 回显机构
												$scope.jstre.id = $scope.operatortory.agency.id;
												$scope.jstre.name = $scope.operatortory.agency.name;

											} else {
												layer.msg(res.data.errorMsg);
											}
										}, function(err) {
											layer.msg(err.data.message);
										});
					}
					// 人员类型
					$scope.getClassifications = function(a) {
						$http({
							url : mainUrl + '/v1/user/getAgency',
							method : 'GET',
							async : false,
						}).then(function(res) {
							$scope.fields = res.data.data;
						}, function(err) {
							layer.msg(err.data.message);
						});
					}
					$scope.getClassifications();
					$scope.operatorFind();
					// 选中id jstree配置---警种
					$scope.jstree = {
						id : "",
						name : "",
						dataUrl : mainUrl + "/v1/mapTree/getJsTree?t="
								+ new Date().getTime(),
						whetherJsTree : false,
						"types" : {
							'default' : {
								icon : 'blue fas fa-user-circle'
							}
						},
						plugins : [ 'types' ]
					};
					$scope.whetherJs = false;
					// 隐藏部门
					$scope.hideJs = function() {
						$scope.whetherJs = false;
					}
					// 显示部门
					$scope.showJs = function(events) {
						var parentObj = $(events.target);
						var width = $(events.target).width();
						var position = $(events.target).position();
						angular.element('#tree').css("min-width", width + "px")
								.css("left", position.left + "px");
						$scope.whetherJs = true;
					}

					$scope.show = function() {
						console.log($scope.whetherJs)
					}
					// 选中id jstree配置---机构
					$scope.jstre = {
						id : "",
						name : "",
						dataUrl : mainUrl + "/v1/agency/getJsTree?t="
								+ new Date().getTime(),
						whetherJsTre : false,
						"types" : {
							'default' : {
								icon : 'blue glyphicon glyphicon-book'
							},
							'department' : {
								icon : 'green far fa-building'
							},
							'unit' : {
								icon : 'green fas fa-university'
							}
						},
						plugins : [ 'types' ]
					};
					$scope.whetherJsTree = false;
					// 隐藏部门
					$scope.hideJsTree = function() {
						$scope.whetherJsTree = false;
					}
					// 显示部门
					$scope.showJsTree = function(event) {
						var parentObj = $(event.target);
						var width = $(event.target).width();
						var position = $(event.target).position();
						angular.element('#treeDiv').css("min-width",
								width + "px").css("left", position.left + "px");
						$scope.whetherJsTree = true;
					}

					$scope.editMinistrators = function() {
						// 转换人员类型
						personType = $scope.operatortory.personType;
						$http({
							method : 'PATCH',
							url : mainUrl + '/v1/user/modifyUser',
							data : {
								id : $scope.operatortory.id,
								userName : $scope.operatortory.userName,
								password : $scope.operatortory.password2,
								policeId : $scope.operatortory.policeId,
								policeSpecies : {
									id : $scope.jstree.id,
									name : $scope.jstree.name
								},
								agency : {
									id : $scope.jstre.id,
									name : $scope.jstre.name
								},
								personType : personType
							}
						}).then(function(res) {
							if (res.data.responseCode == "_200") {
								layer.msg("修改成功！");
								$state.go("me.ministrators");
							} else {
								layer.msg(res.data.errorMsg);
							}
						}, function(err) {
							layer.msg(err.data.message);
						})
					}
				});
