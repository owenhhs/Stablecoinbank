<!--
  * 支付渠道支付方式配置表
  *
  * @Author:    Sunny
  * @Date:      2024-09-03 15:16:42
  * @Copyright  Sunny
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
    <a-form ref="formRef" :model="form" :rules="rules" >
        <!-- <a-form-item label="主键"  name="id">
          <a-input-number style="width: 100%" v-model:value="form.id" placeholder="主键" />
        </a-form-item> -->
        <a-form-item label="渠道ID"  name="channelId">
          <a-input-number style="width: 100%" v-model:value="form.channelId" placeholder="渠道商户id" disabled />
        </a-form-item>
        <a-form-item label="交易类型">
          <a-select v-model:value="form.paymentType" style="width: 100px" placeholder="请选择" allow-clear>
              <a-select-option class="pay-status" v-for="item in channelInfoConst.FUNCTION_LIST" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="支付方式"  name="paymentMethod">
          <!-- <a-input style="width: 100%" v-model:value="form.paymentMethod" placeholder="支付方式，bank-银行卡；Alipay-支付宝;" /> -->
          <a-select v-model:value="form.paymentMethod" style="width: 100px" placeholder="请选择" allow-clear>
              <a-select-option class="pay-status" v-for="item in channelInfoConst.PAY_METHOD_LIST" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态"  name="status">
          <a-switch v-model:checked="form.status" :checkedValue="1" checkedChildren="开通" :unCheckedValue="0" unCheckedChildren="关闭"/>
        </a-form-item>
        <a-form-item label="创建时间"  name="createTime">
          <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime" style="width: 100%" placeholder="创建时间" disabled />
        </a-form-item>
        <a-form-item label="更新时间"  name="updateTime">
          <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.updateTime" style="width: 100%" placeholder="更新时间" disabled />
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
  import { reactive, ref, nextTick, watch, computed } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { paymentMethodCfgApi } from '/@/api/business/payment-channel-info/payment-method-cfg-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import BooleanSelect from '/@/components/framework/boolean-select/index.vue';
  import channelInfoConst from '/@/constants/business/erp/channel-info-const';

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
    channelId: undefined, //渠道商户id
    paymentType: undefined, //支付方式类型，payment-支付方式；cash-兑付方式；
    paymentMethod: undefined, //支付方式，bank-银行卡；Alipay-支付宝;
    status: undefined, //状态，0-禁用；1-启用；
    createTime: undefined, //创建时间
    updateTime: undefined, //更新时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
    channelId: [{ required: true, message: '渠道商户id 必填' }],
    paymentType: [{ required: true, message: '支付方式类型，payment-支付方式；cash-兑付方式； 必填' }],
    paymentMethod: [{ required: true, message: '支付方式，bank-银行卡；Alipay-支付宝; 必填' }],
    status: [{ required: true, message: '状态，0-禁用；1-启用； 必填' }],
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
        await paymentMethodCfgApi.update(form);
      } else {
        await paymentMethodCfgApi.add(form);
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
