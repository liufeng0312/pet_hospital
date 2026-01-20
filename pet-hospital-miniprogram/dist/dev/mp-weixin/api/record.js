"use strict";
const api_request = require("./request.js");
const recordApi = {
  // 获取我的病历列表
  getMyRecords: (params) => api_request.request.get("/api/medical-records/my-records", params),
  // 获取病历详情
  getDetail: (id) => api_request.request.get(`/api/medical-records/${id}`)
};
exports.recordApi = recordApi;
