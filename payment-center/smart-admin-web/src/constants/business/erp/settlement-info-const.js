// export const SETTLEMENT_STATUS = {
//   UNFINISHED: 0,
//   FINISHED: 1,
// };

// export default {
//   SETTLEMENT_STATUS: {
//     UNFINISHED: {
//       value: 0,
//       desc: '未结算',
//     },
//     FINISHED: {
//       value: 1,
//       desc: '已结算',
//     },
//   },
// };

export const SETTLEMENT_STATUS = {
  WAIT_SETTLE: 0,
  SETTLING: 1,
  // WAIT_CONFIRM: 3,
  SETTLED: 2,
};

export default {
  /**
   * 结算单状态
   * 1:待结算，2:结算中，3:待确认，4:已结算
   */
  SETTLEMENT_STATUS: {
    WAIT_SETTLE: {
      value: 0,
      desc: '待结算',
    },
    SETTLING: {
      value: 1,
      desc: '结算中',
    },
    // WAIT_CONFIRM: {
    //   value: 3,
    //   desc: '待确认',
    // },
    SETTLED: {
      value: 2,
      desc: '已结算',
    },
  },

  /**
   * 付款方式
   * bank/Alipay/WeChat/qrcode
   */
  PAYMENT_METHOD: {
    BANK: {
      value: 'bank',
      desc: '银行卡',
    },
    ALIPAY: {
      value: 'Alipay',
      desc: '支付宝',
    },
    WECHAT: {
      value: 'WeChat',
      desc: '微信',
    },
    QRCODE: {
      value: 'qrcode',
      desc: '二维码',
    },
  },

  CHANNEL_SETTLEMENT_STATUS: {
    WAIT_SETTLE: {
      value: 1,
      desc: '待结算',
    },
    SETTLING: {
      value: 2,
      desc: '结算中',
    },
    // WAIT_CONFIRM: {
    //   value: 3,
    //   desc: '待确认',
    // },
    SETTLED: {
      value: 4,
      desc: '已结算',
    },
  },

  /**
   * 交易方式
   * 1: 支付单，2: 兑付单
   */
  TRADE_TYPE: {
    ORDER: {
      value: 1,
      desc: '支付单',
    },
    CASH: {
      value: 2,
      desc: '兑付单',
    },
  },
};
