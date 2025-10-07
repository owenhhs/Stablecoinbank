<!--
  * 面包屑
  * 
  * @Author:    1024创新实验室-主任：卓大 
  * @Date:      2022-09-06 20:29:12 
  * @Wechat:    zhuda1024 
  * @Email:     lab1024@163.com 
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012 
-->
<template>
  <a-breadcrumb separator=">" v-if="breadCrumbFlag" class="breadcrumb">
    <a-breadcrumb-item v-for="(item, index) in parentMenuList" :key="index">{{ getDisplayTitle(item.title) }}</a-breadcrumb-item>
    <a-breadcrumb-item>{{ getDisplayTitle(currentRoute.meta.title) }}</a-breadcrumb-item>
  </a-breadcrumb>
</template>
<script setup>
  import { useRoute } from 'vue-router';
  import { useUserStore } from '/@/store/modules/system/user';
  import { computed } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { useAppConfigStore } from '/@/store/modules/system/app-config';
  import { getMenuI18nName } from '/@/constants/menu-i18n';

  const { t, locale } = useI18n();

  // 获取显示标题的函数
  function getDisplayTitle(title) {
    if (!title) return '';
    
    // 如果是国际化键（包含点号），使用$t函数
    if (title.includes('.')) {
      return t(title);
    }
    
    // 否则使用菜单国际化函数
    return getMenuI18nName(title, locale.value);
  }

  // 是否显示面包屑
  const breadCrumbFlag = computed(() =>  useAppConfigStore().$state.breadCrumbFlag);

  let currentRoute = useRoute();
  //根据路由监听面包屑
  const parentMenuList = computed(() => {
    let currentName = currentRoute.name;
    if (!currentName || typeof currentName !== 'string') {
      return [];
    }
    let menuParentIdListMap = useUserStore().getMenuParentIdListMap;
    return menuParentIdListMap.get(currentName) || [];
  });
</script>
<style scoped lang="less">
.breadcrumb{
  line-height: @page-tag-height;
}
</style>