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
        <a-input style="width: 240px" v-model:value="queryForm.orderNo" placeholder="订单号" />
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
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="showForm(record)" type="link">查看</a-button>
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

    <FailOrderInfoForm ref="formRef" @reloadList="queryData" />
  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import FailOrderInfoForm from './fail-order-info-form.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: '主键',
      dataIndex: 'id',
      ellipsis: true,
      width: 50,
    },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      ellipsis: true,
      width: 170,
    },
    {
      title: '请求参数',
      dataIndex: 'params',
      ellipsis: true,
      width: 270,
    },
    {
      title: '失败信息',
      dataIndex: 'failMsg',
      ellipsis: true,
      width: 270,
    },
    {
      title: '请求流水号',
      dataIndex: 'requestId',
      ellipsis: true,
      width: 170,
    },
    {
      title: '记录时间',
      dataIndex: 'createTime',
      ellipsis: true,
      width: 170,
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
    orderNo: null,
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });

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
    queryData();
  }

  async function search() {
    queryForm.pageNum = 1;
    await queryData();
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await orderInfoApi.queryPageFailOrder(queryForm);
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

  // ---------------------------- 查看 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
  }
</script>
