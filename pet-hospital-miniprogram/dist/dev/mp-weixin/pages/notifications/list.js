"use strict";
const common_vendor = require("../../common/vendor.js");
const api_notification = require("../../api/notification.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const notifications = common_vendor.ref([]);
    const unreadCount = common_vendor.ref(0);
    common_vendor.onMounted(async () => {
      await loadNotifications();
      await loadUnreadCount();
    });
    async function loadNotifications() {
      try {
        loading.value = true;
        const res = await api_notification.notificationApi.getMyNotifications({ page: 1, size: 50 });
        if (res.code === 200) {
          notifications.value = res.data.records;
        }
      } catch (error) {
        console.error("加载通知列表失败:", error);
      } finally {
        loading.value = false;
      }
    }
    async function loadUnreadCount() {
      try {
        const res = await api_notification.notificationApi.getUnreadCount();
        if (res.code === 200) {
          unreadCount.value = res.data;
        }
      } catch (error) {
        console.error("加载未读数量失败:", error);
      }
    }
    async function markAllAsRead() {
      try {
        const res = await api_notification.notificationApi.markAllAsRead();
        if (res.code === 200) {
          common_vendor.index.showToast({ title: "已全部标记为已读", icon: "success" });
          await loadNotifications();
          unreadCount.value = 0;
        }
      } catch (error) {
        console.error("标记已读失败:", error);
      }
    }
    async function handleClick(item) {
      if (item.isRead === 0) {
        try {
          await api_notification.notificationApi.markAsRead(item.id);
          item.isRead = 1;
          unreadCount.value = Math.max(0, unreadCount.value - 1);
        } catch (error) {
          console.error("标记已读失败:", error);
        }
      }
      if (item.relatedType && item.relatedId) {
        const pageMap = {
          appointment: "/pages/appointment/detail",
          reminder: "/pages/reminders/list",
          consultation: "/pages/consultations/detail"
        };
        const page = pageMap[item.relatedType];
        if (page) {
          common_vendor.index.navigateTo({
            url: `${page}?id=${item.relatedId}`
          });
        }
      }
    }
    function getTypeIcon(type) {
      const iconMap = {
        APPOINTMENT: "📅",
        REMINDER: "⏰",
        CONSULTATION: "💬",
        SYSTEM: "📢"
      };
      return iconMap[type] || "🔔";
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: unreadCount.value > 0
      }, unreadCount.value > 0 ? {
        b: common_vendor.t(unreadCount.value),
        c: common_vendor.o(markAllAsRead, "65")
      } : {}, {
        d: loading.value
      }, loading.value ? {} : notifications.value.length === 0 ? {} : {
        f: common_vendor.f(notifications.value, (item, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(getTypeIcon(item.type)),
            b: item.isRead === 0
          }, item.isRead === 0 ? {} : {}, {
            c: common_vendor.t(item.title),
            d: common_vendor.t(item.content),
            e: common_vendor.t(item.createdAt),
            f: item.id,
            g: item.isRead === 0 ? 1 : "",
            h: common_vendor.o(($event) => handleClick(item), item.id)
          });
        })
      }, {
        e: notifications.value.length === 0
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-b64adb5d"]]);
wx.createPage(MiniProgramPage);
