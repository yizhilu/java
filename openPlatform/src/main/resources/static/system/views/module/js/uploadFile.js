angular.module('ylmf.uploadFile', []).controller(
		'uploadFileCtrl',
		function($scope, $http, $uibModalInstance, Upload, mainUrl, url) {
			// 上传-------------------------------------------------
			$scope.uploadFile = function(files, errFiles) {

				$scope.files = files;
				$scope.errFiles = errFiles;
				angular.forEach(files, function(file) {
					file.upload = Upload.upload({
						url : url,
						data : {
							file : file
						}
					});

					file.upload.then(function(response) {
						if (response.status == "200") {
							if (response.data.responseCode == '_200') {
								$scope.close();
							} else {
								layer.msg(response.data.errorMsg);
								$scope.close();
							}
						}
					}, function(response) {
						if (response.status > 0)
							$scope.errorMsg = response.status + ': '
									+ response.data;
					}, function(evt) {
						file.progress = Math.min(100, parseInt(100.0
								* evt.loaded / evt.total));
					});
				});
			}
			/**
			 * 关闭弹窗
			 */
			$scope.close = function() {
				$uibModalInstance.close()
			}
		})