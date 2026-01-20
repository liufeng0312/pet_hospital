"use strict";
const common_vendor = require("../common/vendor.js");
const baseURL = "http://localhost:8080";
function request(config) {
  const token = common_vendor.index.getStorageSync("token");
  return new Promise((resolve, reject) => {
    common_vendor.index.request({
      url: `${baseURL}${config.url}`,
      method: config.method || "GET",
      data: config.data,
      header: {
        "Content-Type": "application/json",
        "Authorization": token ? `Bearer ${token}` : "",
        ...config.header
      },
      success: (res) => {
        const data = res.data;
        if (data.code === 200) {
          resolve(data);
        } else if (data.code === 401) {
          common_vendor.index.removeStorageSync("token");
          common_vendor.index.removeStorageSync("user");
          common_vendor.index.reLaunch({
            url: "/pages/login/index"
          });
          reject(data);
        } else {
          common_vendor.index.showToast({
            title: data.msg || "请求失败",
            icon: "none"
          });
          reject(data);
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({
          title: "网络错误",
          icon: "none"
        });
        reject(err);
      }
    });
  });
}
const request$1 = {
  get: (url, params) => request({ url, method: "GET", data: params }),
  post: (url, data) => request({ url, method: "POST", data }),
  put: (url, data) => request({ url, method: "PUT", data }),
  delete: (url, params) => request({ url, method: "DELETE", data: params })
};
exports.baseURL = baseURL;
exports.request = request$1;
