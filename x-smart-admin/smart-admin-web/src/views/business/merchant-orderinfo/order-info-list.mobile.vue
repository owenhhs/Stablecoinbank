<template>
  <div class="container">
    <div class="filter">
      <van-button class="option-button" type="primary" @click="openFilterOptionPopup">{{ $t('business.form.filter') }}</van-button>
      <van-button class="query-button" type="primary" @click="queryData">{{ $t('business.form.query') }}</van-button>
      <van-popup v-model:show="filterOptionPopupShow" position="top">
        <van-form>
          <van-cell-group inset>
            <van-field v-model="queryForm.orderNo" :label="$t('order.form.orderNo')" :placeholder="$t('order.form.orderNo')" />
            <van-field
              v-model="queryForm.collectionCardNo"
              :label="$t('order.form.collectionCardNo')"
              :placeholder="$t('order.form.collectionCardNo')"
            />
            <van-field v-model="queryForm.depositHolder" :label="$t('order.form.depositHolder')" :placeholder="$t('order.form.depositHolder')" />
            <!-- <van-field v-model="queryForm.status" name="订单状态" label="订单状态" placeholder="订单状态" /> -->
            <van-field
              v-model="queryForm.dateRange"
              is-link
              readonly
              name="datePicker"
              :label="$t('order.form.queryDateRange')"
              :placeholder="$t('order.form.queryDateRange')"
              @click="setFilterRangeDate"
            >
              <template v-if="queryForm.startDate && queryForm.endDate" #input>
                <div>{{ queryForm.startDate }} ~ {{ queryForm.endDate }}</div>
              </template>
            </van-field>
          </van-cell-group>
          <div class="filter-action">
            <van-button class="filter-action-button" @click="resetQuery">{{ $t('business.form.reset') }}</van-button>
            <van-button class="filter-action-button" type="primary" @click="queryData">{{ $t('business.form.query') }}</van-button>
          </div>
        </van-form>
      </van-popup>

      <van-popup v-model:show="filterDateRangePopupShow" position="bottom">
        <van-picker-group
          :title="$t('order.form.queryDateRange')"
          :tabs="['开始日期', '结束日期']"
          @confirm="changeQueryDate"
          @cancel="filterDateRangePopupShow = false"
        >
          <van-date-picker />
          <van-date-picker />
        </van-picker-group>
      </van-popup>
    </div>

    <div class="table-list">
      <template v-if="tableData.length">
        <div v-for="item in tableData" class="list-card" :key="item.id">
          <template v-for="columnItem in columns.slice(0, 6)" :key="columnItem.dataIndex">
            <van-field v-if="columnItem.dataIndex === 'receiptFileUrl'" :label="columnItem.title" :model-value="item[columnItem.dataIndex]" readonly>
              <template v-if="item[columnItem.dataIndex]" #input>
                <van-image height="100" :src="item[columnItem.dataIndex]" @click="viewImages([item[columnItem.dataIndex]])" />
              </template>
            </van-field>
            <van-field
              v-else-if="columnItem.dataIndex === 'status'"
              :label="columnItem.title"
              :model-value="$smartEnumPlugin.getDescByValue('ORDER_STATUS', item[columnItem.dataIndex])"
              readonly
            />
            <van-field
              v-else-if="columnItem.dataIndex === 'depositType'"
              :label="columnItem.title"
              :model-value="$smartEnumPlugin.getDescByValue('ORDER_STATUS', item[columnItem.dataIndex])"
              readonly
            />
            <van-field v-else :label="columnItem.title" :model-value="item[columnItem.dataIndex]" readonly />
          </template>
          <div class="list-card-footer">
            <van-button class="list-card-button" type="default" @click="showForm(item)">{{ $t('business.form.view') }}</van-button>
            <van-button v-if="item.status === 1" class="list-card-button" type="primary" @click="onOrderConfirmation(item)">{{
              $t('business.form.confirm')
            }}</van-button>
            <van-button v-if="item.status !== 2 && item.status !== 5" class="list-card-button" type="warning" @click="onOrderRefund(item)">{{
                $t('business.form.refund')
              }}</van-button>
            <van-button v-if="item.status > 2" @click="addBlack(item)" type="link" v-privilege="'payment:user:black'">{{ $t('business.form.user.black') }}</van-button>
          </div>
        </div>
      </template>
      <van-empty v-else description="暂无数据" />
      <van-pagination
        v-model="queryForm.pageNum"
        class="table-pagination"
        :total-items="total"
        :items-per-page="queryForm.pageSize"
        @change="queryData"
      />
    </div>
  </div>

  <!-- 查看表单 -->
  <van-popup v-model:show="formDetailPopupShow" style="max-height: 90%" position="bottom" closeable>
    <template v-for="item in columns" :key="item.dataIndex">
      <van-field v-if="item.dataIndex === 'receiptFileUrl'" :label="item.title" :model-value="formDetailData[item.dataIndex]" readonly>
        <template v-if="formDetailData[item.dataIndex]" #input>
          <van-image height="100" :src="formDetailData[item.dataIndex]" @click="viewImages([formDetailData[item.dataIndex]])" />
        </template>
      </van-field>
      <van-field
        v-else-if="item.dataIndex === 'status'"
        :label="item.title"
        :model-value="$smartEnumPlugin.getDescByValue('ORDER_STATUS', formDetailData[item.dataIndex])"
        readonly
      />
      <van-field
        v-else-if="item.dataIndex === 'depositType'"
        :label="item.title"
        :model-value="$smartEnumPlugin.getDescByValue('ORDER_STATUS', formDetailData[item.dataIndex])"
        readonly
      />
      <van-field v-else :label="item.title" :model-value="formDetailData[item.dataIndex]" readonly />
    </template>
  </van-popup>
  <OrderRefundForm ref="orderRefundRef" @reloadList="queryData" />
