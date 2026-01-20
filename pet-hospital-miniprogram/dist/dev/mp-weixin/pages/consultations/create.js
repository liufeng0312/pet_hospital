"use strict";
const common_vendor = require("../../common/vendor.js");
const api_consultation = require("../../api/consultation.js");
const api_pet = require("../../api/pet.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "create",
  setup(__props) {
    const form = common_vendor.ref({
      title: "",
      petId: void 0,
      content: ""
    });
    const images = common_vendor.ref([]);
    const pets = common_vendor.ref([]);
    const selectedPet = common_vendor.ref(null);
    const submitting = common_vendor.ref(false);
    common_vendor.onMounted(async () => {
      await loadPets();
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
    function onPetChange(e) {
      const index = e.detail.value;
      if (index >= 0 && index < pets.value.length) {
        selectedPet.value = pets.value[index];
        form.value.petId = selectedPet.value.id;
      }
    }
    function chooseImage() {
      common_vendor.index.chooseImage({
        count: 3 - images.value.length,
        sizeType: ["compressed"],
        success: (res) => {
          images.value.push(...res.tempFilePaths);
        }
      });
    }
    function deleteImage(index) {
      images.value.splice(index, 1);
    }
    async function submit() {
      if (!form.value.title.trim()) {
        common_vendor.index.showToast({ title: "请输入标题", icon: "none" });
        return;
      }
      if (!form.value.content.trim()) {
        common_vendor.index.showToast({ title: "请输入咨询内容", icon: "none" });
        return;
      }
      try {
        submitting.value = true;
        const data = {
          title: form.value.title,
          content: form.value.content
        };
        if (form.value.petId) {
          data.petId = form.value.petId;
        }
        if (images.value.length > 0) {
          data.images = JSON.stringify(images.value);
        }
        const res = await api_consultation.consultationApi.create(data);
        if (res.code === 200) {
          common_vendor.index.showToast({ title: "提交成功", icon: "success" });
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        }
      } catch (error) {
        console.error("提交咨询失败:", error);
        common_vendor.index.showToast({ title: "提交失败，请重试", icon: "none" });
      } finally {
        submitting.value = false;
      }
    }
    function goBack() {
      common_vendor.index.navigateBack();
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: form.value.title,
        b: common_vendor.o(($event) => form.value.title = $event.detail.value, "d4"),
        c: common_vendor.t(selectedPet.value ? selectedPet.value.name : "请选择宠物"),
        d: pets.value,
        e: common_vendor.o(onPetChange, "4b"),
        f: form.value.content,
        g: common_vendor.o(($event) => form.value.content = $event.detail.value, "4d"),
        h: common_vendor.t(form.value.content.length),
        i: common_vendor.f(images.value, (img, index, i0) => {
          return {
            a: img,
            b: common_vendor.o(($event) => deleteImage(index), index),
            c: index
          };
        }),
        j: images.value.length < 3
      }, images.value.length < 3 ? {
        k: common_vendor.o(chooseImage, "3d")
      } : {}, {
        l: common_vendor.o(goBack, "cb"),
        m: common_vendor.t(submitting.value ? "提交中..." : "提交咨询"),
        n: common_vendor.o(submit, "d0"),
        o: submitting.value
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-2edd8bc0"]]);
wx.createPage(MiniProgramPage);
