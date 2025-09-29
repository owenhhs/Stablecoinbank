<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-drawer title="结算单明细" width="90%" :open="visibleFlag" :maskClosable="false" :destroyOnClose="true" @close="onClose">
    <a-card size="small" :bordered="false" :hoverable="true">
      <!---------- 表格操作行 begin ----------->
      <!-- <a-row class="smart-table-btn-block">
        <div class="smart-table-setting-block">
          <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.ORDER_INFO" :refresh="queryData" />
        </div>
      </a-row> -->
      <!---------- 表格操作行 end ----------->

      <!---------- 表格 begin ----------->
      <a-table size="small" :dataSource="tableData" :columns="columns" rowKey="id" bordered :loading="tableLoading" :pagination="false">
        <template #bodyCell="{ text, record, column, index }">
          <template v-if="column.dataIndex === 'serialNumber'">
            <span>{{ index + 1 }}</span>
          </template>
          <template v-if="column.dataIndex === 'payTime' && record.payTime > 0">
            <span>{{ dayjs(record.payTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
          </template>
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
          <template v-if="column.dataIndex === 'paymentMethod'">
            <span>{{ $smartEnumPlugin.getDescByValue('PAYMENT_METHOD', text) }}</span>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button @click="viewInfo(record)" type="link">查看</a-button>
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

      <SettlementDetailForm ref="settlementDetailFormRef" />
    </a-card>
    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref } from 'vue';
  import _ from 'lodash';
  import { smartSentry } from '/@/lib/smart-sentry';
  // import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import { settlementInfoApi } from '/@/api/business/settlement/settlement-info-api';
  import SettlementDetailForm from '/@/views/business/settlement/settlement-detail-form.vue';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import dayjs from 'dayjs';
  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 表格列 ----------------------------

  const columns = ref([
    // {
    //   title: '序号',
    //   dataIndex: 'serialNumber',
    //   ellipsis: true,
    //   width: 50,
    // },
    {
      title: '交易时间',
      dataIndex: 'payTime',
      ellipsis: true,
      width: 180,
    },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      ellipsis: true,
      width: 150,
    },
    {
      title: '金额',
      dataIndex: 'amount',
      ellipsis: true,
      width: 150,
    },
    {
      title: '商户名称',
      dataIndex: 'merName',
      ellipsis: true,
      width: 120,
    },
    {
      title: '商户编号',
      dataIndex: 'merNo',
      ellipsis: true,
      width: 120,
    },
    {
      title: '支付方式',
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
      title: '收款账号',
      dataIndex: 'bankAccount',
      ellipsis: true,
      width: 150,
    },
    {
      title: '支付状态',
      dataIndex: 'payStatus',
      ellipsis: true,
      fixed: 'right',
      width: 80,
    },
    {
      title: '确认状态',
      dataIndex: 'status',
      ellipsis: true,
      fixed: 'right',
      width: 80,
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
  // const queryDateRange = ref([]);
  // const defaultChooseTimeRange = defaultTimeRanges;
  // 表格加载loading
  const tableLoading = ref(false);
  // 表格数据
  const tableData = ref([]);
  // 总数
  const total = ref(0);

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      const params = {
        ...queryForm,
        settleId: form.id,
      };
      let queryResult = await settlementInfoApi.orderList(params);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);

  async function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    await queryData();
    visibleFlag.value = true;
    // nextTick(() => {
    //   formRef.value.clearValidate();
    // });
  }

  function onClose() {
    Object.assign(form, formDefault);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
    id: undefined,
  };

  let form = reactive({ ...formDefault });

  defineExpose({
    show,
  });

  // ------------------------ 查看 -------------------------
  const settlementDetailFormRef = ref(null);

  const viewInfo = (data) => {
    settlementDetailFormRef.value.show(data);
  };
</script>
