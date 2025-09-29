<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-drawer :title="$t('settle.detail.title')" width="90%" :open="visibleFlag" :maskClosable="false" :destroyOnClose="true" @close="onClose">
    <a-card size="small" :bordered="false" :hoverable="true">
      <!---------- 查询表单form begin ----------->
      <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
          <a-form-item :label="$t('settle.detail.queryForm.date')" class="smart-query-form-item">
            <a-range-picker v-model:value="queryDateRange" :presets="defaultChooseTimeRange" style="width: 240px" @change="changeQueryDate" />
          </a-form-item>
          <a-form-item class="smart-query-form-item">
            <a-button type="primary" @click="queryData">
              <template #icon>
                <SearchOutlined />
              </template>
              {{ $t('settle.detail.queryForm.query') }}
            </a-button>
            <a-button @click="resetQuery" class="smart-margin-left10">
              <template #icon>
                <ReloadOutlined />
              </template>
              {{ $t('settle.detail.queryForm.reset') }}
            </a-button>
          </a-form-item>
        </a-row>
      </a-form>
      <!---------- 查询表单form end ----------->
      <!---------- 表格操作行 begin ----------->
      <!-- <a-row class="smart-table-btn-block">
        <div class="smart-table-setting-block">
          <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.ORDER_INFO" :refresh="queryData" />
        </div>
      </a-row> -->
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
          <!-- <template v-if="colunmn.dataIndex === 'serialNumber'">
            <span>{{ index + 1 }}</span>
          </template> -->
          <template v-if="column.dataIndex === 'paymentMethod'">
            <span>{{ $smartEnumPlugin.getDescByValue('PAYMENT_METHOD', text) }}</span>
          </template>
          <template v-if="column.dataIndex === 'status'">
            <span>{{ $smartEnumPlugin.getDescByValue('ORDER_STATUS', text) }}</span>
          </template>
          <template v-if="column.dataIndex === 'settleStatus'">
            <span>{{ $smartEnumPlugin.getDescByValue('SETTLEMENT_STATUS', text) }}</span>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button @click="viewInfo(record)" type="link">{{ $t('settle.detail.table.operation.view') }}</a-button>
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

      <SettlementDetailForm ref="settlementDetailFormRef" />
    </a-card>
    <template #footer>
      <a-space>
        <a-button @click="onClose">{{ $t('settle.detail.cancel') }}</a-button>
      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref } from 'vue';
  import _ from 'lodash';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import { settlementInfoApi } from '/@/api/business/settlement/settlement-info-api';
  import SettlementDetailForm from '/@/views/business/settlement/settlement-detail-form.vue';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 表格列 ----------------------------

  const columns = ref([
    {
      title: t('settle.detail.table.column.tradeDate'),
      dataIndex: 'tradeDate',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.orderNo'),
      dataIndex: 'orderNo',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.businessScope'),
      dataIndex: 'businessScope',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.amount'),
      dataIndex: 'amount',
      ellipsis: true,
      width: 80,
    },
    {
      title: t('settle.detail.table.column.merName'),
      dataIndex: 'merName',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('settle.detail.table.column.merCode'),
      dataIndex: 'merCode',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.paymentMethod'),
      dataIndex: 'paymentMethod',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.depositHolder'),
      dataIndex: 'depositHolder',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.bankAccount'),
      dataIndex: 'bankAccount',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.collectionHolder'),
      dataIndex: 'collectionHolder',
      ellipsis: true,
      width: 80,
    },
    {
      title: t('settle.detail.table.column.collectionAccount'),
      dataIndex: 'collectionAccount',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.merAward'),
      dataIndex: 'merAward',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.merBrokerage'),
      dataIndex: 'merBrokerage',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.merShouldSettled'),
      dataIndex: 'merShouldSettled',
      ellipsis: true,
      width: 220,
    },
    {
      title: t('settle.detail.table.column.settleStatus'),
      dataIndex: 'settleStatus',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.createTime'),
      dataIndex: 'createTime',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.updateTime'),
      dataIndex: 'updateTime',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('settle.detail.table.column.status'),
      dataIndex: 'status',
      ellipsis: true,
      fixed: 'right',
      width: 120,
    },
    {
      title: t('settle.detail.table.column.action'),
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
    queryForm.startTime = dateStrings[0];
    queryForm.endTime = dateStrings[1];
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      const params = {
        ...queryForm,
        settleId: form.id,
      };
      let queryResult = await settlementInfoApi.queryPageBySettleDetail(params);
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
