<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal
      title="订单确认"
      :width="600"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-divider/>
    <div class="order-info-row">
      <div class="order-info-row title">订单编号：</div>
      <div class="order-info-row value">{{ form.orderNo }}</div>
    </div>
    <div class="order-info-row">
      <div class="order-info-row title">申请时间：</div>
      <div class="order-info-row value">{{ form.applyTime }}</div>
    </div>
    <div class="order-info-row">
      <div class="order-info-row title">价格：</div>
      <div class="order-info-row value">{{ form.price }}元/件</div>
    </div>
    <div class="order-info-row">
      <div class="order-info-row title">存款人：</div>
      <div class="order-info-row value">{{ form.depositHolder }}</div>
    </div>
    <div class="order-info-row">
      <div class="order-info-row title">存款账号：</div>
      <div class="order-info-row value">{{ form.bankAccount }}</div>
    </div>
    <div class="order-info-row">
      <div class="order-info-row title">银行类型：</div>
      <div class="order-info-row value">{{ form.depositType }}</div>
    </div>
    <div class="order-info-row" v-if="form.depositType==='bank'">
      <div class="order-info-row title">收款银行：</div>
      <div class="order-info-row value">{{ form.collectionBank }}</div>
    </div>
    <div class="order-info-row" v-if="form.depositType==='bank'">
      <div class="order-info-row title">收款银行卡号：</div>
      <div class="order-info-row value">{{ form.collectionCardNo }}</div>
    </div>
    <div class="order-info-row" v-if="form.depositType==='qrcode'">
      <div class="order-info-row title">二维码渠道：</div>
      <div class="order-info-row value">{{ form.paymentChannel }}</div>
    </div>
    <div class="order-info-row">
      <div class="order-info-row title">存款金额：</div>
      <div class="order-info-row value">{{ form.amount }}元</div>
    </div>
    <div class="order-info-row">
      <div class="order-info-row title">交易数量：</div>
      <div class="order-info-row value">{{ form.amount / form.price }}件</div>
    </div>
    <a-divider/>
    <template #footer>
      <a-space>
        <a-button v-if="form.status === 1" type="primary" @click="onSubmit">确认</a-button>
        <a-button v-if="form.status === 1" danger @click="onReject">挂起</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
import {reactive, ref, nextTick} from 'vue';
import _ from 'lodash';
import {Modal} from 'ant-design-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
import {orderInfoApi} from '/@/api/business/order-info/order-info-api';
import {smartSentry} from '/@/lib/smart-sentry';

// ------------------------ 事件 ------------------------

const emits = defineEmits(['reloadList']);

// ------------------------ 显示与隐藏 ------------------------
// 是否显示
const visibleFlag = ref(false);

function show(rowData) {
  Object.assign(form, formDefault);
  if (rowData && !_.isEmpty(rowData)) {
    Object.assign(form, rowData);
  }
  visibleFlag.value = true;

}

function onClose() {
  Object.assign(form, formDefault);
  visibleFlag.value = false;
}

// ------------------------ 表单 ------------------------
const formDefault = {
  id: undefined,
  orderNo: undefined, //订单编号
  depositType: undefined, //银行类型，bank/qrcode
  paymentChannel: undefined, //二维码渠道，deposit_type=qrcode时有值
  amount: undefined, //存款金额
  depositHolder: undefined, //存款人
  bankAccount: undefined, //存款账号
  depositRemark: undefined, //存款备注
  depositFileId: undefined, //回单地址,存款成功后银行回单地址,图片url
  callback: undefined, //订单确认回调地址
  landingPage: undefined,//落地页地址
  skuNo: undefined, //商品编号
  skuName: undefined, //商品名称
  price: undefined, //价格
  charge: undefined, //手续费
  award: undefined, //奖励
  collectionBank: undefined, //收款银行
  collectionCardNo: undefined, //收款银行卡号
  collectionHolder: undefined, //收款银行卡持有人
  applyTime: undefined, //申请时间
  status: undefined, //订单状态 1 待确认 2 已确认 3 挂起
  payStatus: undefined,
  createTime: undefined, //创建时间
  updateTime: undefined, //更新时间
};

let form = reactive({...formDefault});

// 点击确定，验证表单
async function onSubmit() {
  Modal.confirm({
    title: '提示',
    content: '确认该订单，确认后即向接入方发送处理结果',
    okText: '确认',
    okType: 'primary',
    onOk() {
      update(2)
    },
    cancelText: '取消',
    onCancel() {
    },
  });
}

async function onReject() {
  Modal.confirm({
    title: '提示',
    content: '挂起该订单？',
    okText: '挂起',
    okType: 'danger',
    onOk() {
      update(3)
    },
    cancelText: '取消',
    onCancel() {
    },
  });
}

async function update(status) {
  let params = {
    id: form.id,
    status: status
  }
  SmartLoading.show();
  try {
    await orderInfoApi.update(params);
    emits('reloadList');
    onClose();
  } catch (err) {
    smartSentry.captureError(err);
  } finally {
    SmartLoading.hide();
  }
}

defineExpose({
  show,
});
</script>

<style scoped lang="less">
.order-info-row {
  font-size: 14px;

  &.title {
    display: inline-flex;
    color: #000000;
  }

  &.value {
    display: inline-flex;
    color: #4a4a4a;
  }
}
</style>
