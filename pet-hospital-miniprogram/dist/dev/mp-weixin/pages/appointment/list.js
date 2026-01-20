"use strict";
const common_vendor = require("../../common/vendor.js");
const api_appointment = require("../../api/appointment.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const tabs = [
      { label: "待就诊", value: "PENDING" },
      { label: "已完成", value: "COMPLETED" },
      { label: "已取消", value: "CANCELLED" }
    ];
    const currentTab = common_vendor.ref("PENDING");
    const appointments = common_vendor.ref([]);
    const filteredAppointments = common_vendor.computed(() => {
      if (currentTab.value === "PENDING") {
        return appointments.value.filter((item) => ["PENDING", "WAITING", "CONFIRMED"].includes(item.status));
      }
      return appointments.value.filter((item) => item.status === currentTab.value);
    });
    common_vendor.onMounted(() => {
      loadAppointments();
    });
    async function loadAppointments() {
      try {
        const res = await api_appointment.appointmentApi.getMyAppointments();
        if (res.code === 200) {
          appointments.value = res.data;
        }
      } catch (error) {
        console.error("加载预约列表失败:", error);
      }
    }
    function getStatusText(status) {
      const map = {
        "PENDING": "待确认",
        "WAITING": "待就诊",
        // 候诊中
        "CONFIRMED": "已预约",
        "IN_PROGRESS": "就诊中",
        "COMPLETED": "已完成",
        "CANCELLED": "已取消"
      };
      return map[status] || status;
    }
    function getStatusClass(status) {
      const map = {
        "PENDING": "status-pending",
        "WAITING": "status-pending",
        "CONFIRMED": "status-confirmed",
        "IN_PROGRESS": "status-confirmed",
        "COMPLETED": "status-completed",
        "CANCELLED": "status-cancelled"
      };
      return map[status] || "";
    }
    function formatDateTime(dateTime) {
      const date = new Date(dateTime);
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const hour = date.getHours().toString().padStart(2, "0");
      const minute = date.getMinutes().toString().padStart(2, "0");
      return `${month}月${day}日 ${hour}:${minute}`;
    }
    function cancelAppointment(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要取消预约吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await api_appointment.appointmentApi.cancelAppointment(id);
              common_vendor.index.showToast({
                title: "取消成功",
                icon: "success"
              });
              loadAppointments();
            } catch (error) {
              console.error("取消预约失败:", error);
            }
          }
        }
      });
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(tabs, (tab, k0, i0) => {
          return {
            a: common_vendor.t(tab.label),
            b: tab.value,
            c: currentTab.value === tab.value ? 1 : "",
            d: common_vendor.o(($event) => currentTab.value = tab.value, tab.value)
          };
        }),
        b: filteredAppointments.value.length > 0
      }, filteredAppointments.value.length > 0 ? {
        c: common_vendor.f(filteredAppointments.value, (appointment, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(appointment.petName),
            b: common_vendor.t(getStatusText(appointment.status)),
            c: common_vendor.n(getStatusClass(appointment.status)),
            d: common_vendor.t(formatDateTime(appointment.appointmentTime)),
            e: common_vendor.t(appointment.doctorName),
            f: appointment.queueNumber
          }, appointment.queueNumber ? {
            g: common_vendor.t(appointment.queueNumber)
          } : {}, {
            h: appointment.status === "PENDING"
          }, appointment.status === "PENDING" ? {
            i: common_vendor.o(($event) => cancelAppointment(appointment.id), appointment.id)
          } : {}, {
            j: appointment.id
          });
        })
      } : {});
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-6e84a449"]]);
wx.createPage(MiniProgramPage);
