<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-drawer :title="$t('business.form.detail')" width="600" :open="visibleFlag" @close="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item :label="$t('order.form.orderNo')" name="orderNo">
        <a-input disabled style="width: 100%" v-model:value="form.orderNo" :placeholder="$t('order.form.orderNo')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.status')" name="status">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.status" :placeholder="$t('order.form.status')" enumName="ORDER_STATUS" />
      </a-form-item>
      <a-form-item :label="$t('order.form.depositType')" name="depositType">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.depositType" :placeholder="$t('order.form.depositType')" enumName="PAYMENT_METHOD" />
      </a-form-item>
      <a-form-item v-if="form.depositType === 'qrcode'" :label="$t('order.form.paymentChannel')" name="paymentChannel">
        <a-input disabled style="width: 100%" v-model:value="form.paymentChannel" />
      </a-form-item>
      <a-form-item :label="$t('order.form.amount')" name="amount">
        <a-input-number disabled style="width: 100%" v-model:value="form.amount" :placeholder="$t('order.form.amount')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.currency')" name="currency">
        <a-input disabled style="width: 100%" v-model:value="form.currency" :placeholder="$t('order.form.currency')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.country')" name="country">
        <a-input disabled style="width: 100%" v-model:value="form.country" :placeholder="$t('order.form.country')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.depositHolder')" name="depositHolder">
        <a-input disabled style="width: 100%" v-model:value="form.depositHolder" :placeholder="$t('order.form.depositHolder')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.bankAccount')" name="bankAccount">
        <a-input disabled style="width: 100%" v-model:value="form.bankAccount" :placeholder="$t('order.form.bankAccount')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.receiptFileUrl')" name="receiptFileUrl">
        <a-input disabled style="width: 100%" v-model:value="form.receiptFileUrl" :placeholder="$t('order.form.receiptFileUrl')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.depositRemark')" name="depositRemark">
        <a-input disabled style="width: 100%" v-model:value="form.depositRemark" placeholder="$t('order.form.depositRemark')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.depositFileId')" name="depositFileId">
        <a-input disabled style="width: 100%" v-model:value="form.depositFileId" :placeholder="$t('order.form.depositFileId')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.callback')" name="callback">
        <a-input disabled style="width: 100%" v-model:value="form.callback" :placeholder="$t('order.form.callback')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.landingPage')" name="landingPage">
        <a-input disabled style="width: 100%" v-model:value="form.landingPage" :placeholder="$t('order.form.landingPage')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.award')" name="award">
        <a-input disabled style="width: 100%" v-model:value="form.award" :placeholder="$t('order.form.award')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.collectionBank')" name="collectionBank">
        <a-input disabled style="width: 100%" v-model:value="form.collectionBank" :placeholder="$t('order.form.collectionBank')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.collectionCardNo')" name="collectionCardNo">
        <a-input disabled style="width: 100%" v-model:value="form.collectionCardNo" :placeholder="$t('order.form.collectionCardNo')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.collectionHolder')" name="collectionHolder">
        <a-input disabled style="width: 100%" v-model:value="form.collectionHolder" :placeholder="$t('order.form.collectionHolder')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.applyTime')" name="applyTime">
        <a-input disabled style="width: 100%" v-model:value="form.applyTime" :placeholder="$t('order.form.applyTime')" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">{{ $t('business.form.cancel') }}</a-button>
        <!--        <a-button v-if="form.status === 1 && form.payStatus === 1" type="primary" @click="onSubmit">确认</a-button>-->
        <!--        <a-button v-if="form.status === 1 && form.payStatus === 1" danger @click="onReject">挂起</a-button>-->
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref, nextTick, createVNode } from 'vue';
  import _ from 'lodash';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import {useI18n} from "vue-i18n";
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
    currency: undefined,
    country: undefined,
    depositHolder: undefined, //存款人
    bankAccount: undefined, //存款账号
    depositRemark: undefined, //存款备注
    depositFileId: undefined, //回单地址,存款成功后银行回单地址,图片url
    callback: undefined, //订单确认回调地址
    landingPage: undefined, //落地页地址
    award: undefined, //奖励
    collectionBank: undefined, //收款银行
    collectionCardNo: undefined, //收款银行卡号
    collectionHolder: undefined, //收款银行卡持有人
    applyTime: undefined, //申请时间
    status: undefined, //订单状态 1 待确认 2 已确认 3 挂起
    // payStatus: undefined,
    createTime: undefined, //创建时间
    updateTime: undefined, //更新时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
  };

  // 点击确定，验证表单
  // async function onSubmit() {
  //   Modal.confirm({
  //     title: '提示',
  //     content: '确认该订单，确认后即向接入方发送处理结果',
  //     okText: '确认',
  //     okType: 'primary',
  //     onOk() {
  //       update(2);
  //     },
  //     cancelText: '取消',
  //     onCancel() {},
  //   });
  // }
  // async function onReject() {
  //   Modal.confirm({
  //     title: '提示',
  //     content: '挂起该订单？',
  //     okText: '挂起',
  //     okType: 'danger',
  //     onOk() {
  //       update(3);
  //     },
  //     cancelText: '取消',
  //     onCancel() {},
  //   });
  // }

  // async function update(status) {
  //   let params = {
  //     id: form.id,
  //     status: status,
  //   };
  //   SmartLoading.show();
  //   try {
  //     await orderInfoApi.update(params);
  //     emits('reloadList');
  //     onClose();
  //   } catch (err) {
  //     smartSentry.captureError(err);
  //   } finally {
  //     SmartLoading.hide();
  //   }
  // }

  // 新建、编辑API
  // async function save() {
  //   try {
  //     await formRef.value.validateFields();
  //   } catch (err) {
  //     message.error('参数验证错误，请仔细填写表单数据!');
  //   }
  //   SmartLoading.show();
  //   try {
  //     if (form.id) {
  //       await orderInfoApi.update(form);
  //     } else {
  //       await orderInfoApi.add(form);
  //     }
  //     message.success('操作成功');
  //     emits('reloadList');
  //     onClose();
  //   } catch (err) {
  //     smartSentry.captureError(err);
  //   } finally {
  //     SmartLoading.hide();
  //   }
  // }

  defineExpose({
    show,
  });
</script>
