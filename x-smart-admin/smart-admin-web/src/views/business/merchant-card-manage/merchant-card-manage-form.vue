<!--
  * 商户表
  *
  * @Author:    sunyu
  * @Date:      2024-07-24 16:21:32
  * @Copyright  sunyu
-->
<template>
  <a-drawer :title="drawerTitle" width="600" :open="visibleFlag" @close="onClose" :maskClosable="false" :destroyOnClose="true">
    <a-form :disabled="disabledForm" ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item :label="$t('merchantCardManage.form.status')" name="status">
        <a-switch
          style="margin-right: 6px"
          :checked="form.status === 1"
          :checked-children="$t('business.form.open')"
          :un-checked-children="$t('business.form.close')"
          @click="form.status = form.status === 0 ? 1 : 0"
        />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.departmentId')" name="departmentId">
        <a-select
          v-model:value="form.departmentId"
          :disabled="disabledForm || visibleType !== 'add'"
          style="width: 100%"
          allow-clear
          :placeholder="$t('business.form.selector.placeholder')"
        >
          <a-select-option v-for="item in merchantOptions" :key="item.id" :value="item.departmentId">{{ item.merName }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.currency')" name="currency">
        <a-select
            v-model:value="form.currency"
            style="width: 100%"
            allow-clear
            :placeholder="$t('merchantCardManage.form.currency')"
            @change="onCurrencyChange"
        >
          <a-select-option v-for="item in currencyCfg" :key="item" :value="item">{{ item }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.country')" name="country">
        <a-select
            v-model:value="form.country"
            style="width: 100%"
            allow-clear
            :placeholder="$t('merchantCardManage.form.country')"
            @change="onCountryChange"
        >
          <a-select-option v-for="item in countryCfg[form.currency]" :key="item" :value="item">{{ item }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.type')" name="type">
        <a-select
            v-model:value="form.type"
            :disabled="disabledForm || visibleType !== 'add'"
            style="width: 100%"
            allow-clear
            :placeholder="form.type"
        >
          <a-select-option v-for="item in paymentMethodCfg[form.country]" :key="item" :value="item">{{ $smartEnumPlugin.getDescByValue('MER_CARD_PAY_TYPE', item) }}</a-select-option>
        </a-select>
<!--        <SmartEnumSelect v-else-->
<!--          v-model:value="form.type"-->
<!--          :disabled="disabledForm || visibleType !== 'add'"-->
<!--          enumName="MER_CARD_PAY_TYPE"-->
<!--          style="width: 100%"-->
<!--        />-->
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.username')" name="username">
        <a-input style="width: 100%" v-model:value="form.username" :placeholder="$t('merchantCardManage.form.username')"
                 :disabled="visibleType !== 'add' && visibleType !== 'edit'" />
      </a-form-item>
      <a-form-item v-if="form.currency === 'CNY'" :label="$t('merchantCardManage.form.xinjiang')" name="xinjiang">
        <SmartEnumSelect
            v-model:value="form.xinjiang"
            :disabled="visibleType !== 'add' && visibleType !== 'edit'"
            enumName="XINJIANG_FLAG"
            style="width: 100%"
        />
      </a-form-item>
      <a-form-item v-if="form.type === 'bank'" :label="$t('merchantCardManage.form.bankInfo')" name="bankInfo">
        <a-input style="width: 100%" v-model:value="form.bankInfo" :placeholder="$t('merchantCardManage.form.bankInfo')"
                 :disabled="visibleType !== 'add' && visibleType !== 'edit'" />
      </a-form-item>
      <a-form-item v-if="form.type === 'bank'" :label="$t('merchantCardManage.form.bankName')" name="bankName">
        <a-input style="width: 100%" v-model:value="form.bankName" :placeholder="$t('merchantCardManage.form.bankName')"
                 :disabled="visibleType !== 'add' && visibleType !== 'edit'" />
      </a-form-item>
      <a-form-item v-if="form.type === 'bank'" :label="$t('merchantCardManage.form.bankNo3')" name="bankNo">
        <a-input style="width: 100%" v-model:value="form.bankNo" :placeholder="$t('merchantCardManage.form.bankNo3')"
                 :disabled="visibleType !== 'add' && visibleType !== 'edit'" />
      </a-form-item>
      <a-form-item v-else :label="$t('merchantCardManage.form.bankNo2')" name="bankNo">
        <a-input style="width: 100%" v-model:value="form.bankNo" :placeholder="$t('merchantCardManage.form.bankNo2')"
                 :disabled="visibleType !== 'add' && visibleType !== 'edit'" />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.amountMin')" name="amountMin">
        <a-input-number v-model:value="form.amountMin" addon-after="￥" style="width: 100%" :placeholder="$t('merchantCardManage.form.amountMin')" />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.amountMax')" name="amountMax">
        <a-input-number v-model:value="form.amountMax" addon-after="￥" style="width: 100%" :placeholder="$t('merchantCardManage.form.amountMax')" />
      </a-form-item>
      <a-form-item v-if="visibleType === 'view'" :label="$t('merchantCardManage.form.blackList')" name="blackList">
        <SmartEnumSelect v-model:value="form.blackList" :disabled="true" enumName="MER_CARD_BLACK_LIST" style="width: 100%" :placeholder="$t('business.form.selector.placeholder')" />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.paymentScale')" name="paymentScale">
        <a-input style="width: 100%" v-model:value="form.paymentScale" :placeholder="$t('merchantCardManage.form.paymentScale')"
                 :disabled="visibleType !== 'add' && visibleType !== 'edit'" />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.paymentLimit')" name="paymentLimit">
        <a-input-number
          v-model:value="form.paymentLimit"
          addon-after="￥"
          style="width: 100%"
          :placeholder="$t('merchantCardManage.form.paymentLimit')"
          :disabled="visibleType !== 'add' && visibleType !== 'edit'"
        />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.paymentCount')" name="paymentCount">
        <a-input-number
          v-model:value="form.paymentCount"
          addon-after="笔"
          style="width: 100%"
          :placeholder="$t('merchantCardManage.form.paymentCount')"
          :disabled="visibleType !== 'add' && visibleType !== 'edit'"
        />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.workTimeRange')" name="workTimeRange">
        <a-time-range-picker
          v-model:value="form.workTimeRange"
          show-time
          format="HH:mm"
          valueFormat="HH:mm"
          :allowClear="false"
          :default-value="form.workTimeRange"
          style="width: 100%"
          :disabled="visibleType !== 'add' && visibleType !== 'edit'"
        />
      </a-form-item>
      <a-form-item v-if="form.type !== 'bank'" :label="$t('merchantCardManage.form.fileList')" name="fileList">
        <Upload
          v-if="visibleType === 'add' || visibleType === 'edit'"
          accept=".jpg,.jpeg,.png"
          :maxUploadSize="1"
          :buttonText="$t('business.form.upload.tip')"
          :default-file-list="form.fileList"
          :disabled="disabledForm"
          @change="onAlipayQrcodeChange"
        />
        <a-image v-else :width="100" :src="form.payUrl" />
      </a-form-item>
    </a-form>

    <template v-if="visibleType === 'add' || visibleType === 'edit'" #footer>
      <a-space>
        <a-button @click="onClose">{{ $t('business.form.cancel') }}</a-button>
        <a-button type="primary" @click="onSubmit">{{ $t('business.form.save') }}</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref, nextTick, computed } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import Upload from '/@/components/support/file-upload/index.vue';
  import { merchantCardManageApi } from '/@/api/business/merchant-card-manage/merchant-card-manage-api.js';
  import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api';
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();

  // ------------------------ 事件 ------------------------

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
    () =>
      ({
        add: t('business.form.create'),
        view: t('business.form.view'),
        edit: t('business.form.edit'),
      })[visibleType.value]
  );
  const disabledForm = computed(() => visibleType.value === 'view');

  // ---------------------- 币种 --------------------------
  const currencyCfg = [
    'CNY',
    'THB',
    'IDR',
    'VND',
  ]
  const countryCfg = {
    CNY: ['CN'],
    THB: ['TH'],
    IDR: ['ID'],
    VND: ['VN'],
  };

  const paymentMethodCfg = {
    CN: ['Alipay', 'WeChat', 'bank'],
    TH: ['bank', 'QR_CODE', 'TRUE_MONEY'],
    ID: ['bank', 'MOMOPAY', 'ZALO', 'VIETTEL_PAY', 'P2C'],
    VN: ['bank', 'QRIS'],
  };

  function onCurrencyChange() {
    form.country = countryCfg[form.currency][0]
    form.type = paymentMethodCfg[form.country][0]
  }

  function onCountryChange() {
    form.type = paymentMethodCfg[form.country][0]
  }

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);
  const visibleType = ref('add'); // add view edit

  async function show(rowData, type) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
      if (type === 'edit' || type === 'view') {
        Object.assign(form, {
          fileList: [
            {
              fileUrl: form.payUrl,
            },
          ],
          workTimeRange: (form.workTime || []).split('-'),
        });
      } else if (type === 'add') {
        Object.assign(form, {
          fileList: [],
        });
      }
    }
    // 请求商户列表
    let paymentChannelInfoResult = await paymentChannelInfoApi.options(merchantOptionsForm);
    merchantOptions.value = paymentChannelInfoResult?.data || [];

    visibleType.value = type || 'add';
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
    visibleType.value = 'add';
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
    id: undefined, // 支付方式id
    status: 1, // 支付方式状态，0-禁用；1-启用；
    departmentId: undefined, // 商户
    type: 'Alipay', // 支付类型，Alipay、Bank
    accountInfo: undefined, // 账户名称
    username: undefined, // 收款人姓名
    bankInfo: undefined, // 银行名称（只有银行卡类型有值）
    bankName: undefined, // 银行开户行（只有银行卡类型有值）
    bankNo: undefined, // 银行卡号（只有银行卡类型有值）
    currency: 'CNY',
    country: 'CN',
    blackList: undefined, // 是否在黑名单
    paymentScale: undefined, // 支付比例
    paymentLimit: undefined, // 每日支付限额
    paymentCount: undefined, // 每日支付限笔
    payUrl: undefined, // 支付二维码图片链接（只有支付宝类型有值）
    workTime: undefined, // 支付方式的工作时间 HH:SS
    amountMin: undefined, // 最小限额
    amountMax: undefined, // 最大限额
    xinjiang: 0,
    workTimeRange: ['00:00', '23:59'],
    fileList: [],
  };

  let form = reactive({ ...formDefault });

  const rules = computed(() => ({
    departmentId: [{ required: true, message: t('business.form.selector.placeholder') }],
    type: [{ required: true, message: t('business.form.selector.placeholder') }],
    username: [{ required: true, message: t('business.form.input.placeholder') }],
    bankInfo: [{ required: form.type === 'bank', message: t('business.form.input.placeholder') }],
    bankName: [{ required: form.type === 'bank', message: t('business.form.input.placeholder') }],
    bankNo: [{ required: true, message: t('business.form.input.placeholder') }],
    paymentScale: [{ required: true, message: t('business.form.input.placeholder') }],
    paymentLimit: [{ required: true, message: t('business.form.input.placeholder') }],
    paymentCount: [{ required: true, message: t('business.form.input.placeholder') }],
    workTimeRange: [{ required: false, message: t('business.form.input.placeholder') }],
    status: [{ required: true, message: t('business.form.selector.placeholder') }],
    xinjiang: [{ required: form.currency === 'CNY', message: t('business.form.selector.placeholder') }],
    currency: [{ required: true, message: t('business.form.selector.placeholder') }],
    country: [{ required: true, message: t('business.form.selector.placeholder') }],
    amountMin: [
      {
        required: true,
        message: t('business.form.input.placeholder'),
      },
      {
        validator: (rule, value) => {
          if (typeof value !== 'number') {
            return Promise.reject(t('merchantCardManage.form.rule.correntAcmount'));
          }
          if (!(value >= 0)) {
            return Promise.reject(t('merchantCardManage.form.rule.correntAcmount'));
          }
          if (typeof value === 'number' && typeof form.amountMax === 'number') {
            if (value > form.amountMax) {
              return Promise.reject(t('merchantCardManage.form.rule.maxGreaterMin'));
            }
          }
          return Promise.resolve();
        },
      },
    ],
    amountMax: [
      {
        required: true,
        message: t('business.form.input.placeholder'),
      },
      {
        validator: (rule, value) => {
          if (typeof value !== 'number') {
            return Promise.reject(t('merchantCardManage.form.rule.correntAcmount'));
          }
          if (!(value >= 0)) {
            return Promise.reject(t('merchantCardManage.form.rule.correntAcmount'));
          }
          if (typeof value === 'number' && typeof form.amountMax === 'number') {
            if (form.amountMin > value) {
              return Promise.reject(t('merchantCardManage.form.rule.maxGreaterMin'));
            }
          }
          return Promise.resolve();
        },
      },
    ],
    fileList: [{ required: form.type !== 'bank', message: t('business.form.rule.upload.tip') }],
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
      if (visibleType.value === 'edit') {
        const params = {
          ...form,
          payInfoId: form.id,
          workTime: form.workTimeRange.join('-'),
        };
        if (form?.fileList?.length > 0 && form?.fileList?.[0]?.fileId !== undefined) {
          params['fileId'] = form?.fileList?.[0]?.fileId;
        }
        delete params.id
        delete params.payUrl
        delete params.fileList;
        delete params.workTimeRange;
        await merchantCardManageApi.update(params);
      } else if (visibleType.value === 'add') {
        const params = {
          ...form,
          fileId: form?.fileList?.[0]?.fileId,
          workTime: form.workTimeRange.join('-'),
        };
        delete params.fileList;
        delete params.workTimeRange;
        await merchantCardManageApi.add(params);
      }
      message.success(t('business.form.status.success'));
      emits('reloadList');
      onClose();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  // 支付宝二维码上传
  function onAlipayQrcodeChange(fileList) {
    form.fileList = fileList;
  }

  defineExpose({
    show,
  });
</script>
