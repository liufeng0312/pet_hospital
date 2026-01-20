"use strict";
const common_vendor = require("../../common/vendor.js");
const api_reminder = require("../../api/reminder.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const reminders = common_vendor.ref([]);
    common_vendor.onMounted(async () => {
      await loadReminders();
    });
    async function loadReminders() {
      try {
        loading.value = true;
        const res = await api_reminder.reminderApi.getMyReminders();
        if (res.code === 200) {
          reminders.value = res.data;
        }
      } catch (error) {
        console.error("加载提醒失败:", error);
      } finally {
        loading.value = false;
      }
    }
    async function handleMarkAsRead(reminder) {
      if (reminder.status === 1)
        return;
      try {
        const res = await api_reminder.reminderApi.markAsRead(reminder.id);
        if (res.code === 200) {
          reminder.status = 1;
          common_vendor.index.showToast({
            title: "已标记为已读",
            icon: "success"
          });
        }
      } catch (error) {
        console.error("标记失败:", error);
      }
    }
    function getTypeIcon(type) {
      const icons = {
        VACCINE: "💉",
        BIRTHDAY: "🎂",
        CHECKUP: "🏥",
        MEDICINE: "💊"
      };
      return icons[type] || "📌";
    }
    function getTypeName(type) {
      const names = {
        VACCINE: "疫苗提醒",
        BIRTHDAY: "生日祝福",
        CHECKUP: "体检提醒",
        MEDICINE: "用药提醒"
      };
      return names[type] || "提醒";
    }
    function getContent(reminder) {
      if (reminder.content)
        return reminder.content;
      const templates = {
        VACCINE: "您的宠物疫苗即将到期，请及时接种",
        BIRTHDAY: "今天是您宠物的生日，祝生日快乐！",
        CHECKUP: "该进行定期体检了",
        MEDICINE: "请按时给宠物服药"
      };
      return templates[reminder.type] || "您有一条新提醒";
    }
    function formatDate(dateStr) {
      const date = new Date(dateStr);
      const now = /* @__PURE__ */ new Date();
      const diff = date.getTime() - now.getTime();
      const days = Math.floor(diff / (1e3 * 60 * 60 * 24));
      if (days === 0)
        return "今天";
      if (days === 1)
        return "明天";
      if (days > 0)
        return `${days}天后`;
      return dateStr;
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: loading.value
      }, loading.value ? {} : reminders.value.length === 0 ? {} : {
        c: common_vendor.f(reminders.value, (reminder, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(getTypeIcon(reminder.type)),
            b: common_vendor.t(getTypeName(reminder.type)),
            c: common_vendor.t(getContent(reminder)),
            d: common_vendor.t(formatDate(reminder.dueDate)),
            e: reminder.status === 0
          }, reminder.status === 0 ? {} : {}, {
            f: reminder.id,
            g: reminder.status === 0 ? 1 : "",
            h: common_vendor.o(($event) => handleMarkAsRead(reminder), reminder.id)
          });
        })
      }, {
        b: reminders.value.length === 0
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-49ed56c0"]]);
wx.createPage(MiniProgramPage);
