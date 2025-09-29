<template>
  <IndexMobile v-if="mobile" v-bind="{ ...props }" />
  <IndexDesktop v-else v-bind="{ ...props }" />
</template>

<script setup>
  import isMobile from 'is-mobile';
  import IndexDesktop from './index.desktop.vue';
  import IndexMobile from './index.mobile.vue';

  const mobile = isMobile();

  const props = defineProps({
    collapsed: {
      type: Boolean,
      required: false,
      default: false,
    },
  });
</script>

<style lang="less" scoped></style>
