<template>
  <div class="card-content">
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">收款码：</div>
      <div id="desc-text" class="desc-text" >{{ orderDetail.paymentMethod === 'Alipay' ? '支付宝' : '微信支付' }}</div>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')" v-if="orderDetail.qrCodeInfo.username.length > 0">
      <div class="desc-label" >收款人：</div>
      <div id="user-name" class="desc-text" >{{orderDetail.qrCodeInfo.username}}</div>
      <a-button type="text" :onclick="copyUser">
        <template #icon>
          <CopyOutlined />
        </template>
      </a-button>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')" v-if="orderDetail.qrCodeInfo.account.length > 0">
      <div class="desc-label" >收款账号：</div>
      <div id="user-account" class="desc-text" >{{orderDetail.qrCodeInfo.account}}</div>
      <a-button type="text" :onclick="copy">
        <template #icon>
          <CopyOutlined />
        </template>
      </a-button>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label"></div>
      <div id="qrcode-img-box" class="qrcode-img-box">
        <img class="qrcode-img" :src="orderDetail.qrCodeInfo.qrcode" alt="qrcode" />
      </div>
    </div>
  </div>
</template>

<script setup>
import isMobile from 'is-mobile';
import {message} from "ant-design-vue";

const mobile = isMobile()

const props = defineProps({
  orderDetail: {
    orderInfo:{},
    bankInfo:{},
    qrCodeInfo:{}
  },
})

function copyUser() {
  navigator.clipboard.writeText(props.orderDetail.qrCodeInfo.username)
      .then(() => {
        message.success('复制成功')
      })
      .catch((err) => {
        message.error('复制失败')
      });
}

function copy() {
  navigator.clipboard.writeText(props.orderDetail.qrCodeInfo.account)
      .then(() => {
        message.success('复制成功')
      })
      .catch((err) => {
        message.error('复制失败')
      });
}
</script>

<style scoped lang="less">
@import 'cashier-card.less';
</style>