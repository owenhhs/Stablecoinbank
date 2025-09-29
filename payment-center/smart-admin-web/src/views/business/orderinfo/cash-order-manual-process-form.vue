<!--
  * 订单信息表
  *
  * @Author:    sunyu
  * @Date:      2024-07-23 14:25:56
  * @Copyright  sunyu
-->
<template>
  <a-modal
      title="人工处理"
      :width="1000"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-divider/>    

    <a-card size="small" title="出金订单基本信息" :headStyle="{background: '#eee'}">
      <a-row :gutter="16" :wrap="true">
        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">订单编号：</div>
            <div class="order-info-row value">{{ form.orderNo }}</div>
          </div>
        </a-col>
        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">申请时间：</div>
            <div class="order-info-row value">{{ dayjs(form.applyTime).format('YYYY-MM-DD HH:mm:ss') }}</div>
          </div>
        </a-col>

        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">金额：</div>
            <div class="order-info-row value import">{{ form.amount }}元</div>
          </div>
        </a-col>
        <!-- <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">币种：</div>
            <div class="order-info-row value">{{ form.currency }}</div>
          </div>
        </a-col> -->
        <!-- <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">国家：</div>
            <div class="order-info-row value">{{ form.country }}</div>
          </div>
        </a-col> -->

        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">收款人：</div>
            <div class="order-info-row value">{{ form.accountHolder }}</div>
          </div>
        </a-col>
        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">收款账号：</div>
            <div class="order-info-row value">{{ form.bankAccount }}</div>
          </div>
        </a-col>
        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">收款银行：</div>
            <div class="order-info-row value">{{ form.bankName }}</div>
          </div>
        </a-col>
        <a-col class="gutter-row" :span="24">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">人工处理原因：</div>
            <div class="order-info-row value import warning">{{ form.manualReason }}</div>
          </div>
        </a-col>
        
      </a-row>
    </a-card>

    <a-card :loading="loading" size="small" title="出金情况" :headStyle="{background: '#eee'}" style="margin-top: 10px;">
      <a-row :gutter="16" :wrap="true">
        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">成功金额：</div>
            <div class="order-info-row value import">{{ subOrderSummary.amountSuccessed }}元</div>
          </div>
        </a-col>
        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">在途金额：</div>
            <div class="order-info-row value import">{{ subOrderSummary.amountProcessing }}元</div>
          </div>
        </a-col>
        <a-col class="gutter-row" :span="8">
          <div class="order-info-row gutter-box">
            <div class="order-info-row title">剩余金额：</div>
            <div class="order-info-row value import warning">{{ subOrderSummary.amountLeft }}元</div>
          </div>
        </a-col>
      </a-row>

      <a-table
        ref="detailTableRef"
        size="small"
        :dataSource="tableData"
        :columns="columns"
        rowKey="id"
        bordered
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1300, y: 1000 }"
      >
        <template #bodyCell="{ text, record, column }">
          <!-- <template v-if="column.dataIndex === 'serialNumber'">
            <span>{{ index + 1 }}</span>
          </template> -->
          <!-- <template v-if="column.dataIndex === 'billFileUrl'">
            <a-image v-if="text" :height="60" :src="text" />
          </template> -->
          <template v-if="column.dataIndex === 'applyTime' && record.applyTime === 0">
            <span>-</span>
          </template>
          <template v-if="column.dataIndex === 'applyTime' && record.applyTime > 0">
            <span>{{ dayjs(record.applyTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
          </template>

          <template v-if="column.dataIndex === 'finishedTime' && record.finishedTime === 0">
            <span>-</span>
          </template>
          <template v-if="column.dataIndex === 'finishedTime' && record.finishedTime > 0">
            <span>{{ dayjs(record.finishedTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
          </template>

          <template v-if="column.dataIndex === 'amount'">
            <span>{{ record.amount }}元</span>
          </template>
          <!-- <template v-if="column.dataIndex === 'settle'">
            <span>{{ (record.amount / record.price) * (1 - record.charge) }}</span>
          </template> -->
          <template v-if="column.dataIndex === 'status'">
            <span>{{ $smartEnumPlugin.getDescByValue('CASH_MAIN_ORDER_STATUS', text) }}</span>
          </template>
          <template v-if="column.dataIndex === 'manualFlag'">
            <!-- <span>{{ record.manualFlag === 1 ? '是（' + record.manualReason + '）' : '否' }}</span> -->
            <span>{{ record.manualFlag === 1 ? '人工处理' : '自动处理' }}</span>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button @click="showForm(record)" type="link">查看</a-button>
              <!-- <a-button v-if="record.billFileId === 0" @click="uploadReceipt(record)" type="link" v-privilege="'order:receipt:upload'" >补充回单</a-button> -->

              <a-button
                v-if="record.manualFlag === 1"
                @click="onOrderManualProc(record)"
                danger
                type="link"
              >人工处理</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card :loading="loading" size="small" title="人工指定渠道" :headStyle="{background: '#eee'}" style="margin-top: 10px;">
      <!---------- 表格操作行 begin ----------->
      <a-row class="smart-table-btn-block">
        <div class="smart-table-setting-block">
          <a-button @click="addRow" type="primary" size="small">
            <template #icon>
              <ExportOutlined />
            </template>
            添加一行
          </a-button>
        </div>
      </a-row>

      <!---------- 表格 begin ----------->
      <a-form ref="tableFormRef" :model="manualTableData" :label-col="{ style: { width: '10px' } }" :wrapper-col="{ span: 0 }" :rules="rules">
        <a-table
          ref="manualTableRef"
          size="small"
          :dataSource="manualTableData"
          :columns="manualColumns"
          rowKey="seqNo"
          bordered
          :pagination="false"
          :scroll="{ x: 900, y: 1000 }"
        >
          <template #bodyCell="{ text, record, column, index }">
            <!-- <template v-if="column.dataIndex === 'serialNumber'">
              <span>{{ index + 1 }}</span>
            </template> -->

            <template v-if="column.dataIndex === 'chnlType'">
              <a-form-item class="custom-form-item" label=" " :name="[index, 'chnlType']" :rules="rules.blur">
                <SmartEnumSelect style="width: 100%" v-model:value="record.chnlType" placeholder="渠道类型" enumName="CASH_CHNL_TYPE"/>
              </a-form-item>
            </template>

            <template v-if="column.dataIndex === 'channelId'">
              <a-form-item class="custom-form-item" label=" " :name="[index, 'channelId']" v-if="record.chnlType === 1"
                :required="record.chnlType === 1" :rules="rules.blur" >
                <CashChnlSelect style="width: 100%" v-model:value="record.channelId" />
              </a-form-item>

              <a-form-item class="custom-form-item" label=" " :name="[index, 'channelId']" v-if="record.chnlType === 2"
                :required="record.chnlType === 2" :rules="rules.blur">
                <!-- <SmartEnumSelect disabled style="width: 100%" v-model:value="record.chnlType" placeholder="渠道类型" enumName="CASH_CHNL_TYPE"/> -->
                <a-input-number style="width: 100%" v-model:value="record.channelId" placeholder="渠道编号"/>
              </a-form-item>
              <a-form-item class="custom-form-item" label=" " :name="[index, 'channelName']" v-if="record.chnlType === 2"
                :required="record.chnlType === 2" :rules="rules.blur">
                <!-- <SmartEnumSelect disabled style="width: 100%" v-model:value="record.chnlType" placeholder="渠道类型" enumName="CASH_CHNL_TYPE"/> -->
                <a-input style="width: 100%" v-model:value="record.channelName" placeholder="渠道名称"/>
              </a-form-item>
            </template>

            <template v-if="column.dataIndex === 'amount'">
              <a-form-item class="custom-form-item" label=" " :name="[index, 'amount']" :rules="rules.blur">
                <a-input style="width: 100%" v-model:value="record[column.dataIndex]" />
              </a-form-item>
            </template>

            <template v-if="column.dataIndex === 'thirdpartyOrderNo' && record.chnlType === 2">
              <a-form-item class="custom-form-item" label=" " :name="[index, 'thirdpartyOrderNo']" :rules="rules.blur">
                <a-input style="width: 100%" v-model:value="record.thirdpartyOrderNo" placeholder="第三方订单编号" />
              </a-form-item>
              <a-form-item class="custom-form-item" label=" " :name="[index, 'finishedTime']" :rules="rules.blur">
                <a-input style="width: 100%" v-model:value="record.finishedTime" placeholder="出金完成时间" />
              </a-form-item>
            </template>

            <template v-if="column.dataIndex === 'billFileId'">
              <a-form-item class="custom-form-item" label=" " :name="[index, 'billFileId']">
                <!-- <a-image v-if="text" :height="60" :src="text" /> -->
                <Upload accept=".jpg,.jpeg,.png" :maxUploadSize="1" buttonText="点击上传" :value="record.fileList" />
              </a-form-item>
            </template>
            
            <template v-if="column.dataIndex === 'action'">
              <div class="smart-table-operate">
                <!-- <a-button @click="showForm(record)" type="link">查看</a-button> -->
                <!-- <a-button v-if="record.billFileId === 0" @click="uploadReceipt(record)" type="link" v-privilege="'order:receipt:upload'" >补充回单</a-button> -->
              </div>
            </template>
          </template>
        </a-table>
      </a-form>
    </a-card>
    <a-divider/>
    <template #footer>
      <a-space>
        <a-button v-if="form.status === 1" type="primary" @click="onSubmit">确认处理</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
import {reactive, ref, nextTick} from 'vue';
import _ from 'lodash';
import {Modal, message} from 'ant-design-vue';
import {SmartLoading} from '/@/components/framework/smart-loading';
import { cashOrderInfoApi } from '/@/api/business/order-info/cash-order-info-api';
import {smartSentry} from '/@/lib/smart-sentry';
import dayjs from 'dayjs';
import Decimal from 'decimal.js';
import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
import CashChnlSelect from '/@/components/business/order/cash-chnl-select/index.vue';
import Upload from '/@/components/support/file-upload/index.vue';

// ------------------------ 事件 ------------------------

const emits = defineEmits(['reloadList']);

// ------------------------ 显示与隐藏 ------------------------
// 是否显示
const visibleFlag = ref(false);

function show(rowData) {
  Object.assign(form, formDefault);
  if (rowData && !_.isEmpty(rowData)) {
    Object.assign(form, rowData);
  }
  visibleFlag.value = true;

  //调用获取成功出金、在途明细等
  queryData();

}

// 加载loading
const loading = ref(false);

const columns = ref([
    // {
    //   title: '序号',
    //   dataIndex: 'serialNumber',
    //   ellipsis: true,
    //   width: 50,
    // },
    {
      title: '批次',
      dataIndex: 'batchNo',
      ellipsis: true,
      width: 60,
    },
    {
      title: '子订单号',
      dataIndex: 'subOrderNo',
      ellipsis: true,
      width: 100,
    },
    {
      title: '出金金额',
      dataIndex: 'amount',
      ellipsis: true,
      width: 80,
    },
    // {
    //   title: '出金金额USDT',
    //   dataIndex: 'amountUsdt',
    //   ellipsis: true,
    //   width: 90,
    // },
    // {
    //   title: '币种',
    //   dataIndex: 'currency',
    //   ellipsis: true,
    //   width: 60,
    // },
    // {
    //   title: '国家',
    //   dataIndex: 'country',
    //   ellipsis: true,
    //   width: 60,
    // },
    {
      title: '渠道类型',
      dataIndex: 'chnlType',
      ellipsis: true,
      width: 80,
    },
    {
      title: '渠道商户Id',
      dataIndex: 'channelId',
      ellipsis: true,
      width: 130,
    },
    {
      title: '申请时间',
      dataIndex: 'applyTime',
      ellipsis: false,
      width: 130,
    },
    {
      title: '交易完成时间',
      dataIndex: 'finishedTime',
      ellipsis: false,
      width: 130,
    },
    {
      title: '出金状态',
      dataIndex: 'status',
      ellipsis: true,
      // fixed: 'right',
      width: 100,
    },
    {
      title: '第三方订单编号',
      dataIndex: 'thirdpartyOrderNo',
      ellipsis: true,
      width: 130,
    },
    // {
    //   title: '需人工处理的原因',
    //   dataIndex: 'manualReason',
    //   ellipsis: true,
    //   // fixed: 'right',
    //   width: 100,
    // },
    // {
    //   title: '操作',
    //   dataIndex: 'action',
    //   fixed: 'right',
    //   width: 80,
    // },
  ]);

let subOrderSummary = {}
let tableData = ref([])

// 查询数据
async function queryData() {
  let params = {
    orderNo: form.orderNo
  }
  loading.value = true;
  try {
    let queryResult = await cashOrderInfoApi.querySubOrderSummary(params);
    if (queryResult.code === 0) {
      subOrderSummary = queryResult.data;
      let orders = []
      // if (subOrderSummary.processing && subOrderSummary.processing.size > 0) {
      orders.push(...subOrderSummary.processing);
      // }
      orders.push(...subOrderSummary.successed);
      orders.push(...subOrderSummary.failed);

      tableData.value = orders;
    }
    
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    loading.value = false;
  }
}

const tableFormRef = ref(); // form标签

// 校验规则
const rules = {
  blur: [{ required: true, message: '请输入', trigger: 'blur' }],
  change: [{ required: true, message: '请选择', trigger: 'change' }],
  cloud: [{ required: true, message: '请选择所属平台', trigger: 'change' }],
  relatedPool: [{ required: true, message: '请选择硬件资源池', trigger: 'change' }]
};

let manualTableData = ref([])
const manualColumns = ref([
    {
      title: '序号',
      dataIndex: 'seqNo',
      ellipsis: true,
      width: 50,
    },

    {
      title: '渠道类型',
      dataIndex: 'chnlType',
      ellipsis: true,
      width: 80,
    },
    {
      title: '渠道商户',
      dataIndex: 'channelId',
      ellipsis: true,
      width: 130,
    },
    {
      title: '出金金额(元)',
      dataIndex: 'amount',
      ellipsis: true,
      width: 80,
    },
    {
      title: '外部订单',
      dataIndex: 'thirdpartyOrderNo',
      ellipsis: true,
      width: 80,
    },
    {
      title: '出金单据',
      dataIndex: 'billFileId',
      ellipsis: false,
      width: 130,
    },
    
  ]);

function addRow() {
  let amountL = new Decimal(subOrderSummary.amountLeft)
  for (let i = 0; i < manualTableData.value.length; i++) {
    amountL = amountL.sub(new Decimal(manualTableData[i].amount))
  }

  if (amountL.comparedTo(Decimal(0)) <= 0) {
    message.error('无剩余出金金额！');
    return;
  }
  
  let row = { };

  row['seqNo'] = manualTableData.value.length + 1;
  row['chnlType'] = 1; //默认 1-自有对接渠道
  // row['channelId'] = -1;
  row['channelName'] = '';
  row['amount'] = amountL.toFixed(2, Decimal.ROUND_HALF_UP); //四舍五入
  row['thirdpartyOrderNo'] = '';
  row['finishedTime'] = '';
  // row['billFileId'] = -1;
  row['fileList'] = [];
  
  manualTableData.value.push(row);
  console.log(manualTableData)
}

function onClose() {
  Object.assign(form, formDefault);
  visibleFlag.value = false;
}

// ------------------------ 表单 ------------------------
const formDefault = {
  id: undefined,
  orderNo: undefined, //订单编号
  depositType: undefined, //银行类型，bank/qrcode
  paymentChannel: undefined, //二维码渠道，deposit_type=qrcode时有值
  amount: undefined, //存款金额
  depositHolder: undefined, //存款人
  bankAccount: undefined, //存款账号
  depositRemark: undefined, //存款备注
  depositFileId: undefined, //回单地址,存款成功后银行回单地址,图片url
  callback: undefined, //订单确认回调地址
  landingPage: undefined,//落地页地址
  skuNo: undefined, //商品编号
  skuName: undefined, //商品名称
  price: undefined, //价格
  charge: undefined, //手续费
  award: undefined, //奖励
  currency: undefined,
  country: undefined,
  collectionBank: undefined, //收款银行
  collectionCardNo: undefined, //收款银行卡号
  collectionHolder: undefined, //收款银行卡持有人
  applyTime: undefined, //申请时间
  status: undefined, //订单状态 1 待确认 2 已确认 3 挂起
  payStatus: undefined,
  createTime: undefined, //创建时间
  updateTime: undefined, //更新时间
};

let form = reactive({...formDefault});

// 点击确定，验证表单
async function onSubmit() {

  tableFormRef.value.validate().then(() => {
    Modal.confirm({
      title: '提示',
      content: '确认该订单，确认后即向渠道方发送出金请求',
      okText: '确认',
      okType: 'primary',
      onOk() {
        manual()
      },
      cancelText: '取消',
      onCancel() {
      },
    });
  })
}

async function manual() {
  // for (let i = 0; i < manualTableData.value.length; i++) {
  //   amountL = amountL.sub(new Decimal(manualTableData[i].amount))
  // }

  let params = {
    orderNo: form.orderNo,
    manualRemark: 'TODO',
    subOrderList: manualTableData.value
  };

  SmartLoading.show();
  try {
    await cashOrderInfoApi.manual(params);
    emits('reloadList');
    onClose();
  } catch (err) {
    smartSentry.captureError(err);
  } finally {
    SmartLoading.hide();
  }
}

defineExpose({
  show,
});
</script>

<style scoped lang="less">
.order-info-row {
  font-size: 14px;

  &.title {
    display: inline-flex;
    color: #000000;
  }

  &.value {
    display: inline-flex;
    color: #4a4a4a;
  }

  &.import {
    font-size: 16px;
    font-weight: bolder;
  }

  &.warning {
    color: red;
  }
}
</style>
