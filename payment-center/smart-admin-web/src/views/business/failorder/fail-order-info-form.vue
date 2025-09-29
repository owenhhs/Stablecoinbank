<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-drawer title="详情" width="600" :open="visibleFlag" @close="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-form ref="formRef" :model="form" layout="vertical">
      <a-form-item label="订单号" name="orderNo">
        <a-input disabled style="width: 100%" v-model:value="form.orderNo" />
      </a-form-item>
      <a-form-item label="请求流水号" name="requestId">
        <a-input disabled style="width: 100%" v-model:value="form.requestId" />
      </a-form-item>
      <a-form-item label="请求参数" name="params">
        <JsonViewer :value="form.params ? JSON.parse(form.params) : ''" theme="jv-dark" copyable boxed sort />
      </a-form-item>
      <a-form-item label="失败信息" name="failMsg">
        <JsonViewer :value="form.failMsg ? JSON.parse(form.failMsg) : ''" theme="jv-dark" copyable boxed sort />
      </a-form-item>
      <a-form-item label="记录时间" name="createTime">
        <a-input disabled style="width: 100%" v-model:value="form.createTime" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">关闭</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import { JsonViewer } from 'vue3-json-viewer';
  import _ from 'lodash';

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
    console.error('form:', form);
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
    orderNo: undefined,
    params: undefined,
    failMsg: undefined,
    requestId: undefined,
    createTime: undefined,
  };

  let form = reactive({ ...formDefault });

  defineExpose({
    show,
  });
</script>
