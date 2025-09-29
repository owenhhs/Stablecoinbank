<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item :label="$t('settle.queryForm.label.merchant')" class="smart-query-form-item">
        <a-select v-model:value="queryForm.departmentId" style="width: 200px" allow-clear
                  :placeholder="$t('business.form.selector.placeholder')">
          <a-select-option v-for="item in merchantOptions" :key="item.departmentId" :value="item.departmentId">
            {{ item.merName }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :label="$t('settle.queryForm.label.settleStatus')" class="smart-query-form-item">
        <SmartEnumSelect style="width: 100%" v-model:value="queryForm.settleStatus"
                         :placeholder="$t('business.form.selector.placeholder')" enumName="SETTLEMENT_STATUS"/>
      </a-form-item>
      <a-form-item :label="$t('settle.queryForm.label.currency')" class="smart-query-form-item">
        <a-select
            v-model:value="queryForm.currency"
            style="width: 80px"
            allow-clear
            :placeholder="$t('settle.queryForm.label.currency')"
        >
          <a-select-option v-for="item in currencyOptions" :key="item" :value="item">{{ item }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :label="$t('settle.queryForm.label.date')" class="smart-query-form-item">
        <a-range-picker v-model:value="queryDateRange" :presets="defaultChooseTimeRange" style="width: 240px"
                        @change="changeQueryDate"/>
      </a-form-item>
      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="search">
          <template #icon>
            <SearchOutlined/>
          </template>
          {{ $t('settle.queryForm.label.query') }}
        </a-button>
        <a-button @click="resetQuery" class="smart-margin-left10">
          <template #icon>
            <ReloadOutlined/>
          </template>
          {{ $t('settle.queryForm.label.reset') }}
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>
  <!---------- 查询表单form end ----------->

  <a-card size="small" :bordered="false" :hoverable="true">
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-setting-block">
        <a-button style="margin-right: 8px" @click="exportOrders" type="primary" size="small"
                  v-privilege="'settle:parent:export'">
          <template #icon>
            <ExportOutlined/>
          </template>
          {{ $t('settle.queryForm.label.exportSettle') }}
        </a-button>
        <a-button @click="recalculateSettle" type="primary" size="small" v-privilege="'settle:calc:date'">
          <template #icon>
            <RedoOutlined/>
          </template>
          {{ $t('settle.queryForm.label.recalculate') }}
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.ORDER_INFO" :refresh="queryData"/>
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <!---------- 表格 begin ----------->
    <a-table
        size="small"
        :dataSource="tableData"
        :columns="columns"
        rowKey="id"
        bordered
        :loading="tableLoading"
        :pagination="false"
        :scroll="{ x: 1300, y: 1000 }"
    >
      <template #bodyCell="{ text, record, column }">
        <!-- <template v-if="column.dataIndex === 'serialNumber'">
          <span>{{ index + 1 }}</span>
        </template> -->
        <template v-if="column.dataIndex === 'status'">
          <span>{{ $smartEnumPlugin.getDescByValue('ORDER_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'payStatus'">
          <span>{{ $smartEnumPlugin.getDescByValue('ORDER_PAY_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'settleStatus'">
          <span>{{ $smartEnumPlugin.getDescByValue('SETTLEMENT_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'tradeType'">
          <span>{{ $smartEnumPlugin.getDescByValue('TRADE_TYPE', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'settleUrl'">
          <a-button type="link" :disabled="!text || !text.length" @click="viewSettleUrl(record)">查看</a-button>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="viewDetail(record)" type="link" v-privilege="'settle:parent:detail'">
              {{ $t('settle.table.operation.viewDetail') }}
            </a-button>
<!--            <a-button v-if="record.settleStatus === SETTLEMENT_STATUS.WAIT_SETTLE" @click="onSettlement(record)" danger-->
<!--                      type="link"-->
<!--                      v-privilege="'settle:parent:exchange:set'">{{ $t('settle.table.operation.settlement') }}-->
<!--            </a-button>-->
<!--            <a-button-->
<!--                v-else-if="record.settleStatus === SETTLEMENT_STATUS.SETTLING || record.settleStatus === SETTLEMENT_STATUS.WAIT_CONFIRM"-->
<!--                danger-->
<!--                type="link"-->
<!--                v-privilege="'settle:parent:payment:confirm'"-->
<!--                @click="onConfirmSettlement(record)"-->
<!--            >{{ $t('settle.table.operation.confirmPayment') }}-->
<!--            </a-button>-->
          </div>
        </template>
      </template>
    </a-table>
    <!---------- 表格 end ----------->

    <div class="smart-query-table-page">
      <a-pagination
          showSizeChanger
          showQuickJumper
          show-less-items
          :pageSizeOptions="PAGE_SIZE_OPTIONS"
          :defaultPageSize="queryForm.pageSize"
          v-model:current="queryForm.pageNum"
          v-model:pageSize="queryForm.pageSize"
          :total="total"
          @change="queryData"
          @showSizeChange="queryData"
          :show-total="(total) => `${total} ${$t('business.table.items')}`"
      />
    </div>

    <OrderSettlementForm ref="settlementFormRef" @reloadList="queryData"/>
    <SettlementDetailList ref="settlementDetailListRef"/>
    <OrderConfirmForm ref="orderConfirmFormRef" @reloadList="queryData"/>
    <RecalculateSettle ref="recalculateSettleRef" @reloadList="queryData"/>
    <orderMultipleImages ref="orderMultipleImagesRef"/>
  </a-card>
</template>
<script setup>
import {reactive, ref, onMounted} from 'vue';
import {settlementInfoApi} from '/@/api/business/settlement/settlement-info-api';
import {paymentChannelInfoApi} from '/@/api/business/payment-channel-info/payment-channel-info-api';
import {PAGE_SIZE_OPTIONS} from '/@/constants/common-const';
import {smartSentry} from '/@/lib/smart-sentry';
import TableOperator from '/@/components/support/table-operator/index.vue';
import {TABLE_ID_CONST} from '/@/constants/support/table-id-const.js';
import {defaultTimeRanges} from '/@/lib/default-time-ranges.js';
import OrderSettlementForm from '/@/views/business/parent-settlement/order-settlement-form.vue';
import {SETTLEMENT_STATUS} from '/@/constants/business/erp/settlement-info-const';
import SettlementDetailList from '/@/views/business/parent-settlement/settlement-detail-list.vue';
import OrderConfirmForm from './order-confirm-form.vue';
import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
import {ExportOutlined, RedoOutlined} from '@ant-design/icons-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
import RecalculateSettle from './recalculate-settle.vue';
import {useI18n} from 'vue-i18n';
import OrderMultipleImages from "/@/components/business/settle/order-multiple-images.vue";

const {t} = useI18n();

// ---------------------------- 表格列 ----------------------------

const columns = ref([
  {
    title: t('settle.table.column.tradeDate'),
    dataIndex: 'tradeDate',
    ellipsis: true,
    width: 150,
  },
  {
    title: t('settle.table.column.merName'),
    dataIndex: 'merName',
    ellipsis: true,
    width: 150,
  },
  {
    title: t('settle.table.column.merCode'),
    dataIndex: 'merCode',
    ellipsis: true,
    width: 150,
  },
  {
    title: t('settle.table.column.tradeType'),
    dataIndex: 'tradeType',
    ellipsis: true,
    width: 150,
  },
  {
    title: t('settle.table.column.amount'),
    dataIndex: 'amount',
    ellipsis: true,
    width: 150,
  },
  {
    title: t('settle.table.column.currency'),
    dataIndex: 'currency',
    ellipsis: true,
    width: 60,
  },
  {
    title: t('settle.table.column.countAmount'),
    dataIndex: 'countAmount',
    ellipsis: true,
    width: 80,
  },
  {
    title: t('settle.table.column.exchangeRate'),
    dataIndex: 'exchangeRate',
    ellipsis: true,
    width: 150,
  },
  {
    title: t('settle.table.column.merBrokerage'),
    dataIndex: 'merBrokerage',
    ellipsis: true,
    width: 180,
  },
  {
    title: t('settle.table.column.merAward'),
    dataIndex: 'merAward',
    ellipsis: true,
    width: 180,
  },
  {
    title: t('settle.table.column.merShouldSettled'),
    dataIndex: 'merShouldSettled',
    ellipsis: true,
    width: 220,
  },
  {
    title: t('settle.table.column.settleUrl'),
    dataIndex: 'settleUrl',
    ellipsis: true,
    width: 150,
  },
  {
    title: t('settle.table.column.createTime'),
    dataIndex: 'createTime',
    ellipsis: true,
    width: 160,
  },
  {
    title: t('settle.table.column.updateTime'),
    dataIndex: 'updateTime',
    ellipsis: true,
    width: 160,
  },
  {
    title: t('settle.table.column.settleStatus'),
    dataIndex: 'settleStatus',
    ellipsis: true,
    width: 150,
    fixed: 'right',
  },
  {
    title: t('settle.table.column.action'),
    dataIndex: 'action',
    fixed: 'right',
    width: 150,
  },
]);

// ---------------------------- 查询数据表单和方法 ----------------------------

const queryFormState = {
  pageNum: 1,
  pageSize: 10,
  departmentId: null,
  merNo: '',
  settleStatus: null,
  settleStartDate: '',
  settleEndDate: '',
  currency: null
};
// 查询表单form
const queryForm = reactive({...queryFormState});
// 查询时间
const queryDateRange = ref([]);
const defaultChooseTimeRange = defaultTimeRanges;
// 商户列表
const defaultMerchantOptionsFormState = {
  pageNum: 1,
  pageSize: 100,
};
const merchantOptionsForm = reactive({...defaultMerchantOptionsFormState});
const merchantOptions = ref([]);
// 表格加载loading
const tableLoading = ref(false);
// 表格数据
const tableData = ref([]);
// 总数
const total = ref(0);

// 币种列表
const currencyOptions = ref([])

// 重置查询条件
function resetQuery() {
  let pageSize = queryForm.pageSize;
  Object.assign(queryForm, queryFormState);
  Object.assign(merchantOptionsForm, defaultMerchantOptionsFormState);
  queryForm.pageSize = pageSize;
  queryDateRange.value = [];
  queryData();
}

function changeQueryDate(dates, dateStrings) {
  queryForm.settleStartDate = dateStrings[0];
  queryForm.settleEndDate = dateStrings[1];
}

async function search() {
  queryForm.pageNum = 1
  await queryData();
}

// 查询数据
async function queryData() {
  tableLoading.value = true;
  try {
    let queryResult = await settlementInfoApi.queryParentPage(queryForm);
    const list = (queryResult.data.list || []).map((item) => {
      if (item?.settleUrl) {
        item.settleUrl = item.settleUrl.split(';');
      }
      return item;
    });
    tableData.value = list;
    total.value = queryResult.data.total;
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    tableLoading.value = false;
  }
}

onMounted(async () => {
  await getCurrencyOptions()

  let paymentChannelInfoResult = await paymentChannelInfoApi.parentChannelOptions(merchantOptionsForm);
  merchantOptions.value = paymentChannelInfoResult?.data || [];

  await queryData();
});

async function getCurrencyOptions() {
  try {
    const res = await paymentChannelInfoApi.currencyOptions();
    currencyOptions.value = res.data;
  } catch (e) {
    smartSentry.captureError(e)
  }
}

// ---------------------------- 结算 ------------------------------
const settlementFormRef = ref();

function onSettlement(data) {
  settlementFormRef.value.show(data);
}

// ---------------------------- 查看明细 -------------------------------
const settlementDetailListRef = ref();

function viewDetail(data) {
  settlementDetailListRef.value.show(data);
}

// ---------------------------- 确认结算单 -------------------------------
const orderConfirmFormRef = ref();

function onConfirmSettlement(data) {
  orderConfirmFormRef.value.show(data);
}

// 导出订单
async function exportOrders() {
  try {
    SmartLoading.show();
    await settlementInfoApi.exportParentOrders(queryForm);
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    SmartLoading.hide();
  }
}

// -------------------- 重算 ---------------------------
const recalculateSettleRef = ref(null);

const recalculateSettle = () => {
  recalculateSettleRef.value.show();
};

// -------------------- 查看凭证弹窗 ---------------------
const orderMultipleImagesRef = ref(null)

const viewSettleUrl = (record) => {
  const {settleUrl} = record || {}
  orderMultipleImagesRef.value.show({images: settleUrl})
}
</script>
