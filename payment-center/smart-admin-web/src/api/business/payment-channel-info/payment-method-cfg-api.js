/**
 * 支付渠道支付方式配置表 api 封装
 *
 * @Author:    Sunny
 * @Date:      2024-09-03 15:16:42
 * @Copyright  Sunny
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const paymentMethodCfgApi = {

  /**
   * 分页查询  @author  Sunny
   */
  queryPage : (param) => {
    return postRequest('/paymentMethodCfg/queryPage', param);
  },

  /**
   * 增加  @author  Sunny
   */
  add: (param) => {
      return postRequest('/paymentMethodCfg/add', param);
  },

  /**
   * 修改  @author  Sunny
   */
  update: (param) => {
      return postRequest('/paymentMethodCfg/update', param);
  },


  /**
   * 删除  @author  Sunny
   */
  delete: (id) => {
      return getRequest(`/paymentMethodCfg/delete/${id}`);
  },

  /**
   * 批量删除  @author  Sunny
   */
  batchDelete: (idList) => {
      return postRequest('/paymentMethodCfg/batchDelete', idList);
  },

};
