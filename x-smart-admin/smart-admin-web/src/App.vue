<!--
  * 主应用页面
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-09-12 23:46:47
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->

<template>
  <van-config-provider v-if="mobile" :locale="antdLocale">
    <!---全局loading--->
    <div class="mobile">
      <div v-if="spinning" class="mobile-loading">
        <van-loading />
      </div>
      <div class="mobile-router">
        <RouterView />
      </div>
    </div>
  </van-config-provider>
  <a-config-provider
    v-else
    :locale="antdLocale"
    :theme="{
      algorithm: compactFlag ? theme.compactAlgorithm : theme.defaultAlgorithm,
      token: {
        colorPrimary: themeColors[colorIndex].primaryColor,
        colorLink: themeColors[colorIndex].primaryColor,
        colorLinkActive: themeColors[colorIndex].activeColor,
        colorLinkHover: themeColors[colorIndex].hoverColor,
        colorIcon: themeColors[colorIndex].primaryColor,
        borderRadius: borderRadius,
      },
      components: {
        Button: {
          colorLink: themeColors[colorIndex].primaryColor,
          colorLinkActive: themeColors[colorIndex].activeColor,
          colorLinkHover: themeColors[colorIndex].hoverColor,
        },
        Icon: {
          colorIcon: themeColors[colorIndex].primaryColor,
        },
      },
    }"
  >
    <!---全局loading--->
    <a-spin :spinning="spinning" tip="稍等片刻，我在拼命加载中..." size="large">
      <!--- 路由 -->
      <RouterView />
    </a-spin>
  </a-config-provider>
</template>

<script setup>
  import dayjs from 'dayjs';
  import { computed } from 'vue';
  import { messages } from '/@/i18n';
  import { useAppConfigStore } from '/@/store/modules/system/app-config';
  import { useSpinStore } from '/@/store/modules/system/spin';
  import { theme } from 'ant-design-vue';
  import { themeColors } from '/@/theme/color.js';
  import isMobile from 'is-mobile';
  import { Locale } from 'vant';

  const mobile = isMobile();
  const antdLocale = computed(() => messages[useAppConfigStore().language].antdLocale);
  const dayjsLocale = computed(() => messages[useAppConfigStore().language].dayjsLocale);
  const vantLocale = computed(() => messages[useAppConfigStore().language].vantLocale);
  dayjs.locale(dayjsLocale);
  Locale.use(useAppConfigStore().language, vantLocale)

  // 全局loading
  let spinStore = useSpinStore();
  const spinning = computed(() => spinStore.loading);
  // 是否紧凑
  const compactFlag = computed(() => useAppConfigStore().compactFlag);
  // 主题颜色
  const colorIndex = computed(() => {
    return useAppConfigStore().colorIndex;
  });
  // 圆角
  const borderRadius = computed(() => {
    return useAppConfigStore().borderRadius;
  });
</script>

<style lang="less" scoped>
.mobile {
  width: 100vw;
  height: 100vh;
  position: relative;
}

.mobile-loading {
  position: absolute;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  background-color: rgba(255, 255, 255, 0.7);
}

.mobile-router {
  width: 100vw;
  height: 100vh;
  overflow: auto;
}
</style>
