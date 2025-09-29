<!--
  * 支付渠道业务范围配置表
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
    <a-form ref="formRef" :model="form" :rules="rules"  >
                    <!-- <a-form-item label="主键"  name="id">
                      <a-input-number style="width: 100%" v-model:value="form.id" placeholder="主键" />
                    </a-form-item> -->
                    <a-form-item label="渠道ID"  name="channelId">
                      <a-input-number style="width: 100%" v-model:value="form.channelId" placeholder="渠道商户id" disabled />
                    </a-form-item>
                    <a-form-item label="交易类型"  name="paymentType">
                      <!-- <a-input style="width: 100%" v-model:value="form.paymentType" placeholder="支付方式类型，payment-支付方式；cash-兑付方式；" /> -->
                      <a-select v-model:value="form.paymentType" style="width: 100px" placeholder="请选择" allow-clear>
                          <a-select-option class="pay-status" v-for="item in channelInfoConst.FUNCTION_LIST" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
                      </a-select>
                    </a-form-item>
                    <a-form-item label="币种"  name="currency">
                      <a-input style="width: 100%" v-model:value="form.currency" placeholder="币种" />
                    </a-form-item>
                    <a-form-item label="国家"  name="country">
                      <a-input style="width: 100%" v-model:value="form.country" placeholder="国家" />
                    </a-form-item>

                    <a-form-item label="单笔限额最小值"  name="amountMin">
                      <a-input-number style="width: 100%" v-model:value="form.amountMin" placeholder="支付限额最小值" />
                    </a-form-item>
                    <a-form-item label="单笔限额最大值"  name="amountMax">
                      <a-input-number style="width: 100%" v-model:value="form.amountMax" placeholder="支付限额最大值" />
                    </a-form-item>

                    <a-form-item label="佣金比例（%）"  name="brokerage">
                      <a-input-number style="width: 100%" v-model:value="form.brokerage" placeholder="佣金比例，百分比" />
                    </a-form-item>
                    <a-form-item label="单笔奖励"  name="award">
                      <a-input-number style="width: 100%" v-model:value="form.award" placeholder="单笔奖励" />
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
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { paymentChannelBusinessCfgApi } from '/@/api/business/payment-channel-info/payment-channel-business-cfg-api';
  import { smartSentry } from '/@/lib/smart-sentry';
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
      amountMin: undefined, //支付限额最小值
      amountMax: undefined, //支付限额最大值
      currency: undefined, //币种
      country: undefined, //国家
      brokerage: undefined, //佣金比例，百分比
      award: undefined, //单笔奖励
      createTime: undefined, //创建时间
      updateTime: undefined, //更新时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
        // id: [{ required: true, message: '主键 必填' }],
        channelId: [{ required: true, message: '渠道商户id 必填' }],
        paymentType: [{ required: true, message: '支付方式类型，payment-支付方式；cash-兑付方式； 必填' }],
        amountMin: [{ required: true, message: '支付限额最小值 必填' }],
        amountMax: [{ required: true, message: '支付限额最大值 必填' }],
        currency: [{ required: true, message: '币种 必填' }],
        country: [{ required: true, message: '国家 必填' }],
        brokerage: [{ required: true, message: '佣金比例，百分比 必填' }],
        award: [{ required: true, message: '单笔奖励 必填' }],
        // createTime: [{ required: true, message: '创建时间 必填' }],
        // updateTime: [{ required: true, message: '更新时间 必填' }],
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
        await paymentChannelBusinessCfgApi.update(form);
      } else {
        await paymentChannelBusinessCfgApi.add(form);
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
