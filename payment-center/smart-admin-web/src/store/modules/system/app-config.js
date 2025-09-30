/*
 * 项目的配置信息
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-06 20:53:47
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import { defineStore } from 'pinia';
import { appDefaultConfig } from '/@/config/app-config';
import localStorageKeyConst from '/@/constants/local-storage-key-const';
import { smartSentry } from '/@/lib/smart-sentry';
import { localRead } from '/@/utils/local-util';

// 始终使用默认配置
let state = { ...appDefaultConfig };
let language = appDefaultConfig.language; // 默认为英文

// 尝试读取本地存储
let appConfigStr = localRead(localStorageKeyConst.APP_CONFIG);
if (appConfigStr) {
  try {
    let storedConfig = JSON.parse(appConfigStr);
    // 如果本地存储中没有语言设置，或者语言设置无效，则使用默认英文
    if (!storedConfig.language || !['en_US', 'zh_CN', 'fr_FR', 'es_ES', 'pt_PT'].includes(storedConfig.language)) {
      storedConfig.language = appDefaultConfig.language;
    }
    state = storedConfig;
    language = storedConfig.language;
  } catch (e) {
    smartSentry.captureError(e);
  }
}

/**
 * 获取初始化的语言
 */
export const getInitializedLanguage = function () {
  return language; // 返回实际的语言设置
};

export const useAppConfigStore = defineStore({
  id: 'appConfig',
  state: () => ({
    // 读取config下的默认配置
    ...state,
  }),
  actions: {
    reset() {
      for (const k in appDefaultConfig) {
        this[k] = appDefaultConfig[k];
      }
    },
    showHelpDoc() {
      this.helpDocFlag = true;
    },
    hideHelpDoc() {
      this.helpDocFlag = false;
    },
  },
});
