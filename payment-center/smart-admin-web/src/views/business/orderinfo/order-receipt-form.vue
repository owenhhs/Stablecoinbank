<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal
      title="补充回单"
      :width="600"
      :bodyStyle="{ maxHeight: '70vh', overflow: 'auto' }"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-divider/>
    <a-descriptions :column="1">
      <a-descriptions-item label="订单编号">{{ form.orderNo }}</a-descriptions-item>
    </a-descriptions>
    <a-form ref="formRef" :model="form" :rules="rules">
      <a-form-item label="回单" name="fileList">
        <Upload accept=".jpg,.jpeg,.png" :maxUploadSize="1" buttonText="点击上传" :default-file-list="form.fileList" @change="onReceiptChange" />
      </a-form-item>
    </a-form>

    <a-divider/>
    <template #footer>
      <a-space>
        <a-button type="primary" @click="onSubmit">确认上传</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
import {reactive, ref, computed, nextTick} from 'vue';
import _ from 'lodash';
import {Modal} from 'ant-design-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
import {orderInfoApi} from '/@/api/business/order-info/order-info-api';
import {smartSentry} from '/@/lib/smart-sentry';
import Upload from '/@/components/support/file-upload/index.vue';

// ------------------------ 事件 ------------------------

const emits = defineEmits(['reloadList']);

const rules = computed(() => ({
  fileList: [{ required: true, message: "请上传" }],
}));

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
  currency: undefined,
  country: undefined,
  collectionBank: undefined, //收款银行
  collectionCardNo: undefined, //收款银行卡号
  collectionHolder: undefined, //收款银行卡持有人
  applyTime: undefined, //申请时间
  status: undefined, //订单状态 1 待确认 2 已确认 3 挂起
  payStatus: undefined,
  createTime: undefined, //创建时间
  updateTime: undefined, //更新时间
};

let form = reactive({
  fileList: []
});

// 点击确定，验证表单
async function onSubmit() {
  Modal.confirm({
    title: '提示',
    content: '确认补充上传回单',
    okText: '确认',
    okType: 'primary',
    onOk() {
      update()
    },
    cancelText: '取消',
    onCancel() {
    },
  });
}

async function update() {
  let params = {
    orderNo: form.orderNo,
    fileId: form?.fileList?.[0]?.fileId,
  };
  SmartLoading.show();
  try {
    await orderInfoApi.receiptUpload(params);
    emits('reloadList');
    onClose();
  } catch (err) {
    smartSentry.captureError(err);
  } finally {
    SmartLoading.hide();
  }
}

// 回单上传
function onReceiptChange(fileList) {
  form.fileList = fileList;
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
