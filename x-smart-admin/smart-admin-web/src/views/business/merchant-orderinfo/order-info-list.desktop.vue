<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form" id="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item :label="$t('order.form.orderNo')" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.orderNo" :placeholder="$t('order.form.orderNo')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.collectionCardNo')" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.collectionCardNo" :placeholder="$t('order.form.collectionCardNo')" />
      </a-form-item>
    </a-row>
    <a-row class="smart-query-form-row">
      <a-form-item :label="$t('order.form.depositHolder')" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="queryForm.depositHolder" :placeholder="$t('order.form.depositHolder')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.status')" class="smart-query-form-item">
        <SmartEnumSelect width="120px" enum-name="ORDER_STATUS" v-model:value="queryForm.status" :placeholder="$t('business.form.selector.placeholder')" />
      </a-form-item>
      <a-form-item :label="$t('order.form.queryDateRange')" class="smart-query-form-item">
        <a-range-picker @change="changeQueryDate" v-model:value="queryDateRange" :presets="defaultChooseTimeRange" style="width: 240px" />
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
        <!-- <template v-if="column.dataIndex === 'serialNumber'">
          <span>{{ index + 1 }}</span>
        </template> -->
        <template v-if="column.dataIndex === 'receiptFileUrl'">
          <a-image v-if="text" :height="60" :src="text" />
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
        <template v-if="column.dataIndex === 'depositType'">
          <span>{{ $smartEnumPlugin.getDescByValue('PAYMENT_METHOD', text) }}</span>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="showForm(record)" type="link">{{ $t('business.form.view') }}</a-button>
            <a-button v-if="record.status === 1" @click="onOrderConfirmation(record)" danger type="link"
                      v-privilege="'order:info:update'">{{ $t('business.form.confirm') }}</a-button>

            <a-button v-if="record.status === 4" @click="onOrderConfirmation(record)" danger type="link"
                      v-privilege="'order:info:reconfirm'">{{ $t('business.form.confirm') }}</a-button>

            <a-button v-if="record.status !== 2 && record.status !== 5" @click="onOrderRefund(record)" danger type="link"
                      v-privilege="'order:info:update'">{{ $t('business.form.refund') }}</a-button>
            <a-button v-if="record.status > 2"  @click="showBlack(record)" type="link" v-privilege="'payment:user:black'">{{ $t('business.form.user.black') }}</a-button>
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

    <OrderInfoForm ref="formRef" @reloadList="queryData" />
    <OrderConfirmForm ref="orderConfirmRef" @reloadList="queryData" />
    <OrderRefundForm ref="orderRefundRef" @reloadList="queryData" />
    <Modal v-model:open="blackRef" :title="$t('business.alert.title.black')" @ok="addBlack" />
  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted, onBeforeUnmount } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import OrderInfoForm from './order-info-form.vue';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  // import { collectioncardApi } from '/@/api/business/collectioncard/collectioncard-api.js';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import OrderConfirmForm from './order-confirm-form.vue';
  import { useI18n } from 'vue-i18n';
  import OrderRefundForm from "/@/views/business/merchant-orderinfo/order-refund-form.vue";
  import {paymentChannelInfoApi} from "/@/api/business/payment-channel-info/payment-channel-info-api.js";

  const { t } = useI18n()
  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: t('order.form.orderNo'),
      dataIndex: 'orderNo',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('order.form.depositType'),
      dataIndex: 'depositType',
      ellipsis: true,
      width: 120,
    },
    {
      title: t('order.form.paymentChannel'),
      dataIndex: 'paymentChannel',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('order.form.amount'),
      dataIndex: 'amount',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('order.form.currency'),
      dataIndex: 'currency',
      ellipsis: true,
      width: 80,
    },
    {
      title: t('order.form.country'),
      dataIndex: 'country',
      ellipsis: true,
      width: 80,
    },
    {
      title: t('order.form.receiptFileUrl'),
      dataIndex: 'receiptFileUrl',
      ellipsis: true,
      width: 120,
    },
    {
      title:  t('settle.detail.table.column.depositHolder'),
      dataIndex: 'depositHolder',
      ellipsis: true,
      width: 120,
    },
    {
      title: t('settle.detail.table.column.bankAccount'),
      dataIndex: 'bankAccount',
      ellipsis: true,
      width: 150,
    },
    {
      title: t('order.form.collectionBank'),
      dataIndex: 'collectionBank',
      ellipsis: true,
      width: 120,
    },
    {
      title: t('order.form.collectionBankAddress'),
      dataIndex: 'collectionBankAddress',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('order.form.collectionCardNo'),
      dataIndex: 'collectionCardNo',
      ellipsis: true,
      width: 220,
    },
    {
      title: t('order.form.collectionHolder'),
      dataIndex: 'collectionHolder',
      ellipsis: true,
      width: 220,
    },
    {
      title: t('order.form.applyTime'),
      dataIndex: 'applyTime',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('order.form.refundReason'),
      dataIndex: 'refundReason',
      ellipsis: true,
      width: 120,
    },
    {
      title: t('order.form.status'),
      dataIndex: 'status',
      ellipsis: true,
      fixed: 'right',
      width: 120,
    },
    {
      title: t('settle.table.column.action'),
      dataIndex: 'action',
      fixed: 'right',
      width: 140,
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
  // 收款卡选项
  // const cardOptions = ref([])
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
    queryForm.startDate = dateStrings[0];
    queryForm.endDate = dateStrings[1];
  }

  let rollTimer = null;

  async function search() {
    queryForm.pageNum = 1
    await queryData();
  }

  // 查询数据
  async function queryData(refresh) {
    if (rollTimer) {
      clearTimeout(rollTimer);
    }
    tableLoading.value = true;
    try {
      // let cardOptionsResult = await collectioncardApi.options()
      // cardOptions.value = cardOptionsResult.data
      let queryResult = await orderInfoApi.queryPage(queryForm);
      // 如果请求数据与当前数据不一致，则触发推送（自动刷新页面时）
      if (queryForm.pageNum === 1 && refresh && tableData.value.length > 0 && queryResult.data.list.length > 0) {
        if (tableData.value[0].id !== queryResult.data.list[0].id) {
          pushNotification()
        }
      }
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;

      rollTimer = setTimeout(() => {
        queryData(true)
      }, 10 * 1000);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function pushNotification() {
    // 检查浏览器是否支持通知
    if ("Notification" in window) {
      // 请求用户授权
      Notification.requestPermission().then(function(permission) {
        // 如果用户同意，则发送通知
        if (permission === "granted") {
          var notification = new Notification("有新订单", {
            body: "有新的订单，请注意及时处理！"
          });
          notification.onclick = function() {
            // 通知被点击时的操作
            console.log("Notification clicked!");
          };
          notification.onclose = function() {
            // 通知被关闭时的操作
            console.log("Notification closed!");
          };
        }
      });
    } else {
      console.log("Notifications are not supported in this browser.");
    }
  }

  onMounted(() => {
    // 检查浏览器是否支持通知
    if ("Notification" in window) {
      // 请求用户授权
      Notification.requestPermission().then(function(permission) {
      });
    } else {
      console.log("Notifications are not supported in this browser.");
    }
    queryData();
  });

  onBeforeUnmount(() => {
    if (rollTimer) {
      clearTimeout(rollTimer);
    }
  });

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
  }

  // -------------------------- 用户添加黑名单 ----------------------------
  const blackRef = ref(false);
  const userId = ref(0)

  function showBlack(data) {
    blackRef.value = true;
    userId.value = data.userId
  }

  async function addBlack() {
    await paymentChannelInfoApi.updateUserBlack(userId.value, 1)
    blackRef.value = false;
    message.success("添加成功")
  }

  // ---------------------------- 订单确认 ------------------------------
  const orderConfirmRef = ref();

  function onOrderConfirmation(data) {
    orderConfirmRef.value.show(data);
  }

  // ---------------------------- 订单退款 ------------------------------
  const orderRefundRef = ref();

  function onOrderRefund(data) {
    orderRefundRef.value.show(data);
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
      await orderInfoApi.delete(data.id);
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
      await orderInfoApi.batchDelete(selectedRowKeyList.value);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }
</script>
