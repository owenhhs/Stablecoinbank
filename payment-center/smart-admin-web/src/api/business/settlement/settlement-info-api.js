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
    return postRequest('/settleInfo/queryPage', param);
  },

  /**
   * 增加  @author  sunyu
   */
  add: (param) => {
    return postRequest('/settleInfo/add', param);
  },

  /**
   * 修改  @author  sunyu
   */
  update: (param) => {
    return postRequest('/settleInfo/update', param);
  },

  /**
   * 删除  @author  sunyu
   */
  delete: (id) => {
    return getRequest(`/settleInfo/delete/${id}`);
  },

  /**
   * 批量删除  @author  sunyu
   */
  batchDelete: (idList) => {
    return postRequest('/settleInfo/batchDelete', idList);
  },

  /**
   * 结算汇率设置  @author  sunyu
   */
  exchangeRate: (params) => {
    return postRequest('/settleInfo/exchangeRate', params);
  },

  /**
   * 结算完成  @author
   */
  finishSettleInfo: (params) => {
    return postRequest('/settleInfo/finished', params);
  },

  /**
   * 结算单明细列表  @author
   */
  orderList: (params) => {
    return postRequest('/settleInfo/orderList', params);
  },

  /**
   * 导出订单  @author
   */
  exportOrders: (params, progress) => {
    return postDownload('/settleInfo/exportExcel', params, progress);
  },

  /**
   * 重算  @author
   */
  calcByDate: (params) => {
    return postRequest('/settle/calcByDate', params);
  },

  /**
   * channel分页查询  @author  sunyu
   */
  queryPageChannel: (param) => {
    return postRequest('/settleInfo/channel/queryPage', param);
  },
};
