"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "index",
  setup(__props) {
    const userStore = store_user.useUserStore();
    const quickActions = common_vendor.ref([
      { title: "在线预约", icon: "📅", path: "/pages/appointment/create", gradient: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" },
      { title: "我的账单", icon: "💰", path: "/pages/bills/list", gradient: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)" },
      { title: "病历查询", icon: "📋", path: "/pages/records/list", gradient: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)" },
      { title: "在线咨询", icon: "💬", path: "/pages/consultations/list", gradient: "linear-gradient(135deg, #30cfd0 0%, #330867 100%)" },
      { title: "健康资讯", icon: "📰", path: "/pages/articles/list", gradient: "linear-gradient(135deg, #fa709a 0%, #fee140 100%)" },
      { title: "消息通知", icon: "🔔", path: "/pages/notifications/list", gradient: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)" }
    ]);
    const services = common_vendor.ref([
      { title: "全科医疗", icon: "🏥", desc: "从幼宠咨询到老年病管理，提供全生命周期医疗服务" },
      { title: "手术服务", icon: "⚕️", desc: "配备顶级数字化手术室与麻醉监控系统" },
      { title: "重症监护", icon: "🚑", desc: "24小时恒温吸氧监护室，专业护理团队" },
      { title: "影像诊断", icon: "🔬", desc: "进口数字化DR、超声波检查，精准快速" }
    ]);
    common_vendor.onMounted(() => {
      userStore.init();
      if (!userStore.isLoggedIn) {
        common_vendor.index.reLaunch({
          url: "/pages/login/index"
        });
      }
    });
    function navigateTo(path) {
      if (!path) {
        common_vendor.index.showToast({
          title: "功能开发中",
          icon: "none"
        });
        return;
      }
      const tabPages = [
        "/pages/index/index",
        "/pages/pets/list",
        "/pages/appointment/list",
        "/pages/profile/index"
      ];
      if (tabPages.includes(path)) {
        common_vendor.index.switchTab({
          url: path
        });
      } else {
        common_vendor.index.navigateTo({
          url: path
        });
      }
    }
    function makePhoneCall() {
      common_vendor.index.makePhoneCall({
        phoneNumber: "400-123-4567"
      });
    }
    return (_ctx, _cache) => {
      return {
        a: common_vendor.o(makePhoneCall, "94"),
        b: common_vendor.f(quickActions.value, (action, k0, i0) => {
          return {
            a: common_vendor.t(action.icon),
            b: action.gradient,
            c: common_vendor.t(action.title),
            d: action.title,
            e: common_vendor.o(($event) => navigateTo(action.path), action.title)
          };
        }),
        c: common_vendor.f(services.value, (service, k0, i0) => {
          return {
            a: common_vendor.t(service.icon),
            b: common_vendor.t(service.title),
            c: common_vendor.t(service.desc),
            d: service.title
          };
        })
      };
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-83a5a03c"]]);
wx.createPage(MiniProgramPage);
