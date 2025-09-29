<!--
  * 商户表
  *
  * @Author:    sunyu
  * @Date:      2024-07-24 16:21:32
  * @Copyright  sunyu
-->
<template>
  <a-drawer :title="drawerTitle" width="600" :open="visibleFlag" @close="onClose" :maskClosable="false"
            :destroyOnClose="true">
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item :label="$t('channelManage.form.merName')" name="merName">
        <a-input style="width: 100%" v-model:value="form.merName" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.merName')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.merCode')" name="merCode">
        <a-input style="width: 100%" v-model:value="form.merCode" :disabled="readonlyFlag || form.id !== undefined" :placeholder="$t('channelManage.form.merCode')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.status')" name="status">
        <SmartEnumSelect
            v-model:value="form.status"
            enumName="MER_STATUS"
            :disabled="readonlyFlag"
            style="width: 100%"
        />
      </a-form-item>
    </a-form>

    <template v-if="readonlyFlag === false" #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
import {reactive, ref, nextTick, computed} from 'vue';
import _ from 'lodash';
import {message} from 'ant-design-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
import {smartSentry} from '/@/lib/smart-sentry';
import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
import {paymentChannelInfoApi} from '/@/api/business/payment-channel-info/payment-channel-info-api';

import { useI18n } from 'vue-i18n';

const { t } = useI18n();
// ------------------------ 事件 ------------------------

const visibleFlag = ref(false);
const readonlyFlag = ref(false);

const emits = defineEmits(['reloadList']);

// ------------------------ 弹窗配置 ---------------------
const drawerTitle = computed(
    () => form.id === undefined ? t('business.form.create') : t('business.form.edit')
);

// ------------------------ 显示与隐藏 ------------------------

async function show(rowData, readonly) {
  Object.assign(form, formDefault);
  if (rowData && !_.isEmpty(rowData)) {
    Object.assign(form, rowData);
  }

  visibleFlag.value = true;
  readonlyFlag.value = readonly;
  nextTick(() => {
    formRef.value.clearValidate();
  });
}

function onClose() {
  Object.assign(form, formDefault);
  visibleFlag.value = false;
  readonlyFlag.value = false;
}

// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref();

const formDefault = {
  id: undefined,
  merName: undefined, //商户名称
  merCode: undefined, // 商户编码
  status: undefined, //订单状态 1 待确认 2 已确认 3 挂起
  createTime: undefined, //创建时间
  updateTime: undefined, //更新时间
};

let form = reactive({...formDefault});

const rules = computed(() => ({
  merName: [{required: true, message: t('business.form.input.placeholder')}],
  merCode: [{required: true, message: t('business.form.input.placeholder')}],
  status: [{required: true, message: t('business.form.selector.placeholder')}],
}));

// 点击确定，验证表单
async function onSubmit() {
  await formRef.value
      .validateFields()
      .then(() => {
        save();
      })
      .catch((e) => {
        message.error(t('business.form.params.error'));
      });
}

// 新建、编辑API
async function save() {
  SmartLoading.show();
  try {
    if (form.id === undefined) {
      await paymentChannelInfoApi.addParentChannelInfo(form);
    } else {
      await paymentChannelInfoApi.updateParentChannelInfo(form);
    }
    message.success(t( 'business.form.status.success'));
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
