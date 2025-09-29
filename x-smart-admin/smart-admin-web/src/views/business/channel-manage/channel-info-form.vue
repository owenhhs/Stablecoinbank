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
        <a-input style="width: 100%" v-model:value="form.merCode" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.merCode')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.parentDepartmentId')" name="parentDepartmentId">
        <a-select
            v-model:value="form.parentDepartmentId"
            :disabled="readonlyFlag || form.id !== undefined"
            style="width: 100%"
            allow-clear
            :placeholder="$t('business.form.selector.placeholder')"
        >
          <a-select-option v-for="item in merchantOptions" :key="item.id" :value="item.departmentId">{{ item.merName }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.phone')" name="phone">
        <a-input style="width: 100%" v-model:value="form.phone" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.phone')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.paymentScale')" name="paymentScale" v-if="form.paymentScale === undefined || form.paymentScale > -1">
        <a-input style="width: 100%" v-model:value="form.paymentScale" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.paymentScale')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.paymentLimit')" name="paymentLimit">
        <a-input style="width: 100%" v-model:value="form.paymentLimit" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.paymentLimit')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.paymentCount')" name="paymentCount">
        <a-input style="width: 100%" v-model:value="form.paymentCount" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.paymentCount')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.amountMin')" name="amountMin">
        <a-input style="width: 100%" v-model:value="form.amountMin" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.amountMin')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.amountMax')" name="amountMax">
        <a-input style="width: 100%" v-model:value="form.amountMax" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.amountMax')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.platformBrokerage')" name="platformBrokerage" v-if="form.platformBrokerage !== null">
        <a-input style="width: 100%" v-model:value="form.platformBrokerage" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.platformBrokerage')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.merBrokerage')" name="merBrokerage" v-if="form.merBrokerage !== null">
        <a-input style="width: 100%" v-model:value="form.merBrokerage" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.merBrokerage')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.platformAward')" name="platformAward" v-if="form.platformAward !== null">
        <a-input style="width: 100%" v-model:value="form.platformAward" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.platformAward')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.merAward')" name="merAward" v-if="form.merAward !== null">
        <a-input style="width: 100%" v-model:value="form.merAward" :disabled="readonlyFlag" :placeholder="$t('channelManage.form.merAward')" />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.blackList')" name="blackList">
        <SmartEnumSelect
            v-model:value="form.blackList"
            enumName="MER_CARD_BLACK_LIST"
            :disabled="readonlyFlag"
            style="width: 100%"
        />
      </a-form-item>
      <a-form-item :label="$t('channelManage.form.routeType')" name="routeType">
        <SmartEnumSelect
            v-model:value="form.routeType"
            enumName="MER_CARD_ROUTE_TYPE"
            :disabled="readonlyFlag"
            style="width: 100%"
        />
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

// ------------------------ 商户列表 ---------------------
// 商户列表
const defaultMerchantOptionsFormState = {
  pageNum: 1,
  pageSize: 100,
};
const merchantOptionsForm = reactive({ ...defaultMerchantOptionsFormState });
const merchantOptions = ref([]);

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
  // 请求商户列表
  let paymentChannelInfoResult = await paymentChannelInfoApi.parentChannelOptions(merchantOptionsForm);
  merchantOptions.value = paymentChannelInfoResult?.data || [];

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
  parentDepartmentId: undefined, // 主商户部门id
  merName: undefined, //商户名称
  merCode: undefined, // 商户编码
  phone: undefined,
  paymentScale: undefined,
  paymentLimit: undefined,
  paymentCount: undefined,
  blackList: undefined,
  amountMin: undefined,
  amountMax: undefined,
  platformBrokerage: undefined,
  platformAward: undefined,
  merAward: undefined,
  merBrokerage: undefined,
  status: undefined, //订单状态 1 待确认 2 已确认 3 挂起
  createTime: undefined, //创建时间
  updateTime: undefined, //更新时间
};

let form = reactive({...formDefault});

const rules = computed(() => ({
  parentDepartmentId: [{required: true, message: t('business.form.selector.placeholder')}],
  merName: [{required: true, message: t('business.form.input.placeholder')}],
  merCode: [{required: true, message: t('business.form.input.placeholder')}],
  phone: [{required: false, message: 'business.form.input.placeholder'}],
  paymentScale: [{required: false, message: t('business.form.input.placeholder')}],
  paymentLimit: [{required: true, message: t('business.form.input.placeholder')}],
  paymentCount: [{required: true, message: t('business.form.input.placeholder')}],
  amountMin: [{required: true, message: t('business.form.input.placeholder')}],
  amountMax: [{required: true, message: t('business.form.input.placeholder')}],
  platformBrokerage: [{required: false, message: t('business.form.input.placeholder')}],
  merBrokerage: [{required: false, message: t('business.form.input.placeholder')}],
  platformAward: [{required: false, message: t('business.form.input.placeholder')}],
  merAward: [{required: false, message: t('business.form.input.placeholder')}],
  blackList: [{required: true, message: t('business.form.selector.placeholder')}],
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
      await paymentChannelInfoApi.addChannelInfo(form);
    } else {
      await paymentChannelInfoApi.updateChannelInfo(form);
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
