angular.module('ylmf.editRole', [])
    .controller('editRoleCtrl', function ($scope, $http, mainUrl, $state, $stateParams, $cookies) {

        // var id = $stateParams.id;

        $scope.roleInfo = {
            id: $stateParams.id,
            "comment": "",    //备注
            "name": ""
        };

        //获取角色
        $scope.getRoleInfo = function () {
            $http({
                method: 'GET',
                url: mainUrl + '/v1/roles/getById',
                params: {roleId: $stateParams.id}
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == "_200") {
                    $scope.roleInfoAll = res.data.data;
                    $scope.roleInfo.comment = res.data.data.comment;
                    $scope.roleInfo.name = res.data.data.name;
                }
            }, function (err) {
                console.log(err);
                layer.msg(err.data.message);
            })
        };
        $scope.getRoleInfo();


        //角色绑定的功能列表。（修改之前）
        $scope.beforeModify = {};
        //获取角色绑定的功能列表
        $scope.getRolePower = function () {
            $http({
                method: 'GET',
                url: mainUrl + '/v1/competence/findByRoleId',
                params: {roleId: $stateParams.id}
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == "_200") {
                    for (var i = 0, l1 = $scope.competences.length; i < l1; i++) {
                        for (var n = 0, l2 = res.data.data.length; n < l2; n++) {
                            if ($scope.competences[i].id == res.data.data[n].id) {
                                $scope.competences[i].hasChecked = true;
                                //将修改前的绑定的信息存起来
                                $scope.beforeModify[$scope.competences[i].id] = $scope.competences[i].id;
                                break;
                            }
                        }
                    }
                }
                $scope.getGroups($scope.competences);
            }, function (err) {
                console.log(err);
                layer.msg(err.data.message);
            })
        };
        // $scope.getRolePower();


        //获取所有功能列表
        $scope.getAllPower = function () {
            $http({
                method: 'GET',
                url: mainUrl + '/v1/competence/getByAllNoPage',
                data: {}
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == '_200') {
                    $scope.competences = res.data.data;
                    $scope.competences.forEach(function (item, index) {
                        item.hasChecked = false;
                    });
                    $scope.getRolePower($stateParams.id);
                } else {
                    layer.msg(res.data.errorMsg);
                }
            }, function (err) {
                if (err.data) {
                    layer.msg(err.data.message);
                }
            })
        };
        $scope.getAllPower();

        //角色和功能绑定（一对多绑定）
        $scope.roleCompetences = {
            roleId: '',
            competenceIds: []
        };

        //分组
        $scope.getGroups = function (res) {
            console.log(res);
            var cache = {};
            $scope.groups = {};
            res.forEach(function (item, index) {
                var group = (item.resource.split('/'))[2];
                item.flag = group + 'FLAG';
                if (!cache[group]) {
                    $scope.groups[group] = {id: group, hasChecked: true};
                    cache[group] = 1;
                }
                if (cache[group] && !item.hasChecked) {
                    $scope.groups[group].hasChecked = false;
                }
            });
            console.log($scope.groups);
        };

        //全选和取消全选
        $scope.checkAll = function (resource, flag) {
            $scope.competences.forEach(function (item, index) {
                if ((item.resource.split('/'))[2] === resource) {
                    item.hasChecked = flag;
                }
            })
        };

        //单击某个功能时
        $scope.singleClick = function (resource) {
            var resource = (resource.split('/'))[2];
            //初始化分组的组长（暂时这样么称呼）hasChecked为true，然后遍历所有功能，
            //分组下的某一个的hasChecked为false时，组长的hasChecked置为false
            $scope.groups[resource].hasChecked = true;
            /*$scope.competences.forEach(function (item, index) {
             if ((item.resource.split('/')[2]) === resource && item.hasChecked === false) {
             $scope.groups[resource].hasChecked = false;
             }
             })*/
            for (var i = 0, l = $scope.competences.length; i < l; i++) {
                if (($scope.competences[i].resource.split('/')[2]) === resource && $scope.competences[i].hasChecked === false) {
                    $scope.groups[resource].hasChecked = false;
                    break;
                }
            }
        };

        //角色绑定和解绑功能的参数
        $scope.roleCompetencesBind = {
            roleId: $stateParams.id,
            competenceIds: []
        };
        $scope.roleCompetencesUnbind = {
            roleId: $stateParams.id,
            competenceIds: []
        };

        //修改角色信息，包括comment和绑定的功能
        $scope.editRole = function () {
            //清空绑定和解绑的参数
            $scope.roleCompetencesBind.competenceIds = [];
            $scope.roleCompetencesUnbind.competenceIds = [];

            //遍历用户勾选和未勾选的功能分别放到不同的参数里面
            $scope.competences.forEach(function (item, index) {
                if (item.hasChecked) {
                    if (!$scope.beforeModify[item.id])
                        $scope.roleCompetencesBind.competenceIds.push(item.id);
                } else {
                    if ($scope.beforeModify[item.id])
                        $scope.roleCompetencesUnbind.competenceIds.push(item.id);
                }
            });

            var hasSuccess = false;

            //基本信息是否修改过
            if ($scope.roleInfo.comment != $scope.roleInfoAll.comment) {
                $http({
                    method: 'PATCH',
                    url: mainUrl + '/v1/roles/',
                    data: $scope.roleInfo
                }).then(function (res) {
                    console.log(res);
                    if (res.data.responseCode = "_200") {
                        layer.msg("基本信息修改成功！");
                        $state.go('me.roles')
                    } else {
                        layer.msg("基本信息修改失败！");
                    }
                }, function (err) {
                    console.log(err);
                    if (err.status === 403) {
                        layer.msg('该角色不能修改描述信息。')
                    }
                })
            }
            //绑定的功能集合
            if ($scope.roleCompetencesBind.competenceIds.length != 0) {
                $http({
                    method: 'post',
                    url: mainUrl + '/v1/security/bindRoleForCompetences',
                    params: $scope.roleCompetencesBind
                }).then(function (res) {
                    console.log(res);
                    if (res.data.responseCode = "_200") {
                        layer.msg("权限绑定成功！");
                        $state.go('me.roles')
                    } else {
                        layer.msg("权限绑定失败！");
                    }
                }, function (err) {
                    console.log(err);
                    layer.msg(err.data.message);
                })
            }
            //未绑定的功能集合
            if ($scope.roleCompetencesUnbind.competenceIds.length != 0) {
                $http({
                    method: 'post',
                    url: mainUrl + '/v1/security/unbindRoleForCompetences',
                    params: $scope.roleCompetencesUnbind
                }).then(function (res) {
                    console.log(res);
                    if (res.data.responseCode = "_200") {
                        layer.msg("权限解绑成功！");
                        $state.go('me.roles')
                    } else {
                        layer.msg("权限解绑失败！");
                    }
                }, function (err) {
                    console.log(err);
                    layer.msg(err.data.message);
                })
            }

        }


    });