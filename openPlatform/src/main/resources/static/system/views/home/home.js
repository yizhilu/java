angular.module('ylmf.home', [])
    .controller('homeCtrl', function ($scope, $http, $window) {
        console.log('home');

        // 是否能去审核
        $scope.toAudit = false;
        /*getBrand();
        function getBrand(id) {
            if (id == undefined || id == '') {
                id = '';
            }
            $http({
                method: "get",
                url: 'dataJson/selectBrand.json'
                // params: {id: id}
            }).then(function (response) {
                console.log(response);
                $scope.itemArray = response.data.content;
            }, function (response) {
                //console.log(response)
            });
        }

        $scope.obj = {
            aaa: '111'
        };
        $scope.bbb = 222;*/

        if ($window.sessionStorage['userStatus'] === 'STATUS_PENDING') {
            console.log('用户未审核通过。');
            $scope.toAudit = true;
        }

    });