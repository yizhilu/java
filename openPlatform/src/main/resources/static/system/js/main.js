angular.module('ylmf', [/*'hc.marked', 'hljs', 'angular-markdown-editor',*/'ng.ueditor',"ng.ueditor.readonly",'ngAnimate', 'ngSanitize', 'ui.router', 'oc.lazyLoad', 'ui.bootstrap', 'ngScrollbars', 'ui.select', 'ngCookies', 'ngFileUpload','monospaced.qrcode'])

    .config(function ($httpProvider) {
        //配置自定义拦截器
        $httpProvider.interceptors.push('UserInterceptor');
    })
    //滚动条插件配置
    .config(function (ScrollBarsProvider) {
        ScrollBarsProvider.defaults = {
            scrollButtons: {
                scrollAmount: 'auto', // scroll amount when button pressed
                enable: false // 是否启用滚动按钮;上下的来年各个箭头
            },
            scrollInertia: 100, // 滚动速度
            autoDraggerLength: true,
            axis: 'y', // 垂直滚动条
            theme: 'dark',  //minimal、dark、minimal-dark
            autoHideScrollbar: true,    //自动隐藏滚动条
            autoExpandScrollbar: true,   //鼠标指向滚动条放大
            alwaysShowScrollbar: '1',
            scrollbarPosition: 'inside'  //滚动条相对于内容的位置
        };
    })
    //懒加载配置
    .config(['$ocLazyLoadProvider', function ($ocLazyLoadProvider) {
        $ocLazyLoadProvider.config({
            debug: true,
            event: false,
            modules: [
                //上传
                {
                    name: 'fileUpload',
                    files: ['lib/ng-file-upload/ng-file-upload-shim.js', 'lib/ng-file-upload/ng-file-upload.js']
                },
                //排序
                {
                    name: 'sortable',
                    files: ['lib/angular-sortable-view/angular-sortable-view.js']
                },
                {
                    name: "jsTree",
                    files: ['lib/jstree/jstree.min.js', 'lib/jstree/themes/style.min.css']
                }
            ]
        });
    }])
