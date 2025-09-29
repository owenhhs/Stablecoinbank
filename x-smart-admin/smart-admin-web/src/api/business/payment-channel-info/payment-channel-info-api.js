/**
 * 收款银行卡表 api 封装
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:43
 * @Copyright  sunyu
 */
import { postRequest, getRequest } from '/src/lib/axios';

export const paymentChannelInfoApi = {

  queryParentChannelInfoList() {
    return postRequest('/paymentChannelInfo/parent/list');
  },

  addParentChannelInfo(channelInfo) {
    return postRequest('/paymentChannelInfo/parent/add', channelInfo);
  },

  updateParentChannelInfo(channelInfo) {
    return postRequest('/paymentChannelInfo/parent/update', channelInfo);
  },

  queryChannelInfoList() {
    return postRequest('/paymentChannelInfo/list');
  },

  addChannelInfo(channelInfo) {
    return postRequest('/paymentChannelInfo/add', channelInfo);
  },

  updateChannelInfo(channelInfo) {
    return postRequest('/paymentChannelInfo/update', channelInfo);
  },

  /**
   * 主商户选项列表  @author  sunyu
   * @param params
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  parentChannelOptions: (params) => {
    return postRequest('/paymentChannelInfo/parent/queryOptions', params);
  },

  /**
   * 子商户选项列表  @author  sunyu
   */
  options: (params) => {
    return postRequest('/paymentChannelInfo/queryOptions', params);
  },

  /**
   * 商户选项列表  @author  sunyu
   */
  currencyOptions: (params) => {
    return postRequest('/paymentCurrency/queryOptions', params);
  },


  paymentUserQueryPage: (params) => {
    return postRequest('/payment-user/queryPage', params);
  },

  updateUserBlack: (userId, black) => {
    return postRequest(`/payment-user/black/update?userId=${userId}&black=${black}`);
  }
};
