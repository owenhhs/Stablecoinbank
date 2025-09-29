<template>
  <loginMobile v-if="mobile" />
  <loginDesktop v-else />
</template>

<script setup>
  import isMobile from 'is-mobile';
  import loginDesktop from './login.desktop.vue';
  import loginMobile from './login.mobile.vue';

  const mobile = isMobile();
</script>
