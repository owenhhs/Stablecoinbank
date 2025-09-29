<template>
  <div :class="'desc-row' + (mobile ? '-mobile' : '')" >
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">支付金额：</div>
      <div class="desc-text">{{ '¥ ' + orderDetail.orderInfo.amount + '元' }}</div>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">付款人：</div>
      <div class="desc-text">{{ orderDetail.orderInfo.payer }}</div>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">支付时效：</div>
      <div id="timer" class="desc-text">{{ timerText }}</div>
    </div>
  </div>
</template>

<script setup>
import isMobile from 'is-mobile';

const mobile = isMobile()

const props = defineProps({
  orderDetail: {
    orderInfo: {},
    bankInfo: {},
    qrCodeInfo: {}
  },
  timerText: String
})
</script>

<style scoped lang="less">
@import 'cashier-card.less';
</style>