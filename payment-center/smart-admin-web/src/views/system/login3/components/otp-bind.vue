<template>
  <div class="box-item bind-otp">
    <div class="bind-otp-title">绑定MFA</div>
    <div class="bind-otp-qr-box">
      <a-qrcode class="bind-otp-qrcode" :size='240' :value="qrcode" />
    </div>
    <div class="bind-otp-qr-tip">
      请使用 <span class="google-authenticator">Google Authenticator</span> 扫描二维码绑定
    </div>
    <a-form ref="formRef" class="login-form" :model="bindForm" :rules="rules">
      <a-form-item name="code">
        <a-input v-model:value.trim="bindForm.code" placeholder="请输入验证码" />
      </a-form-item>
      <a-form-item>
        <div class="btn" @click="onBind">绑定MFA</div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import { loginApi } from '/@/api/system/login-api';
import {SmartLoading} from "/@/components/framework/smart-loading/index.js";
import {smartSentry} from "/@/lib/smart-sentry.js";

const props = defineProps({
  token: {
    type: String,
    default: null,
  },
});

let emits = defineEmits(['next']);

const formRef = ref();

const bindForm = reactive({
  code: undefined,
  scrip: ''
});
const rules = {
  code: [{ required: true, message: '验证码不能为空' }],
};
const qrcode = ref('')

onMounted(() => {
  getEmployeeOtpQrcode()
});

async function getEmployeeOtpQrcode() {
  const res = await loginApi.getEmployeeOtpQrcode(props.token);
  qrcode.value = res.data
}

async function onBind() {
  formRef.value.validate().then(async () => {
    try {
      SmartLoading.show();
      bindForm.scrip = props.token
      await loginApi.bindEmployeeOtp(bindForm);
      emits('next')
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  });
}


</script>

<style scoped lang="less">
.bind-otp-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  color: #1e1e1e;
  margin-bottom: 35px;
}
.bind-otp-qr-box {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-top: 20px;
}
.bind-otp-qr-tip {
  font-size: 16px;
  text-align: center;
  color: #9e9d9d;
  margin-top: 15px;
  margin-bottom: 10px;
}
.google-authenticator {
  color: #2a79ef;
}
.ant-input,
.ant-input-affix-wrapper {
  height: 44px;
  border: 1px solid #ededed;
  border-radius: 4px;
}
.btn {
  width: 350px;
  height: 50px;
  background: #1748FD;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 700;
  text-align: center;
  color: #ffffff;
  line-height: 50px;
  cursor: pointer;
}
</style>