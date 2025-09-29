import { postRequest, getRequest } from '/src/lib/axios';

export const dashBoardApi = {

    /**
     * 分页查询  @author  sunyu
     */
    todayOrderInfo: () => {
        return getRequest('/dashboard/order-data/today');
    },

}