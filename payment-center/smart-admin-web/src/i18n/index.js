/*
 * 国际化入口文件
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-06 20:01:19
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */

import en_US from './lang/en-US/index';
import zh_CN from './lang/zh-CN/index';
import fr_FR from './lang/fr-FR/index';
import es_ES from './lang/es-ES/index';
import pt_PT from './lang/pt-PT/index';
import { createI18n } from 'vue-i18n';
import { getInitializedLanguage } from '/@/store/modules/system/app-config';

// 语言选择数组
export const i18nList = [
  {
    text: 'English',
    value: 'en_US',
    flag: '🇺🇸',
  },
  {
    text: '简体中文',
    value: 'zh_CN',
    flag: '🇨🇳',
  },
  {
    text: 'Français',
    value: 'fr_FR',
    flag: '🇫🇷',
  },
  {
    text: 'Español',
    value: 'es_ES',
    flag: '🇪🇸',
  },
  {
    text: 'Português',
    value: 'pt_PT',
    flag: '🇵🇹',
  },
];

export const messages = {
  en_US: en_US,
  zh_CN: zh_CN,
  fr_FR: fr_FR,
  es_ES: es_ES,
  pt_PT: pt_PT,
};

const i18n = createI18n({
  fallbackLocale: 'en_US', //预设语言环境
  globalInjection: true,
  legacy: false, //
  locale: getInitializedLanguage(), //默认初始化的语言
  messages, //
});

export default i18n;
