"use strict";
const common_vendor = require("../../common/vendor.js");
const api_bill = require("../../api/bill.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const bills = common_vendor.ref([]);
    common_vendor.onMounted(async () => {
      await loadBills();
    });
    async function loadBills() {
      try {
        loading.value = true;
        const res = await api_bill.billApi.getMyBills({ page: 1, size: 20 });
        if (res.code === 200) {
          bills.value = res.data.records;
        }
      } catch (error) {
        console.error("加载账单失败:", error);
      } finally {
        loading.value = false;
      }
    }
    function viewDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/bills/detail?id=${id}`
      });
    }
    function getStatusClass(status) {
      const classes = {
        UNPAID: "status-unpaid",
        PAID: "status-paid",
        PARTIAL: "status-partial"
      };
      return classes[status] || "";
    }
    function getStatusText(status) {
      const texts = {
        UNPAID: "未支付",
        PAID: "已支付",
        PARTIAL: "部分支付"
      };
      return texts[status] || status;
    }
    function formatDate(dateStr) {
      if (!dateStr)
        return "";
      return dateStr.split(" ")[0];
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: loading.value
      }, loading.value ? {} : bills.value.length === 0 ? {} : {
        c: common_vendor.f(bills.value, (bill, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(bill.id),
            b: common_vendor.t(getStatusText(bill.paymentStatus)),
            c: common_vendor.n(getStatusClass(bill.paymentStatus)),
            d: common_vendor.t(bill.totalAmount.toFixed(2)),
            e: bill.paidAmount > 0
          }, bill.paidAmount > 0 ? {
            f: common_vendor.t(bill.paidAmount.toFixed(2))
          } : {}, {
            g: common_vendor.t(formatDate(bill.createdAt)),
            h: bill.id,
            i: common_vendor.o(($event) => viewDetail(bill.id), bill.id)
          });
        })
      }, {
        b: bills.value.length === 0
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-8742e3fd"]]);
wx.createPage(MiniProgramPage);
