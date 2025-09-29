<!--
  * 收款银行卡表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:43
  * @Copyright  sunyu
-->
<template>
  <a-drawer
      :title="form.id ? '编辑' : '添加'"
      width="600"
      :open="visibleFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item label="银行名称" name="bankName">
        <a-input style="width: 100%" v-model:value="form.bankName" placeholder="银行名称"/>
      </a-form-item>
      <a-form-item label="银行卡号" name="cardNo">
        <a-input style="width: 100%" v-model:value="form.cardNo" placeholder="银行卡号"/>
      </a-form-item>
      <a-form-item label="开户人名称" name="accountName">
        <a-input style="width: 100%" v-model:value="form.accountName" placeholder="开户人名称"/>
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
import {reactive, ref, nextTick} from 'vue';
import _ from 'lodash';
import {message} from 'ant-design-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
import {collectioncardApi} from '/@/api/business/collectioncard/collectioncard-api';
import {smartSentry} from '/@/lib/smart-sentry';
import BooleanSelect from '/@/components/framework/boolean-select/index.vue';

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
  bankName: undefined, //银行名称
  cardNo: undefined, //银行卡号
  accountName: undefined, //开户人名称
};

let form = reactive({...formDefault});

const rules = {
  bankName: [{required: true, message: '银行名称 必填'}],
  cardNo: [{required: true, message: '银行卡号 必填'}],
  accountName: [{required: true, message: '开户人名称 必填'}],
};

// 点击确定，验证表单
async function onSubmit() {
  try {
    await formRef.value.validateFields();
    save();
  } catch (err) {
    message.error('参数验证错误，请仔细填写表单数据!');
  }
}

// 新建、编辑API
async function save() {
  SmartLoading.show();
  try {
    if (form.id) {
      await collectioncardApi.update(form);
    } else {
      await collectioncardApi.add(form);
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
