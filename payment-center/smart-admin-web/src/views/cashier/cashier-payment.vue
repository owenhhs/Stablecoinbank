<template>
  <div class="cashier-container">
    <a-spin :spinning="loading" size="large">
      <a-flex vertical>
        <div class="cashier-header">付款</div>
        <div :class="'cashier-base' + (mobile ? ' mobile' : '')">
          <div :class="'card' + (mobile ? ' mobile' : '')">
            <div :class="'card-title' + (mobile ? ' mobile' : '')">支付信息</div>
            <div class="card-content">
              <a-skeleton active :loading="loading">
                <cashierOrderInfo v-model:order-detail="orderDetail" v-model:timer-text="timerText" />
              </a-skeleton>
            </div>
          </div>
          <div style="display: flex; gap: 24px;">
            <div style="flex: 1;" :class="'card' + (mobile ? ' mobile' : '')">
              <div :class="'card-title' + (mobile ? ' mobile' : '')">收款信息</div>
              <a-skeleton active :loading="loading">
                <cashierCardBank v-model:order-detail="orderDetail" v-if="orderDetail.paymentMethod === 'bank'"/>
                <cashierCardQrcode v-model:order-detail="orderDetail" v-else/>
              </a-skeleton>
            </div>
            <div style="flex: 1; background: #FCF5EF;" :class="'card' + (mobile ? ' mobile' : '')" v-if="!mobile">
              <div :class="'card-title' + (mobile ? ' mobile' : '')">提示</div>
              <a-skeleton active :loading="loading">
                <cashierTips v-model:order-detail="orderDetail"/>
              </a-skeleton>
            </div>
          </div>
          <div style="display: flex; gap: 24px;" v-if="mobile">
            <div style="flex: 1; background: #FCF5EF;" :class="'card' + (mobile ? ' mobile' : '')">
              <div :class="'card-title' + (mobile ? ' mobile' : '')">提示</div>
              <a-skeleton active :loading="loading">
                <cashierTips v-model:order-detail="orderDetail"/>
              </a-skeleton>
            </div>
          </div>
          <div :class="'footer' + (mobile ? ' mobile' : '')">
            <a-button :class="'button' + (mobile ? '-mobile' : '')" type="default" size="large" :onclick="onCancel">取消订单</a-button>
            <a-button :class="'button' + (mobile ? '-mobile' : '')" type="primary" size="large" :onclick="onConfirm">我已完成转账</a-button>
          </div>
        </div>
      </a-flex>
    </a-spin>

    <a-modal v-model:open="showCancelModel" title="提示" centered :confirm-loading="cancelLoading" @ok="onCancelConfirm">
      <p>请确认您未支付此订单，如果已支付的情况下取消订单，会导致无法正常入账。</p>
    </a-modal>

    <a-modal v-model:open="showConfirmModel" title="付款确认" centered @ok="onPaymentConfirm" cancel-text="去转账">
      <p>是否使用您实名的账户完成付款？</p>
    </a-modal>

    <a-modal v-model:open="showEvidenceModel" title="请上传付款凭证" centered :confirm-loading="confirmLoading"
             @ok="onUploadConfirm" ok-text="确认上传">
      <a-upload-dragger
          :max-count="1"
          :before-upload="beforeUpload"
          @remove="handleRemove"
      >
        <p class="ant-upload-text">请点击选择凭证图片</p>
      </a-upload-dragger>
    </a-modal>
  </div>
</template>

<script setup>

import {onMounted, ref} from "vue";
import {message, Upload} from "ant-design-vue";
import {useRoute, useRouter} from 'vue-router'
import {cashierApi} from '/@/api/cashier/cashier-api'
import {PAGE_PATH_CASHIER_ERROR} from "/@/constants/common-const.js";
import cashierTips from './components/cashier-tips.vue'
import cashierCardQrcode from './components/cashier-card-qrcode.vue'
import CashierCardBank from "/@/views/cashier/components/cashier-card-bank.vue";
import CashierOrderInfo from "/@/views/cashier/components/cashier-order-info.vue";
import isMobile from 'is-mobile';

const mobile = isMobile()

const router = useRouter();

const timerText = ref('')

const lang = ref("");
const token = ref("");
const loading = ref(true);

const orderDetail = ref({
  paymentStatus: 1,
  paymentMethod: '',
  expireTime: 0,
  serverTime: 0,
  orderInfo: {},
  bankInfo: {},
  qrCodeInfo: {}
});

onMounted(() => {
  const route = useRoute()
  const langList = ['en', 'zh-cn']
  lang.value = langList.concat(route.params.lang) ? route.params.lang : 'zh-cn'
  token.value = route.params.token
  getOrderInfo();
})

function getOrderInfo() {
  cashierApi.getOrderInfo(token.value).then(res => {
    const {data} = res || {}
    orderDetail.value = data
    loading.value = false

    startTimer();
  }).catch(err => {
    console.log(err)
    if (err.data.code !== 0) {
      message.destroy()
      router.push({path: PAGE_PATH_CASHIER_ERROR})
    }
  })
}

function startTimer() {
  const intervalId = setInterval(function () {
    const timestamp = Date.now();
    const duration = (orderDetail.value.expireTime - Math.max(orderDetail.value.serverTime, timestamp)) / 1000
    let timer = duration, minutes, seconds;

    minutes = parseInt(timer / 60 % 60, 10);
    seconds = parseInt(timer % 60, 10);
    if (minutes <= 0 && seconds <= 0) {
      clearInterval(intervalId);
      window.location = orderDetail.value.jumpUrl
    } else if (minutes === 0) {
      timerText.value = seconds + " 秒";
    } else if (seconds === 0) {
      timerText.value = minutes + "分";
    } else {
      timerText.value = minutes + "分" + seconds + " 秒";
    }
  }, 1000);
}


// ------------------ 确认付款 ----------------------
const showConfirmModel = ref(false);

function onConfirm() {
  showConfirmModel.value = true;
}

function onPaymentConfirm() {
  showConfirmModel.value = false;
  showEvidenceModel.value = true;
}

// ------------------- 上传凭证 -------------------------
const showEvidenceModel = ref(false);
const confirmLoading = ref(false);
const file = ref(undefined);

function onUploadConfirm() {
  if (file.value === undefined) {
    message.error("请上传付款凭证")
    return;
  }
  confirmLoading.value = true;

  const formData = new FormData();
  formData.append('file', file.value);

  cashierApi.confirmPayment(token.value, formData).then(res => {
    showEvidenceModel.value = false;
    message.success('凭证上传成功', 2).then(() => {
      window.location = orderDetail.value.jumpUrl
    })
  }).finally(() => {
    confirmLoading.value = false;
  })
}

const handleRemove = _ => {
  file.value = undefined;
};
const beforeUpload = f => {
  const isPNG = f.type === 'image/png';
  const isJPG = f.type === 'image/jpeg';

  if (!isPNG && !isJPG) {
    message.error(`请上传图片`);
    return Upload.LIST_IGNORE;
  }
  file.value = f;
  return false;
};


// ------------------ 取消订单 ----------------------
const showCancelModel = ref(false);
const cancelLoading = ref(false);

function onCancel() {
  showCancelModel.value = true;
}

/**
 * 确认取消
 */
function onCancelConfirm() {
  cancelLoading.value = true;
  cashierApi.cancelPayment(token.value).then(res => {
    showCancelModel.value = false;
    message.success('付款取消成功', 2).then(() => {
      window.location = orderDetail.value.jumpUrl
    })
  }).finally(() => {
    cancelLoading.value = false;
  })
}

</script>

<style scoped lang="less">
@import "cashier-payment.less";
</style>