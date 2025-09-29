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
        <p>{{ $t('login.welcome.title') }}</p>
        <p class="desc">
          SmartAdmin 是由 河南·洛阳
          <a target="_blank" href="https://www.1024lab.net" style="color: white; weight: bolder; font-size: 15px; text-decoration: underline"
            >1024创新实验室（1024Lab）</a
          >
          基于SpringBoot + Sa-Token + Mybatis-Plus 和 Vue3 + Vite5 + Ant Design Vue 4 (同时支持JavaScript和TypeScript双版本)
          以「高质量代码」为核心，「简洁、高效、安全」的快速开发平台。
          <br />
          <br />
          <span class="setence">
            致伟大的开发者 ：
            <br />
            &nbsp;&nbsp;&nbsp;&nbsp;我们希望用一套漂亮优雅的代码和一套整洁高效的代码规范，让大家在这浮躁的世界里感受到一股把代码写好的清流 !
            <br />
            保持谦逊，保持学习，热爱代码，更热爱生活 !<br />
            永远年轻，永远前行 !<br />
            <span class="author">
              <a target="_blank" href="https://zhuoda.vip" style="color: white; font-size: 13px; text-decoration: underline">
                1024创新实验室-主任：卓大
              </a>
            </span>
          </span>
        </p>
      </div>
      <div class="app-qr-box">
        <div class="app-qr">
          <img :src="zhuoda" />
          <span class="qr-desc"> 加微信，骚扰卓大 :) </span>
        </div>
        <div class="app-qr">
          <img :src="gzh" />
          <div class="qr-desc-marquee">
            <div class="marquee">
              <span>关注：六边形工程师，分享：赚钱、代码、生活</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="box-item login">
      <img class="login-qr" :src="loginQR" />
      <div class="login-header">
        <div class="login-title">{{ $t('login.title') }}</div>
        <LanguageSelector />
      </div>
      <a-form ref="formRef" class="login-form" :model="loginForm" :rules="rules">
        <a-form-item name="loginName">
          <a-input v-model:value.trim="loginForm.loginName" :placeholder="$t('login.username.placeholder')" />
        </a-form-item>
        <a-form-item name="password">
          <a-input-password
            v-model:value="loginForm.password"
            autocomplete="on"
            :type="showPassword ? 'text' : 'password'"
            :placeholder="$t('login.password.placeholder')"
          />
        </a-form-item>
        <a-form-item name="captchaCode">
          <a-input class="captcha-input" v-model:value.trim="loginForm.captchaCode" :placeholder="$t('login.captcha.placeholder')" />
          <img class="captcha-img" :src="captchaBase64Image" @click="getCaptcha" />
        </a-form-item>
        <a-form-item>
          <a-checkbox v-model:checked="rememberPwd">{{ $t('login.remember') }}</a-checkbox>
          <span> ( {{ $t('login.demo.account') }} )</span>
        </a-form-item>
        <a-form-item>
          <div class="btn" @click="onLogin">{{ $t('login.submit') }}</div>
        </a-form-item>
      </a-form>
      <div class="more">
        <div class="title-box">
          <p class="line"></p>
          <p class="title">{{ $t('login.other.ways') }}</p>
          <p class="line"></p>
        </div>
        <div class="login-type">
          <img :src="wechatIcon" />
          <img :src="aliIcon" />
          <img :src="douyinIcon" />
          <img :src="qqIcon" />
          <img :src="weiboIcon" />
          <img :src="feishuIcon" />
          <img :src="googleIcon" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
  import { message, notification, Button } from 'ant-design-vue';
  import { onMounted, onUnmounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useI18n } from 'vue-i18n';
  import { loginApi } from '/@/api/system/login-api';
  import LanguageSelector from '/@/components/framework/LanguageSelector.vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { LOGIN_DEVICE_ENUM } from '/@/constants/system/login-device-const';
  import { useUserStore } from '/@/store/modules/system/user';
  import zhuoda from '/@/assets/images/1024lab/zhuoda-wechat.jpg';
  import loginQR from '/@/assets/images/login/login-qr.png';
  import gzh from '/@/assets/images/1024lab/gzh.jpg';
  import wechatIcon from '/@/assets/images/login/wechat-icon.png';
  import aliIcon from '/@/assets/images/login/ali-icon.png';
  import douyinIcon from '/@/assets/images/login/douyin-icon.png';
  import qqIcon from '/@/assets/images/login/qq-icon.png';
  import weiboIcon from '/@/assets/images/login/weibo-icon.png';
  import feishuIcon from '/@/assets/images/login/feishu-icon.png';
  import googleIcon from '/@/assets/images/login/google-icon.png';

  import { buildRoutes } from '/@/router/index';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { encryptData } from '/@/lib/encrypt';
  import { h } from 'vue';
  import { localSave } from '/@/utils/local-util.js';
  import LocalStorageKeyConst from '/@/constants/local-storage-key-const.js';

  //--------------------- 登录表单 ---------------------------------
  const { t } = useI18n();

  const loginForm = reactive({
    loginName: 'admin',
    password: '',
    captchaCode: '',
    captchaUuid: '',
    loginDevice: LOGIN_DEVICE_ENUM.PC.value,
  });
  const rules = {
    loginName: [{ required: true, message: t('login.validation.username') }],
    password: [{ required: true, message: t('login.validation.password') }],
    captchaCode: [{ required: true, message: t('login.validation.captcha') }],
  };

  const showPassword = ref(false);
  const router = useRouter();
  const formRef = ref();
  const rememberPwd = ref(false);

  onMounted(() => {
    document.onkeyup = (e) => {
      if (e.keyCode == 13) {
        onLogin();
      }
    };

    notification['success']({
      message: '温馨提示',
      description: 'SmartAdmin 提供 9种 登录背景风格哦！',
      duration: 8,
      onClick: () => {},
      btn: () =>
        h(
          Button,
          {
            type: 'primary',
            target: '_blank',
            size: 'small',
            href: 'https://smartadmin.vip/views/v3/front/Login.html',
            onClick: () => {},
          },
          { default: () => '去看看' }
        ),
    });
  });

  onUnmounted(() => {
    document.onkeyup = null;
  });

  //登录
  async function onLogin() {
    formRef.value.validate().then(async () => {
      try {
        SmartLoading.show();
        // 密码加密
        let encryptPasswordForm = Object.assign({}, loginForm, {
          password: encryptData(loginForm.password),
        });
        const res = await loginApi.login(encryptPasswordForm);
        stopRefrestCaptchaInterval();
        localSave(LocalStorageKeyConst.USER_TOKEN, res.data.token ? res.data.token : '');
        message.success('登录成功');
        //更新用户信息到pinia
        useUserStore().setUserLoginInfo(res.data);
        //构建系统的路由
        buildRoutes();
        router.push('/home');
      } catch (e) {
        if (e.data && e.data.code !== 0) {
          loginForm.captchaCode = '';
          getCaptcha();
        }
        smartSentry.captureError(e);
      } finally {
        SmartLoading.hide();
      }
    });
  }

  //--------------------- 验证码 ---------------------------------

  const captchaBase64Image = ref('');
  async function getCaptcha() {
    try {
      let captchaResult = await loginApi.getCaptcha();
      captchaBase64Image.value = captchaResult.data.captchaBase64Image;
      loginForm.captchaUuid = captchaResult.data.captchaUuid;
      beginRefrestCaptchaInterval(captchaResult.data.expireSeconds);
    } catch (e) {
      console.log(e);
    }
  }

  let refrestCaptchaInterval = null;
  function beginRefrestCaptchaInterval(expireSeconds) {
    if (refrestCaptchaInterval === null) {
      refrestCaptchaInterval = setInterval(getCaptcha, (expireSeconds - 5) * 1000);
    }
  }

  function stopRefrestCaptchaInterval() {
    if (refrestCaptchaInterval != null) {
      clearInterval(refrestCaptchaInterval);
      refrestCaptchaInterval = null;
    }
  }

  onMounted(getCaptcha);
</script>
<style lang="less" scoped>
  @import './login.less';
  
  .login-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .login-title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
    }
  }
</style>
