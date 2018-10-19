angular.module('ylmf.addRole', [])
    .controller('addRoleCtrl', function ($scope, $http, mainUrl, $state) {
        $scope.roleInfo = {
            "comment": "",    //备注
            "name": ""
        };
        //新增功能
        $scope.registerCompetence = function () {
            $http({
                method: 'POST',
                url: mainUrl + '/v1/roles/',
                data: $scope.roleInfo
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == '_200') {
                    // layer.msg('角色添加成功！');
                    //添加一个角色成功的回调，把勾选的功能和角色绑定起来
                    $scope.bindRoleForCompetences(res.data.data.id);
                    // $state.go('me.roles');
                } else {
                    layer.msg(res.data.errorMsg);
                }
            }, function (err) {
                if (err.data) {
                    layer.msg(err.data.message);
                }
            })
        };

        //角色和功能绑定（一对多绑定）
        $scope.roleCompetences = {
            roleId: '',
            competenceIds: []
        };

        //形成角色和功能url的绑定关系（一次可以绑定多个url）
        $scope.bindRoleForCompetences = function(roleId) {
            $scope.roleCompetences.roleId = roleId;
            $scope.competences.forEach(function(item, index) {
                if(item.hasChecked) {
                    $scope.roleCompetences.competenceIds.push(item.id)
                }
            });
            $http({
                method: 'post',
                url: mainUrl + '/v1/security/bindRoleForCompetences',
                params: $scope.roleCompetences
            }).then(function(res) {
                layer.msg('创建角色成功');
                $state.go('me.roles')
            }, function(err) {
                console.log(err);
                layer.msg(err.data.message);
            })
        };


        //获取所有功能列表
        $scope.getAllPower = function () {
            $http({
                method: 'GET',
                url: mainUrl + '/v1/competence/getByAllNoPage',
                data: {}
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == '_200') {
                    // layer.msg('角色添加成功！');
                    // $state.go('me.roles');
                    $scope.competences = res.data.data;
                    $scope.competences.forEach(function(item, index) {
                        item.hasChecked = false;
                    });
                    $scope.getGroups(res.data.data)
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

        //分组
        $scope.getGroups = function(res) {
            var cache = {};
            $scope.groups = {};
            res.forEach(function(item, index) {
//        		console.log(index, item.resource.split('/'));
                var group = (item.resource.split('/'))[2];
                item.flag = group + 'FLAG';
//        		console.log('item', item, group);
                if(!cache[group]) {
                    $scope.groups[group] = {id:group, hasChecked: true};
                    cache[group] = 1;
                }
                if(cache[group] && !item.hasChecked) {
                    $scope.groups[group].hasChecked = false;
                }
            });
            console.log($scope.groups);
        };

        //全选和取消全选
        $scope.checkAll = function(resource, flag) {
            $scope.competences.forEach(function(item, index) {
                if((item.resource.split('/'))[2] === resource) {
                    item.hasChecked = flag;
                }
            })
        };

        //单击某个功能时
        $scope.singleClick = function(resource) {
            var resource = (resource.split('/'))[2];
            //初始化分组的组长（暂时这样么称呼）hasChecked为true，然后遍历所有功能，
            //分组下的某一个的hasChecked为false时，组长的hasChecked置为false
            $scope.groups[resource].hasChecked = true;
            $scope.competences.forEach(function(item, index) {
                if((item.resource.split('/')[2]) === resource && item.hasChecked === false) {
                    $scope.groups[resource].hasChecked = false;
                }
            })
        };

    });