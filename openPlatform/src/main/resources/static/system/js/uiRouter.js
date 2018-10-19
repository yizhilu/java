angular.module('ylmf')

//    路由
.config(function ($stateProvider, $urlRouterProvider) {
  $stateProvider
  .state('login', {
    url: '/login',
    templateUrl: 'views/login/login.html',
    resolve: {
      loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
        return $ocLazyLoad
        .load(['views/login/login.js'])
      }]
    },
    controller: 'loginCtrl'
  })
  /*.state('xxxx', {
   url: '/xxxx',
   templateUrl: 'views/login/xxxx.html',
   resolve: {
   loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
   return $ocLazyLoad
   .load(['views/login/xxxx.js'])
   }]
   },
   controller: 'xxxxCtrl'
   })*/
  .state('me', {
    url: '/',
    views: {
      '': {
        templateUrl: 'views/framework.html'
      },
      'header@me': {
        templateUrl: 'views/header.html'
      },
      'nav@me': {
        templateUrl: 'views/nav.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['js/nav.js']/*, {insertBefore: '#mainJs'}*/)
          }]
        },
        controller: 'navCtrl'
      }
    }
  })
  .state('me.home', {
    url: 'home',
    views: {
      'contents@me': {
        templateUrl: 'views/home/home.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/home/home.js']/*, {insertBefore: '#mainJs'}*/)
          }]
        },
        controller: 'homeCtrl'
      }
    }
  })
  // ===== 后台账户管理 =====
  //管理员管理
  .state('me.ministrators', {
    url: 'backstage/ministrators',
    views: {
      'contents@me': {
        templateUrl: 'views/backstage/ministrators.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/backstage/ministrators.js'])
          }]
        },
        controller: 'ministratorsCtrl'
      }
    }
  })
  .state('me.addMinistrators', {
    url: 'backstage/ministrators/add',
    views: {
      'contents@me': {
        templateUrl: 'views/backstage/addMinistrators.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/backstage/addMinistrators.js',
              'lib/jstree/themes/default/style.min.css',
              'lib/jstree/jstree.min.js'])
          }]
        },
        controller: 'addMinistratorsCtrl'
      }
    }
  })
  .state('me.editMinistrators', {
    url: 'backstage/ministrators/edit/:id',
    views: {
      'contents@me': {
        templateUrl: 'views/backstage/editMinistrators.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/backstage/editMinistrators.js',
              'lib/jstree/themes/default/style.min.css',
              'lib/jstree/jstree.min.js'])
          }]
        },
        controller: 'editMinistratorsCtrl'
      }
    }
  }) .state('me.modifyPhone', {
	    url: 'backstage/ministrators/modifyPhone/:id',
	    views: {
	      'contents@me': {
	        templateUrl: 'views/backstage/modifyPhone.html',
	        resolve: {
	          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
	            return $ocLazyLoad
	            .load(['views/backstage/modifyPhone.js'])
	          }]
	        },
	        controller: 'modifyPhoneCtrl'
	      }
	    }
   })
    .state('me.modifyRoles', {
	    url: 'backstage/ministrators/modifyRoles/:id',
	    views: {
	      'contents@me': {
	        templateUrl: 'views/backstage/modifyRoles.html',
	        resolve: {
	          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
	            return $ocLazyLoad
	            .load(['views/backstage/modifyRoles.js'])
	          }]
	        },
	        controller: 'modifyRolesCtrl'
	      }
	    }
   })
  .state('me.editPsw', {
    url: 'backstage/editPsw/:id',
    views: {
      'contents@me': {
        templateUrl: 'views/backstage/editPsw.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/backstage/editPsw.js'])
          }]
        },
        controller: 'editPswCtrl'
      }
    }
  })
  .state('me.userList', {
    url: 'user/list',
    views: {
      'contents@me': {
        templateUrl: 'views/user/list.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/user/js/list.js'])
          }]
        },
        controller: 'userListCtrl'
      }
    }
  })
  // ===== 角色权限管理 =====
  //角色管理
  .state('me.roles', {
    url: 'rolePower/roles',
    views: {
      'contents@me': {
        templateUrl: 'views/rolePower/role.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/rolePower/role.js'])
          }]
        },
        controller: 'roleCtrl'
      }
    }
  })
  .state('me.addRole', {
    url: 'rolePower/roles/add',
    views: {
      'contents@me': {
        templateUrl: 'views/rolePower/addRole.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/rolePower/addRole.js'])
          }]
        },
        controller: 'addRoleCtrl'
      }
    }
  })
  .state('me.editRole', {
    url: 'rolePower/roles/edit/:id',
    views: {
      'contents@me': {
        templateUrl: 'views/rolePower/editRole.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/rolePower/editRole.js'])
          }]
        },
        controller: 'editRoleCtrl'
      }
    }
  })
  // 权限管理
  .state('me.power', {
    url: 'rolePower/power',
    views: {
      'contents@me': {
        templateUrl: 'views/rolePower/power.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/rolePower/power.js'])
          }]
        },
        controller: 'powerCtrl'
      }
    }
  })
  .state('me.addPower', {
    url: 'rolePower/power/add',
    views: {
      'contents@me': {
        templateUrl: 'views/rolePower/addPower.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/rolePower/addPower.js'])
          }]
        },
        controller: 'addPowerCtrl'
      }
    }
  })
  .state('me.editPower', {
    url: 'rolePower/power/edit/:id',
    views: {
      'contents@me': {
        templateUrl: 'views/rolePower/editPower.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/rolePower/editPower.js'])
          }]
        },
        controller: 'editPowerCtrl'
      }
    }
  })
    .state('me.gateway', {
    url: 'gateway/list',
    views: {
      'contents@me': {
        templateUrl: 'views/device/gatewayList.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/device/js/gatewayList.js'])
          }]
        },
        controller: 'gatewayListCtrl'
      }
    }
  })
    .state('me.socket', {
    url: 'socket/list',
    views: {
      'contents@me': {
        templateUrl: 'views/device/socketList.html',
        resolve: {
          loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
            return $ocLazyLoad
            .load(['views/device/js/socketList.js'])
          }]
        },
        controller: 'socketListCtrl'
      }
    }
  })
  $urlRouterProvider.otherwise("/login")
})
 