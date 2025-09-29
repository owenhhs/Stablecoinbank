<template>
  <a-modal title="重算结算单" :width="330" :open="visibleFlag" @cancel="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item name="settleDate">
        <a-date-picker v-model:value="form.settleDate" valueFormat="YYYY-MM-DD" style="width: 100%" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit" v-privilege="'settle:order:recalculation'">确认</a-button>
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
    settleDate: undefined,
  };

  let form = reactive({ ...formDefault });
  const formRef = ref(null);

  const rules = {
    settleDate: [{ required: true, message: '请填写' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
      return;
    }
    Modal.confirm({
      title: '提示',
      content: '确认重算？',
      okText: '确认',
      okType: 'primary',
      onOk() {
        postSubmit();
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  async function postSubmit() {
    SmartLoading.show();
    try {
      await settlementInfoApi.calcByDate(form);
      message.success('已完成重算');
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
