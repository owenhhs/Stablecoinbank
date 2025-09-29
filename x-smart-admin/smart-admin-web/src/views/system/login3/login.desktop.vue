<!--
  * 登录
  * 
  * @Author:    1024创新实验室-主任：卓大 
  * @Date:      2022-09-12 22:34:00 
  * @Wechat:    zhuda1024 
  * @Email:     lab1024@163.com 
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012 
  *
-->
<template>
  <div class="login-container">
    <div class="box-item desc">
      <div class="welcome">
        <p>欢迎登录 ZFX</p>
        <p class="sub-welcome">---------- ZFX运用管理后台 ----------</p>
      </div>
      <img class="welcome-img" :src="leftBg2" />
    </div>
    <div class="box-item login" v-if="tip === 0">
      <!--      <img class="login-qr" :src="loginQR" />-->
      <div class="login-title">账号登录</div>
      <a-form ref="formRef" class="login-form" :model="loginForm" :rules="rules">
        <a-form-item name="loginName">
          <a-input v-model:value.trim="loginForm.loginName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item name="password">
          <a-input-password
            v-model:value="loginForm.password"
            autocomplete="on"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码：至少三种字符，最小 8 位"
          />
        </a-form-item>
        <a-form-item name="captchaCode">
          <a-input class="captcha-input" v-model:value.trim="loginForm.captchaCode" placeholder="请输入验证码" />
          <img class="captcha-img" :src="captchaBase64Image" @click="getCaptcha" />
        </a-form-item>
        <a-form-item>
          <a-checkbox v-model:checked="rememberPwd">记住密码</a-checkbox>
          <!--          <span> ( 账号：admin, 密码：123456)</span>-->
        </a-form-item>
        <a-form-item>
          <div class="btn" @click="onLogin">登录</div>
        </a-form-item>
      </a-form>
    </div>
    <OtpBind v-if="tip === 1" :token="token" @next="tip = 2"/>
    <OtpVerify v-if="tip === 2" :token="token" @next="toHome"/>
  </div>
</template>
<script setup>
  import setup from './setup';
  import leftBg2 from '/@/assets/images/login/left-bg2.png';
  import OtpBind from "/@/views/system/login3/components/otp-bind.desktop.vue";
  import OtpVerify from "/@/views/system/login3/components/otp-verify.desktop.vue";

  const { loginForm, rules, showPassword, formRef, rememberPwd, captchaBase64Image, token, tip, getCaptcha, onLogin, toHome } = setup();
</script>
<style lang="less" scoped>
  @import './login.less';
</style>
