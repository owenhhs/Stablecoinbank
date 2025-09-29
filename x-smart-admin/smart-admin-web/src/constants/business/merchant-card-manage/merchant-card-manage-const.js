/**
 * 商户卡管理 枚举
 *
 * @Author:    sunyu
 * @Date:      2024-07-24 16:21:32
 * @Copyright  sunyu
 */

import i18n from '/@/i18n';

export default {
  MER_CARD_PAY_TYPE: {
    ALIPAY: {
      value: 'Alipay',
      desc: i18n.global.t('constants.payType.alipay'),
    },
    WECHAT: {
      value: 'WeChat',
      desc: i18n.global.t('constants.payType.wechat'),
    },
    BANK: {
      value: 'bank',
      desc: i18n.global.t('constants.payType.bank'),
    },
    QR_CODE: {
      value: 'QR_CODE',
      desc: 'QR_CODE'
    },
    TRUE_MONEY: {
      value: 'TRUE_MONEY',
      desc: 'TRUE_MONEY'
    },
    MOMOPAY: {
      value: 'MOMOPAY',
      desc: 'MOMOPAY'
    },
    ZALO: {
      value: 'ZALO',
      desc: 'ZALO'
    },
    VIETTEL_PAY: {
      value: 'VIETTEL_PAY',
      desc: 'VIETTEL_PAY'
    },
    P2C: {
      value: 'P2C',
      desc: 'P2C'
    },
    QRIS: {
      value: 'QRIS',
      desc: 'QRIS'
    },
  },

  /**
   * 支付方式状态
   */
  MER_CARD_PAY_STATUS: {
    DISABLED: {
      value: 0,
      desc: i18n.global.t('constants.paySwitch.closed'),
    },
    ENABLED: {
      value: 1,
      desc: i18n.global.t('constants.paySwitch.opened'),
    },
  },

  /**
   * 是否在黑名单
   */
  MER_CARD_BLACK_LIST: {
    DISABLED: {
      value: 1,
      desc: i18n.global.t('constants.yes'),
    },
    ENABLED: {
      value: 2,
      desc: i18n.global.t('constants.no'),
    },
  },

  /**
   * 路由方式
   */
  MER_CARD_ROUTE_TYPE: {
    PERCENTAGE: {
      value: 1,
      desc: i18n.global.t('constants.routeType.percentage'),
    },
    POLLING: {
      value: 2,
      desc: i18n.global.t('constants.routeType.polling'),
    },
  },

  XINJIANG_FLAG: {
    DISABLED: {
      value: 0,
      desc: '否',
    },
    ENABLED: {
      value: 1,
      desc: '是',
    },
  },
};
