import { computed } from 'vue';
import { useAppConfigStore } from '/@/store/modules/system/app-config';

export default function setup() {
  const layout = computed(() => useAppConfigStore().$state.layout);

  return {
    layout,
  };
}
