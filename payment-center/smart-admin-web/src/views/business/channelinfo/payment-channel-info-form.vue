<!--
  * 渠道基本信息表
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
          <!-- <a-row> -->
                    <!-- <a-form-item label="主键"  name="id">
                      <a-input-number style="width: 100%" v-model:value="form.id" placeholder="主键"  />
                    </a-form-item> -->
                    <a-form-item label="渠道编码"  name="merCode">
                      <a-input style="width: 100%" v-model:value="form.merCode" placeholder="渠道编码" :disabled="form.id" />
                    </a-form-item>
                    <a-form-item label="渠道名称"  name="merName">
                      <a-input style="width: 100%" v-model:value="form.merName" placeholder="渠道名称" />
                    </a-form-item>
                    <a-form-item label="状态"  name="status">
                      <a-switch v-model:checked="form.status" :checkedValue="1" checkedChildren="开通" :unCheckedValue="0" unCheckedChildren="关闭"/>
                    </a-form-item>

                    <a-form-item label="渠道工作时间"  name="workTime" help="24小时制，不同时间段使用英文;分割">
                      <a-input style="width: 100%" v-model:value="form.workTime" placeholder="示例：07:00-21:00" />
                    </a-form-item>

                    <a-form-item label="新疆交易标志"  name="xjFlag">
                      <a-switch v-model:checked="form.xjFlag" :checkedValue="1" checkedChildren="开通" :unCheckedValue="0" unCheckedChildren="关闭"/>
                    </a-form-item>

                    <a-card size="small" title="连接配置" :headStyle="{background: '#fefefe',}">
                      <a-form-item label="接口类型"  name="interfaceType">
                        <!-- <a-input-number style="width: 100%" v-model:value="form.interfaceType" placeholder="接口类型 1-API方式，2-（通过【渠道管理平台】支持）" /> -->
                        <a-radio-group v-model:value="form.interfaceType" :disabled="!form.id">
                          <a-radio :value="1">API方式</a-radio>
                          <a-radio :value="2">非API方式</a-radio>
                        </a-radio-group>
                      </a-form-item>
                    
                      <!-- <template #extra><a href="#">more</a></template> -->
                      <a-form-item label="商户号"  name="merAk">
                        <a-input style="width: 100%" v-model:value="form.merAk" placeholder="商户号" :disabled="form.interfaceType === 2"/>
                      </a-form-item>
                      <a-form-item label="商户秘钥"  name="merSk">
                        <a-input style="width: 100%" v-model:value="form.merSk" placeholder="商户秘钥" :disabled="form.interfaceType === 2"/>
                      </a-form-item>
                      <!-- <p>card content</p> -->
                    </a-card>
                    
                    
                    <!-- <a-form-item label="域名"  name="domain">
                      <a-input style="width: 100%" v-model:value="form.domain" placeholder="域名" />
                    </a-form-item> -->
                    <!-- <a-form-item label="商户额外配置信息"  name="ext">
                      <a-input style="width: 100%" v-model:value="form.ext" placeholder="商户额外配置信息" />
                    </a-form-item>
                    <a-form-item label="商户持有人姓名"  name="merUsername">
                      <a-input style="width: 100%" v-model:value="form.merUsername" placeholder="商户持有人姓名" />
                    </a-form-item>
                    <a-form-item label="商户绑定手机号"  name="phone">
                      <a-input style="width: 100%" v-model:value="form.phone" placeholder="商户绑定手机号" />
                    </a-form-item>
                    <a-form-item label="商户证件号"  name="idNumber">
                      <a-input style="width: 100%" v-model:value="form.idNumber" placeholder="商户证件号" />
                    </a-form-item> -->
                    <a-card size="small" title="入金功能配置" :headStyle="{background: '#fefefe',}">
                      <a-form-item label="开通标志"  name="paymentFlag" width="">
                        <a-switch v-model:checked="form.paymentFlag" :checkedValue="1" checkedChildren="开通" :unCheckedValue="0" unCheckedChildren="关闭"/>
                      </a-form-item>
                      <a-form-item label="路由比例"  name="paymentScale">
                        <a-input-number style="width: 100%" v-model:value="form.paymentScale" placeholder="支付比例" />
                      </a-form-item>
                      <a-form-item label="单日限额"  name="paymentLimit">
                        <a-input-number style="width: 100%" v-model:value="form.paymentLimit" placeholder="支付限额" addon-after="元"/>
                      </a-form-item>
                      <a-form-item label="单日笔数限制"  name="paymentCount">
                        <a-input-number style="width: 100%" v-model:value="form.paymentCount" placeholder="支付笔数限制"  addon-after="次"/>
                      </a-form-item>
                    </a-card>

                    <a-card size="small" title="出金功能配置" :headStyle="{background: '#fefefe',}">
                      <a-form-item label="开通标志"  name="cashFlag">
                        <a-switch v-model:checked="form.cashFlag" :checkedValue="1" checkedChildren="开通" :unCheckedValue="0" unCheckedChildren="关闭"/>
                      </a-form-item>
                      <a-form-item label="路由比例"  name="cashScale">
                        <a-input-number style="width: 100%" v-model:value="form.cashScale" placeholder="兑付比例" />
                      </a-form-item>
                      <a-form-item label="单日限额"  name="cashLimit">
                        <a-input-number style="width: 100%" v-model:value="form.cashLimit" placeholder="兑付限额" />
                      </a-form-item>
                      <a-form-item label="单日笔数限制"  name="cashCount">
                        <a-input-number style="width: 100%" v-model:value="form.cashCount" placeholder="兑付笔数限制" />
                      </a-form-item>
                    </a-card>

                    <a-form-item label="创建时间"  name="createTime">
                      <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.createTime" style="width: 100%" placeholder="创建时间" disabled />
                    </a-form-item>
                    <a-form-item label="更新时间"  name="updateTime">
                      <a-date-picker show-time valueFormat="YYYY-MM-DD HH:mm:ss" v-model:value="form.updateTime" style="width: 100%" placeholder="更新时间" disabled />
                    </a-form-item>
          <!-- </a-row> -->

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
  import { reactive, ref, nextTick, onMounted } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api';
  import { smartSentry } from '/@/lib/smart-sentry';

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
              merName: undefined, //商户名称
              merCode: undefined, //商户编码
              merAk: '', //商户号
              merSk: '', //商户秘钥
              domain: undefined, //域名
              ext: undefined, //商户额外配置信息
              merUsername: undefined, //商户持有人姓名
              phone: undefined, //商户绑定手机号
              idNumber: undefined, //商户证件号
              paymentFlag: 0, //支付开通标志 1-开通 0-未开通
              paymentScale: 0, //支付比例
              paymentLimit: 0, //支付限额
              paymentCount: 0, //支付笔数限制
              cashFlag: 0, //兑付开通标志 1-开通 0-未开通
              cashScale: 0, //兑付比例
              cashLimit: 0, //兑付限额
              cashCount: 0, //兑付笔数限制
              workTime: '07:00-21:00', //渠道工作时间，示例：07:00-21:00（早七点到晚9点，24小时制）
              xjFlag: 0, //新疆交易标志 1-开通 0-未开通
              interfaceType: 2, //接口类型 1-API方式，2-非API方式（通过【渠道管理平台】支持）
              status: 0, //状态 0-禁用；1-启用；
              createTime: undefined, //创建时间
              updateTime: undefined, //更新时间
  };

  let form = reactive({ ...formDefault });

  const rules = {
                  // id: [{ required: true, message: '主键 必填' }],
                  merName: [{ required: true, message: '商户名称 必填' }],
                  merCode: [{ required: true, message: '商户编码 必填' }],
                  // merAk: [{ required: true, message: '商户号 必填' }],
                  // merSk: [{ required: true, message: '商户秘钥 必填' }],
                  // domain: [{ required: true, message: '域名 必填' }],
                  // ext: [{ required: true, message: '商户额外配置信息 必填' }],
                  // merUsername: [{ required: true, message: '商户持有人姓名 必填' }],
                  // phone: [{ required: true, message: '商户绑定手机号 必填' }],
                  // idNumber: [{ required: true, message: '商户证件号 必填' }],
                  paymentFlag: [{ required: true, message: '支付开通标志 必填' }],
                  // paymentScale: [{ required: true, message: '支付比例 必填' }],
                  // paymentLimit: [{ required: true, message: '支付限额 必填' }],
                  // paymentCount: [{ required: true, message: '支付笔数限制 必填' }],
                  cashFlag: [{ required: true, message: '兑付开通标志  必填' }],
                  // cashScale: [{ required: true, message: '兑付比例 必填' }],
                  // cashLimit: [{ required: true, message: '兑付限额 必填' }],
                  // cashCount: [{ required: true, message: '兑付笔数限制 必填' }],
                  workTime: [{ required: true, message: '渠道工作时间 必填' }],
                  xjFlag: [{ required: true, message: '新疆交易标志 必填' }],
                  interfaceType: [{ required: true, message: '接口类型 必填' }],
                  status: [{ required: true, message: '状态 必填' }],
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
        await paymentChannelInfoApi.update(form);
      } else {
        await paymentChannelInfoApi.add(form);
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
