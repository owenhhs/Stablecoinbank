<template>
  <OrderInfoListMobile v-if="mobile" />
  <OrderInfoListDesktop v-else />
</template>
<script setup>
  import isMobile from 'is-mobile';
  import OrderInfoListDesktop from './order-info-list.desktop.vue';
  import OrderInfoListMobile from './order-info-list.mobile.vue';

  const mobile = isMobile();
</script>
