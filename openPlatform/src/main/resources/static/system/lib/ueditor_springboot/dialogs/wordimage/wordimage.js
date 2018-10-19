/**
 * Created by JetBrains PhpStorm. User: taoqili Date: 12-1-30 Time: 下午12:50 To
 * change this template use File | Settings | File Templates.
 */

var wordImage = {};
// (function(){
var g = baidu.g, uploadImage;

var lang = {
	"static" : {
		"clipboard" : {
			"style" : "background: url(copy.png) -153px -1px no-repeat;"
		},
		"lang_resave" : "转存步骤",
		"lang_step" : "1、点击顶部复制按钮，将地址复制到剪贴板；2、点击添加照片按钮，在弹出的对话框中使用Ctrl+V粘贴地址；3、点击打开后选择图片上传流程。",
		"uploadBtn" : {
			"alt" : "上传",
			"src" : "upload.png"
		},
		"lang_tab_remote" : "插入图片",
		"lang_tab_upload" : "本地上传",
		"lang_tab_online" : "在线管理",
		"lang_tab_search" : "图片搜索",
		"lang_input_url" : "地 址：",
		"lang_input_size" : "大 小：",
		"lang_input_width" : "宽度",
		"lang_input_height" : "高度",
		"lang_input_border" : "边 框：",
		"lang_input_vhspace" : "边 距：",
		"lang_input_title" : "描 述：",
		"lang_input_align" : "图片浮动方式：",
		"lang_imgLoading" : " 图片加载中……",
		"lang_start_upload" : "开始上传",
		"lock" : {
			"title" : "锁定宽高比例"
		},
		"searchType" : {
			"title" : "图片类型",
			"options" : [ "新闻", "壁纸", "表情", "头像" ]
		},
		"searchTxt" : {
			"value" : "请输入搜索关键词"
		},
		"searchBtn" : {
			"value" : "百度一下"
		},
		"searchReset" : {
			"value" : "清空搜索"
		},
		"noneAlign" : {
			"title" : "无浮动"
		},
		"leftAlign" : {
			"title" : "左浮动"
		},
		"rightAlign" : {
			"title" : "右浮动"
		},
		"centerAlign" : {
			"title" : "居中独占一行"
		}
	},
	"uploadSelectFile" : "点击选择图片",
	"uploadAddFile" : "继续添加",
	"uploadStart" : "开始上传",
	"uploadPause" : "暂停上传",
	"uploadContinue" : "继续上传",
	"uploadRetry" : "重试上传",
	"uploadDelete" : "删除",
	"uploadTurnLeft" : "向左旋转",
	"uploadTurnRight" : "向右旋转",
	"uploadPreview" : "预览中",
	"uploadNoPreview" : "不能预览",
	"updateStatusReady" : "选中_张图片，共_KB。",
	"updateStatusConfirm" : "已成功上传_张照片，_张照片上传失败",
	"updateStatusFinish" : "共_张（_KB），_张上传成功",
	"updateStatusError" : "，_张上传失败。",
	"errorNotSupport" : "WebUploader 不支持您的浏览器！如果你使用的是IE浏览器，请尝试升级 flash 播放器。",
	"errorLoadConfig" : "后端配置项没有正常加载，上传插件不能正常使用！",
	"errorExceedSize" : "文件大小超出",
	"errorFileType" : "文件格式不允许",
	"errorInterrupt" : "文件传输中断",
	"errorUploadRetry" : "上传失败，请重试",
	"errorHttp" : "http请求错误",
	"errorServerUpload" : "服务器返回出错",
	"remoteLockError" : "宽高不正确,不能所定比例",
	"numError" : "请输入正确的长度或者宽度值！例如：123，400",
	"imageUrlError" : "不允许的图片格式或者图片域！",
	"imageLoadError" : "图片加载失败！请检查链接地址或网络状态！",
	"searchRemind" : "请输入搜索关键词",
	"searchLoading" : "图片加载中，请稍后……",
	"searchRetry" : " :( ，抱歉，没有找到图片！请重试一次！",
	"copySuccess" : "图片地址已经复制！",//
	"fileType" : "图片",
	"flashError" : "FLASH初始化失败，请检查FLASH插件是否正确安装！",
	"flashI18n" : {},
	"netError" : "网络连接错误，请重试！"
}
setTimeout(function() {
	uploadImage = uploadImage || new UploadImage('queueList');

}, 0)
wordImage.init = function(opt, callbacks) {
	console.log(opt);
	showLocalPath("localPath");
	// createCopyButton("clipboard","localPath");
	createFlashUploader(opt, callbacks);
	addUploadListener();
	addOkListener();
};
wordImage.initFile = function() {
	// console.log(opt);
	showLocalPath("localPath");
	initButtons();
};
/* TODO 上传图片 */
function UploadImage(target) {
	this.$wrap = target.constructor == String ? $('#' + target) : $(target);
	this.init();
}
UploadImage.prototype = {
	init : function() {
		this.imageList = [];
		this.initContainer();
		this.initUploader();
	},
	initContainer : function() {
		this.$queue = this.$wrap.find('.filelist');
	},
	/* 初始化容器 */
	initUploader : function() {
		var _this = this, $ = jQuery, // just in case. Make sure it's not an
		// other
		// libaray.
		$wrap = _this.$wrap,
		// 图片容器
		$queue = $wrap.find('.filelist'),
		// 状态栏，包括进度和控制按钮
		$statusBar = $wrap.find('.statusBar'),
		// 文件总体选择信息。
		$info = $statusBar.find('.info'),
		// 上传按钮
		$upload = $wrap.find('.uploadBtn'),
		// 上传按钮
		$filePickerBtn = $wrap.find('.filePickerBtn'),
		// 上传按钮
		$filePickerBlock = $wrap.find('.filePickerBlock'),
		// 没选择文件之前的内容。
		$placeHolder = $wrap.find('.placeholder'),
		// 总体进度条
		$progress = $statusBar.find('.progress').hide(),
		// 添加的文件数量
		fileCount = 0,
		// 添加的文件总大小
		fileSize = 0,
		// 优化retina, 在retina下这个值是2
		ratio = window.devicePixelRatio || 1,
		// 缩略图大小
		thumbnailWidth = 113 * ratio, thumbnailHeight = 113 * ratio,
		// 可能有pedding, ready, uploading, confirm, done.
		state = '',
		// 所有文件的进度信息，key为file id
		percentages = {}, supportTransition = (function() {
			var s = document.createElement('p').style, r = 'transition' in s
					|| 'WebkitTransition' in s || 'MozTransition' in s
					|| 'msTransition' in s || 'OTransition' in s;
			s = null;
			return r;
		})(),
		// WebUploader实例
		uploader, actionUrl = editor.getOpt('imageServerPath') ? editor
				.getOpt('imageServerPath') : editor.getActionUrl(editor
				.getOpt('imageActionName')), acceptExtensions = (editor
				.getOpt('imageAllowFiles') || []).join('').replace(/\./g, ',')
				.replace(/^[,]/, ''), imageMaxSize = editor
				.getOpt('imageMaxSize'), imageCompressBorder = editor
				.getOpt('imageCompressBorder');
		console.log(1111);
		if (!WebUploader.Uploader.support()) {
			$('#filePickerReady').after($('<div>').html(lang.errorNotSupport))
					.hide();
			return;
		} else if (!editor.getOpt('imageActionName')) {
			$('#filePickerReady').after($('<div>').html(lang.errorLoadConfig))
					.hide();
			return;
		}

		uploader = _this.uploader = WebUploader.create({
			pick : {
				id : '#filePickerReady',
				label : lang.uploadSelectFile
			},
			accept : {
				title : 'Images',
				extensions : acceptExtensions,
				mimeTypes : 'image/*'
			},
			swf : '../../third-party/webuploader/Uploader.swf',
			server : actionUrl,
			fileVal : editor.getOpt('imageFieldName'),
			duplicate : true,
			fileSingleSizeLimit : imageMaxSize, // 默认 2 M
			compress : editor.getOpt('imageCompressEnable') ? {
				width : imageCompressBorder,
				height : imageCompressBorder,
				// 图片质量，只有type为`image/jpeg`的时候才有效。
				quality : 90,
				// 是否允许放大，如果想要生成小图的时候不失真，此选项应该设置为false.
				allowMagnify : false,
				// 是否允许裁剪。
				crop : false,
				// 是否保留头部meta信息。
				preserveHeaders : true
			} : false
		});
		uploader.addButton({
			id : '#filePickerBlock'
		});
		uploader.addButton({
			id : '#filePickerBtn',
			label : lang.uploadAddFile
		});

		setState('pedding');

		// 当有文件添加进来时执行，负责view的创建
		function addFile(file) {
			var $li = $('<li id="' + file.id + '">' + '<p class="title">'
					+ file.name + '</p>' + '<p class="imgWrap"></p>'
					+ '<p class="progress"><span></span></p>' + '</li>'),

			$btns = $(
					'<div class="file-panel">' + '<span class="cancel">'
							+ lang.uploadDelete + '</span>'
							+ '<span class="rotateRight">'
							+ lang.uploadTurnRight + '</span>'
							+ '<span class="rotateLeft">' + lang.uploadTurnLeft
							+ '</span></div>').appendTo($li), $prgress = $li
					.find('p.progress span'), $wrap = $li.find('p.imgWrap'), $info = $(
					'<p class="error"></p>').hide().appendTo($li),

			showError = function(code) {
				switch (code) {
				case 'exceed_size':
					text = lang.errorExceedSize;
					break;
				case 'interrupt':
					text = lang.errorInterrupt;
					break;
				case 'http':
					text = lang.errorHttp;
					break;
				case 'not_allow_type':
					text = lang.errorFileType;
					break;
				default:
					text = lang.errorUploadRetry;
					break;
				}
				$info.text(text).show();
			};

			if (file.getStatus() === 'invalid') {
				showError(file.statusText);
			} else {
				$wrap.text(lang.uploadPreview);
				if (browser.ie && browser.version <= 7) {
					$wrap.text(lang.uploadNoPreview);
				} else {
					uploader.makeThumb(file, function(error, src) {
						if (error || !src) {
							$wrap.text(lang.uploadNoPreview);
						} else {
							var $img = $('<img src="' + src + '">');
							$wrap.empty().append($img);
							$img.on('error', function() {
								$wrap.text(lang.uploadNoPreview);
							});
						}
					}, thumbnailWidth, thumbnailHeight);
				}
				percentages[file.id] = [ file.size, 0 ];
				file.rotation = 0;

				/* 检查文件格式 */
				if (!file.ext
						|| acceptExtensions.indexOf(file.ext.toLowerCase()) == -1) {
					showError('not_allow_type');
					uploader.removeFile(file);
				}
			}

			file.on('statuschange', function(cur, prev) {
				if (prev === 'progress') {
					$prgress.hide().width(0);
				} else if (prev === 'queued') {
					$li.off('mouseenter mouseleave');
					$btns.remove();
				}
				// 成功
				if (cur === 'error' || cur === 'invalid') {
					showError(file.statusText);
					percentages[file.id][1] = 1;
				} else if (cur === 'interrupt') {
					showError('interrupt');
				} else if (cur === 'queued') {
					percentages[file.id][1] = 0;
				} else if (cur === 'progress') {
					$info.hide();
					$prgress.css('display', 'block');
				} else if (cur === 'complete') {
				}

				$li.removeClass('state-' + prev).addClass('state-' + cur);
			});

			$li.on('mouseenter', function() {
				$btns.stop().animate({
					height : 30
				});
			});
			$li.on('mouseleave', function() {
				$btns.stop().animate({
					height : 0
				});
			});

			$btns.on('click', 'span', function() {
				var index = $(this).index(), deg;

				switch (index) {
				case 0:
					uploader.removeFile(file);
					return;
				case 1:
					file.rotation += 90;
					break;
				case 2:
					file.rotation -= 90;
					break;
				}

				if (supportTransition) {
					deg = 'rotate(' + file.rotation + 'deg)';
					$wrap.css({
						'-webkit-transform' : deg,
						'-mos-transform' : deg,
						'-o-transform' : deg,
						'transform' : deg
					});
				} else {
					$wrap.css('filter',
							'progid:DXImageTransform.Microsoft.BasicImage(rotation='
									+ (~~((file.rotation / 90) % 4 + 4) % 4)
									+ ')');
				}

			});

			$li.insertBefore($filePickerBlock);
		}

		// 负责view的销毁
		function removeFile(file) {
			var $li = $('#' + file.id);
			delete percentages[file.id];
			updateTotalProgress();
			$li.off().find('.file-panel').off().end().remove();
		}

		function updateTotalProgress() {
			var loaded = 0, total = 0, spans = $progress.children(), percent;

			$.each(percentages, function(k, v) {
				total += v[0];
				loaded += v[0] * v[1];
			});

			percent = total ? loaded / total : 0;

			spans.eq(0).text(Math.round(percent * 100) + '%');
			spans.eq(1).css('width', Math.round(percent * 100) + '%');
			updateStatus();
		}

		function setState(val, files) {

			if (val != state) {

				var stats = uploader.getStats();

				$upload.removeClass('state-' + state);
				$upload.addClass('state-' + val);

				switch (val) {

				/* 未选择文件 */
				case 'pedding':
					$queue.addClass('element-invisible');
					$statusBar.addClass('element-invisible');
					$placeHolder.removeClass('element-invisible');
					$progress.hide();
					$info.hide();
					uploader.refresh();
					break;

				/* 可以开始上传 */
				case 'ready':
					$placeHolder.addClass('element-invisible');
					$queue.removeClass('element-invisible');
					$statusBar.removeClass('element-invisible');
					$progress.hide();
					$info.show();
					$upload.text(lang.uploadStart);
					uploader.refresh();
					break;

				/* 上传中 */
				case 'uploading':
					$progress.show();
					$info.hide();
					$upload.text(lang.uploadPause);
					break;

				/* 暂停上传 */
				case 'paused':
					$progress.show();
					$info.hide();
					$upload.text(lang.uploadContinue);
					break;

				case 'confirm':
					$progress.show();
					$info.hide();
					$upload.text(lang.uploadStart);

					stats = uploader.getStats();
					if (stats.successNum && !stats.uploadFailNum) {
						setState('finish');
						return;
					}
					break;

				case 'finish':
					$progress.hide();
					$info.show();
					if (stats.uploadFailNum) {
						$upload.text(lang.uploadRetry);
					} else {
						$upload.text(lang.uploadStart);
					}
					break;
				}

				state = val;
				updateStatus();

			}

			if (!_this.getQueueCount()) {
				$upload.addClass('disabled')
			} else {
				$upload.removeClass('disabled')
			}

		}

		function updateStatus() {
			var text = '', stats;
			// console.log(JSON.stringify(lang));
			if (state === 'ready') {
				text = lang.updateStatusReady.replace('_', fileCount).replace(
						'_KB', WebUploader.formatSize(fileSize));
			} else if (state === 'confirm') {
				stats = uploader.getStats();
				if (stats.uploadFailNum) {
					text = lang.updateStatusConfirm.replace('_',
							stats.successNum).replace('_', stats.successNum);
				}
			} else {
				stats = uploader.getStats();
				text = lang.updateStatusFinish.replace('_', fileCount).replace(
						'_KB', WebUploader.formatSize(fileSize)).replace('_',
						stats.successNum);

				if (stats.uploadFailNum) {
					text += lang.updateStatusError.replace('_',
							stats.uploadFailNum);
				}
			}

			$info.html(text);
		}

		uploader.on('fileQueued', function(file) {
			fileCount++;
			fileSize += file.size;

			if (fileCount === 1) {
				$placeHolder.addClass('element-invisible');
				$statusBar.show();
			}

			addFile(file);
		});

		uploader.on('fileDequeued', function(file) {
			fileCount--;
			fileSize -= file.size;

			removeFile(file);
			updateTotalProgress();
		});

		uploader.on('filesQueued', function(file) {
			if (!uploader.isInProgress()
					&& (state == 'pedding' || state == 'finish'
							|| state == 'confirm' || state == 'ready')) {
				setState('ready');
			}
			updateTotalProgress();
		});

		uploader.on('all', function(type, files) {
			switch (type) {
			case 'uploadFinished':
				setState('confirm', files);
				break;
			case 'startUpload':
				/* 添加额外的GET参数 */
				var params = utils.serializeParam(editor
						.queryCommandValue('serverparam'))
						|| '', url = utils.formatUrl(actionUrl
						+ (actionUrl.indexOf('?') == -1 ? '?' : '&')
						+ 'encode=utf-8&' + params);
				uploader.option('server', url);
				setState('uploading', files);
				break;
			case 'stopUpload':
				setState('paused', files);
				break;
			}
		});

		uploader.on('uploadBeforeSend', function(file, data, header) {
			// 这里可以通过data对象添加POST参数
			header['X_Requested_With'] = 'XMLHttpRequest';
		});

		uploader.on('uploadProgress', function(file, percentage) {
			var $li = $('#' + file.id), $percent = $li.find('.progress span');

			$percent.css('width', percentage * 100 + '%');
			percentages[file.id][1] = percentage;
			updateTotalProgress();
		});

		uploader.on('uploadSuccess', function(file, ret) {
			var $file = $('#' + file.id);
			try {
				var responseText = (ret._raw || ret), json = utils
						.str2json(responseText);
				console.log(responseText);
				var flag = editor.getOpt('imageServerPath') ? "imageServer"
						: "localServer";
				if (flag == "imageServer") {
					var result = JSON.parse(responseText)
					json = {
						"originalName" : result.originalFilename,
						"type" : (result.originalFilename)
								.substring((result.originalFilename)
										.lastIndexOf('.')),
						"url" : result.relativePath,
						"state" : "SUCCESS"
					}
				}
				if (json.state == 'SUCCESS') {
					_this.imageList.push(json);
					$file.append('<span class="success"></span>');
				} else {
					$file.find('.error').text(json.state).show();
				}
			} catch (e) {
				$file.find('.error').text(lang.errorServerUpload).show();
			}
		});

		uploader.on('uploadError', function(file, code) {
		});
		uploader.on('error', function(code, file) {
			if (code == 'Q_TYPE_DENIED' || code == 'F_EXCEED_SIZE') {
				addFile(file);
			}
		});
		uploader.on('uploadComplete', function(file, ret) {
		});

		$upload.on('click', function() {
			if ($(this).hasClass('disabled')) {
				return false;
			}

			if (state === 'ready') {
				uploader.upload();
			} else if (state === 'paused') {
				uploader.upload();
			} else if (state === 'uploading') {
				uploader.stop();
			}
		});

		$upload.addClass('state-' + state);
		updateTotalProgress();
	},
	getQueueCount : function() {
		var file, i, status, readyFile = 0, files = this.uploader.getFiles();
		for (i = 0; file = files[i++];) {
			status = file.getStatus();
			if (status == 'queued' || status == 'uploading'
					|| status == 'progress')
				readyFile++;
		}
		return readyFile;
	},
	destroy : function() {
		this.$wrap.remove();
	},
	getInsertList : function() {
		var i, data, list = [], prefix = editor
				.getOpt('imageUrlPrefix');
		var align = 'none';
        align == 'none' ? '':align;
		for (i = 0; i < this.imageList.length; i++) {
			data = this.imageList[i];
			list.push({
				src : prefix + data.url,
				_src : prefix + data.url,
				title : data.title,
				alt : data.original,
				original:data.originalName,
				floatStyle : align,
				title:data.originalName,
			    url:data.url
			});
		}
		return list;
	}
};
function showLocalPath(id) {
	// 单张编辑
	var img = editor.selection.getRange().getClosedNode();
	var images = editor.execCommand('wordimage');
	if (images.length == 1 || img && img.tagName == 'IMG') {
		g(id).value = images[0];
		console.log(images[0]);
		return;
	}
	var path = images[0];
	var leftSlashIndex = path.lastIndexOf("/") || 0, // 不同版本的doc和浏览器都可能影响到这个符号，故直接判断两种
	rightSlashIndex = path.lastIndexOf("\\") || 0, separater = leftSlashIndex > rightSlashIndex ? "/"
			: "\\";

	path = path.substring(0, path.lastIndexOf(separater) + 1);
	g(id).value = path;
}

