import { postRequest, getRequest } from '/src/lib/axios';

export const cashierApi = {

  getOrderInfo: (token) => {
    return getRequest(`/openapi/order/payment/cashier/info?token=${token}`);
  },

  confirmPayment: (token, params) => {
    return postRequest(`/openapi/order/payment/cashier/confirm?token=${token}`, params);
  },

  cancelPayment: (token) => {
    return postRequest(`/openapi/order/payment/cashier/cancel?token=${token}`);
  }

}