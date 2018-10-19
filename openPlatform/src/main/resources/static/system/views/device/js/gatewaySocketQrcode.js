angular.module('ylmf.gateway', []).controller(
		'gatewaySocketQrcodeCtrl',
		function($scope, $http, $uibModalInstance, mainUrl, permission, mac,
				type) {
			$scope.close = function() {
				$uibModalInstance.close();
			};
			if(type=="gateway"){
				$scope.mac="http://aih.91-ec.com/wx/wxsso/scan/bindGateway?mac="+mac
			}else{
				$scope.mac="http://aih.91-ec.com/wx/wxsso/scan/bindSocket?mac="+mac
			}
		})