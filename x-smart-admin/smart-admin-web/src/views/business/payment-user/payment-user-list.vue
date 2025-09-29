<!--
  * 用户信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="用户ID" class="smart-query-form-item">
        <a-input style="width: 120px" v-model:value="queryForm.userId" placeholder="用户ID" />
      </a-form-item>
      <a-form-item label="用户名" class="smart-query-form-item">
        <a-input style="width: 120px" v-model:value="queryForm.userName" placeholder="用户名" />
      </a-form-item>
      <a-form-item label="新疆用户" class="smart-query-form-item">
        <SmartEnumSelect width="120px" enum-name="XJ_STATUS" v-model:value="queryForm.isXj" :placeholder="$t('business.form.selector.placeholder')" />
      </a-form-item>
      <a-form-item label="黑名单用户" class="smart-query-form-item">
        <SmartEnumSelect width="120px" enum-name="BLACK_STATUS" v-model:value="queryForm.black" :placeholder="$t('business.form.selector.placeholder')" />
      </a-form-item>
      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="search">
          <template #icon>
            <SearchOutlined />
          </template>
          {{ $t('business.form.query') }}
        </a-button>
        <a-button @click="resetQuery" class="smart-margin-left10">
          <template #icon>
            <ReloadOutlined />
          </template>
          {{ $t('business.form.reset') }}
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>
  <!---------- 查询表单form end ----------->

  <a-card size="small" :bordered="false" :hoverable="true">
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-setting-block">
        <a-button @click="exportOrders" type="primary" size="small">
          <template #icon>
            <ExportOutlined />
          </template>
          {{ $t('order.form.export') }}
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.MER_MANAGE.PAYMENT_USER_INFO" :refresh="queryData" />
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
        <template v-if="column.dataIndex === 'black'">
          <a-switch :checked="record.black === 1" checked-children="开" un-checked-children="关" @change="blackChange(record)" />
        </template>
        <template v-if="column.dataIndex === 'isXj'">
          <span>{{ $smartEnumPlugin.getDescByValue('XJ_STATUS', text) }}</span>
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

  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api.js';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import { ExportOutlined } from '@ant-design/icons-vue';
  import { useI18n } from 'vue-i18n';
  import SmartEnumSelect from "/@/components/framework/smart-enum-select/index.vue";

  const { t } = useI18n();
  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: '用户ID',
      dataIndex: 'id',
      ellipsis: true,
      width: 180,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      ellipsis: true,
      width: 100,
    },
    {
      title: '支付中心用户ID',
      dataIndex: 'extUserId',
      ellipsis: true,
      width: 150,
    },
    {
      title: '新疆用户',
      dataIndex: 'isXj',
      ellipsis: true,
      width: 150,
    },
    {
      title: '黑名单用户',
      dataIndex: 'black',
      ellipsis: true,
      width: 80,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      ellipsis: true,
      width: 80,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      ellipsis: true,
      width: 150,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
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
    queryForm.pageNum = 1
    await queryData();
  }


  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await paymentChannelInfoApi.paymentUserQueryPage(queryForm);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(init);

  async function init() {
    await queryData()
  }


  // -------------------------- 用户添加黑名单 ----------------------------

  async function blackChange(data) {
    await paymentChannelInfoApi.updateUserBlack(data.id, data.black ^ 1)
    queryData()
  }


</script>
