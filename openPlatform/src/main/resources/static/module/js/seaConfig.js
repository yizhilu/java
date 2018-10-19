seajs.config({
	alias : {
		// css
		'style' : '/module/css/style.css',
		'layercss' : '/module/lib/mobile/need/layer.css',
		// js包
		'layer' : '/module/lib/mobile/layer.js',
		'jquery' : '/module/lib/jquery/jquery-3.3.1-seajs.min.js',
		'jqueryLazyLoad' : '/module/js/lib/jquery.lazyload.js',
		'swiper' : '/module/lib/swiper/swiper-3.3.1.min.sea.js',
		'common' : '/module/js/common.js',
		'commonDate' : '/module/js/commonDate.js',
		'cookies' : '/module/js/cookies.js',
		// 路径配置文件
		'config' : '/module/js/config.js',
		//
		'loginModule' : "/module/views/login/js/loginModule.js",
		'myStudiesModule' : "/module/views/user/js/myStudiesModule.js",
		'continueStudyingModule' : "/module/views/user/js/continueStudyingModule.js",
		'exercisesModule' : "/module/views/exercises/js/exercisesModule.js",
		'myAlreadyStudied' : "/module/views/user/js/myAlreadyStudiedModule.js",
		'myExercisesModule' : "/module/views/user/js/myExercisesModule.js",
		'displayNoticModule': "/module/views/notifications/js/displayNotic.js",
    'courseExercisesModule' : "/module/views/exercises/js/courseExercisesModule.js",
    'examinationModule' : "/module/views/examination/js/examinationModule.js",
    'homeModule' : "/module/views/home/js/homeModule.js",
    'addCommentsModule' : "/module/views/wrongTopicComment/js/addCommentsModule.js",
    'studyExchangeModule' : "/module/views/studyExchange/js/studyExchangeModule.js",
    'studyExchangeDeteilsModule' : "/module/views/studyExchange/js/studyExchangeDeteilsModule.js"


	},
	preload : [ "jquery", "style", "layercss" ],
	map : [ [ /^(.*\.(?:css|js))(.*)$/i, '$1?v=0003' ] ], // map,批量更新时间戳
	charset : 'utf-8',
	timeout : 20000,
	debug : false
});
