/**
 * 商品配置 api 封装
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:50
 * @Copyright  sunyu
 */
import { postRequest, getRequest } from '/src/lib/axios';

export const skucfgApi = {

  /**
   * 分页查询  @author  sunyu
   */
  queryPage : (param) => {
    return postRequest('/skuCfg/queryPage', param);
  },

  /**
   * 增加  @author  sunyu
   */
  add: (param) => {
      return postRequest('/skuCfg/add', param);
  },

  /**
   * 修改  @author  sunyu
   */
  update: (param) => {
      return postRequest('/skuCfg/update', param);
  },


  /**
   * 删除  @author  sunyu
   */
  delete: (id) => {
      return getRequest(`/skuCfg/delete/${id}`);
  },

  /**
   * 批量删除  @author  sunyu
   */
  batchDelete: (idList) => {
      return postRequest('/skuCfg/batchDelete', idList);
  },

};
