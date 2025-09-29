<!--
  * 商户表
  *
  * @Author:    sunyu
  * @Date:      2024-07-24 16:21:32
  * @Copyright  sunyu
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item :label="$t('merchantCardManage.form.type')" class="smart-query-form-item">
        <SmartEnumSelect
          v-model:value="queryForm.type"
          enumName="MER_CARD_PAY_TYPE"
          :placeholder="$t('business.form.selector.placeholder')"
        />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.username')" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.username" :placeholder="$t('merchantCardManage.form.username')" />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.bankNo')" class="smart-query-form-item">
        <a-input style="width: 280px" v-model:value="queryForm.bankNo" :placeholder="$t('merchantCardManage.form.bankNo')" />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.status')" class="smart-query-form-item">
        <SmartEnumSelect
          v-model:value="queryForm.status"
          enumName="MER_STATUS"
        />
      </a-form-item>
      <a-form-item :label="$t('merchantCardManage.form.departmentId')" class="smart-query-form-item">
        <a-select
            v-model:value="queryForm.departmentId"
            style="width: 200px"
            allow-clear
            :placeholder="$t('merchantCardManage.form.departmentId')"
        >
          <a-select-option v-for="item in merList" :key="item.id" :value="item.departmentId">{{ item.merName }}</a-select-option>
        </a-select>
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
      <div class="smart-table-operate-block">
        <a-button @click="showForm(null, 'add')" type="primary" size="small" v-privilege="'channel:payment:add'">
          <template #icon>
            <PlusOutlined />
          </template>
          {{ $t('business.form.create') }}
        </a-button>
        <!-- <a-button @click="confirmBatchDelete" type="danger" size="small" :disabled="selectedRowKeyList.length == 0">
          <template #icon>
            <DeleteOutlined />
          </template>
          批量删除
        </a-button> -->
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.MER_MANAGE.MER_INFO" :refresh="queryData" />
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
        <template v-if="column.dataIndex === 'status'">
          <span>{{ $smartEnumPlugin.getDescByValue('MER_STATUS', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'type'">
          <span>{{ $smartEnumPlugin.getDescByValue('MER_CARD_PAY_TYPE', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'payUrl'">
          <a-image v-if="text" :height="80" :src="text" />
        </template>
        <template v-if="column.dataIndex === 'blackList'">
          {{ $smartEnumPlugin.getDescByValue('MER_CARD_BLACK_LIST', text) }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div style="display: flex; align-items: center" class="smart-table-operate">
            <a-switch
              style="margin-right: 6px"
              :checked="record.status === 1"
              :checked-children="$t('business.form.open')"
              :un-checked-children="$t('business.form.close')"
            v-privilege="'channel:payment:update'"
            @click="updateStatus(record)"
            />
            <a-button @click="showForm(record, 'view')" type="link">{{ $t('business.form.view') }}</a-button>
            <a-button @click="showForm(record, 'edit')" type="link">{{ $t('business.form.edit') }}</a-button>
            <!-- <a-button @click="onDelete(record)" danger type="link">删除</a-button> -->
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

    <MerchantCardManageForm ref="formRef" @reloadList="queryData" />
  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { merchantCardManageApi } from '/@/api/business/merchant-card-manage/merchant-card-manage-api';
  import {PAGE_SIZE, PAGE_SIZE_OPTIONS} from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import MerchantCardManageForm from './merchant-card-manage-form.vue';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import {paymentChannelInfoApi} from "/@/api/business/payment-channel-info/payment-channel-info-api.js";
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();

  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: t('merchantCardManage.form.type'),
      dataIndex: 'type',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('merchantCardManage.form.departmentId'),
      dataIndex: 'merName',
      ellipsis: true,
      width: 100,
    },
    {
      title: t('merchantCardManage.form.username'),
      dataIndex: 'username',
      ellipsis: true,
      width: 100,
    },
    {
      title: t('merchantCardManage.form.bankNo'),
      dataIndex: 'bankNo',
      ellipsis: true,
      width: 280,
    },
    {
      title: t('merchantCardManage.form.bankInfo'),
      dataIndex: 'bankInfo',
      ellipsis: true,
      width: 120,
    },
    {
      title: t('merchantCardManage.form.bankName'),
      dataIndex: 'bankName',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('merchantCardManage.form.currency'),
      dataIndex: 'currency',
      ellipsis: true,
      width: 80,
    },
    {
      title: t('merchantCardManage.form.country'),
      dataIndex: 'country',
      ellipsis: true,
      width: 80,
    },
    {
      title: t('merchantCardManage.form.payUrl'),
      dataIndex: 'payUrl',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('merchantCardManage.form.workTime'),
      dataIndex: 'workTime',
      ellipsis: true,
      width: 240,
    },
    // {
    //   title: '支付比例',
    //   dataIndex: 'paymentScale',
    //   ellipsis: true,
    //   width: 80,
    // },
    {
      title: t('merchantCardManage.form.paymentLimit'),
      dataIndex: 'paymentLimit',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('merchantCardManage.form.paymentCount'),
      dataIndex: 'paymentCount',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('merchantCardManage.form.amountMin'),
      dataIndex: 'amountMin',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('merchantCardManage.form.amountMax'),
      dataIndex: 'amountMax',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('merchantCardManage.form.blackList'),
      dataIndex: 'blackList',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('merchantCardManage.form.updateTime'),
      dataIndex: 'updateTime',
      ellipsis: true,
      width: 240,
    },
    {
      title: t('merchantCardManage.form.status'),
      dataIndex: 'status',
      ellipsis: true,
      fixed: 'right',
      width: 180,
    },
    {
      title: t('business.form.action'),
      dataIndex: 'action',
      fixed: 'right',
      width: 150,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    pageNum: 1,
    pageSize: 5,
    type: null,
    username: null,
    bankNo: null,
    departmentId: null,
    status: null
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 表格加载loading
  const tableLoading = ref(false);
  // 表格数据
  const tableData = ref([]);
  // 总数
  const total = ref(0);
  // 商户列表
  const merList = ref([]);

  // 重置查询条件
  function resetQuery() {
    let pageSize = queryForm.pageSize;
    Object.assign(queryForm, queryFormState);
    queryForm.pageSize = pageSize;
    queryData();
  }

  async function search() {
    queryForm.pageNum = 1
    await queryData()
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await merchantCardManageApi.queryPage(queryForm);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);

      // tableData.value = [
      //   {
      //     id: 1, // 支付方式id
      //     type: 'Alipay', // 支付类型，Alipay、bank
      //     username: 'username', // 收款人姓名
      //     bankInfo: '广大', // 银行名称（只有银行卡类型有值）
      //     bankName: '光大刘洋', // 银行开户行（只有银行卡类型有值）
      //     bankNo: 1283402304234, // 银行卡号（只有银行卡类型有值）
      //     blackList: 1, // 是否在黑名单
      //     paymentScale: 0.1, // 支付比例
      //     paymentLimit: 20000, // 每日支付限额
      //     paymentCount: 5, // 每日支付限笔
      //     payUrl: 'https://ecmb.bdimg.com/tam-ogel/1033851030_-2035694540_4000_1214.jpg?x-bce-process=image/format,f_auto,/quality,q_80', // 支付二维码图片链接（只有支付宝类型有值）
      //     workTime: undefined, // 支付方式的工作时间 HH:SS
      //     status: 0, // 支付方式状态，0-禁用；1-启用；
      //     amountMin: 2, // 最小限额
      //     amountMax: 200, // 最大限额
      //     file: undefined, // 支付二维码
      //   },
      // ];
      // total.value = 2;
    } finally {
      tableLoading.value = false;
    }
  }

  async function queryMerList() {
    const paymentChannelInfoResult = await paymentChannelInfoApi.options();
    merList.value = paymentChannelInfoResult?.data || [];
  }

  onMounted(() => {
    queryMerList()
    queryData();
  });

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data, type) {
    formRef.value.show(data, type);
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
      await merchantCardManageApi.delete(data.id);
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
      await merchantCardManageApi.batchDelete(selectedRowKeyList.value);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // -------------------------- 修改状态 ----------------------------
  const updateStatus = async (record) => {
    try {
      SmartLoading.show();
      const params = {
        departmentId: record.departmentId,
        payInfoId: record.id,
        status: record.status === 0 ? 1 : 0,
      };
      console.log(params, record);
      await merchantCardManageApi.update(params);
      message.success(t('merchantCardManage.updateStatus.success'));
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  };
</script>
