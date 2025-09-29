/*
 * 收银台页面
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-06 20:51:50
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */

export const cashierRouters = [
  {
    path: '/cashier/:lang/:token',
    name: 'Cashier',
    component: () => import('/@/views/cashier/cashier-payment.vue'),
    meta: {
      title: '收银台',
      hideInMenu: true,
    },
  },
  {
    path: '/cashier/error',
    name: 'CashierError',
    component: () => import('/@/views/cashier/cashier-error.vue'),
    meta: {
      title: 'CashierError',
      hideInMenu: true,
    },
  },
];
