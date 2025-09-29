import { message } from 'ant-design-vue';
import { onMounted, onUnmounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { loginApi } from '/@/api/system/login-api';
import { SmartLoading } from '/@/components/framework/smart-loading';
import { LOGIN_DEVICE_ENUM } from '/@/constants/system/login-device-const';
import { useUserStore } from '/@/store/modules/system/user';
import loginQR from '/@/assets/images/login/login-qr.png';
import leftBg2 from '/@/assets/images/login/left-bg2.png';
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
import { localSave } from '/@/utils/local-util.js';
import LocalStorageKeyConst from '/@/constants/local-storage-key-const.js';

export default function setup() {
  //--------------------- 登录表单 ---------------------------------

  const loginForm = reactive({
    loginName: 'admin',
    password: '',
    captchaCode: '',
    captchaUuid: '',
    loginDevice: LOGIN_DEVICE_ENUM.PC.value,
  });
  const rules = {
    loginName: [{ required: true, message: '用户名不能为空' }],
    password: [{ required: true, message: '密码不能为空' }],
    captchaCode: [{ required: true, message: '验证码不能为空' }],
  };

  const showPassword = ref(false);
  const router = useRouter();
  const formRef = ref();
  const rememberPwd = ref(false);
  const tip = ref(0);
  const token = ref();


  onMounted(() => {
    document.onkeyup = (e) => {
      if (e.keyCode == 13) {
        onLogin();
      }
    };
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
        if (res.data.otpTip > 0) {
          tip.value = res.data.otpTip
          token.value = res.data.token;
          return
        }
        await toHome(res.data)
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

  async function toHome(data) {
    localSave(LocalStorageKeyConst.USER_TOKEN, data.token ? data.token : '');
    message.success('登入成功');
    //更新用户信息到pinia
    useUserStore().setUserLoginInfo(data);
    //构建系统的路由
    buildRoutes();
    router.push('/home');
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

  return {
    loginForm,
    rules,
    showPassword,
    formRef,
    rememberPwd,
    token,
    tip,
    captchaBase64Image,
    getCaptcha,
    onLogin,
    toHome
  }
}
