<template>
  <Mobile v-if="mobile" ref="contentRef" />
  <Desktop v-else ref="contentRef" />
</template>
<script setup>
  import Desktop from './header-setting.desktop.vue';
  import Mobile from './header-setting.mobile.vue';
  import isMobile from 'is-mobile';
  import { ref } from 'vue';

  const mobile = isMobile();
  const contentRef = ref();

  defineExpose({
    show,
    close,
  });

  function close() {
    contentRef.value.close();
  }

  function show() {
    contentRef.value.show();
  }
</script>
