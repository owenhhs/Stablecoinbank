<template>
  <indexMobile v-if="mobile" />
  <indexDesktop v-else />
</template>
<script setup>
  import isMobile from 'is-mobile';
  import indexDesktop from './index.desktop.vue';
  import indexMobile from './index.mobile.vue';

  const mobile = isMobile();
</script>
