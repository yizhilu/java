angular
  .module('angular-markdown-editor', ['ui.bootstrap','oc.lazyLoad'])
  .directive('markdownEditor', ['$rootScope','$uibModal', function ($rootScope,$uibModal) {
    return {
        restrict: 'A',
        require:  'ngModel',
        link: function(scope, element, attrs, ngModel) {
          var options = scope.$eval(attrs.markdownEditor);

            // Only initialize the $.markdown plugin once.
            if (! element.hasClass('processed')) {
                element.addClass('processed');

                var markdownOptions = {
                  autofocus: options.autofocus || false, //表示编辑器将在实例化之后自动聚焦。默认为false
                  saveable: options.saveable || false,
                  savable: options.savable || false,//表示该编辑器将具有保存按钮和操作。默认为false
                  iconlibrary: options.iconlibrary || 'glyph',//要使用的图标库。
                  hideable: options.hideable || false,//如果设置为，true那么编辑器将隐藏在blur事件中。默认为false
                  width: options.width || 'inherit',//编辑宽度
                  height: options.height || 'inherit',//编辑高度
                  resize: options.resize || 'none',//选项来禁用或更改调整大小的属性，可能的值none，both，horizontal，vertical。默认none 如果启用此选项，用户将能够调整编辑器和预览屏幕的大小。
                  language: options.language || 'en',//本地化设置。默认为en
                  footer: options.footer || '',//页脚dom。可以是字符串或回调。默认为空字符串
                  fullscreen: options.fullscreen || { enable: true, icons: {}},//	包含enable（bool）和icons（object）键。
                  hiddenButtons: options.hiddenButtons || [],//要隐藏的按钮名称的数组或字符串。默认为空字符串
                  disabledButtons: options.disabledButtons || [],//要禁用的按钮名称的数组或字符串。默认为空字符串
                  initialstate: options.initialstate || 'editor',
                  parser: options.parser || null,
                  dropZoneOptions: options.dropZoneOptions || {},//启用与DropZone的集成，允许通过拖放进行文件上传/链接。这些选项直接传递给DropZone库。这里描述了有效的选项
                  enableDropDataUri: options.enableDropDataUri || false,
                  showButtons: options.showButtons || {},
                  additionalButtons: options.additionalButtons || (options.addExtraButtons ? addNewButtons() : []),

                  //-- Events/Hooks --
                  // each of them are defined as callback available in the directive
                  // example: <textarea markdown-editor="{'iconlibrary': 'fa'}" on-fullscreen-exit="vm.exitFullScreenCallback()"></textarea>
                  //  NOTE: If you want this one to work, you will have to manually download the JS file, not sure why but they haven't released any versions in a while
                  //       https://github.com/toopay/bootstrap-markdown/tree/master/js
                  onPreview: function (e) { runScopeFunction(scope, attrs.onPreview, e); },
                  onPreviewEnd: function (e) { runScopeFunction(scope, attrs.onPreviewEnd, e); },
                  onSave: function (e) { runScopeFunction(scope, attrs.onSave, e); },
                  onBlur: function (e) { runScopeFunction(scope, attrs.onBlur, e); },
                  onFocus: function (e) { runScopeFunction(scope, attrs.onFocus, e); },
                  onFullscreen: function (e) { runScopeFunction(scope, attrs.onFullscreen, e); },
                  onSelect: function (e) { runScopeFunction(scope, attrs.onSelect, e); },
                  onFullscreenExit: function (e) { runScopeFunction(scope, attrs.onFullscreenExit, e); },
                  onChange: function(e) {
                    // When a change occurs, we need to update scope in case the user clicked one of the plugin buttons
                    // (which isn't the same as a keydown event that angular would listen for).
                    ngModel.$setViewValue(e.getContent());

                    runScopeFunction(scope, attrs.onChange, e);
                  },
                  onShow: function (e) {
                    // keep the Markdown Object in $rootScope so that it's available also from anywhere (like in the parent controller)
                    // we will keep this in an object under the ngModel name so that it also works having multiple editor in same controller
                    $rootScope.markdownEditorObjects = $rootScope.markdownEditorObjects || {};
                    $rootScope.markdownEditorObjects[ngModel.$name] = e;

                    if (!!attrs.onShow) {
                      runScopeFunction(scope, attrs.onShow, e);
                    }
                  }
                };

                // Setup the markdown WYSIWYG.

                // if the markdown editor was added dynamically the markdown function will be undefined
                // so it has to be called explicitely
                if (element.markdown === undefined){
                    element.data('markdown', (data = new $.fn.markdown.Constructor(element[0], markdownOptions)))
                } else {
                    element.markdown(markdownOptions);
                }
                /**
                 * Add new extra buttons: Strikethrough & Table
                 * @return mixed additionButtons
                 */
                function addNewButtons() {
                  return [[{
                        name: "groupFont",
                        data: [{
                          name: "cmdStrikethrough",
                          toggle: false,
                          title: "Strikethrough",
                          icon: {
                            fa: "fa fa-strikethrough",
                            glyph: "glyphicon glyphicon-minus"
                          },
                          callback: function(e) {
                            // Give/remove ~~ surround the selection
                            var chunk, cursor, selected = e.getSelection(),
                              content = e.getContent();

                            if (selected.length === 0) {
                              // Give extra word
                              chunk = e.__localize('strikethrough');
                            } else {
                              chunk = selected.text;
                            }

                            // transform selection and set the cursor into chunked text
                            if (content.substr(selected.start - 2, 2) === '~~' &&
                              content.substr(selected.end, 2) === '~~') {
                              e.setSelection(selected.start - 2, selected.end + 2);
                              e.replaceSelection(chunk);
                              cursor = selected.start - 2;
                            } else {
                              e.replaceSelection('~~' + chunk + '~~');
                              cursor = selected.start + 2;
                            }

                            // Set the cursor
                            e.setSelection(cursor, cursor + chunk.length);
                          }
                        }]
                  },
                  {
                        name: "groupTable",
                        data: [{
                          name: "cmdTable",
                          toggle: false,
                          title: "Table",
                          icon: {
                            fa: "fa fa-table",
                            glyph: "glyphicon glyphicon-th"
                          },
                          callback: function(e) {
                            // Replace selection with some drinks
                            var chunk, cursor,
                                selected = e.getSelection(), content = e.getContent(),
                                chunk = "\n| Tables        | Are           | Cool  | \n"
                                + "| ------------- |:-------------:| -----:| \n"
                                + "| col 3 is      | right-aligned | $1600 | \n"
                                + "| col 2 is      | centered      |   $12 | \n"
                                + "| zebra stripes | are neat      |    $1 |"

                            // transform selection and set the cursor into chunked text
                            e.replaceSelection(chunk)
                            cursor = selected.start

                            // Set the cursor
                            e.setSelection(cursor,cursor+chunk.length);
                          }
                        }]
                  },{
                      name: 'groupCmdImage',
                      data: [{
                          name: 'myImage',
                          title: '图片',
                          hotkey: 'Ctrl+G',
                          icon: { glyph: 'glyphicon glyphicon-picture', fa: 'fa fa-picture-o', 'fa-3': 'icon-picture' },
                          callback: function(e){
                            // Give ![] surround the selection and prepend the image link
                            var chunk, cursor, selected = e.getSelection(), content = e.getContent(), link;
                            if (selected.length === 0) {
                              // Give extra word
                              chunk = e.__localize('enter image description here');
                            } else {
                              chunk = selected.text;
                            }
                            var meModal = $uibModal.open({
                                animation: true,         //动画
                                //appendTo:'body',        //将模态框添加到某个元素
                                ariaDescribedBy: '',      //描述
                                ariaLabelledBy: '',          //描述
                                backdrop: true,                          //背景
                                backdropClass: 'modal_bg',           //背景class
                                windowClass: 'modal_container',     //modal容器class
                                templateUrl: 'views/markdown/image/upload.html',
                                size: 'md',
                                controller: 'uploadImageCtr',
                                resolve: {
                                	loadPlugIn: ['$ocLazyLoad', function ($ocLazyLoad) {
                                        return $ocLazyLoad
                                            .load(['views/markdown/image/upload.js'])
                                    }],
                                    e:function(){
                                    	return e;
                                    }
                                }
                            });

                            meModal.result.then(function (result) {
                                //保存调用该函数
                                link =result.url;
                                e=result.e;
                                console.log(result);
                                // Give ![] surround the selection and prepend the image link
                                var chunk, cursor, selected = e.getSelection(), content = e.getContent(), link;
                                if (selected.length === 0) {
                                  // Give extra word
                                  chunk = e.__localize('enter image description here');
                                } else {
                                  chunk = selected.text;
                                }
                                var urlRegex = new RegExp('^((http|https)://|(//))[a-z0-9]', 'i');
                                if (link !== null && link !== '' && link !== 'http://' && urlRegex.test(link)) {
                                  var sanitizedLink = $('<div>'+link+'</div>').text();

                                  // transform selection and set the cursor into chunked text
                                  e.replaceSelection('!['+chunk+']('+sanitizedLink+' "'+e.__localize('enter image title here')+'")');
                                  cursor = selected.start+2;

                                  // Set the next tab
                                  e.setNextTab(e.__localize('enter image title here'));

                                  // Set the cursor
                                  e.setSelection(cursor,cursor+chunk.length);
                                }
                            }, function (a) {
                                //关闭调用该函数
                                // console.log(a)
                                // layer.msg('玩命提示中');
                            });
                            

                          }
                        }]
                    },]];
                } 
            }
        }
    };
}]);



