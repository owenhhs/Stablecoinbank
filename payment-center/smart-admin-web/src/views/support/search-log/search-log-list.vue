<!--
  * 操作记录 列表
  * 
  * @Author:    1024创新实验室-主任：卓大 
  * @Date:      2022-06-02 20:23:08 
  * @Wechat:    zhuda1024 
  * @Email:     lab1024@163.com 
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012 
-->
<template>
  <a-form class="smart-query-form" v-privilege="'support:searchLog:query'">
    <a-row class="smart-query-form-row">
      <a-form-item label="关键字" class="smart-query-form-item">
        <a-input style="width: 300px" v-model:value="queryForm.keyword" placeholder="关键字" />
      </a-form-item>

      <a-form-item label="查询日期" class="smart-query-form-item">
        <a-date-picker :format="dateFormat" @change="onDateChange" />
      </a-form-item>

      <a-form-item label="日志级别" class="smart-query-form-item">
        <a-radio-group v-model:value="queryForm.type">
          <a-radio-button value="all">全部</a-radio-button>
          <a-radio-button value="debug">debug</a-radio-button>
          <a-radio-button value="info">info</a-radio-button>
          <a-radio-button value="error">error</a-radio-button>
          <a-radio-button value="warn">warn</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <a-form-item label="查询条数" class="smart-query-form-item">
        <a-radio-group v-model:value="queryForm.count">
          <a-radio-button :value="100">100条</a-radio-button>
          <a-radio-button :value="200">200条</a-radio-button>
          <a-radio-button :value="500">500条</a-radio-button>
          <a-radio-button :value="1000">1000条</a-radio-button>
        </a-radio-group>
      </a-form-item>

      <a-form-item class="smart-query-form-item smart-margin-left10">
        <a-button type="primary" @click="onSearch" class="smart-margin-right10">
          <template #icon>
            <ReloadOutlined />
          </template>
          查询
        </a-button>
        <a-button @click="resetQuery">
          <template #icon>
            <SearchOutlined />
          </template>
          重置
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>

  <a-card size="small" :bordered="false" :hoverable="true" style="height: 100%">
    <a-row justify="end">
      <TableOperator class="smart-margin-bottom5" v-model="columns" :tableId="TABLE_ID_CONST.SUPPORT.CONFIG" :refresh="ajaxQuery" />
    </a-row>
    <a-table size="small" :loading="tableLoading" :dataSource="tableData" :columns="columns" bordered rowKey="index" :pagination="false">
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="showDetail(record)" type="link" v-privilege="'support:searchLog:query'">详情</a-button>
            <a-button @click="copDetail(record)" type="link" v-privilege="'support:searchLog:query'">复制</a-button>
          </div>
        </template>
      </template>
    </a-table>

    <searchLogDetailModal ref="detailModal" />
  </a-card>
</template>
<script setup>
  import { onMounted, reactive, ref } from 'vue';
  import searchLogDetailModal from './search-log-detail-modal.vue';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import { searchLogApi } from "/@/api/support/search-log-api.js";
  import {message} from "ant-design-vue";

  const dateFormat = 'YYYY-MM-DD';

  const onDateChange = (date, dateString) => {
    queryForm.date = dateString;
  };

  const columns = ref([
    {
      title: '日志时间',
      dataIndex: 'ts',
      width: 190,
    },
    {
      title: '日志级别',
      dataIndex: 'level',
      width: 80,
      ellipsis: true,
    },
    {
      title: '请求流水号',
      dataIndex: 'requestId',
      ellipsis: true,
      width: 120,
    },
    {
      title: '线程名',
      dataIndex: 'thread',
      ellipsis: true,
      width: 120
    },
    {
      title: '日志位置',
      dataIndex: 'className',
      ellipsis: true,
      width: 200,
    },
    {
      title: '日志信息',
      dataIndex: 'msg',
      ellipsis: true,
    },
    {
      title: '堆栈信息',
      dataIndex: 'stack',
      ellipsis: true,
      width: 200,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 60,
    },
  ]);

  const queryFormState = {
    keyword: '',
    count: 100,
    type: 'all',
    date: undefined,
  };
  const queryForm = reactive({ ...queryFormState });


  const tableLoading = ref(false);
  const tableData = ref([]);

  function resetQuery() {
    Object.assign(queryForm, queryFormState);
    ajaxQuery();
  }

  function onSearch() {
    ajaxQuery();
  }

  async function ajaxQuery() {
    try {
      tableLoading.value = true;
      let responseModel = await searchLogApi.queryList(queryForm);
      tableData.value = responseModel.data;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(ajaxQuery);

  // ---------------------- 详情 ----------------------
  const detailModal = ref();
  function showDetail(record) {
    detailModal.value.show(record);
  }

  function copDetail(record) {
    navigator.clipboard.writeText(JSON.stringify(record));
    message.success("已复制到剪贴板");
  }

</script>
