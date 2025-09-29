<template>
  <div class="container">
    <van-nav-bar :title="route?.meta?.title">
      <template #left>
        <van-icon name="list-switching" size="24" @click="openMenuPopup" />
      </template>
      <template #right>
        <van-icon name="setting-o" size="24" @click="openSettingPopup" />
      </template>
    </van-nav-bar>
    <div class="content">
      <router-view v-slot="{ Component }">
        <keep-alive :include="keepAliveIncludes">
          <component :is="Component" :key="route.name" />
        </keep-alive>
      </router-view>
    </div>
  </div>

  <van-popup v-model:show="menuPopupShow" position="left" :style="{ width: '50%', height: '100%' }">
    <SideMenu />
  </van-popup>

  <HeaderSetting ref="headerSettingRef"/>
</template>
<script setup>
  import { ref } from 'vue';
  import setup from './setup';
  import { smartKeepAlive } from './components/smart-keep-alive';
  import SideMenu from './components/side-menu/index.vue';
  import HeaderSetting from './components/header-user-space/header-setting/header-setting.vue'
  import { LAYOUT_ENUM } from '/@/constants/layout-const';
  import SideExpandLayout from './side-expand-layout.vue';
  import SideLayout from './side-layout.vue';
  import TopLayout from './top-layout.vue';
  import { useAppConfigStore } from '/@/store/modules/system/app-config';

  const { layout } = setup();

  let { route, keepAliveIncludes, iframeNotKeepAlivePageFlag, keepAliveIframePages } = smartKeepAlive();

  /** 菜单 */
  const menuPopupShow = ref(false);

  const openMenuPopup = () => {
    menuPopupShow.value = true;
  };

  /** 设置 */
  const headerSettingRef = ref(null)

  const openSettingPopup = () => {
    headerSettingRef.value.show()
  };
</script>

<style lang="less" scoped>
  .container {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
  }

  .content {
    width: 100%;
    flex: 1;
    overflow: auto;
  }
</style>
