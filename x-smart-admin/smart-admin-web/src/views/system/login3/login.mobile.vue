<template>
  <div>
    <div class="container">
      <div class="title">ZFX运用管理后台</div>
      <div class="welcome">
        <img class="welcome-img" :src="leftBg2" />
      </div>
      <van-form ref="formRef" @submit="onLogin" v-if="tip === 0">
        <van-cell-group inset>
          <van-field
            v-model="loginForm.loginName"
            name="用户名"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请填写用户名' }]"
          />
          <van-field
            v-model="loginForm.password"
            type="password"
            name="密码"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
          />
          <van-field
            v-model="loginForm.captchaCode"
            name="验证码"
            label="验证码"
            placeholder="请输入验证码"
            :rules="[{ required: true, message: '请输入验证码' }]"
          >
            <template #button>
              <img class="captcha-img" :src="captchaBase64Image" @click="getCaptcha" />
            </template>
          </van-field>
        </van-cell-group>
        <div style="margin: 16px">
          <van-button round block type="primary" native-type="submit"> 登录 </van-button>
        </div>
      </van-form>
      <OtpBind v-if="tip === 1" :token="token" @next="tip = 2"/>
      <OtpVerify v-if="tip === 2" :token="token" @next="toHome"/>
    </div>
  </div>
</template>

<script setup>
  import setup from './setup';
  import leftBg2 from '/@/assets/images/login/left-bg2.png';
  import OtpBind from "/@/views/system/login3/components/otp-bind.mobile.vue";
  import OtpVerify from "/@/views/system/login3/components/otp-verify.mobile.vue";

  const { loginForm, rules, showPassword, formRef, rememberPwd, captchaBase64Image, token, tip, getCaptcha, onLogin, toHome } = setup();
</script>

<style lang="less" scoped>
.container {
  padding-top: 40px;
}

.title {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
}

.welcome {
  text-align: center;
}

.welcome-img {
  width: 50%;
  margin: 0 auto;
}

.captcha-img {
  height: 20px;
}
</style>