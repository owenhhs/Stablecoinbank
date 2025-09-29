<template>
  <default-home-card icon="FlagOutlined" title="渠道订单数据（每30秒自动刷新）">
    <a-empty v-if="$lodash.isEmpty(data)" />
    <ul v-else>
      <!---------- 表格 begin ----------->
      <a-table
        size="small"
        :dataSource="data"
        :columns="columns"
        rowKey="channelId"
        bordered
        :loading="tableLoading"
        :pagination="false"
      >
        <template #bodyCell="{ text, record, column }">
        
          <!-- <template v-if="column.dataIndex === 'totalAmount' || column.dataIndex === 'totalAmountPayment' || column.dataIndex === 'totalAmountCash'">
            <span>{{ text }}元</span>
          </template> -->
          
        </template>
      </a-table>
      <!---------- 表格 end ----------->
    </ul>
  </default-home-card>
</template>

<script setup>

import DefaultHomeCard from "/@/views/system/home/components/default-home-card.vue";
import {dashBoardApi} from "/@/api/business/dashboard/dashboard-api.js"
import {smartSentry} from "/@/lib/smart-sentry.js";
import {onMounted, ref} from "vue";

const columns = ref([
    {
      title: '渠道编号',
      dataIndex: 'merCode',
      ellipsis: true,
      width: 100,
    },  
    {
      title: '渠道名称',
      dataIndex: 'channelName',
      ellipsis: true,
      width: 120,
    },
    {
      title: '当日订单金额（轧差）',
      dataIndex: 'totalAmount',
      ellipsis: true,
      width: 150,
    },
    {
      title: '当日订单数',
      dataIndex: 'totalOrderCount',
      ellipsis: true,
      width: 80,
    },
    {
      title: '【入金】当日总额',
      dataIndex: 'totalAmountPayment',
      ellipsis: true,
      width: 130,
    },
    {
      title: '【入金】当日笔数',
      dataIndex: 'totalOrderCountPayment',
      ellipsis: true,
      width: 130,
    },
    {
      title: '【出金】当日总额',
      dataIndex: 'totalAmountCash',
      ellipsis: true,
      width: 130,
    },
    {
      title: '【出金】当日笔数',
      dataIndex: 'totalOrderCountCash',
      ellipsis: true,
      width: 130,
    },
  ]);

let data = ref([]);

const loading = ref(false);

let rollTimer = null;

async function queryTodayOrderInfo() {
  if (rollTimer) {
    clearTimeout(rollTimer);
  }
  loading.value = true;
  try {
    let queryResult = await dashBoardApi.todayOrderInfo();
    data.value = queryResult.data;

    rollTimer = setTimeout(() => {
      queryTodayOrderInfo()
    }, 30 * 1000);
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    loading.value = false;
  }
}

onMounted(queryTodayOrderInfo);


</script>


<style scoped lang="less">
// 需要变色的偶数行，可以在2n更改
/deep/.ant-table-tbody tr:nth-child(2n) {
  background-color: #fafafa;
}
</style>