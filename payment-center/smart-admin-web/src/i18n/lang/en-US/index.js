/*
 * 英文国际化
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-06 20:00:57
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import antd from 'ant-design-vue/es/locale/en_US';
import dayjs from 'dayjs/locale/eu';

export default {
  antdLocale: antd,
  dayjsLocale: dayjs,
  'setting.title': 'Setting',
  'setting.color': 'Theme Color',
  'setting.menu.layout': 'Menu Layout',
  'setting.menu.width': 'Menu Width',
  'setting.menu.theme': 'Menu Theme',
  'setting.page.width': 'Page Width',
  'setting.border.radius': 'Border Radius',
  'setting.compact': 'Page Compact',
  'setting.bread': 'Show Bread',
  'setting.pagetag': 'Show PageTag',
  'setting.footer': 'Show Footer',
  'setting.helpdoc': 'Show Helpdoc',
  'setting.watermark': 'Show Watermark',
  
  // 业务相关翻译
  'common.login': 'Login',
  'common.logout': 'Logout',
  'common.submit': 'Submit',
  'common.cancel': 'Cancel',
  'common.confirm': 'Confirm',
  'common.delete': 'Delete',
  'common.edit': 'Edit',
  'common.add': 'Add',
  'common.search': 'Search',
  'common.reset': 'Reset',
  'common.export': 'Export',
  'common.import': 'Import',
  'common.save': 'Save',
  'common.back': 'Back',
  'common.next': 'Next',
  'common.previous': 'Previous',
  'common.refresh': 'Refresh',
  'common.operate': 'Operation',
  'common.status': 'Status',
  'common.createTime': 'Create Time',
  'common.updateTime': 'Update Time',
  'common.remark': 'Remark',
  
  // 支付相关
  'payment.order.title': 'Payment Order Management',
  'payment.order.no': 'Order Number',
  'payment.order.amount': 'Amount',
  'payment.order.status': 'Payment Status',
  'payment.order.method': 'Payment Method',
  'payment.order.time': 'Payment Time',
  'payment.channel.title': 'Payment Channel Management',
  'payment.channel.name': 'Channel Name',
  'payment.channel.code': 'Channel Code',
  'payment.channel.type': 'Channel Type',
  'payment.merchant.title': 'Merchant Management',
  'payment.merchant.name': 'Merchant Name',
  'payment.merchant.code': 'Merchant Code',
  'payment.settlement.title': 'Settlement Management',
  'payment.settlement.no': 'Settlement Number',
  'payment.settlement.amount': 'Settlement Amount',
  
  // 状态
  'status.enable': 'Enable',
  'status.disable': 'Disable',
  'status.pending': 'Pending',
  'status.success': 'Success',
  'status.failed': 'Failed',
  'status.processing': 'Processing',
  'status.completed': 'Completed',
  'status.cancelled': 'Cancelled',
  'status.expired': 'Expired',
  
  // 登录相关
  'login.title': 'Account Login',
  'login.welcome.title': 'Welcome to SmartAdmin V3',
  'login.username.placeholder': 'Please enter username',
  'login.password.placeholder': 'Please enter password: at least 3 character types, minimum 8 digits',
  'login.captcha.placeholder': 'Please enter verification code',
  'login.remember': 'Remember Password',
  'login.demo.account': 'Account: admin, Password: 123456',
  'login.submit': 'Login',
  'login.other.ways': 'Other Login Methods',
  'login.validation.username': 'Username cannot be empty',
  'login.validation.password': 'Password cannot be empty',
  'login.validation.captcha': 'Verification code cannot be empty',
  
  // 首页相关
  'home.department': 'Department',
  'common.home': 'Home',
  
  // 设置相关
  'setting.language': 'Language',
  'setting.compact.default': 'Default',
  'setting.compact.compact': 'Compact',
  'setting.unit.pixel': 'pixel (px)',
  'setting.unit.pixel_or_percent': 'pixel (px) or percentage',
  'setting.show': 'Show',
  'setting.hide': 'Hide',
  'setting.copy.config': 'Copy Config',
  'setting.reset.default': 'Reset to Default',
};
