/**
 * 商户表 枚举
 *
 * @Author:    sunyu
 * @Date:      2024-07-24 16:21:32
 * @Copyright  sunyu
 */

import i18n from '/@/i18n';

export default {
    /**
     * 商户状态
     */
    MER_STATUS: {
        DISABLED: {
            value: 0,
            desc: i18n.global.t('constants.paySwitch.closed'),
        },
        ENABLED: {
            value: 1,
            desc: i18n.global.t('constants.paySwitch.opened'),
        },
    },

    XJ_STATUS: {
        UNXJ: {
            value: 0,
            desc: '否',
        },
        XJ: {
            value: 1,
            desc: '是',
        }
    },

    BLACK_STATUS: {
        UNBLACK: {
            value: 0,
            desc: '否',
        },
        BLACK: {
            value: 1,
            desc: '是',
        }
    }
};