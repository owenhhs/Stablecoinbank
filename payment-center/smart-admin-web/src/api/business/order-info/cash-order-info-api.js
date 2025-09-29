/**
 * 出金订单 api 封装
 */
import { postRequest, getRequest, postDownload } from '/src/lib/axios';

export const cashOrderInfoApi = {

  /**
   * 【手工导入表】分页查询
   */
  manualImportQueryPage : (param) => {
    return postRequest('/cashOrderManualImport/queryPage', param);
  },

  /**
   * 【手工导入表】导入excel
   */
  manualImportExcel: (param) => {
      return postRequest('/cashOrderManualImport/importExcel', param);
  },
  
  //#######################分割线#######################

  /**
   * 分页查询
   */
  queryPage : (param) => {
    return postRequest('/cashOrderInfo/queryPage', param);
  },

  querySubOrderSummary : (param) => {
    return postRequest('/cashOrderDetail/querySubOrderSummary', param);
  },

  /**
   * 人工处理
   */
  manual: (param) => {
      return postRequest('/cashOrderInfo/manual', param);
  },

};
