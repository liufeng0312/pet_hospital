"use strict";
const api_request = require("./request.js");
const authApi = {
  // 微信登录
  wechatLogin: (code) => api_request.request.post("/api/auth/wechat-login", { code }),
  // 绑定手机号
  bindPhone: (phone, openid) => api_request.request.post("/api/auth/bind-phone", { phone, openid })
};
exports.authApi = authApi;
