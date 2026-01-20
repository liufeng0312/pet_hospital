"use strict";
const common_vendor = require("../../common/vendor.js");
const api_record = require("../../api/record.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "detail",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const record = common_vendor.ref(null);
    const recordId = common_vendor.ref(0);
    common_vendor.onMounted(async () => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      recordId.value = parseInt(currentPage.options.id);
      await loadDetail();
    });
    async function loadDetail() {
      try {
        loading.value = true;
        const res = await api_record.recordApi.getDetail(recordId.value);
        if (res.code === 200) {
          record.value = res.data;
        }
      } catch (error) {
        console.error("加载病历详情失败:", error);
      } finally {
        loading.value = false;
      }
    }
    return (_ctx, _cache) => {
      var _a, _b;
      return common_vendor.e({
        a: loading.value
      }, loading.value ? {} : record.value ? common_vendor.e({
        c: common_vendor.t(((_a = record.value.pet) == null ? void 0 : _a.name) || "未知宠物"),
        d: common_vendor.t(((_b = record.value.doctor) == null ? void 0 : _b.name) || "未知医生"),
        e: common_vendor.t(record.value.visitTime),
        f: common_vendor.t(record.value.symptoms || "无"),
        g: common_vendor.t(record.value.diagnosis),
        h: common_vendor.t(record.value.treatment || "无"),
        i: record.value.notes
      }, record.value.notes ? {
        j: common_vendor.t(record.value.notes)
      } : {}) : {}, {
        b: record.value
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-493a5ada"]]);
wx.createPage(MiniProgramPage);
