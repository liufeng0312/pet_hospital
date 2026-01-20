"use strict";
const api_request = require("./request.js");
const notificationApi = {
  // 获取我的通知列表
  getMyNotifications: (params) => {
    return api_request.request.get("/api/notifications/my-notifications", params);
  },
  // 标记已读
  markAsRead: (id) => {
    return api_request.request.put(`/api/notifications/${id}/read`);
  },
  // 全部标记已读
  markAllAsRead: () => {
    return api_request.request.put("/api/notifications/read-all");
  },
  // 获取未读数量
  getUnreadCount: () => {
    return api_request.request.get("/api/notifications/unread-count");
  }
};
exports.notificationApi = notificationApi;
