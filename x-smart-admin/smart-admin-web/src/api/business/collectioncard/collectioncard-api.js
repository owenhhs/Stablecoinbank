/**
 * 收款银行卡表 api 封装
 *
 * @Author:    sunyu
 * @Date:      2024-07-23 14:25:43
 * @Copyright  sunyu
 */
import {postRequest, getRequest} from '/src/lib/axios';

export const collectioncardApi = {

    /**
     * 分页查询  @author  sunyu
     */
    queryPage: (param) => {
        return postRequest('/collectionCard/queryPage', param);
    },

    /**
     * 增加  @author  sunyu
     */
    add: (param) => {
        return postRequest('/collectionCard/add', param);
    },

    /**
     * 修改  @author  sunyu
     */
    update: (param) => {
        return postRequest('/collectionCard/update', param);
    },


    /**
     * 删除  @author  sunyu
     */
    delete: (id) => {
        return getRequest(`/collectionCard/delete/${id}`);
    },

    /**
     * 批量删除  @author  sunyu
     */
    batchDelete: (idList) => {
        return postRequest('/collectionCard/batchDelete', idList);
    },

    /**
     * 选项列表  @author  sunyu
     */
    options: () => {
        return postRequest('/collectionCard/options');
    },
};