function extendProperty(fromObj, toObj) {
	for ( var i in fromObj) {
		if (!toObj[i]) {
			toObj[i] = fromObj[i];
		}
	}
	return toObj;
}

// })();

function getPasteData(id) {
	baidu.g("msg").innerHTML = lang.copySuccess + "</br>";
	setTimeout(function() {
		baidu.g("msg").innerHTML = "";
	}, 5000);
	return baidu.g(id).value;
}

function createCopyButton(id, dataFrom) {
	baidu.swf.create({
		id : "copyFlash",
		url : "fClipboard_ueditor.swf",
		width : "58",
		height : "25",
		errorMessage : "",
		bgColor : "#CBCBCB",
		wmode : "transparent",
		ver : "10.0.0",
		vars : {
			tid : dataFrom
		}
	}, id);

	var clipboard = baidu.swf.getMovie("copyFlash");
	var clipinterval = setInterval(function() {
		if (clipboard && clipboard.flashInit) {
			clearInterval(clipinterval);
			clipboard.setHandCursor(true);
			clipboard.setContentFuncName("getPasteData");
			// clipboard.setMEFuncName("mouseEventHandler");
		}
	}, 500);
}
createCopyButton("clipboard", "localPath");
/* 初始化onok事件 */
function initButtons() {
	dialog.onok = function() {
		var imageUrls = [];
		imageUrls = uploadImage.getInsertList();
		if (!imageUrls.length) {
			return;
		}
		var count = uploadImage.getQueueCount();
		if (count) {
			$('.info', '#queueList').html(
					'<span style="color:red;">'
							+ '还有2个未上传文件'.replace(/[\d]/, count) + '</span>');
			return false;
		}
		var urlPrefix = editor.getOpt('imageUrlPrefix'), images = domUtils
				.getElementsByTagName(editor.document, "img");
		editor.fireEvent('saveScene');
		for (var i = 0, img; img = images[i++];) {
			var src = img.getAttribute("word_img");
			if (!src)
				continue;
			for (var j = 0, url; url = imageUrls[j++];) {
				if (src.indexOf(url.original.replace(" ", "")) != -1) {
					img.src = urlPrefix + url.url;
					img.setAttribute("_src", urlPrefix + url.url); // 同时修改"_src"属性
					img.setAttribute("title", url.title);
					domUtils.removeAttributes(img, [ "word_img", "style",
							"width", "height" ]);
					editor.fireEvent("selectionchange");
					break;
				}
			}
		}
		editor.fireEvent('saveScene');
		//hideFlash();
	};
//	dialog.oncancel = function() {
//		hideFlash();
//	}
}
