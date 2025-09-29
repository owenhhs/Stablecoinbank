<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal title="订单退款" :width="600" :open="visibleFlag" @cancel="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-divider />
    <div class="tip">
      <a-textarea style="width: 100%" v-model:value="form.refundReason" :placeholder="$t('business.form.refund.placeholder')" />
    </div>
    <a-divider />
    <template #footer>
      <a-space>
        <a-button @click="onClose">{{ $t('business.form.cancel') }}</a-button>
        <a-button v-if="form.status !== 2" type="primary" @click="onSubmit">{{ $t('business.form.confirm') }}</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref } from 'vue';
  import _ from 'lodash';
  import { Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();

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
    landingPage: undefined, //落地页地址
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
    refundReason: undefined,
    payStatus: undefined,
    createTime: undefined, //创建时间
    updateTime: undefined, //更新时间
  };

  let form = reactive({ ...formDefault });

  // 点击确定，验证表单
  async function onSubmit() {
    Modal.confirm({
      title: t('business.modal.confirm.title'),
      content: t('order.refund.content'),
      okText: t('business.form.confirm'),
      okType: 'primary',
      onOk() {
        update(5);
      },
      cancelText: t('business.form.cancel'),
      onCancel() {},
    });
  }

  async function update(status) {
    let params = {
      id: form.id,
      status: status,
      refundReason: form.refundReason
    };
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
  .tip--highlight {
    color: #008dff;
    font-weight: bold;
  }

  .order-info-row {
    font-size: 14px;
    margin-bottom: 4px;

    &.title {
      display: inline-flex;
      color: #000000;
      min-width: 120px;
    }

    &.value {
      display: inline-flex;
      color: #4a4a4a;
    }
  }
</style>
