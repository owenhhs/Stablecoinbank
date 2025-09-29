<!--
  * 渠道基本信息表
  *
  * @Author:    Sunny
  * @Date:      2024-09-03 15:16:42
  * @Copyright  Sunny
-->
<template>
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
        <a-row class="smart-query-form-row">
            <a-col :span="18">
                <a-row class="smart-query-form-row">
                    <a-form-item label="渠道名称" class="smart-query-form-item">
                        <a-input style="width: 150px" v-model:value="queryForm.merName" placeholder="渠道名称" />
                    </a-form-item>
                    <a-form-item label="渠道编码" class="smart-query-form-item">
                        <a-input style="width: 150px" v-model:value="queryForm.merCode" placeholder="渠道编码" />
                    </a-form-item>

                    <a-form-item label="渠道状态" class="smart-query-form-item">
                        <a-select v-model:value="queryForm.status" style="width: 120px" placeholder="请选择" allow-clear>
                            <a-select-option class="pay-status" v-for="item in channelInfoConst.CHNL_STATUS_LIST" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
                        </a-select>
                    </a-form-item>

                    <a-form-item label="接口类型" class="smart-query-form-item">
                        <a-select v-model:value="queryForm.interfaceType" style="width: 150px" placeholder="请选择" allow-clear>
                            <a-select-option class="cash-status" v-for="item in channelInfoConst.INTERFACE_TYPE_LIST" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
                        </a-select>
                    </a-form-item>
                </a-row>
                <a-row class="smart-query-form-row">
                    <a-form-item label="入金开通标志" class="smart-query-form-item">
                        <a-select v-model:value="queryForm.paymentFlag" style="width: 120px" placeholder="请选择" allow-clear>
                            <a-select-option class="pay-status" v-for="item in channelInfoConst.FUNCTION_SWITCH" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
                        </a-select>
                    </a-form-item>
                    <a-form-item label="出金开通标志" class="smart-query-form-item">
                        <a-select v-model:value="queryForm.cashFlag" style="width: 120px" placeholder="请选择" allow-clear>
                            <a-select-option class="cash-status" v-for="item in channelInfoConst.FUNCTION_SWITCH" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
                        </a-select>
                    </a-form-item>

                </a-row>
            </a-col>
            <a-col :span="6">
                <a-form-item class="smart-query-form-item">
                    <a-button type="primary" @click="queryData">
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
            </a-col>
        </a-row>
    </a-form>
    <!---------- 查询表单form end ----------->

    <a-card size="small" :bordered="false" :hoverable="true">
        <!---------- 表格操作行 begin ----------->
        <a-row class="smart-table-btn-block">
            <div class="smart-table-operate-block">
                <a-button @click="showForm" type="primary" size="small">
                    <template #icon>
                        <PlusOutlined />
                    </template>
                    新建
                </a-button>
                <!-- <a-button @click="confirmBatchDelete" type="danger" size="small" :disabled="selectedRowKeyList.length == 0">
                    <template #icon>
                        <DeleteOutlined />
                    </template>
                    批量删除
                </a-button> -->
            </div>
            <div class="smart-table-setting-block">
                <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.CHANNEL_INFO" :refresh="queryData" />
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
                :scroll="{ x: 1300, y: 1000 }"
                :loading="tableLoading"
                :pagination="false"
                :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
        >
            <template #bodyCell="{ text, record, column }">

                <template v-if="column.dataIndex === 'status'">
                    <a-tag color="success" v-if="text === 1">启用</a-tag>
                    <a-tag color="default" v-if="text === 0">关闭</a-tag>
                </template>
                <template v-if="column.dataIndex === 'xjFlag'">
                    <a-tag color="success" v-if="text === 1">已开通</a-tag>
                    <a-tag color="default" v-if="text === 0">未开通</a-tag>
                </template>
                <template v-if="column.dataIndex === 'paymentFlag'">
                    <a-tag color="success" v-if="text === 1">已开通</a-tag>
                    <a-tag color="default" v-if="text === 0">未开通</a-tag>
                    <!-- <a-switch :checked="text === 1" :disabled="true"/> -->
                </template>
                <template v-if="column.dataIndex === 'cashFlag'">
                    <a-tag color="success" v-if="text === 1">已开通</a-tag>
                    <a-tag color="default" v-if="text === 0">未开通</a-tag>
                </template>

                <template v-if="column.dataIndex === 'interfaceType'">
                    <a-tag color="blue" v-if="text === 1">API</a-tag>
                    <a-tag color="default" v-if="text === 2">非API</a-tag>
                </template>

                <template v-if="column.dataIndex === 'action'">
                    <div class="smart-table-operate">
                        <a-button @click="showForm(record)" type="link" v-privilege="'channel:info:edit'">编辑</a-button>
                        <!-- <a-button @click="onDelete(record)" danger type="link">删除</a-button> -->
                        <a-button @click="viewSingleLimit(record)" type="link">单笔限额</a-button>
                        <!-- <a-button @click="viewSingleLimit(record)" type="link">单笔限额管理</a-button> -->
                        <a-button @click="viewFunction(record)" type="link">开通功能</a-button>

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

        <PaymentChannelInfoForm  ref="formRef" @reloadList="queryData"/>
        <PaymentChannelBusinessCfgList ref="paymentChannelBusinessCfgListRef" />
        <PaymentMethodCfgList ref="paymentChannelMethodListRef" />

    </a-card>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import { message, Modal } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { paymentChannelInfoApi } from '/@/api/business/payment-channel-info/payment-channel-info-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import PaymentChannelInfoForm from './payment-channel-info-form.vue';
    import PaymentChannelBusinessCfgList from './payment-channel-business-cfg-list.vue';
    import PaymentMethodCfgList from './payment-method-cfg-list.vue';

    import channelInfoConst from '/@/constants/business/erp/channel-info-const';
    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        {
            title: '主键',
            dataIndex: 'id',
            ellipsis: true,
            width: 100,
        },
        {
            title: '渠道名称',
            dataIndex: 'merName',
            ellipsis: true,
            width: 100,
        },
        {
            title: '渠道编码',
            dataIndex: 'merCode',
            ellipsis: true,
            width: 100,
        },
        {
            title: '状态',
            dataIndex: 'status',
            ellipsis: true,
            width: 80,
        },
        {
            title: '工作时间',
            dataIndex: 'workTime',
            ellipsis: true,
            width: 100,
        },
        {
            title: '接口类型',
            dataIndex: 'interfaceType',
            ellipsis: true,
            width: 100,
        },
        {
            title: '新疆交易标志',
            dataIndex: 'xjFlag',
            ellipsis: true,
            width: 120,
        },
        // {
        //     title: '渠道号',
        //     dataIndex: 'merAk',
        //     ellipsis: true,
        // },
        // {
        //     title: '渠道秘钥',
        //     dataIndex: 'merSk',
        //     ellipsis: true,
        // },
        // {
        //     title: '域名',
        //     dataIndex: 'domain',
        //     ellipsis: true,
        // },
        // {
        //     title: '渠道额外配置信息',
        //     dataIndex: 'ext',
        //     ellipsis: true,
        // },
        // {
        //     title: '渠道持有人姓名',
        //     dataIndex: 'merUsername',
        //     ellipsis: true,
        // },
        // {
        //     title: '渠道绑定手机号',
        //     dataIndex: 'phone',
        //     ellipsis: true,
        // },
        // {
        //     title: '渠道证件号',
        //     dataIndex: 'idNumber',
        //     ellipsis: true,
        // },
        {
            title: '入金开通标志',
            dataIndex: 'paymentFlag',
            ellipsis: true,
            width: 120,
        },
        {
            title: '入金路由比例',
            dataIndex: 'paymentScale',
            ellipsis: true,
            width: 120,
        },
        {
            title: '入金单日限额',
            dataIndex: 'paymentLimit',
            ellipsis: true,
            width: 120,
        },
        {
            title: '入金单日笔数',
            dataIndex: 'paymentCount',
            ellipsis: true,
            width: 120,
        },
        {
            title: '出金开通标志',
            dataIndex: 'cashFlag',
            ellipsis: true,
            width: 120,
        },
        {
            title: '出金路由比例',
            dataIndex: 'cashScale',
            ellipsis: true,
            width: 120,
        },
        {
            title: '出金单日限额',
            dataIndex: 'cashLimit',
            ellipsis: true,
            width: 120,
        },
        {
            title: '出金单日笔数',
            dataIndex: 'cashCount',
            ellipsis: true,
            width: 120,
        },
        // {
        //     title: '创建时间',
        //     dataIndex: 'createTime',
        //     ellipsis: true,
        // },
        {
            title: '最后修改时间',
            dataIndex: 'updateTime',
            ellipsis: true,
            width: 130,
        },
        {
            title: '操作',
            dataIndex: 'action',
            fixed: 'right',
            width: 120,
        },
    ]);

    // ---------------------------- 查询数据表单和方法 ----------------------------

    const queryFormState = {
        merName: undefined, //渠道名称
        merCode: undefined, //渠道编码
        paymentFlag: undefined, //入金开通标志
        cashFlag: undefined, //出金开通标志
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

    // 查询数据
    async function queryData() {
        tableLoading.value = true;
        try {
            let queryResult = await paymentChannelInfoApi.queryPage(queryForm);
            tableData.value = queryResult.data.list;
            total.value = queryResult.data.total;
        } catch (e) {
            smartSentry.captureError(e);
        } finally {
            tableLoading.value = false;
        }
    }


    onMounted(queryData);

    // ---------------------------- 添加/修改 ----------------------------
    const formRef = ref();

    function showForm(data) {
        formRef.value.show(data);
    }

    // ---------------------------- 管理单笔限额 ----------------------------
    const paymentChannelBusinessCfgListRef = ref();
    function viewSingleLimit(data) {
        paymentChannelBusinessCfgListRef.value.show(data);
    }

    // ---------------------------- 管理交易功能 ----------------------------
    const paymentChannelMethodListRef = ref();
    function viewFunction(data) {
        paymentChannelMethodListRef.value.show(data);
    }
</script>
