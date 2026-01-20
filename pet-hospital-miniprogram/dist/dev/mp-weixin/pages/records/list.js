"use strict";
const common_vendor = require("../../common/vendor.js");
const api_record = require("../../api/record.js");
const api_pet = require("../../api/pet.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "list",
  setup(__props) {
    const loading = common_vendor.ref(true);
    const records = common_vendor.ref([]);
    const pets = common_vendor.ref([]);
    const selectedPet = common_vendor.ref(null);
    common_vendor.onMounted(async () => {
      await loadPets();
      await loadRecords();
    });
    async function loadPets() {
      try {
        const res = await api_pet.petApi.getMyPets();
        if (res.code === 200) {
          pets.value = res.data;
        }
      } catch (error) {
        console.error("加载宠物列表失败:", error);
      }
    }
    async function loadRecords() {
      try {
        loading.value = true;
        const params = { page: 1, size: 20 };
        if (selectedPet.value) {
          params.petId = selectedPet.value.id;
        }
        const res = await api_record.recordApi.getMyRecords(params);
        if (res.code === 200) {
          records.value = res.data.records;
        }
      } catch (error) {
        console.error("加载病历失败:", error);
      } finally {
        loading.value = false;
      }
    }
    function onPetChange(e) {
      const index = e.detail.value;
      selectedPet.value = index >= 0 ? pets.value[index] : null;
      loadRecords();
    }
    function viewDetail(id) {
      common_vendor.index.navigateTo({
        url: `/pages/records/detail?id=${id}`
      });
    }
    function formatDate(dateStr) {
      if (!dateStr)
        return "";
      return dateStr.split(" ")[0];
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.t(selectedPet.value ? selectedPet.value.name : "全部宠物"),
        b: pets.value,
        c: common_vendor.o(onPetChange, "3d"),
        d: loading.value
      }, loading.value ? {} : records.value.length === 0 ? {} : {
        f: common_vendor.f(records.value, (record, k0, i0) => {
          var _a, _b;
          return {
            a: common_vendor.t(((_a = record.pet) == null ? void 0 : _a.name) || "未知宠物"),
            b: common_vendor.t(formatDate(record.visitTime)),
            c: common_vendor.t(((_b = record.doctor) == null ? void 0 : _b.name) || "未知医生"),
            d: common_vendor.t(record.diagnosis),
            e: record.id,
            f: common_vendor.o(($event) => viewDetail(record.id), record.id)
          };
        })
      }, {
        e: records.value.length === 0
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-0993c350"]]);
wx.createPage(MiniProgramPage);
