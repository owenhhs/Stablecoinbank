/**
 * 出金订单 枚举
 */

export default {
  /**
   * 开通标志
   * 1-待处理 2-处理中 3-已完成
   */
  FUNCTION_SWITCH: {
    ON: {
      value: 1,
      desc: '已开通',
      color: 'waiting'
    },
    OFF: {
      value: 0,
      desc: '未开通',
      color: 'processing'
    }
  },

  FUNCTION_LIST: {
    PAYMENT: {
      value: 'payment',
      desc: '入金',
      color: 'waiting'
    },
    CASH: {
      value: 'cash',
      desc: '出金',
      color: 'processing'
    }
  },
  
  PAY_METHOD_LIST: {
    BANK: {
      value: 'bank',
      desc: '银行卡',
      color: 'waiting'
    },
    ALIPAY: {
      value: 'Alipay',
      desc: '支付宝',
      color: 'waiting'
    },
    WEPAY: {
      value: 'WeChat',
      desc: '微信支付',
      color: 'waiting'
    },
  },

  CHNL_STATUS_LIST: {
    ON: {
      value: 1,
      desc: '启用',
    },
    OFF: {
      value: 0,
      desc: '关闭',
    },
  },

  STATUS_LIST: {
    ON: {
      value: 1,
      desc: '有效',
    },
    OFF: {
      value: 0,
      desc: '无效',
    },
  },

  INTERFACE_TYPE_LIST: {
    API: {
      value: 1,
      desc: 'API方式',
    },
    NON_API: {
      value: 2,
      desc: '非API方式',
    },
  }
};
