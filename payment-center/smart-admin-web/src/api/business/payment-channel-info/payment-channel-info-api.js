/**
 * 收款銀行卡表 api 封裝
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:43
 * @Copyright  sunyu
 */
import { postRequest, getRequest } from '/src/lib/axios';

export const paymentChannelInfoApi = {
  /**
   * 分頁查詢  @author  sunyu
   */
  queryPage: (param) => {
    return postRequest('/paymentChannelInfo/queryPage', param);
  },

  /**
   * 增加  @author  sunyu
   */
  add: (param) => {
    return postRequest('/paymentChannelInfo/add', param);
  },

  /**
   * 修改  @author  sunyu
   */
  update: (param) => {
    return postRequest('/paymentChannelInfo/update', param);
  },

  /**
   * 刪除  @author  sunyu
   */
  delete: (id) => {
    return getRequest(`/paymentChannelInfo/delete/${id}`);
  },

  /**
   * 批量刪除  @author  sunyu
   */
  batchDelete: (idList) => {
    return postRequest('/paymentChannelInfo/batchDelete', idList);
  },

  /**
   * 選項列表  @author  sunyu
   */
  options: () => {
    return postRequest('/paymentChannelInfo/options');
  },

    /**
   * 選項列表  @author  sunyu
   */
  cashoptions: () => {
    return postRequest('/cashChannelInfo/options');
  },
};
