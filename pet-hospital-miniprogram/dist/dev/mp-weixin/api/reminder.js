"use strict";
const api_request = require("./request.js");
const reminderApi = {
  // 获取我的提醒列表
  getMyReminders: () => api_request.request.get("/api/reminders/my-reminders"),
  // 标记提醒已读
  markAsRead: (id) => api_request.request.put(`/api/reminders/${id}/read`)
};
exports.reminderApi = reminderApi;
