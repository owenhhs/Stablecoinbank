import i18n from '/@/i18n'

export const SETTLEMENT_STATUS = {
  WAIT_SETTLE: 1,
  SETTLING: 2,
  WAIT_CONFIRM: 3,
  SETTLED: 4,
};

export default {
  /**
   * 结算单状态
   * 1:待结算，2:结算中，3:待确认，4:已结算
   */
  SETTLEMENT_STATUS: {
    WAIT_SETTLE: {
      value: 1,
      desc: i18n.global.t('constants.settlementStatus.pendingSettlement'),
    },
    SETTLING: {
      value: 2,
      desc: i18n.global.t('constants.settlementStatus.settling'),
    },
    // WAIT_CONFIRM: {
    //   value: 3,
    //   desc: '待确认',
    // },
    SETTLED: {
      value: 4,
      desc: i18n.global.t('constants.settlementStatus.alreadySettled'),
    },
  },

  /**
   * 結算單狀態
   * 1:待結算，2:結算中，3:待確認，4:已結算
   */
  SETTLEMENT_STATUS_TW: {
    WAIT_SETTLE: {
      value: 1,
      desc: '待結算',
    },
    SETTLING: {
      value: 2,
      desc: '結算中',
    },
    // WAIT_CONFIRM: {
    //   value: 3,
    //   desc: '待確認',
    // },
    SETTLED: {
      value: 4,
      desc: '已結算',
    },
  },

  /**
   * 付款方式
   * bank/Alipay/WeChat
   */
  PAYMENT_METHOD: {
    BANK: {
      value: 'bank',
      desc: i18n.global.t('constants.payType.bank'),
    },
    ALIPAY: {
      value: 'Alipay',
      desc: i18n.global.t('constants.payType.alipay'),

    },
    WECHAT: {
      value: 'WeChat',
      desc: i18n.global.t('constants.payType.wechat'),

    },
    QRCODE: {
      value: 'qrcode',
      desc: i18n.global.t('constants.payType.qrcode'),
    },
  },

  /**
   * 付款方式
   * bank/Alipay/WeChat
   */
  PAYMENT_METHOD_TW: {
    BANK: {
      value: 'bank',
      desc: '銀行卡',
    },
    ALIPAY: {
      value: 'Alipay',
      desc: '支付寶',
    },
    WECHAT: {
      value: 'WeChat',
      desc: '微信',
    },
    QRCODE: {
      value: 'qrcode',
      desc: '二維碼',
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
