"use strict";
const common_vendor = require("../common/vendor.js");
const useUserStore = common_vendor.defineStore("user", () => {
  const token = common_vendor.ref("");
  const userInfo = common_vendor.ref(null);
  const isLoggedIn = common_vendor.computed(() => !!token.value);
  function init() {
    token.value = common_vendor.index.getStorageSync("token") || "";
    const user = common_vendor.index.getStorageSync("user");
    if (user) {
      userInfo.value = JSON.parse(user);
    }
  }
  function login(tokenValue, user) {
    token.value = tokenValue;
    userInfo.value = user;
    common_vendor.index.setStorageSync("token", tokenValue);
    common_vendor.index.setStorageSync("user", JSON.stringify(user));
  }
  function logout() {
    token.value = "";
    userInfo.value = null;
    common_vendor.index.removeStorageSync("token");
    common_vendor.index.removeStorageSync("user");
  }
  return {
    token,
    userInfo,
    isLoggedIn,
    init,
    login,
    logout
  };
});
exports.useUserStore = useUserStore;
