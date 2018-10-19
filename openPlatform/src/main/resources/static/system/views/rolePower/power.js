angular.module('ylmf.power', [])
    .controller('powerCtrl', function ($scope, $http, mainUrl,permission) {
    	
		// 验证是否有修改权限
		permission.verifyPermission("PATCH",
				'/v1/competence', 'modify',
				$scope);
		// 验证是否有禁用权限
		permission.verifyPermission("POST",
				'/v1/competence/disableOrUndisable', 'disableOrUndisable', $scope);
        var cpi = angular.fromJson(localStorage.getItem('currentPageInfo'));

        $scope.searchInfo = {
            method: '',    //POST或者POST|GET|DELETE
            comment: '',        //权限名称
            status: '',     //状态
            resource: '',   //功能川
            page: 0
        };
        //分页组件的参数
        $scope.pageParams = {
            maxSize: 5,
            totalItems: 0,
            currentPage: 1,
            itemsPerPage: 15,
            totalPages: 1
        };

        if (cpi != null) {
            $scope.searchInfo = {
                method: cpi.method,
                comment: cpi.comment,
                resource: cpi.resource,
                status: cpi.status,
                page: 1 + cpi.page
            };
            $scope.pageParams.currentPage = 1 + cpi.page;
        }

        $scope.gatDataLists = function (a) {
            if (a == 0) {
                $scope.pageParams.currentPage = 1;
            }
            $scope.searchInfo.page = $scope.pageParams.currentPage - 1;
            localStorage.setItem('currentPageInfo', angular.toJson($scope.searchInfo));
            $http({
                url: mainUrl + '/v1/competence/getByCondition',
                method: 'GET',
                params: $scope.searchInfo
            }).then(function (res) {
                console.log(res)
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

            // 、、操作标识（flag：true为启用，false为禁用）

            layer.confirm('您确定要' + text + '该权限?', { icon: 3, title: '提示' }, function (index) {
                $http({
                    url: mainUrl + '/v1/competence/disableOrUndisable',
                    method: 'POST',
                    params: { id: item.id, flag: !isDis }
                }).then(function (res) {
                    console.log(res);
                    layer.closeAll();
                    if (res.data.responseCode == '_200') {
                        layer.msg(text + '成功！');
                        isDis ? item.status = "禁用" : item.status = "STATUS_NORMAL";
                    } else {
                        layer.msg(text + '失败！');
                    }
                }, function (err) {
                    layer.msg(text + '失败！');
                });
            })
        };

        $scope.resetSearchInfo = function () {
            $scope.searchInfo = {
                method: '',    //POST或者POST|GET|DELETE
                comment: '',        //权限名称
                status: '',     //状态
                resource: '',   //功能川
                page: 0
            };
            localStorage.removeItem("currentPageInfo");
            $scope.gatDataLists('0');
        }


    });
