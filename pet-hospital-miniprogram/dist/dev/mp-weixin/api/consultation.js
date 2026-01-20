"use strict";
const api_request = require("./request.js");
const consultationApi = {
  // 创建咨询
  create: (data) => {
    return api_request.request.post("/api/consultations/create", data);
  },
  // 获取我的咨询列表
  getMyConsultations: (params) => {
    return api_request.request.get("/api/consultations/my-consultations", params);
  },
  // 获取咨询详情
  getDetail: (id) => {
    return api_request.request.get(`/api/consultations/${id}`);
  }
};
exports.consultationApi = consultationApi;
