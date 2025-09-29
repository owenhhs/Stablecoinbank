/**
 * 结算单 api 封装
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:56
 * @Copyright  sunyu
 */
import { postRequest, getRequest, postDownload } from '/src/lib/axios';

export const settlementInfoApi = {
  /**
   * 分页查询  @author  sunyu
   */
  queryPage: (param) => {
    return postRequest('/settle/queryPageBySettle', param);
  },
  /**
   * 结算汇率设置  @author  sunyu
   */
  exchangeRate: (params) => {
    return postRequest('/settle/setExchangeRate', params);
  },

  /**
   * 结算确认收款  @author
   */
  confirmPayment: (params) => {
    return postRequest('/settle/confirmPayment', params);
  },

  /**
   * 结算单明细  @author
   */
  queryPageBySettleDetail: (params) => {
    return postRequest('/settle/queryPageBySettleDetail', params);
  },

  /**
   * 导出订单  @author
   */
  exportOrders: (param) => {
    return postDownload('/settle/exportExcel', param);
  },

  /**
   * 重算  @author
   */
  calcByDate: (params) => {
    return postRequest('/settle/calcByDate', params);
  },


  /**
   * 分页查询  @author  sunyu
   */
  queryParentPage: (param) => {
    return postRequest('/settle/parent/queryPageBySettle', param);
  },
  /**
   * 结算汇率设置  @author  sunyu
   */
  parentExchangeRate: (params) => {
    return postRequest('/settle/parent/setExchangeRate', params);
  },

  /**
   * 结算确认收款  @author
   */
  confirmParentPayment: (params) => {
    return postRequest('/settle/parent/confirmPayment', params);
  },

  /**
   * 结算单明细  @author
   */
  queryPageByParentSettleDetail: (params) => {
    return postRequest('/settle/parent/queryPageBySettleDetail', params);
  },

  /**
   * 导出订单  @author
   */
  exportParentOrders: (param) => {
    return postDownload('/settle/parent/exportExcel', param);
  },
};
