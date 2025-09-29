<template>
  <a-dropdown placement="bottomRight" :trigger="['click']">
    <div class="language-selector">
      <GlobalOutlined />
      <span class="language-text">{{ currentLanguage.text }}</span>
      <DownOutlined />
    </div>
    <template #overlay>
      <a-menu @click="handleLanguageChange">
        <a-menu-item v-for="language in i18nList" :key="language.value">
          <div class="language-item">
            <span class="language-flag">{{ language.flag }}</span>
            <span class="language-name">{{ language.text }}</span>
            <CheckOutlined v-if="language.value === currentLanguage.value" class="selected-icon" />
          </div>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script>
import { defineComponent, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useAppConfigStore } from '/@/store/modules/system/app-config';
import { i18nList } from '/@/i18n';
import { GlobalOutlined, DownOutlined, CheckOutlined } from '@ant-design/icons-vue';

export default defineComponent({
  name: 'LanguageSelector',
  components: {
    GlobalOutlined,
    DownOutlined,
    CheckOutlined,
  },
  setup() {
    const { locale } = useI18n();
    const appConfigStore = useAppConfigStore();

    // 当前语言
    const currentLanguage = computed(() => {
      return i18nList.find(item => item.value === locale.value) || i18nList[0];
    });

    // 切换语言
    const handleLanguageChange = ({ key }) => {
      locale.value = key;
      appConfigStore.language = key;
      
      // 保存到本地存储
      localStorage.setItem('app-config', JSON.stringify({
        ...appConfigStore.$state,
        language: key
      }));
      
      // 重新加载页面以应用新语言
      setTimeout(() => {
        window.location.reload();
      }, 100);
    };

    return {
      i18nList,
      currentLanguage,
      handleLanguageChange,
    };
  },
});
</script>

<style scoped>
.language-selector {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.language-selector:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.language-text {
  margin: 0 8px;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.85);
}

.language-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.language-flag {
  font-size: 16px;
  margin-right: 8px;
}

.language-name {
  flex: 1;
  font-size: 14px;
}

.selected-icon {
  color: #1890ff;
  font-size: 12px;
}

/* 暗色主题适配 */
:global(.dark) .language-selector:hover {
  background-color: rgba(255, 255, 255, 0.08);
}

:global(.dark) .language-text {
  color: rgba(255, 255, 255, 0.85);
}

:global(.dark) .language-name {
  color: rgba(255, 255, 255, 0.85);
}
</style>
