"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_auth = require("../../api/auth.js");
const store_user = require("../../store/user.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "index",
  setup(__props) {
    const userStore = store_user.useUserStore();
    const loading = common_vendor.ref(false);
    async function handleWechatLogin(e) {
      if (!e.detail.userInfo) {
        common_vendor.index.showToast({
          title: "请授权登录",
          icon: "none"
        });
        return;
      }
      loading.value = true;
      try {
        const loginRes = await new Promise((resolve, reject) => {
          common_vendor.index.login({
            provider: "weixin",
            success: resolve,
            fail: reject
          });
        });
        if (!loginRes.code) {
          throw new Error("获取登录凭证失败");
        }
        const res = await api_auth.authApi.wechatLogin(loginRes.code);
        if (res.code === 200) {
          if (res.data.token) {
            if (res.data.user) {
              userStore.login(res.data.token, res.data.user);
            } else {
              console.error("Login successful but user info missing");
            }
            common_vendor.index.showToast({
              title: "登录成功",
              icon: "success"
            });
            setTimeout(() => {
              common_vendor.index.switchTab({
                url: "/pages/index/index"
              });
            }, 1e3);
          } else {
            const openid = res.data.openid;
            common_vendor.index.showModal({
              title: "绑定手机号",
              editable: true,
              placeholderText: "请输入手机号",
              success: async (modalRes) => {
                if (modalRes.confirm && modalRes.content && openid) {
                  try {
                    const bindRes = await api_auth.authApi.bindPhone(modalRes.content, openid);
                    if (bindRes.code === 200) {
                      userStore.login(bindRes.data.token, bindRes.data.user);
                      common_vendor.index.showToast({
                        title: "绑定成功",
                        icon: "success"
                      });
                      setTimeout(() => {
                        common_vendor.index.switchTab({
                          url: "/pages/index/index"
                        });
                      }, 1e3);
                    }
                  } catch (error) {
                    common_vendor.index.showToast({
                      title: error.msg || "绑定失败",
                      icon: "none"
                    });
                  }
                }
              }
            });
          }
        }
      } catch (error) {
        console.error("登录失败:", error);
        common_vendor.index.showToast({
          title: error.msg || "登录失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    }
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0,
        b: common_vendor.o(handleWechatLogin, "70")
      };
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-45258083"]]);
wx.createPage(MiniProgramPage);
