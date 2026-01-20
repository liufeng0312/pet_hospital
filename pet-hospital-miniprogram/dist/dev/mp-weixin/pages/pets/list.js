"use strict";
const common_vendor = require("../../common/vendor.js");
const api_pet = require("../../api/pet.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const pets = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    common_vendor.onShow(() => {
      loadPets();
    });
    async function loadPets() {
      loading.value = true;
      try {
        const res = await api_pet.petApi.getMyPets();
        if (res.code === 200) {
          pets.value = res.data;
        }
      } catch (error) {
        console.error("加载宠物列表失败:", error);
      } finally {
        loading.value = false;
      }
    }
    function goToDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/pets/detail?id=${id}`
      });
    }
    function goToAdd() {
      common_vendor.index.navigateTo({
        url: "/pages/pets/edit"
      });
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: pets.value.length > 0
      }, pets.value.length > 0 ? {
        b: common_vendor.f(pets.value, (pet, k0, i0) => {
          return {
            a: pet.photoUrl || "/static/images/default-pet.png",
            b: common_vendor.t(pet.name),
            c: common_vendor.t(pet.species),
            d: common_vendor.t(pet.breed || "未知品种"),
            e: pet.id,
            f: common_vendor.o(($event) => goToDetail(pet.id), pet.id)
          };
        })
      } : {}, {
        c: common_vendor.o(goToAdd, "cf")
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-7a393ae9"]]);
wx.createPage(MiniProgramPage);
