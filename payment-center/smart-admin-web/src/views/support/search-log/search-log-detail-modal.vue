<!--
  * 日志信息 详情
  * 
  * @Author:    1024创新实验室-主任：卓大 
  * @Date:      2022-06-02 20:23:08 
  * @Wechat:    zhuda1024 
  * @Email:     lab1024@163.com 
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012 
-->
<template>
  <a-modal :open="visible" title="日志详情" width="60%" :footer="null" @cancel="close">
    <div class="info-box">
      <a-row class="smart-margin-top10">
        <a-col :span="24">
          <a-row class="detail-info">
            <a-col :span="12"> 日志时间： {{ detail.ts }}</a-col>
            <a-col :span="12"> 日志级别： {{ detail.level }}</a-col>
          </a-row>
          <a-row class="detail-info">
            <a-col :span="12"> 线程名：{{ detail.thread }}</a-col>
            <a-col :span="12"> 请求流水号： {{ detail.requestId }}</a-col>
          </a-row>
          <a-row class="detail-info">
            <a-col :span="24"> 日志位置： {{ detail.className }}</a-col>
          </a-row>
        </a-col>
      </a-row>
    </div>
    <div class="info-box">
      <h4>日志内容：</h4>
      {{ detail.msg }}
    </div>
    <div class="info-box" v-if="detail.stack">
      <h4>堆栈信息：</h4>
      <div>
        {{ detail.stack }}
      </div>
    </div>
  </a-modal>
</template>

<script setup>
  import { reactive, ref } from 'vue';
  import { JsonViewer } from 'vue3-json-viewer';
  import { operateLogApi } from '/@/api/support/operate-log-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { SmartLoading } from '/@/components/framework/smart-loading';

  defineExpose({
    show,
  });

  const visible = ref(false);

  let detail = reactive({});

  function show(record) {
    visible.value = true;
    clear(detail);
    Object.assign(detail, record);
  }

  const clear = (info) => {
    const keys = Object.keys(info);
    let obj = {};
    keys.forEach((item) => {
      obj[item] = '';
    });
    Object.assign(info, obj);
  };

  function close() {
    visible.value = false;
  }

  function decodeHTMLEntities(text) {
    const textarea = document.createElement('textarea');
    textarea.innerHTML = text;
    return textarea.value;
  }

</script>

<style scoped lang="less">
  .detail-title {
    display: flex;
    align-items: center;
    font-size: 20px;
    font-weight: bold;
  }
  .info-box {
    border-bottom: 1px solid #f0f0f0;
    padding: 10px 8px;
  }
  .detail-info {
    .ant-col {
      line-height: 1.46;
      margin-bottom: 12px;
      padding-right: 5px;
    }
  }
  .detail-right-title {
    text-align: right;
    color: grey;
  }

  :deep(.ant-modal-body) {
    padding: 10px !important;
  }

  .detail-right {
    padding-left: 5px;
    font-size: 20px;
    font-weight: bold;
    text-align: right;
    float: right;
  }
</style>
