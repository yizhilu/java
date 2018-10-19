angular.module('ylmf.addPower', [])
    .controller('addPowerCtrl', function ($scope, $http, mainUrl, $state) {
        $scope.competenceEntity = {
            //			'id': '12cc',
            'comment': '',
            //			'createDate': '2017-08-28T02:40:37.155Z',
            'methods': 'get',
            'resource': ''
            //			'status': 'STATUS_NORMAL'
        };

        //新增功能
        $scope.registerCompetence = function () {
            $scope.competenceEntity.comment = $scope.competenceEntity.comment.trim();
            $scope.competenceEntity.resource = $scope.competenceEntity.resource.trim();
            $http({
                method: 'post',
                url: mainUrl + '/v1/competence/',
                data: $scope.competenceEntity
            }).then(function (res) {
                console.log(res);
                if (res.data.responseCode == '_200') {
                    $scope.competenceEntity.comment = "";
                    $scope.competenceEntity.resource = "";
                    layer.msg('添加权限成功！');
                    $state.go('me.power');
                } else {
                    layer.msg(res.data.errorMsg);
                }
            }, function (err) {
                if (err.data) {
                    layer.msg(err.data.message);
                }
            })
        }

    });