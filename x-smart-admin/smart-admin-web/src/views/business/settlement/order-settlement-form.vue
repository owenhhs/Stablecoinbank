<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal :title="$t('settle.orderSettleForm.title')" :width="300" :open="visibleFlag" @cancel="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item name="exchangeRate">
        <a-input v-model:value="form.exchangeRate" :placeholder="$t('business.form.input.placeholder')" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">{{ $t('business.form.cancel') }}</a-button>
        <a-button type="primary" @click="onSubmit">{{ $t('business.form.confirm') }}</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref } from 'vue';
  import _ from 'lodash';
  import { Modal, message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { settlementInfoApi } from '/@/api/business/settlement/settlement-info-api';
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
    exchangeRate: undefined, // 汇率
  };

  let form = reactive({ ...formDefault });
  const formRef = ref(null);

  const rules = {
    exchangeRate: [
      { required: true, message: t('business.form.input.placeholder') },
      { pattern: /^[+-]?\d+(\.\d+)?$/, message: t('business.form.rule.pattern.tip') },
    ],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
    } catch (err) {
      message.error(t('business.form.params.error'));
      return;
    }
    Modal.confirm({
      title: t('business.modal.confirm.title'),
      content: t('settle.orderSettleForm.confirm'),
      okText: t('business.form.confirm'),
      okType: 'primary',
      onOk() {
        postSubmit();
      },
      cancelText: t('business.form.cancel'),
      onCancel() {},
    });
  }

  async function postSubmit() {
    let params = {
      settleId: form.id,
      exchangeRate: form.exchangeRate,
    };
    SmartLoading.show();
    try {
      await settlementInfoApi.exchangeRate(params);
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
