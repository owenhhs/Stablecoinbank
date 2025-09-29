<!--
  * 商户表
  *
  * @Author:    sunyu
  * @Date:      2024-07-24 16:21:32
  * @Copyright  sunyu
-->
<template>
  <a-card size="small" :bordered="false" :hoverable="true">
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="showForm(null, false)" type="primary" size="small" v-privilege="'channel:parent:add'">
          <template #icon>
            <PlusOutlined/>
          </template>
          {{ $t('business.form.create') }}
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.MER_MANAGE.CHANNEL_INFO" :refresh="queryData"/>
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
    >
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'status'">
          <span>{{ $smartEnumPlugin.getDescByValue('MER_STATUS', text) }}</span>
        </template>

        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="showForm(record, true)" type="link" >{{ $t('business.form.view') }}</a-button>
            <a-button @click="showForm(record, false)" type="link" v-privilege="'channel:parent:update'" >{{ $t('business.form.edit') }}</a-button>
          </div>
        </template>
      </template>
    </a-table>
    <!---------- 表格 end ----------->
    <ChannelInfoForm ref="formRef" @reloadList="queryData"/>

  </a-card>
</template>
<script setup>
import {reactive, ref, onMounted} from 'vue';
import {paymentChannelInfoApi} from '/src/api/business/payment-channel-info/payment-channel-info-api.js';
import {smartSentry} from '/@/lib/smart-sentry';
import TableOperator from '/@/components/support/table-operator/index.vue';
import {TABLE_ID_CONST} from "/@/constants/support/table-id-const.js";
import ChannelInfoForm from "/src/views/business/parent-channel-manage/channel-info-form.vue";

import { useI18n } from 'vue-i18n';

const { t } = useI18n();
// ---------------------------- 表格列 ----------------------------

const columns = ref([
  {
    title: t('channelManage.form.id'),
    dataIndex: 'id',
    ellipsis: true,
  },
  {
    title: t('channelManage.form.merName'),
    dataIndex: 'merName',
    ellipsis: true,
  },
  {
    title: t('channelManage.form.merCode'),
    dataIndex: 'merCode',
    ellipsis: true,
  },
  {
    title: t('channelManage.form.status'),
    dataIndex: 'status',
    ellipsis: true,
  },
  {
    title: t('channelManage.form.createTime'),
    dataIndex: 'createTime',
    ellipsis: true,
  },
  {
    title: t('channelManage.form.updateTime'),
    dataIndex: 'updateTime',
    ellipsis: true,
  },
  {
    title: t('business.form.action'),
    dataIndex: 'action',
    fixed: 'right',
    width: 90,
  },
]);

// ---------------------------- 查询数据表单和方法 ----------------------------

// 表格加载loading
const tableLoading = ref(false);
// 表格数据
const tableData = ref([]);

// 查询数据
async function queryData() {
  tableLoading.value = true;
  try {
    let queryResult = await paymentChannelInfoApi.queryParentChannelInfoList();
    tableData.value = queryResult.data;
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    tableLoading.value = false;
  }
}


onMounted(queryData);

// ---------------------------- 添加/修改 ----------------------------
const formRef = ref();

function showForm(data, readonly) {
  formRef.value.show(data, readonly);
}

</script>
