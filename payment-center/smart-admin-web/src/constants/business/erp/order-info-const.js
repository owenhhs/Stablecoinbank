/**
 * 订单信息表 枚举
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:56
 * @Copyright  sunyu
 */

export default {
  /**
   * 订单状态
   * 1 待确认 2 已确认 3 挂起
   */
  ORDER_STATUS: {
    WAITING: {
      value: 1,
      desc: '待确认',
    },
    CONFIRM: {
      value: 2,
      desc: '已确认',
    },
    HANG_UP: {
      value: 3,
      desc: '挂起',
    },
    REFUNDED: {
      value: 4,
      desc: '已退款',
    },
  },
  ORDER_PAY_STATUS: {
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
    EXPIRED: {
      value: 4,
      desc: '已过期',
    },
  },

  /**
   * 付款方式
   * bank/qrcode
   */
  PAYMENT_METHOD: {
    BANK: {
      value: 'bank',
      desc: '银行卡',
    },
    QRCODE: {
      value: 'qrcode',
      desc: '二维码',
    }
  },

  CURRENCY_ENUM: {
    CNY: {
      value: 'CNY',
      desc: 'CNY',
    },
    THB: {
      value: 'THB',
      desc: 'THB',
    },
    IDR: {
      value: 'IDR',
      desc: 'IDR',
    },
    VND: {
      value: 'VND',
      desc: 'VND',
    },
  },
};
