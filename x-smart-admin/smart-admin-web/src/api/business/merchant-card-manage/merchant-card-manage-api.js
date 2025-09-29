/**
 * 商户表 api 封装
 *
 * @Author:    sunyu
 * @Date:      2024-07-24 16:21:32
 * @Copyright  sunyu
 */
import { postRequest, getRequest } from '/src/lib/axios';

export const merchantCardManageApi = {
  /**
   * 分页查询  @author  sunyu
   */
  queryPage: (param) => {
    return postRequest('/paymentChannelInfo/payinfo/List', param);
  },

  /**
   * 增加  @author  sunyu
   */
  add: (param) => {
    return postRequest('/paymentChannelInfo/payinfo/add', null, param);
  },

  /**
   * 修改  @author  sunyu
   */
  update: (param) => {
    return postRequest('/paymentChannelInfo/payinfo/update', null, param);
  },

  /**
   * 删除  @author  sunyu
   */
  delete: (id) => {
    return getRequest(`/merchantManage/delete/${id}`);
  },

  /**
   * 批量删除  @author  sunyu
   */
  batchDelete: (idList) => {
    return postRequest('/merchantManage/batchDelete', idList);
  },
};