//    .config(['markedProvider', 'hljsServiceProvider', function (markedProvider, hljsServiceProvider) {
//        // marked config
//        markedProvider.setOptions({
//            gfm: true,
//            tables: true,
//            sanitize: true,
//            highlight: function (code, lang) {
//                if (lang) {
//                    return hljs.highlight(lang, code, true).value;
//                } else {
//                    return hljs.highlightAuto(code).value;
//                }
//            }
//        });
//
//        // highlight config
//        hljsServiceProvider.setOptions({
//            // replace tab with 4 spaces
//            tabReplace: '    '
//        });
//    }])
    .run(function ($rootScope, $urlRouter, $state, $cookies, $window) {
        //获取当前地址栏的url
        $rootScope.$on('$locationChangeSuccess', function (evt, toState, toParams, fromState, fromParams) {
            // console.log(toState);
            // console.log(toParams);
            // console.log(fromState);
            // console.log(fromParams);
            if (toState.split("#!/")[1] == "register") {
                return;
            }
            //console.log(window.location.href);
            // var ck = $cookies.getAll();
            // if($cookies.get("UI")){

            // }
            if (!$cookies.get("UI")) {
                evt.preventDefault();//取消默认跳转行为
                $state.go('login');
            }
            $rootScope.ch = window.location.href;
            $rootScope.userName = $cookies.get("UI");
            // console.log(111);
        });

        $rootScope.returnPage = function () {
            // console.log('000')
            window.history.go(-1);
        };

        /*监听自定义服务*/
        $rootScope.$on('userIntercepted', function (errorType) {
            // 跳转到登录界面，这里记录了一个from，这样可以在登录后自动跳转到未登录之前的那个界面
            $cookies.put('UI', '', -1);
            $cookies.put('UID', '', -1);
            // $window.sessionStorage.removeItem('userPermissions');
            // $window.sessionStorage.removeItem('nouserPermissions');
            /*重置权限列表*/
            // permissions.restPermissionList();
            //		  	$state.go('login',{from:$state.current.name,w:'seesionOut'});
            $state.go('login');
        });

        // $cookies.get

    }).factory('permissions', function ($rootScope, $window) {
    var permissionList = [];
    var nopermissionList = [];
    var permissionUsers = [];
    return {
        restPermissionList: function () {
            permissionList = [];
            nopermissionList = [];
        },
        setPermissions: function (key, permission) {

            if (key == 'userPermissions') {
                permissionList.push(permission);
                $window.sessionStorage[key] = permissionList;
            } else {
                nopermissionList.push(permission);
                $window.sessionStorage[key] = nopermissionList;
            }

            $rootScope.$broadcast('permissionsChanged')
        },
        hasPermission: function (key, permission) {
        	if(($window.sessionStorage[key])!=""&&($window.sessionStorage[key])!=null&($window.sessionStorage[key])!=undefined){
        		permissionUsers = ($window.sessionStorage[key]).split(",");        		
        	}
            if (permissionUsers.length == 0) {
                return false;
            }
            return $.inArray(permission, permissionUsers) > -1 ? 1 : 0;
        }
    }
}).directive('hasPermission', function (permissions, $http, $window, $rootScope) {
    return {
        link: function (scope, element, attrs) {

            var value = attrs.hasPermission.trim();
            valueArr = value.split('&');
            if (!($window.sessionStorage['userPermissions'] == undefined || $window.sessionStorage['userPermissions'] == null)) {
                if (permissions.hasPermission('userPermissions', value)) {//判断是否已经存在
                    element.show();
                    return;
                }
            }
            if (!($window.sessionStorage['nouserPermissions'] == undefined || $window.sessionStorage['nouserPermissions'] == null)) {
                if (permissions.hasPermission('nouserPermissions', value)) {//判断是否已经存在
                    element.hide();
                    return;
                }
            }

            /*获取当前登录用户权限------*/
            $http({
                url: '/v1/competence/findCompetencePermissionByUrl',
                method: 'GET',
                params: {url: valueArr[0], methods: valueArr[1]}
            }).then(function (res) {
                if (res.data.data == true) {
                    element.show();

                    if ($window.sessionStorage['userPermissions'] == undefined || $window.sessionStorage['userPermissions'] == null) {
                        permissions.setPermissions('userPermissions', value);
                    } else if (permissions.hasPermission('userPermissions', value)) {//判断是否已经存在
                        return;
                    } else {
                        permissions.setPermissions('userPermissions', value);
                    }

                } else {
                    element.hide();
                    if ($window.sessionStorage['nouserPermissions'] == undefined || $window.sessionStorage['nouserPermissions'] == null) {
                        permissions.setPermissions('nouserPermissions', value);
                    } else if (permissions.hasPermission('nouserPermissions', value)) {//判断是否已经存在
                        return;
                    } else {
                        permissions.setPermissions('nouserPermissions', value);
                    }
                }
            }, function (err) {
                console.log(err);
                layer.msg(err.data.message)
            })
        }
    };
})
//has-or-permission="/v1/gateway/getByConditions&GET|/v1/socket/getByConditions&GET"
.directive('hasOrPermission', function (permissions, $http, $window, $rootScope) {
    return {
        link: function (scope, element, attrs) {

            var value = attrs.hasOrPermission.trim();
            //"/v1/gateway/getByConditions&GET|/v1/socket/getByConditions&GET"
            if (!($window.sessionStorage['userPermissions'] == undefined || $window.sessionStorage['userPermissions'] == null)) {
                if (permissions.hasPermission('userPermissions', value)) {//判断是否已经存在
                    element.show();
                    return;
                }
            }
            if (!($window.sessionStorage['nouserPermissions'] == undefined || $window.sessionStorage['nouserPermissions'] == null)) {
                if (permissions.hasPermission('nouserPermissions', value)) {//判断是否已经存在
                    element.hide();
                    return;
                }
            }

            /*获取当前登录用户权限------*/
            $http({
                url: '/v1/competence/findOrPermissionByUrl',
                method: 'GET',
                params: {urls: value}
            }).then(function (res) {
                if (res.data.data == true) {
                    element.show();

                    if ($window.sessionStorage['userPermissions'] == undefined || $window.sessionStorage['userPermissions'] == null) {
                        permissions.setPermissions('userPermissions', value);
                    } else if (permissions.hasPermission('userPermissions', value)) {//判断是否已经存在
                        return;
                    } else {
                        permissions.setPermissions('userPermissions', value);
                    }

                } else {
                    element.hide();
                    if ($window.sessionStorage['nouserPermissions'] == undefined || $window.sessionStorage['nouserPermissions'] == null) {
                        permissions.setPermissions('nouserPermissions', value);
                    } else if (permissions.hasPermission('nouserPermissions', value)) {//判断是否已经存在
                        return;
                    } else {
                        permissions.setPermissions('nouserPermissions', value);
                    }
                }
            }, function (err) {
                console.log(err);
                layer.msg(err.data.message)
            })
        }
    };
})
// 自定义警种指令
.directive("jsPoliceSpeciesTree", function($window) {
	return {
		restrict : 'A',
		scope : {
			jstree : '=',
			whetherJsTree : '='
		},
		link : function(scope, element, attrs) {
			$(element).jstree({
				core : {
					multiple : false,
					'data' : {
						'url' : scope.jstree.dataUrl,
						'async' : true,
						'data' : function(node) {
							return {
								'id' : $(node).attr("id") == "#"
										? "policeSpecies"
										: $(node).attr("id"),
								includeDisable : "false"
								// 需要操作者部门id
							};
						},

						lang : {
							loading : "目录加载中……"
						}
					},
					force_text : true,
					check_callback : true
				},
				'expand_selected_onload' : true, // 选中项蓝色底显示
				'checkbox' : {
					// 禁用级联选中
					'three_state' : false,
					'cascade' : 'undetermined' // 有三个选项，up, down,
					// undetermined;
					// 使用前需要先禁用three_state
				},
				types : scope.jstree.types,
				/* plugins : [ "contextmenu" ,"state"], */
				plugins : scope.jstree.plugins
			}, false);
			// }, true);
			// select a node
			element.bind("dblclick.jstree", function(e, data) {
						var str = $(event.target).context.id.split("_");
						var id = str[0];
						var name = e.target.name;
						scope.$apply(function() {
									scope.jstree.id = id;
									scope.jstree.name = name;
									scope.whetherJsTree = false;
								});

					});
			element.bind("loaded.jstree", function(e, data) {
						var node = $(element).jstree("get_node",
								scope.jstree.id);
					});
		}
	};
})// 自定义学习领域指令
.directive("jsLearningAreaTree", function($window) {
	return {
		restrict : 'A',
		scope : {
			jstree : '=',
			whetherJsTree : '='
		},
		link : function(scope, element, attrs) {
			$(element).jstree({
				core : {
					multiple : false,
					'data' : {
						'url' : scope.jstree.dataUrl,
						'async' : true,
						'data' : function(node) {
							return {
								'id' : $(node).attr("id") == "#"
										? "learningArea"
										: $(node).attr("id"),
								includeDisable : "false"
								// 需要操作者部门id
							};
						},

						lang : {
							loading : "目录加载中……"
						}
					},
					force_text : true,
					check_callback : true
				},
				'expand_selected_onload' : true, // 选中项蓝色底显示
				'checkbox' : {
					// 禁用级联选中
					'three_state' : false,
					'cascade' : 'undetermined' // 有三个选项，up, down,
					// undetermined;
					// 使用前需要先禁用three_state
				},
				types : scope.jstree.types,
				/* plugins : [ "contextmenu" ,"state"], */
				plugins : scope.jstree.plugins
			}, false);
			// }, true);
			// select a node
			element.bind("dblclick.jstree", function(e, data) {
						var str = $(event.target).context.id.split("_");
						var id = str[0];
						var name = e.target.name;
						scope.$apply(function() {
									scope.jstree.id = id;
									scope.jstree.name = name;
									scope.whetherJsTree = false;
								});

					});
			element.bind("loaded.jstree", function(e, data) {
						var node = $(element).jstree("get_node",
								scope.jstree.id);
					});
		}
	};
})//机构jstree 指令
.directive("jsAgencyTree", function($window) {
	return {
		restrict : 'A',
		scope : {
			jstree : '=',
			whetherJsTree : '='
		},
		link : function(scope, element, attrs) {
			$(element).jstree({
				core : {
					multiple : false,
					'data' : {
						'url' : scope.jstree.dataUrl,
						'async' : true,
						'data' : function(node) {
							return {
								'id' : $(node).attr("id") == "#"
										? "browseAgency"
										: $(node).attr("id"),
								includeDisable : "false"
								// 需要操作者部门id
							};
						},

						lang : {
							loading : "目录加载中……"
						}
					},
					force_text : true,
					check_callback : true
				},
				'expand_selected_onload' : true, // 选中项蓝色底显示
				'checkbox' : {
					// 禁用级联选中
					'three_state' : false,
					'cascade' : 'undetermined' // 有三个选项，up, down,
					// undetermined;
					// 使用前需要先禁用three_state
				},
				types : scope.jstree.types,
				/* plugins : [ "contextmenu" ,"state"], */
				plugins : scope.jstree.plugins
			}, false);
			// }, true);
			// select a node
			element.bind("dblclick.jstree", function(e, data) {
						var str = $(event.target).context.id.split("_");
						var id = str[0];
						var name = e.target.name;
						scope.$apply(function() {
									scope.jstree.id = id;
									scope.jstree.name = name;
									scope.whetherJsTree = false;
								});

					});
			element.bind("loaded.jstree", function(e, data) {
						var node = $(element).jstree("get_node",
								scope.jstree.id);
					});
		}
	};
}).directive('echart', function() {
    return {
        restrict: 'E',
        template: '<div style="height:600px;width:600px;"></div>',
        replace: true,
		scope : {
			echartOption: '=',
			echartId:'='
		},
        link: function(scope, element, attrs, controller) {
        	var id=scope.echartId;
        	var option=scope.echartOption;
            var myChart = echarts.init(document.getElementById("main"));
            myChart.setOption(option);
        }
    };
});