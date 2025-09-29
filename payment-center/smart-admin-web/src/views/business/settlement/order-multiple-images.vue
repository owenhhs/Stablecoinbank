<!--
  * 訂單信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal title="查看付款凭证" :width="670" :open="visibleFlag" @cancel="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-divider />
    <a-image-preview-group>
      <a-space :wrap="true" style="max-height: calc(100vh - 400px); overflow: auto;">
        <a-image v-for="(imgUrl, index) in form.images" :width="200" :height="200" :key="index" :src="imgUrl" />
      </a-space>
    </a-image-preview-group>
    <a-divider />
    <template #footer>
      <a-space>
        <a-button @click="onClose">关闭</a-button>
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

  // ------------------------ 顯示與隱藏 ------------------------
  // 是否顯示
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

  // ------------------------ 表單 ------------------------
  const formDefault = {
    images: [],
  };

  let form = reactive({ ...formDefault });

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
