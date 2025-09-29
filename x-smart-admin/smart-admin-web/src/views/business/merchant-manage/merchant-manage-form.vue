<!--
  * 商户表
  *
  * @Author:    sunyu
  * @Date:      2024-07-24 16:21:32
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
      <a-form-item label="主键" name="id">
        <a-input-number disabled style="width: 100%" v-model:value="form.id" placeholder="主键"/>
      </a-form-item>
      <a-form-item label="商户名称" name="merName">
        <a-input style="width: 100%" v-model:value="form.merName" placeholder="商户名称"/>
      </a-form-item>
      <a-form-item label="商户号AK" name="merNo">
        <a-input disabled style="width: 100%" v-model:value="form.merNo" placeholder="商户号AK"/>
      </a-form-item>
      <a-form-item label="商户秘钥SK" name="secretKey">
        <a-input disabled style="width: 100%" v-model:value="form.secretKey" placeholder="商户秘钥SK"/>
      </a-form-item>
      <a-form-item label="商户状态，0-禁用；1-启用；" name="status">
        <SmartEnumSelect v-model:value="form.status" enumName="MER_STATUS" :placeholder="$t('business.form.selector.placeholder')" style="width: 100%"/>
      </a-form-item>
      <a-form-item label="创建时间" name="createTime">
        <a-date-picker disabled show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime" style="width: 100%"
                       placeholder="创建时间"/>
      </a-form-item>
      <a-form-item label="更新时间" name="updateTime">
        <a-date-picker disabled show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.updateTime" style="width: 100%"
                       placeholder="更新时间"/>
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
import {merchantManageApi} from '/@/api/business/merchant-manage/merchant-manage-api';
import {smartSentry} from '/@/lib/smart-sentry';
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
  id: undefined, //主键
  merNo: undefined, //商户号AK
  merName: undefined, //商户名称
  secretKey: undefined, //商户秘钥SK
  status: undefined, //商户状态，0-禁用；1-启用；
  createTime: undefined, //创建时间
  updateTime: undefined, //更新时间
};

let form = reactive({...formDefault});

const rules = {
  merName: [{required: true, message: '商户名称 必填'}],
  status: [{required: true, message: '商户状态，0-禁用；1-启用； 必填'}],
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
      await merchantManageApi.update(form);
    } else {
      await merchantManageApi.add(form);
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
