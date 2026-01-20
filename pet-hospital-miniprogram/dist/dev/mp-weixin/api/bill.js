"use strict";
const api_request = require("./request.js");
const billApi = {
  // 获取我的账单列表
  getMyBills: (params) => api_request.request.get("/api/bills/my-bills", params),
  // 获取账单详情
  getDetail: (id) => api_request.request.get(`/api/bills/${id}`)
};
exports.billApi = billApi;
