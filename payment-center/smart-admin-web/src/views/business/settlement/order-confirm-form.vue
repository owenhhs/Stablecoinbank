<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal title="订单确认" :width="600" :open="visibleFlag" @cancel="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-divider />
    <a-descriptions :column="1">
      <a-descriptions-item label="交易日期">{{ form.tradeTime }}</a-descriptions-item>
      <a-descriptions-item label="商户名称">{{ form.merName }}</a-descriptions-item>
      <a-descriptions-item label="商户编号">{{ form.merNo }}</a-descriptions-item>
      <a-descriptions-item label="交易类型">{{ form.tradeType }}</a-descriptions-item>
      <a-descriptions-item label="交易限额">{{ form.tradeLimit }}</a-descriptions-item>
      <a-descriptions-item label="结算状态">
        {{ $smartEnumPlugin.getDescByValue('SETTLEMENT_STATUS', form.settleStatus) }}
      </a-descriptions-item>
      <a-descriptions-item label="交易金额">{{ form.tradeAmount }}</a-descriptions-item>
      <a-descriptions-item label="币种">{{ form.currency }}</a-descriptions-item>
      <a-descriptions-item label="国家">{{ form.country }}</a-descriptions-item>
      <a-descriptions-item label="笔数">{{ form.orderCount }}</a-descriptions-item>
      <a-descriptions-item label="汇率">{{ form.exchangeRate }}</a-descriptions-item>
      <a-descriptions-item label="佣金">{{ form.brokerage }}</a-descriptions-item>
      <a-descriptions-item label="奖励">{{ form.award }}</a-descriptions-item>
      <a-descriptions-item label="结算金额">{{ form.settleAmount }}</a-descriptions-item>
    </a-descriptions>
    <a-divider />
    <template #footer>
      <a-space>
        <a-button v-if="form.settleStatus === SETTLEMENT_STATUS.SETTLING" type="primary" @click="onSubmit" v-privilege="'settle:order:finished'">确认</a-button>
        <!-- <a-button v-if="form.settleStatus === 1" danger @click="onReject">挂起</a-button> -->
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref } from 'vue';
  import _ from 'lodash';
  import { Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { SETTLEMENT_STATUS } from '/@/constants/business/erp/settlement-info-const';
  import { settlementInfoApi } from '/@/api/business/settlement/settlement-info-api';

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
    tradeTime: undefined, // 交易日期
    merName: undefined, // 商户名称
    merNo: undefined, // 商户编号
    tradeType: undefined, // 交易类型
    tradeLimit: undefined, // 业务范围(交易限额)
    settleStatus: undefined, // 结算状态
    tradeAmount: undefined, // 交易金额
    currency: undefined,
    country: undefined,
    orderCount: undefined, // 笔数
    exchangeRate: undefined, // 汇率
    brokerage: undefined, // 佣金
    award: undefined, // 奖励
    settleAmount: undefined, // 结算金额
  };

  let form = reactive({ ...formDefault });

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

  async function update() {
    let params = {
      id: form.id,
      // status: status,
    };
    SmartLoading.show();
    try {
      await settlementInfoApi.finishSettleInfo(params);
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
