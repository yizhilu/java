angular.module('ylmf.editPower', [])
    .controller('editPowerCtrl', function ($scope, $http, mainUrl, $state, $stateParams) {

        // 、、/v1/competence/{id}
        // 获取功能详情
        $http({
            method: 'GET',
            url: mainUrl + '/v1/competence/getById',
            params: {id: $stateParams.id}
        }).then(function (res) {
            console.log(res);
            if (res.data.responseCode == '_200') {
                // $scope.competenceEntity = res.data.data;
                $scope.competenceEntity = {
                    'id': res.data.data.id,
                    'comment': res.data.data.comment,
                    'methods': res.data.data.methods,
                    'resource': res.data.data.resource
                };
            } else {
                layer.msg(res.data.errorMsg);
            }
        }, function (err) {
            if (err.data) {
                layer.msg(err.data.message);
            }
        });


        $scope.competenceEntity = {
            id:'',
            'comment': '',
            'methods': '',
            'resource': ''
        };

        //修改功能
        $scope.editPower = function () {
            $http({
                method: 'PATCH',
                url: mainUrl + '/v1/competence/',
                data: $scope.competenceEntity
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == '_200') {
                    layer.msg('修改权限成功！');
                    $state.go('me.power');
                } else {
                    layer.msg(res.data.errorMsg);
                }
            }, function (err) {
                if (err.data) {
                    layer.msg(err.data.message);
                }
            })
        };

        /*var aaa = {
         "cLevel":"2147483647",
         "comment":"修改一个指定的角色信息，注",
         "createDate":"1520503851000",
         "id":"f238b63f-5381-478e-98a6-b2b21a7b77f5",
         "loadingMode":"MODETYPE_URL",
         "methods":"PATCH",
         "modifyDate":"1520504328000",
         "orderIndex":"9223372036854776000",
         "resource":"/v1/roles/update",
         "status":"STATUS_NORMAL"
         }*/

    });