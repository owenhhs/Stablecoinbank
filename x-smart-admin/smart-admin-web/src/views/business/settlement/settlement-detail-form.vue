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
      <a-form-item :label="$t('settle.table.column.tradeDate')" name="tradeDate">
        <a-input disabled style="width: 100%" v-model:value="form.tradeDate" placeholder="交易日期" />
      </a-form-item>
      <a-form-item :label="$t('settle.table.column.tradeDate')" name="settleId">
        <a-input disabled style="width: 100%" v-model:value="form.settleId" placeholder="结算单Id" />
      </a-form-item>
      <a-form-item :label="$t('order.form.orderNo')" name="orderNo">
        <a-input disabled style="width: 100%" v-model:value="form.orderNo" placeholder="订单编号" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.businessScope')" name="businessScope">
        <a-input-number disabled style="width: 100%" v-model:value="form.businessScope" placeholder="业务范围" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.amount')" name="amount">
        <a-input disabled style="width: 100%" v-model:value="form.amount" placeholder="金额" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.merName')" name="merName">
        <a-input disabled style="width: 100%" v-model:value="form.merName" placeholder="商户名称" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.merCode')" name="merCode">
        <a-input disabled style="width: 100%" v-model:value="form.merCode" placeholder="商户编码" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.paymentMethod')" name="paymentMethod">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.paymentMethod" :placeholder="$t('business.form.selector.placeholder')" enumName="PAYMENT_METHOD" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.depositHolder')" name="depositHolder">
        <a-input disabled style="width: 100%" v-model:value="form.depositHolder" placeholder="存款人" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.bankAccount')" name="bankAccount">
        <a-input disabled style="width: 100%" v-model:value="form.bankAccount" placeholder="存款账号" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.collectionHolder')" name="collectionHolder">
        <a-input disabled style="width: 100%" v-model:value="form.collectionHolder" placeholder="收款人" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.collectionAccount')" name="collectionAccount">
        <a-input disabled style="width: 100%" v-model:value="form.collectionAccount" placeholder="收款账号" />
      </a-form-item>
      <!-- <a-form-item label="平台奖励" name="platformAward">
        <a-input-number disabled style="width: 100%" v-model:value="form.platformAward" placeholder="平台奖励" />
      </a-form-item> -->
      <a-form-item :label="$t('settle.detail.table.column.merAward')" name="merAward">
        <a-input-number disabled style="width: 100%" v-model:value="form.merAward" placeholder="商户奖励" />
      </a-form-item>
      <!-- <a-form-item label="平台佣金" name="platformBrokerage">
        <a-input disabled style="width: 100%" v-model:value="form.platformBrokerage" placeholder="平台佣金" />
      </a-form-item> -->
      <a-form-item :label="$t('settle.detail.table.column.merBrokerage')" name="merBrokerage">
        <a-input disabled style="width: 100%" v-model:value="form.merBrokerage" placeholder="商户佣金" />
      </a-form-item>
      <!-- <a-form-item label="平台应结算" name="platformShouldSettled">
        <a-input disabled style="width: 100%" v-model:value="form.platformShouldSettled" placeholder="平台应结算" />
      </a-form-item> -->
      <a-form-item :label="$t('settle.detail.table.column.merShouldSettled')" name="merShouldSettled">
        <a-input disabled style="width: 100%" v-model:value="form.merShouldSettled" placeholder="商户应结算" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.status')" name="status">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.status" placeholder="订单状态" enumName="ORDER_STATUS" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.settleStatus')" name="status">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.settleStatus" placeholder="结算状态" enumName="SETTLEMENT_STATUS" />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.createTime')" name="createTime">
        <a-date-picker
          disabled
          show-time
          valueFormat="YYYY-MM-DD HH:mm:ss"
          v-model:value="form.createTime"
          style="width: 100%"
          :placeholder="$t('settle.detail.table.column.createTime')"
        />
      </a-form-item>
      <a-form-item :label="$t('settle.detail.table.column.updateTime')" name="updateTime">
        <a-date-picker
          disabled
          show-time
          valueFormat="YYYY-MM-DD HH:mm:ss"
          v-model:value="form.updateTime"
          style="width: 100%"
          :placeholder="$t('settle.detail.table.column.updateTime')"
        />
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
    tradeDate: undefined, // 交易日期
    settleId: undefined, // 结算单Id
    orderNo: undefined, // 订单编号
    businessScope: undefined, // 业务范围
    amount: undefined, // 金额
    merName: undefined, // 商户名称
    merCode: undefined, // 商户编码
    paymentMethod: undefined, // 付款方式
    depositHolder: undefined, // 存款人
    bankAccount: undefined, // 存款账号
    collectionHolder: undefined, // 收款人
    collectionAccount: undefined, // 收款账号
    merAward: undefined, // 商户奖励
    merBrokerage: undefined, // 商户佣金
    merShouldSettled: undefined, // 商户应结算
    settleStatus: undefined, // 结算状态
    status: undefined, // 订单状态
    createTime: undefined, // 创建时间
    updateTime: undefined, // 更新时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
    orderNo: [{ required: true, message: '订单编号 必填' }],
    depositType: [{ required: true, message: '银行类型，bank/qrcode 必填' }],
    paymentChannel: [{ required: true, message: '二维码渠道，deposit_type=qrcode时有值 必填' }],
    amount: [{ required: true, message: '存款金额 必填' }],
    depositHolder: [{ required: true, message: '存款人 必填' }],
    bankAccount: [{ required: true, message: '存款账号 必填' }],
    depositRemark: [{ required: true, message: '存款备注 必填' }],
    depositFileId: [{ required: true, message: '回单地址,存款成功后银行回单地址,图片url 必填' }],
    callback: [{ required: true, message: '订单确认回调地址 必填' }],
    landingPage: [{ required: true, message: '落地页地址 必填' }],
    sku: [{ required: true, message: '商品编号 必填' }],
    applyTime: [{ required: true, message: '申请时间 必填' }],
    status: [{ required: true, message: '订单状态 1 待确认 2 已确认 3 挂起' }],
    createTime: [{ required: true, message: '创建时间 必填' }],
    updateTime: [{ required: true, message: '更新时间 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    Modal.confirm({
      title: '提示',
      content: '确认该订单，确认后即向接入方发送处理结果',
      okText: '确认',
      okType: 'primary',
      onOk() {
        update(2);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }
  async function onReject() {
    Modal.confirm({
      title: '提示',
      content: '挂起该订单？',
      okText: '挂起',
      okType: 'danger',
      onOk() {
        update(3);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  async function update(status) {
    let params = {
      id: form.id,
      status: status,
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
