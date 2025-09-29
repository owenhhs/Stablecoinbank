/**
 * 出金订单 枚举
 */

export default {
  /**
   * 主订单状态
   * 1-待处理 2-处理中 3-已完成
   */
  CASH_MAIN_ORDER_STATUS: {
    PROCESSING: {
      value: 1,
      desc: '处理中',
      color: 'processing'
    },
    SUCESSED: {
      value: 2,
      desc: '处理成功',
      color: 'succeed'
    },
    FAILED: {
      value: 3,
      desc: '处理失败',
      color: 'failed'
    },
  },

  CASH_CHNL_TYPE: {
    CONNECTED_CHANNEL: {
      value: 1,
      desc: '自有对接渠道',
      color: 'waiting'
    },
    EXTERNAL_CHANNEL: {
      value: 2,
      desc: '外部渠道',
      color: 'processing'
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
