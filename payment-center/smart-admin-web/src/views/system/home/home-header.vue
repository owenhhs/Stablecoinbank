<!--
  * 首页 用户头部信息
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-09-12 22:34:00
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
  *
-->
<template>
  <div class="user-header">
    <a-page-header :title="welcomeSentence">
      <template #subTitle>
        <span style="color: #666; margin-left: 20px">{{ $t('home.department') }}：{{ departmentName }} </span>
      </template>
      <template #extra>
        <p style="color: #333">{{ dayInfo }}</p>
      </template>
      <a-row class="content">
        <span class="left-content">
          <p class="last-login-info"><AlertOutlined />{{ lastLoginInfo }}</p>
        </span>
      </a-row>
    </a-page-header>
  </div>
</template>
<script setup>
  import { computed } from 'vue';
  import { useI18n } from 'vue-i18n';
  import { useUserStore } from '/@/store/modules/system/user';
  import uaparser from 'ua-parser-js';
  import { Solar, Lunar } from 'lunar-javascript';

  const { t, locale } = useI18n();

  const userStore = useUserStore();

  const departmentName = computed(() => userStore.departmentName);

  // 根据当前语言显示用户名
  const displayName = computed(() => {
    const name = userStore.$state.actualName;
    if (!name) return '';
    
    // 如果是英文用户名，根据当前语言返回对应翻译
    if (name === 'Administrator') {
      return locale.value === 'zh_CN' ? t('common.administrator') : name;
    }
    
    return name;
  });

  // 欢迎语
  const welcomeSentence = computed(() => {
    let sentence = '';
    let now = new Date().getHours();
    if (now > 0 && now <= 6) {
      sentence = t('home.greeting.midnight');
    } else if (now > 6 && now <= 11) {
      sentence = t('home.greeting.morning');
    } else if (now > 11 && now <= 14) {
      sentence = t('home.greeting.afternoon');
    } else if (now > 14 && now <= 18) {
      sentence = t('home.greeting.evening');
    } else {
      sentence = t('home.greeting.night');
    }
    return sentence + displayName.value;
  });

  //上次登录信息
  const lastLoginInfo = computed(() => {
    let info = '';
    if (userStore.$state.lastLoginTime) {
      info = info + t('home.lastLogin') + ': ' + userStore.$state.lastLoginTime;
    }

    if (userStore.$state.lastLoginUserAgent) {
      let ua = uaparser(userStore.$state.lastLoginUserAgent);
      info = info + '; ' + t('home.device') + ':';
      if (ua.browser.name) {
        info = info + ' ' + ua.browser.name;
      }
      if (ua.os.name) {
        info = info + ' ' + ua.os.name;
      }
      let device = ua.device.vendor ? ua.device.vendor + ua.device.model : null;
      if (device) {
        info = info + ' ' + device + ';';
      }
    }

    if (userStore.$state.lastLoginIpRegion) {
      info = info + '; ' + userStore.$state.lastLoginIpRegion;
    }
    if (userStore.$state.lastLoginIp) {
      info = info + '; ' + userStore.$state.lastLoginIp;
    }
    return info;
  });

  //日期、节日、节气
  const dayInfo = computed(() => {
    //阳历
    let solar = Solar.fromDate(new Date());
    let day = solar.toString();
    
    // 根据当前语言获取星期信息
    let week;
    if (locale.value === 'zh_CN') {
      week = solar.getWeekInChinese();
    } else {
      const weekDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      week = weekDays[solar.getWeek()];
    }
    
    //阴历
    let lunar = Lunar.fromDate(new Date());
    let lunarMonth, lunarDay;
    if (locale.value === 'zh_CN') {
      lunarMonth = lunar.getMonthInChinese();
      lunarDay = lunar.getDayInChinese();
    } else {
      lunarMonth = lunar.getMonth();
      lunarDay = lunar.getDay();
    }
    
    //节气
    let jieqi, nextJieqi;
    if (locale.value === 'zh_CN') {
      jieqi = lunar.getPrevJieQi().getName();
      let next = lunar.getNextJieQi();
      nextJieqi = next.getName() + ' ' + next.getSolar().toYmd();
    } else {
      // 对于英文，简化节气显示
      jieqi = 'Solar Term';
      let next = lunar.getNextJieQi();
      nextJieqi = 'Next: ' + next.getSolar().toYmd();
    }

    return t('home.dateInfo', { day, week, lunarMonth, lunarDay, jieqi, nextJieqi });
  });

</script>
<style scoped lang="less">
  .user-header {
    width: 100%;
    background-color: #fff;
    margin-bottom: 10px;

    .left-content {
      width: calc(100% - 420px);
      h3 {
        color: rgba(0, 0, 0, 0.75);
      }
    }

    .content {
      display: flex;
      justify-content: flex-start;
    }

    .last-login-info {
      font-size: 13px;
      color: #333;
      overflow-wrap: break-word;
      padding: 0;
      margin: 1px 0 0 0;
    }

  }
</style>
