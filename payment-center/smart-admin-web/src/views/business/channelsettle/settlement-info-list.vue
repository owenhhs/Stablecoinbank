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
<!--      <a-form-item label="商户" class="smart-query-form-item">-->
<!--        <a-select v-model:value="queryForm.merCode" style="width: 200px" allow-clear placeholder="请选择">-->
<!--          <a-select-option v-for="item in merchantOptions" :key="item.merCode" :value="item.merCode">{{ item.merName }}</a-select-option>-->
<!--        </a-select>-->
<!--      </a-form-item>-->
      <a-form-item label="结算状态" class="smart-query-form-item">
        <SmartEnumSelect style="width: 100%" v-model:value="queryForm.settleStatus" placeholder="请选择" enumName="CHANNEL_SETTLEMENT_STATUS" />
      </a-form-item>
<!--      <a-form-item label="币种" class="smart-query-form-item">-->
<!--        <a-select-->
<!--            v-model:value="queryForm.currency"-->
<!--            style="width: 80px"-->
<!--            allow-clear-->
<!--            placeholder="币种"-->
<!--        >-->
<!--          <a-select-option v-for="item in currencyOptions" :key="item" :value="item">{{ item }}</a-select-option>-->
<!--        </a-select>-->
<!--      </a-form-item>-->
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
        <template v-if="column.dataIndex === 'settleStatus'">
          <span>{{ $smartEnumPlugin.getDescByValue('CHANNEL_SETTLEMENT_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'tradeType'">
          <span>{{ $smartEnumPlugin.getDescByValue('TRADE_TYPE', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'settleUrl'">
          <div style="overflow: auto;">
            <a-image-preview-group>
              <a-space direction="vertical">
                <a-image v-for="(imgUrl, index) in text" :width="60" :height="60" :key="index" :src="imgUrl" />
              </a-space>
            </a-image-preview-group>
          </div>
          <!-- <a-image v-if="text" :height="80" :src="text" /> -->
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
        :show-total="(total) => `${total} 条`"
      />
    </div>

  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { settlementInfoApi } from '/@/api/business/settlement/settlement-info-api';
  import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: '交易日期',
      dataIndex: 'tradeDate',
      ellipsis: true,
      width: 150,
    },
    {
      title: '商户名称',
      dataIndex: 'merName',
      ellipsis: true,
      width: 150,
    },
    {
      title: '商户编号',
      dataIndex: 'merCode',
      ellipsis: true,
      width: 150,
    },
    {
      title: '交易类型',
      dataIndex: 'tradeType',
      ellipsis: true,
      width: 150,
    },
    {
      title: '交易金额',
      dataIndex: 'amount',
      ellipsis: true,
      width: 150,
    },
    {
      title: '币种',
      dataIndex: 'currency',
      ellipsis: true,
      width: 60,
    },
    {
      title: '笔数',
      dataIndex: 'countAmount',
      ellipsis: true,
      width: 80,
    },
    {
      title: '汇率',
      dataIndex: 'exchangeRate',
      ellipsis: true,
      width: 150,
    },
    {
      title: '商户佣金',
      dataIndex: 'merBrokerage',
      ellipsis: true,
      width: 180,
    },
    {
      title: '商户奖励',
      dataIndex: 'merAward',
      ellipsis: true,
      width: 180,
    },
    {
      title: '商户应结算',
      dataIndex: 'merShouldSettled',
      ellipsis: true,
      width: 220,
    },
    {
      title: '结算凭证',
      dataIndex: 'settleUrl',
      ellipsis: true,
      width: 150,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      ellipsis: true,
      width: 160,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      ellipsis: true,
      width: 160,
    },
    {
      title: '结算状态',
      dataIndex: 'settleStatus',
      ellipsis: true,
      width: 150,
      fixed: 'right',
    },
    // {
    //   title: t('settle.table.column.action'),
    //   dataIndex: 'action',
    //   fixed: 'right',
    //   width: 150,
    // },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    departmentId: null,
    settleStatus: null,
    settleStartDate: '',
    settleEndDate: '',
    currency: null
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 查询时间
  const queryDateRange = ref([]);
  const defaultChooseTimeRange = defaultTimeRanges;
  // 商户列表
  const defaultMerchantOptionsFormState = {
    pageNum: 1,
    pageSize: 100,
  };
  const merchantOptionsForm = reactive({ ...defaultMerchantOptionsFormState });
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
      let queryResult = await settlementInfoApi.queryPageChannel(queryForm);
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
    // await getCurrencyOptions()

    // let paymentChannelInfoResult = await paymentChannelInfoApi.options(merchantOptionsForm);
    // merchantOptions.value = paymentChannelInfoResult?.data || [];

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

</script>
