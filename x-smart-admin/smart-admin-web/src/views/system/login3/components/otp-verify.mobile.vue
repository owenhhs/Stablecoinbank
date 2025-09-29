<template>
  <div class="box-item verify-otp">
    <div class="bind-otp-title">校验MFA</div>
    <van-form ref="formRef" @submit="onVerify">
      <van-cell-group inset>
        <van-field
            v-model="verifyForm.code"
            placeholder="请输入验证码"
            :rules="[{ required: true, message: '请输入验证码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit"> 校验MFA </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import {reactive, ref} from "vue";
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

const verifyForm = reactive({
  code: undefined,
  scrip: ''
});
const rules = {
  code: [{ required: true, message: '验证码不能为空' }],
};

async function onVerify() {
  formRef.value.validate().then(async () => {
    try {
      SmartLoading.show();
      verifyForm.scrip = props.token
      const res = await loginApi.verifyEmployeeOtp(verifyForm);
      emits('next', res.data)
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