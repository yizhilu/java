angular.module('ylmf.role', [])
    .controller('roleCtrl', function ($scope, $http, mainUrl,permission) {
		// 验证是否有修改权限
		permission.verifyPermission("PATCH",
				'/v1/roles', 'modify',
				$scope);
		// 验证是否有禁用权限
		permission.verifyPermission("POST",
				'/v1/roles/disable', 'disable', $scope);
		// 验证是否有启用权限
		permission.verifyPermission("POST",
				'/v1/roles/enable', 'enable', $scope);
        var cpi = angular.fromJson(localStorage.getItem('currentPageInfo0'));

        $scope.searchInfo = {
            roleName: '',    //角色名称（模糊查询）
            status: '',     //状态
            page: 0
        };
        //分页组件的参数
        $scope.pageParams = {
            maxSize: 5,
            totalItems: 0,
            currentPage: 1,
            itemsPerPage: 15
        };

        if (cpi != null) {
            $scope.searchInfo = {
                roleName: cpi.roleName,
                status: cpi.status,
                page: 1 + cpi.page
            };
            $scope.pageParams.currentPage = 1 + cpi.page;
        }

        //获取数据
        $scope.gatDataLists = function (a) {
            if (a == 0) {
                $scope.pageParams.currentPage = 1;
            }
            $scope.searchInfo.page = $scope.pageParams.currentPage - 1;
            localStorage.setItem('currentPageInfo0', angular.toJson($scope.searchInfo));
            $http({
                url: mainUrl + '/v1/roles/getByCondition',
                method: 'GET',
                params: $scope.searchInfo
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == '_200') {
                    $scope.dataLists = res.data.data.content;
                    $scope.pageParams.totalItems = res.data.data.totalElements;
                    $scope.pageParams.itemsPerPage = res.data.data.size;
                    $scope.pageParams.totalPages = res.data.data.totalPages
                } else {
                    layer.msg(res.data.errorMsg);
                }
            }, function (err) {
                console.log(err);
                layer.msg(err.data.message);
            });
        };
        $scope.gatDataLists();


        //启用禁用
        $scope.hasDisable = function (item, isDis) {
            //isDis:false:启用；true：禁用
            var text = isDis ? '禁用' : '启用';
            console.log(text)
            layer.confirm('您确定要' + text + '该角色?', {icon: 3, title: '提示'}, function (index) {
                if (isDis) {
                    $http({
                        url: mainUrl + '/v1/roles/disable',
                        method: 'POST',
                        params: {roleId:item.id}
                    }).then(function (res) {
                        console.log(res);
                        layer.closeAll();
                        if (res.data.responseCode == '_200') {
                            layer.msg('禁用成功！');
                            item.status = "禁用";
                            // $scope.gatDataLists();
                        } else {
                            layer.msg(res.data.errorMsg);
                        }
                    },function(err){
                        layer.msg('禁用失败！');
                    });
                } else {
                    $http({
                        url: mainUrl + '/v1/roles/enable',
                        method: 'POST',
                        params: {roleId:item.id}
                    }).then(function (res) {
                        console.log(res);
                        layer.closeAll();
                        if (res.data.responseCode == '_200') {
                            layer.msg('启用成功！');
                            item.status = "STATUS_NORMAL";
                        } else {
                            layer.msg('启用失败！');
                        }
                    },function(err){
                        layer.msg('启用失败！');
                    });
                }
            })
        };


        $scope.resetSearchInfo=function(){
            $scope.searchInfo = {
                roleName: '',    //角色名称（模糊查询）
                status: '',     //状态
                page: 0
            };
            localStorage.removeItem("currentPageInfo0");
            $scope.gatDataLists('0');
        };


    });