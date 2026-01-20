"use strict";
const common_vendor = require("../../common/vendor.js");
const api_pet = require("../../api/pet.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "detail",
  setup(__props) {
    const pet = common_vendor.ref(null);
    const petId = common_vendor.ref(0);
    common_vendor.onMounted(() => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      petId.value = Number(currentPage.options.id);
      if (petId.value) {
        loadPetDetail();
      }
    });
    async function loadPetDetail() {
      try {
        const res = await api_pet.petApi.getPetDetail(petId.value);
        if (res.code === 200) {
          pet.value = res.data;
        }
      } catch (error) {
        console.error("加载宠物详情失败:", error);
      }
    }
    function calculateAge(birthDate) {
      if (!birthDate)
        return "年龄未知";
      const birth = new Date(birthDate);
      const now = /* @__PURE__ */ new Date();
      const years = now.getFullYear() - birth.getFullYear();
      const months = now.getMonth() - birth.getMonth();
      if (years === 0) {
        return `${months}个月`;
      } else if (months < 0) {
        return `${years - 1}岁${12 + months}个月`;
      } else {
        return `${years}岁${months}个月`;
      }
    }
    function getGenderText(gender) {
      const map = {
        1: "公",
        2: "母",
        0: "未知"
      };
      return map[gender || 0] || "未知";
    }
    function formatDate(date) {
      if (!date)
        return "--";
      const d = new Date(date);
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, "0")}-${d.getDate().toString().padStart(2, "0")}`;
    }
    function goToEdit() {
      common_vendor.index.navigateTo({
        url: `/pages/pets/edit?id=${petId.value}`
      });
    }
    return (_ctx, _cache) => {
      var _a, _b, _c, _d, _e, _f, _g, _h, _i, _j;
      return common_vendor.e({
        a: ((_a = pet.value) == null ? void 0 : _a.photoUrl) || "/static/images/default-pet.png",
        b: common_vendor.t((_b = pet.value) == null ? void 0 : _b.name),
        c: common_vendor.t((_c = pet.value) == null ? void 0 : _c.species),
        d: common_vendor.t(((_d = pet.value) == null ? void 0 : _d.breed) || "未知品种"),
        e: common_vendor.t(calculateAge((_e = pet.value) == null ? void 0 : _e.birthDate)),
        f: common_vendor.o(goToEdit, "e3"),
        g: common_vendor.t(getGenderText((_f = pet.value) == null ? void 0 : _f.gender)),
        h: common_vendor.t(((_g = pet.value) == null ? void 0 : _g.weight) || "--"),
        i: common_vendor.t(formatDate((_h = pet.value) == null ? void 0 : _h.birthDate)),
        j: (_i = pet.value) == null ? void 0 : _i.notes
      }, ((_j = pet.value) == null ? void 0 : _j.notes) ? {
        k: common_vendor.t(pet.value.notes)
      } : {});
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-2695226c"]]);
wx.createPage(MiniProgramPage);
