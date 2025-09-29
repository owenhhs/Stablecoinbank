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
      <a-form-item label="订单号" class="smart-query-form-item">
        <a-input style="width: 300px" v-model:value="queryForm.orderNo" placeholder="订单号" />
      </a-form-item>
      <a-form-item label="收款卡" class="smart-query-form-item">
        <a-select v-model:value="queryForm.collectionCardNo" style="width: 300px" allow-clear>
          <a-select-option v-for="item in cardOptions" :key="item.id" :value="item.cardNo"
            >{{ item.bankName }}({{ item.accountName }}-{{ item.cardNo }})
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-row>
    <a-row class="smart-query-form-row">
      <a-form-item label="付款人" class="smart-query-form-item">
        <a-input style="width: 300px" v-model:value="queryForm.depositHolder" placeholder="付款人" />
      </a-form-item>
      <a-form-item label="日期" class="smart-query-form-item">
        <a-range-picker @change="changeQueryDate" v-model:value="queryDateRange" :presets="defaultChooseTimeRange" style="width: 240px" />
      </a-form-item>
      <a-form-item label="状态" class="smart-query-form-item">
        <SmartEnumSelect width="120px" enum-name="ORDER_STATUS" v-model:value="queryForm.status" />
      </a-form-item>
      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="queryData">
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
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
    >
      <template #bodyCell="{ text, record, column }">
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
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="showForm(record)" type="link">详情</a-button>
            <a-button v-if="record.status === 1" @click="onOrderConfirmation(record)" danger type="link">确认</a-button>
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

    <CashInfoForm ref="formRef" @reloadList="queryData" />
    <CashConfirmForm ref="orderConfirmRef" @reloadList="queryData" />
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
  import CashInfoForm from './cash-info-form.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import { collectioncardApi } from '/@/api/business/collectioncard/collectioncard-api.js';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import CashConfirmForm from '/@/views/business/cashinfo/cash-confirm-form.vue';
  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: '申请时间',
      dataIndex: 'applyTime',
      ellipsis: true,
    },
    {
      title: '订单编号',
      dataIndex: 'orderNo',
      ellipsis: true,
    },
    {
      title: '价格',
      dataIndex: 'price',
      ellipsis: true,
    },
    {
      title: '数量',
      dataIndex: 'count',
      ellipsis: true,
    },
    {
      title: '收款信息',
      dataIndex: 'collectionCardNo',
      ellipsis: true,
    },
    {
      title: '付款人',
      dataIndex: 'depositHolder',
      ellipsis: true,
    },
    {
      title: '付款信息',
      dataIndex: 'bankAccount',
      ellipsis: true,
    },
    {
      title: '奖励',
      dataIndex: 'award',
      ellipsis: true,
    },
    {
      title: '手续费',
      dataIndex: 'charge',
      ellipsis: true,
    },
    {
      title: '结算金额',
      dataIndex: 'settle',
      ellipsis: true,
    },
    {
      title: '存款金额',
      dataIndex: 'amount',
      ellipsis: true,
    },
    {
      title: '处理状态',
      dataIndex: 'status',
      ellipsis: true,
    },
    {
      title: '付款状态',
      dataIndex: 'payStatus',
      ellipsis: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      ellipsis: true,
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
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 查询时间
  const queryDateRange = ref([]);
  const defaultChooseTimeRange = defaultTimeRanges;
  // 收款卡选项
  const cardOptions = ref([]);
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
    queryForm.startDate = dateStrings[0];
    queryForm.endDate = dateStrings[1];
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let cardOptionsResult = await collectioncardApi.options();
      cardOptions.value = cardOptionsResult.data;

      let queryResult = await orderInfoApi.queryPage(queryForm);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(queryData);

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
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
</script>
