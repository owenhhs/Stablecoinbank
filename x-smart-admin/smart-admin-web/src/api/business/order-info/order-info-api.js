/**
 * 订单信息表 api 封装
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:56
 * @Copyright  sunyu
 */
import { postRequest, getRequest, postDownload } from '/src/lib/axios';

export const orderInfoApi = {
  /**
   * 分页查询  @author  sunyu
   */
  queryPage: (param) => {
    return postRequest('/orderInfo/queryPage', param);
  },

  /**
   * 增加  @author  sunyu
   */
  add: (param) => {
    return postRequest('/orderInfo/add', param);
  },

  /**
   * 修改  @author  sunyu
   */
  update: (param) => {
    return postRequest('/orderInfo/update', param);
  },

  /**
   * 删除  @author  sunyu
   */
  delete: (id) => {
    return getRequest(`/orderInfo/delete/${id}`);
  },

  /**
   * 批量删除  @author  sunyu
   */
  batchDelete: (idList) => {
    return postRequest('/orderInfo/batchDelete', idList);
  },

  /**
   * 导出订单  @author
   */
  exportOrders: (param) => {
    return postDownload('/orderInfo/exportExcel', param);
  },

  /**
   * 查询当日订单数据
   * @returns {Promise<axios.AxiosResponse<any>>}
   */
  todayOrderInfo: () => {
    return getRequest('/dashboard/order-data/today');
  },
};
