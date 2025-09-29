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
      <a-form-item label="关键字" class="smart-query-form-item">
        <a-input style="width: 240px" v-model:value="queryForm.keyword" placeholder="订单号/付款人姓名/子商户名称" />
      </a-form-item>
      <a-form-item label="平台" class="smart-query-form-item">
        <a-select v-model:value="queryForm.platformId" style="width: 200px" allow-clear placeholder="请选择平台">
          <a-select-option class="platform-name" v-for="item in platformOptions" :key="item.id" :value="item.id">{{ item.merName }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="商户" class="smart-query-form-item">
        <a-select v-model:value="queryForm.merNo" style="width: 200px" allow-clear placeholder="请选择商户">
          <a-select-option class="mername" v-for="item in channelOptions" :key="item.merCode" :value="item.merCode">{{ item.merName }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="支付状态" class="smart-query-form-item">
        <a-select v-model:value="queryForm.payStatus" style="width: 100px" placeholder="请选择" allow-clear>
          <a-select-option class="pay-status" v-for="item in orderInfoConst.ORDER_PAY_STATUS" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="付款方式" class="smart-query-form-item">
        <a-select v-model:value="queryForm.paymentMethod" style="width: 100px" placeholder="请选择" allow-clear>
          <a-select-option class="payment-method" v-for="item in orderInfoConst.PAYMENT_METHOD" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="币种" class="smart-query-form-item">
        <SmartEnumSelect style="width: 100%" v-model:value="queryForm.currency" placeholder="币种" enumName="CURRENCY_ENUM" />
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
        <a-button @click="exportOrders" :loading="exportProgress < 100" type="primary" size="small" v-privilege="'order:list:export'">
          <template #icon>
            <ExportOutlined />
          </template>
          {{exportProgress < 100 ? '导出中(' + exportProgress + '%)' : '导出订单'}}
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.ORDER_INFO" :refresh="queryData" />
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
        <template v-if="column.dataIndex === 'billFileUrl'">
          <a-image v-if="text" :height="60" :src="text" />
        </template>
        <template v-if="column.dataIndex === 'payTime' && record.payTime === 0">
          <span>-</span>
        </template>
        <template v-if="column.dataIndex === 'payTime' && record.payTime > 0">
          <span>{{ dayjs(record.payTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
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
        <template v-if="column.dataIndex === 'settle'">
          <span>{{ (record.amount / record.price) * (1 - record.charge) }}</span>
        </template>
        <template v-if="column.dataIndex === 'status'">
          <span>{{ $smartEnumPlugin.getDescByValue('ORDER_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'payStatus'">
          <span>{{ $smartEnumPlugin.getDescByValue('ORDER_PAY_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="showForm(record)" type="link">查看</a-button>
            <a-button v-if="record.billFileId === 0" @click="uploadReceipt(record)" type="link" v-privilege="'order:receipt:upload'" >补充回单</a-button>

            <!-- <a-button
              v-if="record.status === 1"
              @click="onOrderConfirmation(record)"
              danger
              type="link"
            >确认</a-button> -->
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
    <OrderConfirmForm ref="orderConfirmRef" @reloadList="queryData" />
    <OrderReceiptForm ref="receiptRef" @reloadList="queryData" />
  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import OrderInfoForm from './order-info-form.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import OrderConfirmForm from '/@/views/business/orderinfo/order-confirm-form.vue';
  import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api';
  import orderInfoConst from '/@/constants/business/erp/order-info-const';
  import dayjs from 'dayjs';
  import { ExportOutlined } from '@ant-design/icons-vue';
  import SmartEnumSelect from "/@/components/framework/smart-enum-select/index.vue";
  import OrderReceiptForm from "/@/views/business/orderinfo/order-receipt-form.vue";
  import {merchantManageApi} from "/@/api/business/merchant-manage/merchant-manage-api.js";
  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    // {
    //   title: '序号',
    //   dataIndex: 'serialNumber',
    //   ellipsis: true,
    //   width: 50,
    // },
    {
      title: '交易平台',
      dataIndex: 'platformName',
      width: 170,
    },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      ellipsis: true,
      width: 170,
    },
    {
      title: '交易时间',
      dataIndex: 'payTime',
      ellipsis: true,
      width: 140,
    },
    {
      title: '交易完成时间',
      dataIndex: 'finishedTime',
      ellipsis: true,
      width: 140,
    },
    {
      title: '金额',
      dataIndex: 'amount',
      ellipsis: true,
      width: 80,
    },
    {
      title: '币种',
      dataIndex: 'currency',
      ellipsis: true,
      width: 80,
    },
    {
      title: '国家',
      dataIndex: 'country',
      ellipsis: true,
      width: 80,
    },
    {
      title: '回单图片',
      dataIndex: 'billFileUrl',
      ellipsis: true,
      width: 100,
    },
    {
      title: '支付商户',
      dataIndex: 'merName',
      ellipsis: true,
      width: 120,
    },
    {
      title: '子商户',
      dataIndex: 'subMerName',
      ellipsis: true,
      width: 120,
    },
    {
      title: '付款方式',
      dataIndex: 'paymentMethod',
      ellipsis: true,
      width: 80,
    },
    {
      title: '付款人',
      dataIndex: 'accountName',
      ellipsis: true,
      width: 80,
    },
    {
      title: '付款账号',
      dataIndex: 'bankAccount',
      ellipsis: true,
      width: 150,
    },
    {
      title: '支付状态',
      dataIndex: 'payStatus',
      ellipsis: true,
      fixed: 'right',
      width: 70,
    },
    {
      title: '确认状态',
      dataIndex: 'status',
      ellipsis: true,
      fixed: 'right',
      width: 70,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 90,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    keyword: null,
    paymentMethod: null,
    merNo: null,
    payStatus: null,
    startTime: null,
    endTime: null,
    currency: null,
    platformId: null,
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 查询时间
  const queryDateRange = ref([]);
  const defaultChooseTimeRange = defaultTimeRanges;
  // 收款卡选项
  const cardOptions = ref([]);
  // 商户列表
  const defaultChannelOptionsFormState = {
    pageNum: 1,
    pageSize: 100,
  };
  const channelOptionsForm = reactive({ ...defaultChannelOptionsFormState });
  const channelOptions = ref([]);

  // 平台列表
  const platformOptions = ref([]);

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
      let queryResult = await orderInfoApi.queryPage(queryForm);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(async () => {
    let paymentChannelInfoResult = await paymentChannelInfoApi.options(channelOptionsForm);
    channelOptions.value = paymentChannelInfoResult.data;

    let merOptions = await merchantManageApi.getOptions();
    platformOptions.value = merOptions.data;

    await queryData();
  });

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
  }

  // ---------------------------- 补充回单 ------------------------------
  const receiptRef = ref();
  function uploadReceipt(data) {
    receiptRef.value.show(data);
  }

  // ---------------------------- 订单确认 ------------------------------
  const orderConfirmRef = ref();

  function onOrderConfirmation(data) {
    orderConfirmRef.value.show(data);
  }
  // ---------------------------- 单个删除 ----------------------------
  //确认删除
  function onDelete(data) {
    Modal.confirm({
      title: '提示',
      content: '确定要删除选吗?',
      okText: '删除',
      okType: 'danger',
      onOk() {
        requestDelete(data);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  //请求删除
  async function requestDelete(data) {
    SmartLoading.show();
    try {
      let deleteForm = {
        goodsIdList: selectedRowKeyList.value,
      };
      await orderInfoApi.delete(data.id);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ---------------------------- 批量删除 ----------------------------

  // 选择表格行
  const selectedRowKeyList = ref([]);

  function onSelectChange(selectedRowKeys) {
    selectedRowKeyList.value = selectedRowKeys;
  }

  // 批量删除
  function confirmBatchDelete() {
    Modal.confirm({
      title: '提示',
      content: '确定要批量删除这些数据吗?',
      okText: '删除',
      okType: 'danger',
      onOk() {
        requestBatchDelete();
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  //请求批量删除
  async function requestBatchDelete() {
    try {
      SmartLoading.show();
      await orderInfoApi.batchDelete(selectedRowKeyList.value);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  const exportProgress = ref(100);

  // 导出订单
  async function exportOrders() {
    try {
      message.info("正在导出数据，请等待...")
      exportProgress.value = 0;
      SmartLoading.show();
      await orderInfoApi.exportOrders(queryForm, (percent) => {
        exportProgress.value = percent;
      });
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }
</script>
