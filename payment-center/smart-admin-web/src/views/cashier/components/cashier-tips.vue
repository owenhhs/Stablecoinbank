<template>
  <div class="card-content" v-if="orderDetail.paymentMethod !== undefined">
    <div v-for="(tip, index) in filteredTips()" :key="index">
      <div :class="'desc-col' + (mobile ? ' mobile' : '')" v-if="tip.text || tip[orderDetail.paymentMethod]">
        <div class="desc-text">
        <span :style="tip.style">
          {{ `${index + 1}、` + (tip[orderDetail.paymentMethod] ? tip[orderDetail.paymentMethod] : tip.text) }}
        </span>
        </div>
      </div>
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
})

const tipList = [
  {
    text: '请按实付金额转账'
  },
  {
    bank: '请使用本人银行卡支付，不要使用第三者银行卡付款，否则会造成交易失败',
    Alipay: '请使用本人支付宝账户支付，不要使用第三者账户付款，否则会造成交易失败',
    WeChat: '请使用本人微信支付，不要使用第三者账户付款，否则会造成交易失败'
  },
  {
    text: '请在支付完成之后点击我已完成转账'
  },
  {
    style: "color: #E13730",
    text: '汇款请勿备注任何信息'
  },
  {
    style: "color: #E13730",
    text: '付款完成后，请务必上传付款凭证（截图）。否则将导致延迟上账或无法上账。'
  },
  {
    style: "color: #E13730",
    Alipay: '尊敬的客户，使用支付宝支付时，可能会出现“风险提示”。建议您更换一个与当前支付金额较为接近的日常消费金额。若多次尝试后仍未成功，请联系客服协助，谢谢配合！'
  },
  {
    Alipay: '建议您通过支付宝余额进行支付，将大大提高成功率。'
  },
  {
    Alipay: '由于个别地区网络原因，可能出现付款码展示延迟，请您耐心等待',
    WeChat: '由于个别地区网络原因，可能出现付款码展示延迟，请您耐心等待'
  }
]

function filteredTips() {
  return tipList.filter(tip =>
      tip.text || tip[props.orderDetail.paymentMethod]
  );
}

</script>

<style scoped lang="less">
@import 'cashier-card.less';
</style>