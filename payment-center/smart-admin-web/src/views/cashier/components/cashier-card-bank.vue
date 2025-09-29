
<template>
  <div class="card-content">
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">银行卡号：</div>
      <div class="desc-text" >{{ orderDetail.bankInfo.bankCardNo }}</div>
      <a-button type="text" :onclick="copyBankCardNo">
        <template #icon>
          <CopyOutlined />
        </template>
      </a-button>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">姓名：</div>
      <div class="desc-text" >{{ orderDetail.bankInfo.bankAccount }}</div>
      <a-button type="text" :onclick="copyBankAccount">
        <template #icon>
          <CopyOutlined />
        </template>
      </a-button>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">银行：</div>
      <div id="bank" class="desc-text" >{{ orderDetail.bankInfo.bankName }}</div>
      <a-button type="text" :onclick="copyBankName">
        <template #icon>
          <CopyOutlined />
        </template>
      </a-button>
    </div>
    <div :class="'desc-col' + (mobile ? ' mobile' : '')">
      <div class="desc-label">开户支行：</div>
      <div id="address" class="desc-text" >{{ orderDetail.bankInfo.bankBranch }}</div>
      <a-button type="text" :onclick="copyBankBranch">
        <template #icon>
          <CopyOutlined />
        </template>
      </a-button>
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

function copyBankCardNo() {
  copy(props.orderDetail.bankInfo.bankCardNo)
}
function copyBankAccount() {
  copy(props.orderDetail.bankInfo.bankAccount)
}
function copyBankName() {
  copy(props.orderDetail.bankInfo.bankName)
}
function copyBankBranch() {
  copy(props.orderDetail.bankInfo.bankBranch)
}

function copy(text) {
  navigator.clipboard.writeText(text)
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