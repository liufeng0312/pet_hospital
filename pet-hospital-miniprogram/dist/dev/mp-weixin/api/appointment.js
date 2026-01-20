"use strict";
const api_request = require("./request.js");
const appointmentApi = {
  // 获取医生列表
  getDoctors: () => api_request.request.get("/api/employees/doctors"),
  // 获取可预约时间段
  getAvailableSlots: (doctorId, date) => api_request.request.get("/api/registrations/available-slots", { doctorId, date }),
  // 创建预约
  createAppointment: (data) => api_request.request.post("/api/registrations", data),
  // 我的预约列表
  getMyAppointments: () => api_request.request.get("/api/registrations/my-appointments"),
  // 取消预约
  cancelAppointment: (id) => api_request.request.put(`/api/registrations/${id}/cancel`)
};
exports.appointmentApi = appointmentApi;
