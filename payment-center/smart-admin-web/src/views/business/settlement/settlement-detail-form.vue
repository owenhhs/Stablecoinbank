<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-drawer title="详情" width="600" :open="visibleFlag" @close="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item label="订单号" name="orderNo">
        <a-input disabled style="width: 100%" v-model:value="form.orderNo" placeholder="订单号" />
      </a-form-item>
      <a-form-item label="交易时间" name="payTime">
        <a-input disabled style="width: 100%" v-model:value="form.payTime" placeholder="交易时间" />
      </a-form-item>
      <a-form-item label="金额" name="amount">
        <a-input disabled style="width: 100%" v-model:value="form.amount" placeholder="amount" />
      </a-form-item>
      <a-form-item label="商户名称" name="merName">
        <a-input disabled style="width: 100%" v-model:value="form.merName" placeholder="商户名称" />
      </a-form-item>
      <a-form-item label="商户编号" name="merNo">
        <a-input disabled style="width: 100%" v-model:value="form.merNo" placeholder="商户编号" />
      </a-form-item>
      <a-form-item label="支付方式" name="paymentMethod">
        <a-input disabled style="width: 100%" v-model:value="form.paymentMethod" placeholder="支付方式" />
      </a-form-item>
      <a-form-item label="付款人" name="accountName">
        <a-input disabled style="width: 100%" v-model:value="form.accountName" placeholder="付款人" />
      </a-form-item>
      <a-form-item label="付款账号" name="bankAccount">
        <a-input disabled style="width: 100%" v-model:value="form.bankAccount" placeholder="付款账号" />
      </a-form-item>
      <a-form-item label="支付状态" name="payStatus">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.payStatus" placeholder="支付状态" enumName="ORDER_PAY_STATUS" />
      </a-form-item>
      <a-form-item label="确认状态" name="status">
        <SmartEnumSelect disabled style="width: 100%" v-model:value="form.status" placeholder="确认状态" enumName="ORDER_STATUS" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <!--        <a-button v-if="form.status === 1 && form.payStatus === 1" type="primary" @click="onSubmit">确认</a-button>-->
        <!--        <a-button v-if="form.status === 1 && form.payStatus === 1" danger @click="onReject">挂起</a-button>-->
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref, nextTick, createVNode } from 'vue';
  import _ from 'lodash';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
  import { smartSentry } from '/@/lib/smart-sentry';
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

  // const formDefault = {
  //   id: undefined,
  //   orderNo: undefined, //订单编号
  //   depositType: undefined, //银行类型，bank/qrcode
  //   paymentChannel: undefined, //二维码渠道，deposit_type=qrcode时有值
  //   amount: undefined, //存款金额
  //   depositHolder: undefined, //存款人
  //   bankAccount: undefined, //存款账号
  //   depositRemark: undefined, //存款备注
  //   depositFileId: undefined, //回单地址,存款成功后银行回单地址,图片url
  //   callback: undefined, //订单确认回调地址
  //   landingPage: undefined, //落地页地址
  //   skuNo: undefined, //商品编号
  //   skuName: undefined, //商品名称
  //   price: undefined, //价格
  //   charge: undefined, //手续费
  //   award: undefined, //奖励
  //   collectionBank: undefined, //收款银行
  //   collectionCardNo: undefined, //收款银行卡号
  //   collectionHolder: undefined, //收款银行卡持有人
  //   applyTime: undefined, //申请时间
  //   status: undefined, //订单状态 1 待确认 2 已确认 3 挂起
  //   payStatus: undefined,
  //   createTime: undefined, //创建时间
  //   updateTime: undefined, //更新时间
  // };

  const formDefault = {
    id: undefined,
    payTime: undefined,
    orderNo: undefined,
    amount: undefined,
    merName: undefined,
    merNo: undefined,
    paymentMethod: undefined,
    accountName: undefined,
    bankAccount: undefined,
    payStatus: undefined,
    status: undefined,
  };

  let form = reactive({ ...formDefault });

  const rules = {
    orderNo: [{ required: true, message: '订单编号 必填' }],
    depositType: [{ required: true, message: '银行类型，bank/qrcode 必填' }],
    paymentChannel: [{ required: true, message: '二维码渠道，deposit_type=qrcode时有值 必填' }],
    amount: [{ required: true, message: '存款金额 必填' }],
    depositHolder: [{ required: true, message: '存款人 必填' }],
    bankAccount: [{ required: true, message: '存款账号 必填' }],
    depositRemark: [{ required: true, message: '存款备注 必填' }],
    depositFileId: [{ required: true, message: '回单地址,存款成功后银行回单地址,图片url 必填' }],
    callback: [{ required: true, message: '订单确认回调地址 必填' }],
    landingPage: [{ required: true, message: '落地页地址 必填' }],
    sku: [{ required: true, message: '商品编号 必填' }],
    applyTime: [{ required: true, message: '申请时间 必填' }],
    status: [{ required: true, message: '订单状态 1 待确认 2 已确认 3 挂起' }],
    createTime: [{ required: true, message: '创建时间 必填' }],
    updateTime: [{ required: true, message: '更新时间 必填' }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    Modal.confirm({
      title: '提示',
      content: '确认该订单，确认后即向接入方发送处理结果',
      okText: '确认',
      okType: 'primary',
      onOk() {
        update(2);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }
  async function onReject() {
    Modal.confirm({
      title: '提示',
      content: '挂起该订单？',
      okText: '挂起',
      okType: 'danger',
      onOk() {
        update(3);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  async function update(status) {
    let params = {
      id: form.id,
      status: status,
    };
    SmartLoading.show();
    try {
      await orderInfoApi.update(params);
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  // 新建、编辑API
  async function save() {
    try {
      await formRef.value.validateFields();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
    SmartLoading.show();
    try {
      if (form.id) {
        await orderInfoApi.update(form);
      } else {
        await orderInfoApi.add(form);
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
