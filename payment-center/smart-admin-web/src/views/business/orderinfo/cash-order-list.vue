<!--
  * 出金订单手工导入表
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="关键字" class="smart-query-form-item">
        <a-input style="width: 240px" v-model:value="queryForm.keyword" placeholder="订单号/付款人姓名/子商户名称" />
      </a-form-item>
      <a-form-item label="订单状态" class="smart-query-form-item">
        <a-select v-model:value="queryForm.status" style="width: 100px" placeholder="请选择" allow-clear>
          <a-select-option class="pay-status" v-for="item in cashOrderInfoConst.CASH_MAIN_ORDER_STATUS" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="日期" class="smart-query-form-item">
        <a-range-picker @change="changeQueryDate" v-model:value="queryDateRange" :presets="defaultChooseTimeRange" style="width: 240px" />
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
        <!-- <a-button @click="exportOrders" type="primary" size="small" v-privilege="'order:list:export'">
          <template #icon>
            <ExportOutlined />
          </template>
          导出订单
        </a-button> -->
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.CASH_ORDER_INFO" :refresh="queryData" />
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <!---------- 表格 begin ----------->
    <!-- :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }" -->
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
        <!-- <template v-if="column.dataIndex === 'billFileUrl'">
          <a-image v-if="text" :height="60" :src="text" />
        </template> -->
        <template v-if="column.dataIndex === 'applyTime' && record.applyTime === 0">
          <span>-</span>
        </template>
        <template v-if="column.dataIndex === 'applyTime' && record.applyTime > 0">
          <span>{{ dayjs(record.applyTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
        </template>

        <template v-if="column.dataIndex === 'finishedTime' && record.finishedTime === 0">
          <span>-</span>
        </template>
        <template v-if="column.dataIndex === 'finishedTime' && record.finishedTime > 0">
          <span>{{ dayjs(record.finishedTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
        </template>

        <template v-if="column.dataIndex === 'amount'">
          <span>{{ record.amount }}元</span>
        </template>
        <!-- <template v-if="column.dataIndex === 'settle'">
          <span>{{ (record.amount / record.price) * (1 - record.charge) }}</span>
        </template> -->
        <template v-if="column.dataIndex === 'status'">
          <span>{{ $smartEnumPlugin.getDescByValue('CASH_MAIN_ORDER_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'manualFlag'">
          <!-- <span>{{ record.manualFlag === 1 ? '是（' + record.manualReason + '）' : '否' }}</span> -->
          <span>{{ record.manualFlag === 1 ? '人工处理' : '自动处理' }}</span>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="showForm(record)" type="link">查看</a-button>
            <!-- <a-button v-if="record.billFileId === 0" @click="uploadReceipt(record)" type="link" v-privilege="'order:receipt:upload'" >补充回单</a-button> -->

            <a-button
              v-if="record.manualFlag === 1"
              @click="onOrderManualProc(record)"
              danger
              type="link"
            >人工处理</a-button>
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
    <CashOrderManualProcessForm ref="orderManualRef" @reloadList="queryData" />

  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { cashOrderInfoApi } from '/@/api/business/order-info/cash-order-info-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import OrderInfoForm from './cash-order-info-form.vue';
  import CashOrderManualProcessForm from '/@/views/business/orderinfo/cash-order-manual-process-form.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api';
  import cashOrderInfoConst from '/@/constants/business/erp/cash-order-info-const';
  import dayjs from 'dayjs';
  import { ExportOutlined, ImportOutlined } from '@ant-design/icons-vue';
  import SmartEnumSelect from "/@/components/framework/smart-enum-select/index.vue";
  import UploadExcel from '/@/components/support/upload-excel/index.vue'
  import OrderReceiptForm from "/@/views/business/orderinfo/order-receipt-form.vue";
  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    // {
    //   title: '序号',
    //   dataIndex: 'serialNumber',
    //   ellipsis: true,
    //   width: 50,
    // },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      ellipsis: true,
      width: 150,
    },
    {
      title: '出金类型',
      dataIndex: 'withdrawType',
      ellipsis: true,
      width: 80,
    },
    {
      title: '出金金额',
      dataIndex: 'amount',
      ellipsis: true,
      width: 80,
    },
    {
      title: '出金金额USDT',
      dataIndex: 'amountUsdt',
      ellipsis: true,
      width: 90,
    },
    // {
    //   title: '币种',
    //   dataIndex: 'currency',
    //   ellipsis: true,
    //   width: 60,
    // },
    // {
    //   title: '国家',
    //   dataIndex: 'country',
    //   ellipsis: true,
    //   width: 60,
    // },
    {
      title: '持卡人',
      dataIndex: 'accountHolder',
      ellipsis: true,
      width: 80,
    },
    {
      title: '银行账号',
      dataIndex: 'bankAccount',
      ellipsis: true,
      width: 130,
    },
    {
      title: '申请时间',
      dataIndex: 'applyTime',
      ellipsis: false,
      width: 130,
    },
    {
      title: '交易完成时间',
      dataIndex: 'finishedTime',
      ellipsis: false,
      width: 130,
    },
    {
      title: '出金状态',
      dataIndex: 'status',
      ellipsis: true,
      // fixed: 'right',
      width: 100,
    },
    {
      title: '处理模式',
      dataIndex: 'manualFlag',
      ellipsis: false,
      // fixed: 'right',
      width: 100,
    },
    {
      title: '人工处理原因',
      dataIndex: 'manualReason',
      ellipsis: false,
      // fixed: 'right',
      width: 100,
    },
    // {
    //   title: '需人工处理的原因',
    //   dataIndex: 'manualReason',
    //   ellipsis: true,
    //   // fixed: 'right',
    //   width: 100,
    // },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 80,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    keyword: null,
    paymentMethod: null,
    merNo: null,
    status: null,
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

  // 重置查询条件
  function resetQuery() {
    let pageSize = queryForm.pageSize;
    Object.assign(queryForm, queryFormState);
    queryForm.pageSize = pageSize;
    queryDateRange.value = [];
    queryData();
  }

  function changeQueryDate(dates, dateStrings) {
    queryForm.startTime = Date.parse(dateStrings[0] + " 00:00:00");
    queryForm.endTime = Date.parse(dateStrings[1] + " 23:59:59");
  }

  async function search() {
    queryForm.pageNum = 1;
    await queryData();
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await cashOrderInfoApi.queryPage(queryForm);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(async () => {
    queryData();
  });

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
  }

  // ---------------------------- 订单手工处理 ------------------------------
  const orderManualRef = ref();

  function onOrderManualProc(data) {
    orderManualRef.value.show(data);
  }

  // 导出订单
  async function exportOrders() {
    try {
      SmartLoading.show();
      await cashOrderInfoApi.exportOrders(queryForm);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }
</script>
