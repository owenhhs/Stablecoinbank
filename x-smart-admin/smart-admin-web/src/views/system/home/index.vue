<template>
  <IndexMobile v-if="mobile" />
  <IndexDesktop v-else />
</template>
<script setup>
  import isMobile from 'is-mobile';
  import IndexDesktop from './index.desktop.vue';
  import IndexMobile from './index.mobile.vue';

  const mobile = isMobile();
</script>
<style lang="less" scoped>
  @import './index.less';
</style>
