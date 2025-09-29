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
      <!-- <a-form-item label="商户编号" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.merNo" placeholder="商户编号" />
      </a-form-item> -->
      <a-form-item label="商户" class="smart-query-form-item">
        <a-select v-model:value="queryForm.merNo" style="width: 200px" allow-clear placeholder="请选择商户">
          <a-select-option v-for="item in merchantOptions" :key="item.merCode" :value="item.merCode">{{ item.merName }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="结算状态" class="smart-query-form-item">
        <SmartEnumSelect style="width: 100%" v-model:value="queryForm.settleStatus" placeholder="结算状态" enumName="SETTLEMENT_STATUS" />
      </a-form-item>
      <a-form-item label="币种" class="smart-query-form-item">
        <SmartEnumSelect style="width: 100%" v-model:value="queryForm.currency" placeholder="币种" enumName="CURRENCY_ENUM" />
      </a-form-item>
      <a-form-item label="日期" class="smart-query-form-item">
        <a-range-picker v-model:value="queryDateRange" :presets="defaultChooseTimeRange" style="width: 240px" @change="changeQueryDate" />
      </a-form-item>
      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="search">
          <template #icon>
            <SearchOutlined />
          </template>
          查询
        </a-button>
        <a-button @click="resetQuery" class="smart-margin-left10">
          <template #icon>
            <ReloadOutlined />
          </template>
          重置
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>
  <!---------- 查询表单form end ----------->

  <a-card size="small" :bordered="false" :hoverable="true">
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-setting-block">
        <a-button style="margin-right: 8px;" @click="exportOrders" :loading="exportProgress < 100" type="primary" size="small" v-privilege="'settle:order:export'">
          <template #icon>
            <ExportOutlined />
          </template>
          {{exportProgress < 100 ? '导出中(' + exportProgress + '%)' : '导出结算单'}}
        </a-button>
        <a-button @click="recalculateSettle" type="primary" size="small" v-privilege="'settle:order:recalculation'">
          <template #icon>
            <RedoOutlined />
          </template>
          重算
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.ORDER_INFO" :refresh="queryData" />
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
        <template v-if="column.dataIndex === 'count'">
          <span>{{ record.amount / record.price }}</span>
        </template>
        <template v-if="column.dataIndex === 'settle'">
          <span>{{ (record.amount / record.price) * (1 - record.charge) }}</span>
        </template>
        <template v-if="column.dataIndex === 'status'">
          <span>{{ $smartEnumPlugin.getDescByValue('ORDER_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'payStatus'">
          <span>{{ $smartEnumPlugin.getDescByValue('ORDER_PAY_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'settleStatus'">
          <span>{{ $smartEnumPlugin.getDescByValue('SETTLEMENT_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'settleUrl'">
          <a-button type="link" :disabled="!text || !text.length" @click="viewSettleUrl(record)">查看</a-button>
          <!-- <div style="overflow: auto;">
            <a-image-preview-group>
              <a-space direction="vertical">
                <a-image v-for="(imgUrl, index) in text" :width="60" :height="60" :key="index" :src="imgUrl" />
              </a-space>
            </a-image-preview-group>
          </div> -->
          <!-- <a-image v-if="text" :height="80" :src="text" /> -->
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="viewDetail(record)" type="link">查看明细</a-button>
            <a-button v-if="record.settleStatus === SETTLEMENT_STATUS.WAIT_SETTLE" @click="onSettlement(record)" danger type="link" v-privilege="'settle:order:update'">结算</a-button>
            <a-button v-else-if="record.settleStatus === SETTLEMENT_STATUS.SETTLING" @click="onConfirmSettlement(record)" danger type="link"
                      v-privilege="'settle:order:finished'">确认收款</a-button
            >
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
        :show-total="(total) => `共${total}条`"
      />
    </div>

    <OrderInfoForm ref="formRef" @reloadList="queryData" />
    <OrderSettlementForm ref="settlementFormRef" @reloadList="queryData" />
    <SettlementDetailList ref="settlementDetailListRef" />
    <OrderConfirmForm ref="orderConfirmFormRef" @reloadList="queryData" />
    <RecalculateSettle ref="recalculateSettleRef" @reloadList="queryData" />
    <orderMultipleImages ref="orderMultipleImagesRef" />
  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api';
  import { settlementInfoApi } from '/@/api/business/settlement/settlement-info-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import OrderInfoForm from './order-info-form.vue';
  import OrderConfirmForm from './order-confirm-form.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import OrderSettlementForm from '/@/views/business/settlement/order-settlement-form.vue';
  import { SETTLEMENT_STATUS } from '/@/constants/business/erp/settlement-info-const';
  import SettlementDetailList from '/@/views/business/settlement/settlement-detail-list.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { ExportOutlined, RedoOutlined } from '@ant-design/icons-vue';
  import RecalculateSettle from './recalculate-settle.vue'
  import orderMultipleImages from "./order-multiple-images.vue";
  import {message} from "ant-design-vue";

  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    // {
    //   title: '序号',
    //   dataIndex: 'serialNumber',
    //   ellipsis: true,
    //   width: 50,
    // },
    {
      title: '交易日期',
      dataIndex: 'tradeTime',
      ellipsis: true,
      width: 150,
    },
    {
      title: '商户编号',
      dataIndex: 'merNo',
      ellipsis: true,
      width: 150,
    },
    {
      title: '商户名称',
      dataIndex: 'merName',
      ellipsis: true,
      width: 80,
    },
    {
      title: '交易类型',
      dataIndex: 'tradeType',
      ellipsis: true,
      width: 80,
    },
    {
      title: '业务范围',
      dataIndex: 'tradeLimit',
      ellipsis: true,
      width: 150,
    },
    {
      title: '交易金额',
      dataIndex: 'tradeAmount',
      ellipsis: true,
      width: 150,
    },
    {
      title: '币种',
      dataIndex: 'currency',
      ellipsis: true,
      width: 80,
    },
    {
      title: '笔数',
      dataIndex: 'orderCount',
      ellipsis: true,
      width: 80,
    },
    {
      title: '汇率',
      dataIndex: 'exchangeRate',
      ellipsis: true,
      width: 80,
    },
    {
      title: '佣金',
      dataIndex: 'brokerage',
      ellipsis: true,
      width: 80,
    },
    {
      title: '奖励',
      dataIndex: 'award',
      ellipsis: true,
      width: 80,
    },
    {
      title: '结算金额',
      dataIndex: 'settleAmount',
      ellipsis: true,
      width: 80,
    },
    {
      title: '付款凭证',
      dataIndex: 'settleUrl',
      ellipsis: true,
      width: 80,
    },
    {
      title: '结算状态',
      dataIndex: 'settleStatus',
      ellipsis: true,
      fixed: 'right',
      width: 80,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 160,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    merNo: null,
    settleStatus: null,
    startTime: null,
    endTime: null,
    currency: null,
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 查询时间
  const queryDateRange = ref([]);
  const defaultChooseTimeRange = defaultTimeRanges;
  // 表格加载loading
  const tableLoading = ref(false);
  // 表格数据
  const tableData = ref([]);
  // 总数
  const total = ref(0);
  // 商户列表
  const defaultMerchantOptionsFormState = {
    pageNum: 1,
    pageSize: 100,
  };
  const merchantOptionsForm = reactive({ ...defaultMerchantOptionsFormState });
  const merchantOptions = ref([]);

  // 重置查询条件
  function resetQuery() {
    let pageSize = queryForm.pageSize;
    Object.assign(queryForm, queryFormState);
    queryForm.pageSize = pageSize;
    queryDateRange.value = [];
    queryData();
  }

  function changeQueryDate(dates, dateStrings) {
    queryForm.startTime = dateStrings[0];
    queryForm.endTime = dateStrings[1]
  }

  async function search() {
    queryForm.pageNum = 1
    await queryData();
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await settlementInfoApi.queryPage(queryForm);
      const { data } = queryResult || {}
      const { list } = data || {}
      const fmtList = (list || []).map((item) => {
        if (item?.settleUrl) {
          item.settleUrl = item.settleUrl.split(';');
        }
        return item;
      });
      tableData.value = fmtList;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(async () => {
    let paymentChannelInfoResult = await paymentChannelInfoApi.options(merchantOptionsForm);
    merchantOptions.value = paymentChannelInfoResult.data;

    queryData();
  });

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
  }

  // ---------------------------- 订单确认 ------------------------------
  const settlementFormRef = ref();

  function onSettlement(data) {
    settlementFormRef.value.show(data);
  }

  // ---------------------------- 查看明细 -------------------------------
  const settlementDetailListRef = ref();

  function viewDetail(data) {
    settlementDetailListRef.value.show(data);
  }

  // ---------------------------- 确认收款 -------------------------------
  const orderConfirmFormRef = ref();

  function onConfirmSettlement(data) {
    orderConfirmFormRef.value.show(data);
  }

  const exportProgress = ref(100);

  // 导出订单
  async function exportOrders() {
    try {
      message.info("正在导出数据，请等待...")
      exportProgress.value = 0;
      SmartLoading.show();
      await settlementInfoApi.exportOrders(queryForm, (percent) => {
        exportProgress.value = percent;
      });
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // -------------------- 重算 ---------------------------
  const recalculateSettleRef = ref(null)

  const recalculateSettle = () => {
    recalculateSettleRef.value.show()
  }

  // -------------------- 查看凭证弹窗 ---------------------
  const orderMultipleImagesRef = ref(null)

  const viewSettleUrl = (record) => {
    const { settleUrl } = record || {}
    orderMultipleImagesRef.value.show({ images: settleUrl })
  }
</script>
