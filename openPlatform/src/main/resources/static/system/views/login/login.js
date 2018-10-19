angular.module('ylmf.login', [])
    .controller('loginCtrl', function ($scope, permissions, $http, mainUrl, $state, $cookies, $stateParams, $window) {
        /*$scope.UI={
         un:'',
         up:''
         };*/
        // console.log(mainUrl);

        // cookie.setCookie("meme","123456");
        // console.log(cookie.getCookie("meme"));

        // $cookies.put("test","lin");

        //angualr js 设置cookie及过期时间
        // var expireDate = new Date();
        // expireDate.setDate(expireDate.getDate() + 30);
        // $cookies.put('myFavorite', 'oatmeal', {'expires': expireDate.toUTCString()});

        $scope.login = function (UI) {
            // $state.go("me.home");
            if (UI) {
                $http({
                    method: 'post',
                    url: mainUrl + '/login',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    transformRequest: function (obj) {
                        var str = [];
                        for (var s in obj) {
                            str.push(encodeURIComponent(s) + "=" + encodeURIComponent(obj[s]));
                        }
                        return str.join("&");
                    },
                    data: {
                        username: UI.un,
                        password: UI.up
                    }
                }).then(function (res) {
                    console.log(res);
                    if (res.status == 200) {
                        if (res.data.responseCode == "_200") {

                            $window.sessionStorage.removeItem('userPermissions');
                            $window.sessionStorage.removeItem('nouserPermissions');
                            permissions.restPermissionList();
                            if (res.data.data.account) {
                                $cookies.put("UI", res.data.data.account);
                                $cookies.put("UID", res.data.data.id);
                                $cookies.put("UIT", res.data.data.userType);
                            }
                            // $state.go("me.home");
                            var from = $stateParams["from"];
                            $window.sessionStorage['userStatus'] = res.data.data.status;
                            $state.go(from && from != "login" ? from : 'me.home');
                        } else {
                            $scope.loginError = res.data.errorMsg;
                        }
                    } else {
                        $scope.loginError = "登录失败，密码或账号不正确！";
                    }
                }, function (err) {
                    $scope.loginError = "登录失败，密码或账号不正确！";
                })
            }
        }

    });