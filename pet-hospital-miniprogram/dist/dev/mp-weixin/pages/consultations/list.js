"use strict";
const common_vendor = require("../../common/vendor.js");
const api_consultation = require("../../api/consultation.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const consultations = common_vendor.ref([]);
    const currentStatus = common_vendor.ref("");
    const tabs = [
      { label: "全部", value: "" },
      { label: "待回复", value: "PENDING" },
      { label: "已回复", value: "ANSWERED" }
    ];
    common_vendor.onMounted(async () => {
      await loadConsultations();
    });
    async function loadConsultations() {
      try {
        loading.value = true;
        const params = { page: 1, size: 20 };
        if (currentStatus.value) {
          params.status = currentStatus.value;
        }
        const res = await api_consultation.consultationApi.getMyConsultations(params);
        if (res.code === 200) {
          consultations.value = res.data.records;
        }
      } catch (error) {
        console.error("加载咨询列表失败:", error);
      } finally {
        loading.value = false;
      }
    }
    function changeStatus(status) {
      currentStatus.value = status;
      loadConsultations();
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
    function viewDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/consultations/detail?id=${id}`
      });
    }
    function goToCreate() {
      common_vendor.index.navigateTo({
        url: "/pages/consultations/create"
      });
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(tabs, (tab, k0, i0) => {
          return {
            a: common_vendor.t(tab.label),
            b: tab.value,
            c: currentStatus.value === tab.value ? 1 : "",
            d: common_vendor.o(($event) => changeStatus(tab.value), tab.value)
          };
        }),
        b: loading.value
      }, loading.value ? {} : consultations.value.length === 0 ? {} : {
        d: common_vendor.f(consultations.value, (item, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(item.title),
            b: common_vendor.t(getStatusText(item.status)),
            c: common_vendor.n(getStatusClass(item.status)),
            d: common_vendor.t(item.content),
            e: common_vendor.t(item.createdAt),
            f: item.pet
          }, item.pet ? {
            g: common_vendor.t(item.pet.name)
          } : {}, {
            h: item.id,
            i: common_vendor.o(($event) => viewDetail(item.id), item.id)
          });
        })
      }, {
        c: consultations.value.length === 0,
        e: common_vendor.o(goToCreate, "8c")
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-f3e577fa"]]);
wx.createPage(MiniProgramPage);
