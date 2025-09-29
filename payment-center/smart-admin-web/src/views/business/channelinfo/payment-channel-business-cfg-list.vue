<!--
  * 支付渠道业务范围配置表
  *
  * @Author:    Sunny
  * @Date:      2024-09-03 15:16:42
  * @Copyright  Sunny
-->
<template>
    <a-drawer title="渠道单笔限额配置" width="90%" :open="visibleFlag" :maskClosable="false" :destroyOnClose="true" @close="onClose">

        <!---------- 查询表单form begin ----------->
        <a-form class="smart-query-form">
            <a-row class="smart-query-form-row">
                <a-form-item label="渠道ID" class="smart-query-form-item">
                    <a-input style="width: 150px" v-model:value="queryForm.channelId" placeholder="渠道商户id" disabled />
                </a-form-item>
                <a-form-item label="交易类型" class="smart-query-form-item">
                    <a-select v-model:value="queryForm.paymentType" style="width: 100px" placeholder="请选择" allow-clear>
                        <a-select-option class="pay-status" v-for="item in channelInfoConst.FUNCTION_LIST" :key="item.value" :value="item.value">{{ item.desc }}</a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="币种" class="smart-query-form-item">
                    <a-input style="width: 150px" v-model:value="queryForm.currency" placeholder="币种" />
                </a-form-item>
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
            </a-row>
        </a-form>
        <!---------- 查询表单form end ----------->

        <a-card size="small" :bordered="false" :hoverable="true">
            <!---------- 表格操作行 begin ----------->
            <a-row class="smart-table-btn-block">
                <div class="smart-table-operate-block">
                    <a-button @click="showForm({channelId: queryForm.channelId,})" type="primary" size="small" v-privilege="'channel:info:edit'">
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
                    <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.ERP.PAYMENT_CHANNEL_BUSINESS_CFG" :refresh="queryData" />
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
                    :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
            >
                <template #bodyCell="{ text, record, column }">

                    <template v-if="column.dataIndex === 'paymentType'">
                        <a-tag color="success" v-if="text === 'payment'">入金功能</a-tag>
                        <a-tag color="default" v-if="text === 'cash'">出金功能</a-tag>
                        <!-- <a-switch :checked="text === 1" :disabled="true"/> -->
                    </template>

                    <template v-if="column.dataIndex === 'action'">
                        <div class="smart-table-operate">
                            <a-button @click="showForm(record)" type="link" v-privilege="'channel:info:edit'">编辑</a-button>
                            <a-button @click="onDelete(record)" danger type="link" v-privilege="'channel:info:edit'">删除</a-button>
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

            <PaymentChannelBusinessCfgForm  ref="formRef" @reloadList="queryData"/>

        </a-card>

    </a-drawer>
</template>
<script setup>
    import { reactive, ref, onMounted } from 'vue';
    import _ from 'lodash';
    import { message, Modal } from 'ant-design-vue';
    import { SmartLoading } from '/@/components/framework/smart-loading';
    import { paymentChannelBusinessCfgApi } from '/@/api/business/payment-channel-info/payment-channel-business-cfg-api';
    import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
    import { TABLE_ID_CONST } from '/@/constants/support/table-id-const.js';
    import { smartSentry } from '/@/lib/smart-sentry';
    import TableOperator from '/@/components/support/table-operator/index.vue';
    import PaymentChannelBusinessCfgForm from './payment-channel-business-cfg-form.vue';
    import channelInfoConst from '/@/constants/business/erp/channel-info-const';

    // ---------------------------- 表格列 ----------------------------

    const columns = ref([
        // {
        //     title: '主键',
        //     dataIndex: 'id',
        //     ellipsis: true,
        // },
        {
            title: '渠道ID',
            dataIndex: 'channelId',
            ellipsis: true,
            width: 100,
        },
        {
            title: '交易类型',
            dataIndex: 'paymentType',
            ellipsis: true,
            width: 100,
        },
        {
            title: '币种',
            dataIndex: 'currency',
            ellipsis: true,
            width: 80,
        },
        {
            title: '国家',
            dataIndex: 'country',
            ellipsis: true,
            width: 80,
        },
        {
            title: '单笔最小金额',
            dataIndex: 'amountMin',
            ellipsis: true,
            width: 100,
        },
        {
            title: '单笔最大限额',
            dataIndex: 'amountMax',
            ellipsis: true,
            width: 100,
        },
        {
            title: '佣金比例%',
            dataIndex: 'brokerage',
            ellipsis: true,
            width: 100,
        },
        {
            title: '单笔奖励',
            dataIndex: 'award',
            ellipsis: true,
            width: 100,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            ellipsis: true,
            width: 100,
        },
        {
            title: '更新时间',
            dataIndex: 'updateTime',
            ellipsis: true,
            width: 100,
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
        channelId: undefined, //渠道商户id
        paymentType: undefined, //支付方式类型，payment-支付方式；cash-兑付方式；
        currency: undefined, //币种
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
            let queryResult = await paymentChannelBusinessCfgApi.queryPage(queryForm);
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

    // ---------------------------- 单个删除 ----------------------------
    //确认删除
    function onDelete(data){
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
    async function requestDelete(data){
        SmartLoading.show();
        try {
            let deleteForm = {
                goodsIdList: selectedRowKeyList.value,
            };
            await paymentChannelBusinessCfgApi.delete(data.id);
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

    // ------------------------ 显示与隐藏 ------------------------
    // 是否显示
    const visibleFlag = ref(false);
    
    async function show(rowData) {
        // Object.assign(form, formDefault);
        // if (rowData && !_.isEmpty(rowData)) {
        // Object.assign(form, rowData);
        // }
        queryForm.channelId = rowData.id;
        await queryData();
        visibleFlag.value = true;
        // nextTick(() => {
        //   formRef.value.clearValidate();
        // });
    }
    function onClose() {
        queryForm.channelId = undefined; //渠道商户id
        queryForm.paymentType = undefined; //支付方式类型，payment-支付方式；cash-兑付方式；
        queryForm.currency = undefined; //币种
        visibleFlag.value = false;
    }
    defineExpose({
        show,
    });
</script>
