<!--
  * 商品配置
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:50
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
      <a-form-item label="商品编号" name="sku">
        <a-input style="width: 100%" v-model:value="form.sku" placeholder="商品编号"/>
      </a-form-item>
      <a-form-item label="商品名称" name="name">
        <a-input style="width: 100%" v-model:value="form.name" placeholder="商品名称"/>
      </a-form-item>
      <a-form-item label="价格" name="price">
        <a-input-number style="width: 100%" v-model:value="form.price"
                        :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                        :parser="value => `${value}`.replace(/\$\s?|(,*)/g, '')"
                        :precision="2"
                        placeholder="价格"/>
      </a-form-item>
      <a-form-item label="手续费" name="charge">
        <a-input-number style="width: 100%" v-model:value="form.charge"
                        :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                        :parser="value => `${value}`.replace(/\$\s?|(,*)/g, '')"
                        :precision="2"
                        placeholder="手续费"/>
      </a-form-item>
      <a-form-item label="奖励" name="award">
        <a-input style="width: 100%" v-model:value="form.award" placeholder="奖励"/>
      </a-form-item>
      <a-form-item label="默认收款卡id" name="collectionCardId">
        <a-select v-model:value="form.collectionCardId" style="width: 100%">
          <a-select-option v-for="item in cardOptions" :key="item.id" :value="item.id">{{
              item.bankName
            }}({{ item.accountName }}-{{ item.cardNo }})
          </a-select-option>
        </a-select>
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
import {skucfgApi} from '/@/api/business/skucfg/skucfg-api';
import {smartSentry} from '/@/lib/smart-sentry';
import {collectioncardApi} from '/@/api/business/collectioncard/collectioncard-api';

// ------------------------ 事件 ------------------------

const emits = defineEmits(['reloadList']);

const cardOptions = ref([])

// ------------------------ 显示与隐藏 ------------------------
// 是否显示
const visibleFlag = ref(false);

function show(rowData) {
  getCardOptions()
  Object.assign(form, formDefault);
  if (rowData && !_.isEmpty(rowData)) {
    Object.assign(form, rowData);
  }
  visibleFlag.value = true;
  nextTick(() => {
    formRef.value.clearValidate();
  });
}

async function getCardOptions() {
  let cardOptionsResult = await collectioncardApi.options()
  cardOptions.value = cardOptionsResult.data
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
  sku: undefined, //商品编号
  name: undefined, //商品名称
  price: undefined, //价格
  charge: undefined, //手续费
  award: undefined, //奖励
  collectionCardId: undefined, //收款卡id
};

let form = reactive({...formDefault});

const rules = {
  sku: [{required: true, message: '商品编号 必填'}],
  name: [{required: true, message: '商品名称 必填'}],
  price: [{required: true, message: '价格 必填'}],
  charge: [{required: true, message: '手续费 必填'}],
  award: [{required: true, message: '奖励 必填'}],
  collectionCardId: [{required: true, message: '收款卡id 必填'}],
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
      await skucfgApi.update(form);
    } else {
      await skucfgApi.add(form);
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
