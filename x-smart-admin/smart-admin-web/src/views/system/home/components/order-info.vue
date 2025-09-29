<template>
  <default-home-card icon="FlagOutlined" title="渠道订单数据（每30秒自动刷新）">
    <a-empty v-if="$lodash.isEmpty(data)" />
    <ul v-else>
      <template v-for="(item, index) in data" :key="index">
        <li class="order-item">
          <a-row :gutter="[10, 10]">
            <a-col :span="8">
              <span class="order-data">渠道商：{{item.channelName}}</span>
            </a-col>
            <a-col :span="8">
              <span class="order-data">当日订单金额：{{item.totalAmount}}</span>
            </a-col>
            <a-col :span="8">
              <span class="order-data">当日订单数：{{ item.totalOrderCount }}</span>
            </a-col>
          </a-row>
        </li>
      </template>
    </ul>
  </default-home-card>
</template>

<script setup>

import DefaultHomeCard from "/@/views/system/home/components/default-home-card.vue";
import {orderInfoApi} from "/@/api/business/order-info/order-info-api.js"
import {smartSentry} from "/@/lib/smart-sentry.js";
import {onMounted, ref} from "vue";


let data = ref([]);

let rollTimer = null;

async function queryTodayOrderInfo() {
  if (rollTimer) {
    clearTimeout(rollTimer);
  }
  try {
    let queryResult = await orderInfoApi.todayOrderInfo();
    data.value = queryResult.data;

    rollTimer = setTimeout(() => {
      queryTodayOrderInfo()
    }, 30 * 1000);
  } catch (e) {
    smartSentry.captureError(e);
  }
}

onMounted(queryTodayOrderInfo);


</script>


<style scoped lang="less">

</style>