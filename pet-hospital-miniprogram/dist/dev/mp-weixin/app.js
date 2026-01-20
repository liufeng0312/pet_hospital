"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/login/index.js";
  "./pages/pets/list.js";
  "./pages/pets/detail.js";
  "./pages/pets/edit.js";
  "./pages/appointment/create.js";
  "./pages/reminders/list.js";
  "./pages/records/list.js";
  "./pages/records/detail.js";
  "./pages/bills/list.js";
  "./pages/appointment/list.js";
  "./pages/profile/index.js";
  "./pages/consultations/list.js";
  "./pages/consultations/create.js";
  "./pages/consultations/detail.js";
  "./pages/articles/list.js";
  "./pages/articles/detail.js";
  "./pages/notifications/list.js";
}
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "App",
  setup(__props) {
    common_vendor.onLaunch(() => {
      console.log("App Launch");
    });
    common_vendor.onShow(() => {
      console.log("App Show");
    });
    common_vendor.onHide(() => {
      console.log("App Hide");
    });
    return () => {
    };
  }
});
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  const pinia = common_vendor.createPinia();
  app.use(pinia);
  return {
    app,
    pinia
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
