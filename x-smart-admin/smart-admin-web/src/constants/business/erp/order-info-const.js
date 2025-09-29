/**
 * 订单信息表 枚举
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:56
 * @Copyright  sunyu
 */

import i18n from '/@/i18n';

export default {
  /**
   * 订单状态
   * 1 待确认 2 已确认 3 挂起
   */
  ORDER_STATUS: {
    WAITING: {
      value: 1,
      desc: i18n.global.t('constants.orderStatus.waiting'),
    },
    CONFIRM: {
      value: 2,
      desc: i18n.global.t('constants.orderStatus.confirmed'),
    },
    HANG_UP: {
      value: 3,
      desc: i18n.global.t('constants.orderStatus.hangUp'),
    },
    EXPIRED: {
      value: 4,
      desc: i18n.global.t('constants.orderStatus.expired'),
    },
    REFUND: {
      value: 5,
      desc: i18n.global.t('constants.orderStatus.refund'),
    },
  },

  /**
   * 订单状态
   * 1 待确认 2 已确认 3 挂起
   */
  ORDER_STATUS_TW: {
    WAITING: {
      value: 1,
      desc: '待確認',
    },
    CONFIRM: {
      value: 2,
      desc: '已確認',
    },
    HANG_UP: {
      value: 3,
      desc: '掛起/取消',
    },
    EXPIRED: {
      value: 4,
      desc: '已过期',
    },
    REFUND: {
      value: 5,
      desc: '已退款',
    },
  },

  ORDER_PAY_STATUS: {
    WAITING: {
      value: 1,
      desc: i18n.global.t('constants.payStatus.obligation'),
    },
    CONFIRM: {
      value: 2,
      desc: i18n.global.t('constants.payStatus.paid'),
    },
    HANG_UP: {
      value: 3,
      desc: i18n.global.t('constants.payStatus.canceled'),
    },
  },

  ORDER_PAY_STATUS_TW: {
    WAITING: {
      value: 1,
      desc: '待付款',
    },
    CONFIRM: {
      value: 2,
      desc: '已付款',
    },
    HANG_UP: {
      value: 3,
      desc: '已取消',
    },
  },
};
