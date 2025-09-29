<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal
      title="结算单确认"
      :width="600"
      :bodyStyle="{ maxHeight: '70vh', overflow: 'auto' }"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-divider/>
    <a-descriptions :column="1">
      <a-descriptions-item :label="$t('settle.table.column.settleStatus')">
        {{ $smartEnumPlugin.getDescByValue('SETTLEMENT_STATUS', form.settleStatus) }}
      </a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.merCode')">{{ form.merCode }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.detail.table.column.merName')">{{ form.merName }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.detail.table.column.tradeDate')">{{
          form.tradeDate
        }}
      </a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.tradeType')">{{ form.tradeType }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.amount')">{{ form.amount }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.exchangeRate')">{{ form.exchangeRate }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.countAmount')">{{ form.countAmount }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.merAward')">{{ form.merAward }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.merBrokerage')">{{ form.merBrokerage }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.merShouldSettled')">{{
          form.merShouldSettled
        }}
      </a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.createTime')">{{ form.createTime }}</a-descriptions-item>
      <a-descriptions-item :label="$t('settle.table.column.updateTime')">{{ form.updateTime }}</a-descriptions-item>
    </a-descriptions>
    <a-form ref="formRef" :model="form" :rules="rules">
      <a-form-item :label="$t('settle.table.column.settleUrl')" name="fileList">
        <Upload accept=".jpg,.jpeg,.png"
                :buttonText="$t('business.form.upload.tip')"
                :default-file-list="form.fileList"
                @change="onAlipayQrcodeChange">
          <div v-if="form.fileList.length < 10">
            <a-icon type="plus" />
            <div class="ant-upload-text">
              {{$t('business.form.upload.tip')}}
            </div>
          </div>
        </Upload>
      </a-form-item>
    </a-form>
    <a-divider/>
    <template #footer>
      <a-space>
        <a-button
            v-if="form.settleStatus === SETTLEMENT_STATUS.SETTLING || form.settleStatus === SETTLEMENT_STATUS.WAIT_CONFIRM"
            type="primary"
            @click="onSubmit"
        >{{ $t('business.form.confirm') }}
        </a-button
        >
        <!-- <a-button v-if="form.status === 1" danger @click="onReject">挂起</a-button> -->
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
import {reactive, ref, computed, nextTick} from 'vue';
import _ from 'lodash';
import {Modal} from 'ant-design-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
// import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
import {smartSentry} from '/@/lib/smart-sentry';
import {settlementInfoApi} from '/@/api/business/settlement/settlement-info-api';
import {SETTLEMENT_STATUS} from '/@/constants/business/erp/settlement-info-const';
import Upload from '/@/components/support/file-upload/index.vue';
import {message} from 'ant-design-vue';
import {useI18n} from 'vue-i18n'

const {t} = useI18n()

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
const formDefault = {
  id: undefined,
  departmentId: undefined, // 部门Id
  settleStatus: undefined, // 结算状态
  merCode: undefined, // 商户编码
  merName: undefined, // 商户名称
  tradeDate: undefined, // 交易日期
  tradeType: undefined, // 交易类型
  amount: undefined, // 交易金额
  exchangeRate: undefined, // 交易汇率
  countAmount: undefined, // 笔数
  merAward: undefined, // 商户奖励
  merBrokerage: undefined, // 商户佣金
  merShouldSettled: undefined, // 商户应结算
  createTime: undefined, // 创建时间
  updateTime: undefined, // 更新时间
  fileList: [], // 打款凭证
};

let form = reactive({...formDefault});

const rules = computed(() => ({
  fileList: [{required: true, message: t('business.form.rule.upload.tip')}],
}));

// 组件ref
const formRef = ref();

// 点击确定，验证表单
async function onSubmit() {
  await formRef.value
      .validateFields()
      .then(() => {
        Modal.confirm({
          title: t('business.modal.confirm.title'),
          content: t('settle.confirmorder.content'),
          okText: t('business.form.confirm'),
          okType: 'primary',
          onOk() {
            update(2);
          },
          cancelText: t('business.form.cancel'),
          onCancel() {
          },
        });
      })
      .catch((e) => {
        message.error(t('business.form.params.error'));
      });
}

// async function onReject() {
//   Modal.confirm({
//     title: '提示',
//     content: '挂起该订单？',
//     okText: '挂起',
//     okType: 'danger',
//     onOk() {
//       update(3);
//     },
//     cancelText: '取消',
//     onCancel() {},
//   });
// }

async function update() {
  let fileIds = []
  form?.fileList.forEach((item) => {
    fileIds.push(item.fileId);
  })
  let params = {
    settleId: form.id,
    fileIds: fileIds,
  };
  SmartLoading.show();
  try {
    await settlementInfoApi.confirmPayment(params);
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

<style scoped lang="less">
.order-info-row {
  font-size: 14px;

  &.title {
    display: inline-flex;
    color: #000000;
  }

  &.value {
    display: inline-flex;
    color: #4a4a4a;
  }
}
</style>
