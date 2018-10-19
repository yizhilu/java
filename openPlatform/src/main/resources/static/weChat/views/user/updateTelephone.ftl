<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>手机变更</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" type="text/css" href="/weChat/css/style.css">
<#include "/weChat/views/include/seajs.ftl">
</head>
<body style="background-color: #fff">
<div class="login_bg">
    <div class="cwts orange" id="errorHide"></div>
    <div class="item">
        <input type="text" placeholder="输入手机号" id="blurphonenumber">
        <div class="send" id="SendVerification">发送验证码</div>
        <div class="send" id="disableVerification" style="display: none;"></div>
        <div>
        <input type="hidden" id="target" />
        <input type="hidden" id="mac"/>        
        </div>
        
    </div>
    <div class="item">
        <input type="text" placeholder="输入验证码" id="verification">
    </div>
    <button id="btnVerification">验 证</button>
    <button id="disablebtnVerification" style="display: none;">验 证</button>
</div>
</body>
<script type="text/javascript" src="/weChat/views/user/js/updateTele.js" ></script>
</html>