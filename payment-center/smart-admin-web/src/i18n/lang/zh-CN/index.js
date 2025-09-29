/*
 * 中文国际化
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-06 20:01:06
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import antd from 'ant-design-vue/es/locale/zh_CN';
import dayjs from 'dayjs/locale/zh-cn';

export default {
  antdLocale: antd,
  dayjsLocale: dayjs,
  'setting.title': '网站设置',
  'setting.color': '主题颜色',
  'setting.menu.layout': '菜单布局',
  'setting.menu.width': '菜单宽度',
  'setting.menu.theme': '菜单主题',
  'setting.compact': '页面紧凑',
  'setting.border.radius': '页面圆角',
  'setting.page.width': '页面宽度',
  'setting.bread': '面包屑',
  'setting.pagetag': '标签页',
  'setting.footer': '页脚',
  'setting.helpdoc': '帮助文档',
  'setting.watermark': '水印',
  
  // 业务相关翻译
  'common.login': '登录',
  'common.logout': '退出',
  'common.submit': '提交',
  'common.cancel': '取消',
  'common.confirm': '确认',
  'common.delete': '删除',
  'common.edit': '编辑',
  'common.add': '添加',
  'common.search': '搜索',
  'common.reset': '重置',
  'common.export': '导出',
  'common.import': '导入',
  'common.save': '保存',
  'common.back': '返回',
  'common.next': '下一步',
  'common.previous': '上一步',
  'common.refresh': '刷新',
  'common.operate': '操作',
  'common.status': '状态',
  'common.createTime': '创建时间',
  'common.updateTime': '更新时间',
  'common.remark': '备注',
  
  // 支付相关
  'payment.order.title': '支付订单管理',
  'payment.order.no': '订单编号',
  'payment.order.amount': '金额',
  'payment.order.status': '支付状态',
  'payment.order.method': '支付方式',
  'payment.order.time': '支付时间',
  'payment.channel.title': '支付渠道管理',
  'payment.channel.name': '渠道名称',
  'payment.channel.code': '渠道编码',
  'payment.channel.type': '渠道类型',
  'payment.merchant.title': '商户管理',
  'payment.merchant.name': '商户名称',
  'payment.merchant.code': '商户编码',
  'payment.settlement.title': '结算管理',
  'payment.settlement.no': '结算单号',
  'payment.settlement.amount': '结算金额',
  
  // 状态
  'status.enable': '启用',
  'status.disable': '禁用',
  'status.pending': '待处理',
  'status.success': '成功',
  'status.failed': '失败',
  'status.processing': '处理中',
  'status.completed': '已完成',
  'status.cancelled': '已取消',
  'status.expired': '已过期',
  
  // 登录相关
  'login.title': '账号登录',
  'login.welcome.title': '欢迎登录 ZFX 支付中心',
  'login.welcome.description': 'SmartAdmin 是由 河南·洛阳 1024创新实验室（1024Lab）基于SpringBoot + Sa-Token + Mybatis-Plus 和 Vue3 + Vite5 + Ant Design Vue 4 (同时支持JavaScript和TypeScript双版本) 以「高质量代码」为核心，「简洁、高效、安全」的快速开发平台。致伟大的开发者：我们希望用一套漂亮优雅的代码和一套整洁高效的代码规范，让大家在这浮躁的世界里感受到一股把代码写好的清流！保持谦逊，保持学习，热爱代码，更热爱生活！永远年轻，永远前行！',
  'login.welcome.wechat': '加微信，骚扰卓大 :)',
  'login.username.placeholder': '请输入用户名',
  'login.password.placeholder': '请输入密码：至少三种字符，最小 8 位',
  'login.captcha.placeholder': '请输入验证码',
  'login.remember': '记住密码',
  'login.demo.account': '账号：admin, 密码：123456',
  'login.submit': '登录',
  'login.other.ways': '其他方式登录',
  'login.validation.username': '用户名不能为空',
  'login.validation.password': '密码不能为空',
  'login.validation.captcha': '验证码不能为空',
  
  // 首页相关
  'home.department': '所属部门',
  'common.home': '首页',
  
  // 设置相关
  'setting.language': '语言',
  'setting.compact.default': '默认',
  'setting.compact.compact': '紧凑',
  'setting.unit.pixel': '像素（px）',
  'setting.unit.pixel_or_percent': '像素（px）或者 百分比',
  'setting.show': '显示',
  'setting.hide': '隐藏',
  'setting.copy.config': '复制配置信息',
  'setting.reset.default': '恢复默认配置',
};
