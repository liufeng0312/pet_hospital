"use strict";
const common_vendor = require("../common/vendor.js");
const api_request = require("./request.js");
const petApi = {
  // 获取我的宠物列表
  getMyPets: () => api_request.request.get("/api/pets/my-pets"),
  // 获取宠物详情
  getPetDetail: (id) => api_request.request.get(`/api/pets/${id}`),
  // 创建宠物
  createPet: (data) => api_request.request.post("/api/pets/create", data),
  // 更新宠物
  updatePet: (id, data) => api_request.request.put(`/api/pets/${id}/update`, data),
  // 上传图片
  uploadImage: (filePath) => {
    const token = common_vendor.index.getStorageSync("token");
    return new Promise((resolve, reject) => {
      common_vendor.index.uploadFile({
        url: `${api_request.baseURL}/api/upload/image`,
        filePath,
        name: "file",
        header: {
          "Authorization": token ? `Bearer ${token}` : ""
        },
        success: (res) => {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            resolve(api_request.baseURL + data.data);
          } else {
            common_vendor.index.showToast({
              title: data.msg || "上传失败",
              icon: "none"
            });
            reject(data);
          }
        },
        fail: (err) => {
          common_vendor.index.showToast({
            title: "上传失败",
            icon: "none"
          });
          reject(err);
        }
      });
    });
  }
};
exports.petApi = petApi;
