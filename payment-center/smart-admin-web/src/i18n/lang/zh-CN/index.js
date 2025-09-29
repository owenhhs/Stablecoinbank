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
};
