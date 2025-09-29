<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-drawer
      title="详情"
      width="600"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item label="订单编号" name="orderNo">
        <a-input disabled style="width: 100%" v-model:value="form.orderNo" placeholder="订单编号"/>
      </a-form-item>
      <a-form-item label="银行类型，bank/qrcode" name="depositType">
        <a-input disabled style="width: 100%" v-model:value="form.depositType" placeholder="银行类型，bank/qrcode"/>
      </a-form-item>
      <a-form-item label="二维码渠道，deposit_type=qrcode时有值" name="paymentChannel">
        <a-input disabled style="width: 100%" v-model:value="form.paymentChannel"
                 placeholder="二维码渠道，deposit_type=qrcode时有值"/>
      </a-form-item>
      <a-form-item label="存款金额" name="amount">
        <a-input-number disabled style="width: 100%" v-model:value="form.amount" placeholder="存款金额"/>
      </a-form-item>
      <a-form-item label="存款人" name="depositHolder">
        <a-input disabled style="width: 100%" v-model:value="form.depositHolder" placeholder="存款人"/>
      </a-form-item>
      <a-form-item label="存款账号" name="bankAccount">
        <a-input disabled style="width: 100%" v-model:value="form.bankAccount" placeholder="存款账号"/>
      </a-form-item>
      <a-form-item label="存款备注" name="depositRemark">
        <a-input disabled style="width: 100%" v-model:value="form.depositRemark" placeholder="存款备注"/>
      </a-form-item>
      <a-form-item label="回单地址,存款成功后银行回单地址,图片url" name="depositFileId">
        <a-input disabled style="width: 100%" v-model:value="form.depositFileId"
                 placeholder="回单地址,存款成功后银行回单地址,图片url"/>
      </a-form-item>
      <a-form-item label="订单确认回调地址" name="callback">
        <a-input disabled style="width: 100%" v-model:value="form.callback" placeholder="订单确认回调地址"/>
      </a-form-item>
      <a-form-item label="落地页地址" name="landingPage">
        <a-input disabled style="width: 100%" v-model:value="form.landingPage" placeholder="落地页地址"/>
      </a-form-item>
      <a-form-item label="商品编号" name="skuNo">
        <a-input disabled style="width: 100%" v-model:value="form.skuNo" placeholder="商品编号"/>
      </a-form-item>
      <a-form-item label="商品名称" name="skuName">
        <a-input disabled style="width: 100%" v-model:value="form.skuName" placeholder="商品名称"/>
      </a-form-item>
      <a-form-item label="价格" name="price">
        <a-input-number disabled style="width: 100%" v-model:value="form.price" placeholder="价格"/>
      </a-form-item>
      <a-form-item label="手续费" name="charge">
        <a-input-number disabled style="width: 100%" v-model:value="form.charge" placeholder="手续费"/>
      </a-form-item>
      <a-form-item label="奖励" name="award">
        <a-input disabled style="width: 100%" v-model:value="form.award" placeholder="奖励"/>
      </a-form-item>
      <a-form-item label="收款银行" name="collectionBank">
        <a-input disabled style="width: 100%" v-model:value="form.collectionBank" placeholder="收款银行"/>
      </a-form-item>
      <a-form-item label="收款银行卡号" name="collectionCardNo">
        <a-input disabled style="width: 100%" v-model:value="form.collectionCardNo" placeholder="收款银行卡号"/>
      </a-form-item>
      <a-form-item label="收款银行卡持有人" name="collectionHolder">
        <a-input disabled style="width: 100%" v-model:value="form.collectionHolder" placeholder="收款银行卡持有人"/>
      </a-form-item>
      <a-form-item label="申请时间" name="applyTime">
        <a-date-picker disabled show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.applyTime"
                       style="width: 100%"
                       placeholder="申请时间"/>
      </a-form-item>
      <a-form-item label="订单状态 1 待确认 2 已确认 3 挂起" name="status">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.status"
                         placeholder="订单状态 1 待确认 2 已确认 3 挂起" enumName="ORDER_STATUS"/>
      </a-form-item>
      <a-form-item label="付款状态 1 待付款 2 已付款 3 取消" name="status">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.payStatus"
                         placeholder="付款状态 1 待付款 2 已付款 3 取消" enumName="ORDER_PAY_STATUS"/>
      </a-form-item>
      <a-form-item label="创建时间" name="createTime">
        <a-date-picker disabled show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime"
                       style="width: 100%"
                       placeholder="创建时间"/>
      </a-form-item>
      <a-form-item label="更新时间" name="updateTime">
        <a-date-picker disabled show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.updateTime"
                       style="width: 100%"
                       placeholder="更新时间"/>
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
<!--        <a-button v-if="form.status === 1 && form.payStatus === 1" type="primary" @click="onSubmit">确认</a-button>-->
<!--        <a-button v-if="form.status === 1 && form.payStatus === 1" danger @click="onReject">挂起</a-button>-->
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
import {reactive, ref, nextTick, createVNode} from 'vue';
import _ from 'lodash';
import {message, Modal} from 'ant-design-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
import {orderInfoApi} from '/@/api/business/order-info/order-info-api';
import {smartSentry} from '/@/lib/smart-sentry';
import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

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
  nextTick(() => {
    formRef.value.clearValidate();
  });
}

function onClose() {
  Object.assign(form, formDefault);
  visibleFlag.value = false;
}

// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref();

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

const rules = {
  orderNo: [{required: true, message: '订单编号 必填'}],
  depositType: [{required: true, message: '银行类型，bank/qrcode 必填'}],
  paymentChannel: [{required: true, message: '二维码渠道，deposit_type=qrcode时有值 必填'}],
  amount: [{required: true, message: '存款金额 必填'}],
  depositHolder: [{required: true, message: '存款人 必填'}],
  bankAccount: [{required: true, message: '存款账号 必填'}],
  depositRemark: [{required: true, message: '存款备注 必填'}],
  depositFileId: [{required: true, message: '回单地址,存款成功后银行回单地址,图片url 必填'}],
  callback: [{required: true, message: '订单确认回调地址 必填'}],
  landingPage: [{required: true, message: '落地页地址 必填'}],
  sku: [{required: true, message: '商品编号 必填'}],
  applyTime: [{required: true, message: '申请时间 必填'}],
  status: [{required: true, message: '订单状态 1 待确认 2 已确认 3 挂起'}],
  createTime: [{required: true, message: '创建时间 必填'}],
  updateTime: [{required: true, message: '更新时间 必填'}],
};

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

// 新建、编辑API
async function save() {
  try {
    await formRef.value.validateFields();
  } catch (err) {
    message.error('参数验证错误，请仔细填写表单数据!');
  }
  SmartLoading.show();
  try {
    if (form.id) {
      await orderInfoApi.update(form);
    } else {
      await orderInfoApi.add(form);
    }
    message.success('操作成功');
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
