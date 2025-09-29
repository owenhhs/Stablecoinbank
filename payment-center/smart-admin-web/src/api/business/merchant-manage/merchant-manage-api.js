/**
 * 商户表 api 封装
 *
 * @Author:    sunyu
 * @Date:      2024-07-24 16:21:32
 * @Copyright  sunyu
 */
import {postRequest, getRequest} from '/src/lib/axios';

export const merchantManageApi = {

    /**
     * 分页查询  @author  sunyu
     */
    queryPage: (param) => {
        return postRequest('/merchantManage/queryPage', param);
    },

    /**
     * 获取筛选列表  @author  sunyu
     */
    getOptions: (param) => {
        return getRequest('/merchantManage/options', param);
    },


    /**
     * 增加  @author  sunyu
     */
    add: (param) => {
        return postRequest('/merchantManage/add', param);
    },

    /**
     * 修改  @author  sunyu
     */
    update: (param) => {
        return postRequest('/merchantManage/update', param);
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