/** Evaluate a function name passed as string and run it from the scope.
  * The function name could be passed with/without brackets "()", in any case we will run the function
  * @param object self object
  * @param string function passed as a string
  * @param object Markdown Editor object
  * @result mixed result
  */
function runScopeFunction(scope, fnString, editorObject) {
  if (!fnString) {
    return;
  }

  // Find if our function has the brackets "()"
  if (/\({1}.*\){1}/gi.test(fnString)) {
    var lastParenthese = fnString.indexOf(")");
    scope.$markdownEditorObject = editorObject;
    fnString = fnString.replace(")", "$markdownEditorObject)");
    result = scope.$eval(fnString);
  } else {
    var fct = objectFindById(scope, fnString, '.');
    if (typeof fct === "function") {
      result = fct(editorObject);
    }
  }
  return result;
}

/** Find a property inside an object.
 * If a delimiter is passed as argument, we will split the search ID before searching
 * @param object: source object
 * @param string: searchId
 * @return mixed: property found
 */
function objectFindById(sourceObject, searchId, delimiter) {
  var split = (!!delimiter) ? searchId.split(delimiter) : searchId;

  for (var k = 0, kln = split.length; k < kln; k++) {
    if(!!sourceObject[split[k]]) {
      sourceObject = sourceObject[split[k]];
    }
  }
  return sourceObject;
}