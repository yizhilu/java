angular.module('ylmf').constant("mainUrl", "")
// .constant("imgUrl", "http://192.168.0.111:8082/imageServer")
// .constant("mainUrl", "http://localhost:50300/")
.constant("imgUrl", "http://tqga.91-ec.com")
// 拦截器，用于判断用户登录状态
.factory("UserInterceptor", function($q, $rootScope) {
	return {
		//
		'responseError' : function(response) {
			console.log(response);
			if (response["status"] == "-1") {
				// 全局事件，方便其他view获取该事件，并给以相应的提示或处理
				$rootScope.$emit("userIntercepted", "sessionOut", response);
			}
			return $q.reject(response);
		},
	/*
	 * //http请求返回是执行 'response' : function(response) { console.log(response);
	 * return response; }, //发送请求之前执行 'request' : function(config) {
	 * console.log(config); return config; },
	 * 
	 * 'requestError' : function(config){ console.log(config); return
	 * $q.reject(config); }
	 */
	}
}).factory(
		"cookie",
		function() {
			return {
				setCookie : function(key, value, expiredays) {
					var exdate = new Date();
					exdate.setDate(exdate.getDate() + expiredays);
					document.cookie = key
							+ "="
							+ escape(value)
							+ ((expiredays == null) ? "" : ";expires="
									+ exdate.toGMTString())
				},
				getCookie : function(key) {
					var cookies = document.cookie;
					if (cookies.length > 0) {
						c_start = cookies.indexOf(key + "=");
						if (c_start != -1) {
							c_start = c_start + key.length + 1;
							c_end = cookies.indexOf(";", c_start);
							if (c_end == -1)
								c_end = cookies.length;
							return unescape(cookies.substring(c_start, c_end))
						}
					}
					return ""
				}
			}
		})// 权限验证
.factory("permission", function($rootScope,$http,permissions) {
	return {
		verifyPermission : function(methods, url, tag,scope) {
			if(permissions.hasPermission("userPermissions",url+"&"+methods)){
				scope['permission_' + tag]=true;
			}else{
				$http({
					url : '/v1/competence/findCompetencePermissionByUrl',
					method : 'get',
					params : {
						"methods" : methods,
						"url" : url
					}
				}).then(function(res) {
					if (res.data.responseCode === '_200') {
						scope['permission_' + tag] = res.data.data;
						if(res.data.data){
							permissions.setPermissions("userPermissions",url+"&"+methods);
						}
					}
				})
				
			}
		}
	}
})