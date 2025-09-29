/*
 * 操作日志
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-09-03 21:56:45
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import { getRequest } from '/src/lib/axios';

export const searchLogApi = {
  // 日志查询 @author 卓大
  queryList: (param) => {
    return getRequest('/support/log/search', param);
  },
};