</template>
<script setup>
  import { reactive, ref, onMounted, onBeforeUnmount } from 'vue';
  import { showConfirmDialog, showImagePreview } from 'vant';
  import 'vant/es/dialog/style';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { orderInfoApi } from '/@/api/business/order-info/order-info-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges.js';
  import { useI18n } from 'vue-i18n';
  import OrderRefundForm from "/@/views/business/merchant-orderinfo/order-refund-form.vue";
  import {paymentChannelInfoApi} from "/@/api/business/payment-channel-info/payment-channel-info-api.js";

  const { t } = useI18n();

  // ---------------------------- 查询条件弹窗 ----------------------------

  const filterOptionPopupShow = ref(false);

  function openFilterOptionPopup() {
    filterOptionPopupShow.value = true;
  }

  const filterDateRangePopupShow = ref(false);

  const setFilterRangeDate = () => {
    filterDateRangePopupShow.value = true;
  };

  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: t('order.form.orderNo'),
      dataIndex: 'orderNo',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('order.form.applyTime'),
      dataIndex: 'applyTime',
      ellipsis: true,
      width: 180,
    },
    {
      title: t('order.form.status'),
      dataIndex: 'status',
      ellipsis: true,
      fixed: 'right',
      width: 120,
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
      title: t('settle.detail.table.column.depositHolder'),
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
      dataIndex: 'refundRemark',
      ellipsis: true,
      fixed: 'right',
      width: 120,
    },
    // {
    //   title: t('settle.table.column.action'),
    //   dataIndex: 'action',
    //   fixed: 'right',
    //   width: 120,
    // },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    orderNo: null,
    collectionCardNo: null,
    depositHolder: null,
    dateRange: [],
    startDate: '',
    endDate: '',
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 查询时间
  const queryDateRange = ref([]);
  const queryDateRangeStart = ref();
  const queryDateRangeEnd = ref();
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

  function changeQueryDate(dates) {
    const [startDate, endDate] = dates;
    const startDateString = startDate.selectedValues.join('-');
    const endDateString = endDate.selectedValues.join('-');
    queryForm.startDate = startDateString;
    queryForm.endDate = endDateString;
    filterDateRangePopupShow.value = false;
  }

  let rollTimer = null;

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
      if (refresh && tableData.value.length > 0 && queryResult.data.list.length > 0) {
        if (tableData.value[0].id !== queryResult.data.list[0].id) {
          pushNotification();
        }
      }
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;

      rollTimer = setTimeout(() => {
        queryData(true);
      }, 10 * 1000);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function pushNotification() {
    // 检查浏览器是否支持通知
    if ('Notification' in window) {
      // 请求用户授权
      Notification.requestPermission().then(function (permission) {
        // 如果用户同意，则发送通知
        if (permission === 'granted') {
          var notification = new Notification('有新订单', {
            body: '有新的订单，请注意及时处理！',
          });
          notification.onclick = function () {
            // 通知被点击时的操作
            console.log('Notification clicked!');
          };
          notification.onclose = function () {
            // 通知被关闭时的操作
            console.log('Notification closed!');
          };
        }
      });
    } else {
      console.log('Notifications are not supported in this browser.');
    }
  }

  onMounted(() => {
    // 检查浏览器是否支持通知
    if ('Notification' in window) {
      // 请求用户授权
      Notification.requestPermission().then(function (permission) {});
    } else {
      console.log('Notifications are not supported in this browser.');
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
  const formDetailPopupShow = ref(false);
  const formDetailData = ref();

  function showForm(data) {
    formDetailData.value = data;
    formDetailPopupShow.value = true;
  }

  function viewImages(images) {
    showImagePreview({
      images,
      showIndex: false,
      style: {
        width: '100%',
      },
    });
  }


  // -------------------------- 用户添加黑名单 ----------------------------

  function addBlack(data) {
    showConfirmDialog({
      title: '标题',
      message:
          '如果解决方法是丑陋的，那就肯定还有更好的解决方法，只是还没有发现而已。',
    })
    .then(async () => {
      await paymentChannelInfoApi.updateUserBlack(data.userId, 1)
      message.success("添加成功")
    })
    .catch(() => {
    });
  }

  // ---------------------------- 订单确认 ------------------------------
  const orderConfirmRef = ref();

  function onOrderConfirmation(data) {
    // orderConfirmRef.value.show(data);
    const { id, depositHolder, bankAccount, amount } = data || {};
    showConfirmDialog({
      title: t('order.confirm.form.title'),
      message: `是否收到来自于[${depositHolder}]的[${bankAccount}]卡到账[${amount}]元`,
      confirmButtonText: t('business.form.confirm'),
      cancelButtonText: t('business.form.cancel'),
    }).then(() => {
      showConfirmDialog({
        title: t('business.form.tip'),
        message: t('settle.confirmorder.content'),
        confirmButtonText: t('business.form.confirm'),
        cancelButtonText: t('business.form.cancel'),
      }).then(async () => {
        SmartLoading.show();
        try {
          await orderInfoApi.update({
            id: id,
            status: 2,
          });
          queryData();
        } catch (err) {
          smartSentry.captureError(err);
        } finally {
          SmartLoading.hide();
        }
      });
    });
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

<style lang="less" scoped>
  .container {
    padding: 10px;
  }

  .filter {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;

    .option-button {
      flex: 1;
    }

    .query-button {
      width: 80px;
    }

    .filter-action {
      display: flex;
      justify-content: center;
      gap: 10px;
      margin-top: 10px;
      padding: 10px;
    }

    .filter-action-button {
      flex: 1;
    }
  }

  .table-list {
    padding-bottom: 80px;

    .list-card {
      margin-bottom: 10px;
      border-radius: 8px;
      box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.1);
      padding: 10px;
      background-color: #fff;
    }

    .list-card-footer {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-top: 10px;
    }

    .list-card-button {
      flex: 1;
    }

    .table-pagination {
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      z-index: 100;
      background-color: #fff;
      padding-top: 10px;
    }
  }
</style>
