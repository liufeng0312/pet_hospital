"use strict";
const api_request = require("./request.js");
const articleApi = {
  // 获取文章列表
  getList: (params) => {
    return api_request.request.get("/api/articles/list", params);
  },
  // 获取文章详情
  getDetail: (id) => {
    return api_request.request.get(`/api/articles/${id}`);
  }
};
exports.articleApi = articleApi;
