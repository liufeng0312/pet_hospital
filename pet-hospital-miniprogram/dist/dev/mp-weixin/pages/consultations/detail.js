"use strict";
const common_vendor = require("../../common/vendor.js");
const api_consultation = require("../../api/consultation.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "detail",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const consultation = common_vendor.ref(null);
    const consultationId = common_vendor.ref(0);
    common_vendor.onMounted(async () => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      consultationId.value = parseInt(currentPage.options.id);
      await loadDetail();
    });
    async function loadDetail() {
      try {
        loading.value = true;
        const res = await api_consultation.consultationApi.getDetail(consultationId.value);
        if (res.code === 200) {
          consultation.value = res.data;
        }
      } catch (error) {
        console.error("加载咨询详情失败:", error);
      } finally {
        loading.value = false;
      }
    }
    function getStatusClass(status) {
      const map = {
        PENDING: "status-pending",
        ANSWERED: "status-answered",
        CLOSED: "status-closed"
      };
      return map[status] || "";
    }
    function getStatusText(status) {
      const map = {
        PENDING: "待回复",
        ANSWERED: "已回复",
        CLOSED: "已关闭"
      };
      return map[status] || status;
    }
    return (_ctx, _cache) => {
      var _a;
      return common_vendor.e({
        a: loading.value
      }, loading.value ? {} : consultation.value ? common_vendor.e({
        c: common_vendor.t(getStatusText(consultation.value.status)),
        d: common_vendor.n(getStatusClass(consultation.value.status)),
        e: common_vendor.t(consultation.value.title),
        f: common_vendor.t(consultation.value.content),
        g: consultation.value.pet
      }, consultation.value.pet ? {
        h: common_vendor.t(consultation.value.pet.name),
        i: common_vendor.t(consultation.value.pet.species)
      } : {}, {
        j: common_vendor.t(consultation.value.createdAt),
        k: consultation.value.replyContent
      }, consultation.value.replyContent ? {
        l: common_vendor.t(((_a = consultation.value.doctor) == null ? void 0 : _a.name) || "医生"),
        m: common_vendor.t(consultation.value.replyTime),
        n: common_vendor.t(consultation.value.replyContent)
      } : {}) : {}, {
        b: consultation.value
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-19311ea0"]]);
wx.createPage(MiniProgramPage);
