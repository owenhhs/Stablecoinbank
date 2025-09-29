/**
 * 支付渠道业务范围配置表 api 封装
 *
 * @Author:    Sunny
 * @Date:      2024-09-03 15:16:42
 * @Copyright  Sunny
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const paymentChannelBusinessCfgApi = {

  /**
   * 分页查询  @author  Sunny
   */
  queryPage : (param) => {
    return postRequest('/paymentChannelBusinessCfg/queryPage', param);
  },

  /**
   * 增加  @author  Sunny
   */
  add: (param) => {
      return postRequest('/paymentChannelBusinessCfg/add', param);
  },

  /**
   * 修改  @author  Sunny
   */
  update: (param) => {
      return postRequest('/paymentChannelBusinessCfg/update', param);
  },


  /**
   * 删除  @author  Sunny
   */
  delete: (id) => {
      return getRequest(`/paymentChannelBusinessCfg/delete/${id}`);
  },

  /**
   * 批量删除  @author  Sunny
   */
  batchDelete: (idList) => {
      return postRequest('/paymentChannelBusinessCfg/batchDelete', idList);
  },

};
