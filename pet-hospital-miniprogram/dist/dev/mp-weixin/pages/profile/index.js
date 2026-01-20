"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "index",
  setup(__props) {
    const userStore = store_user.useUserStore();
    common_vendor.ref(0);
    common_vendor.ref(0);
    const userInfo = common_vendor.computed(() => userStore.userInfo);
    common_vendor.onMounted(() => {
    });
    function formatPhone(phone) {
      if (!phone)
        return "未绑定手机号";
      return phone.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
    }
    function getLevelText(level) {
      switch (level) {
        case 1:
          return "VIP";
        case 2:
          return "普通会员";
        default:
          return "会员";
      }
    }
    function goToReminders() {
      common_vendor.index.navigateTo({
        url: "/pages/reminders/list"
      });
    }
    function goToBills() {
      common_vendor.index.navigateTo({
        url: "/pages/bills/list"
      });
    }
    function goToAbout() {
      common_vendor.index.showModal({
        title: "关于我们",
        content: "宠物医院管理系统\n版本: 1.0.0\n致力于为爱宠提供最优质的医疗服务",
        showCancel: false
      });
    }
    function contactService() {
      common_vendor.index.makePhoneCall({
        phoneNumber: "400-123-4567",
        fail: (err) => {
          console.log("用户取消拨打", err);
        }
      });
    }
    function handleLogout() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            userStore.logout();
            common_vendor.index.reLaunch({
              url: "/pages/login/index"
            });
          }
        }
      });
    }
    return (_ctx, _cache) => {
      var _a, _b, _c, _d;
      return {
        a: ((_a = userInfo.value) == null ? void 0 : _a.avatar) || "/static/images/default-avatar.png",
        b: common_vendor.t(((_b = userInfo.value) == null ? void 0 : _b.name) || "宠物主人"),
        c: common_vendor.t(formatPhone((_c = userInfo.value) == null ? void 0 : _c.phone)),
        d: common_vendor.t(getLevelText((_d = userInfo.value) == null ? void 0 : _d.level)),
        e: common_vendor.o(goToReminders, "39"),
        f: common_vendor.o(goToBills, "8d"),
        g: common_vendor.o(goToAbout, "49"),
        h: common_vendor.o(contactService, "d6"),
        i: common_vendor.o(handleLogout, "da")
      };
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-f97f9319"]]);
wx.createPage(MiniProgramPage);
