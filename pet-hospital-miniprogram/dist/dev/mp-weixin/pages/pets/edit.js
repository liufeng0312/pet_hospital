"use strict";
const common_vendor = require("../../common/vendor.js");
const api_pet = require("../../api/pet.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "edit",
  setup(__props) {
    const isEdit = common_vendor.ref(false);
    const petId = common_vendor.ref(0);
    const loading = common_vendor.ref(false);
    const speciesList = ["猫", "狗", "兔子", "仓鼠", "鸟类", "其他"];
    const genderList = [
      { label: "公", value: 1 },
      { label: "母", value: 2 },
      { label: "未知", value: 0 }
    ];
    const form = common_vendor.ref({
      name: "",
      species: "",
      breed: "",
      gender: 0,
      birthDate: "",
      weight: void 0,
      photoUrl: "",
      notes: ""
    });
    common_vendor.onMounted(() => {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      const id = currentPage.options.id;
      if (id) {
        isEdit.value = true;
        petId.value = Number(id);
        loadPetDetail();
      }
    });
    async function loadPetDetail() {
      try {
        const res = await api_pet.petApi.getPetDetail(petId.value);
        if (res.code === 200) {
          form.value = {
            name: res.data.name,
            species: res.data.species,
            breed: res.data.breed,
            gender: res.data.gender,
            birthDate: res.data.birthDate,
            weight: res.data.weight,
            photoUrl: res.data.photoUrl,
            notes: res.data.notes
          };
        }
      } catch (error) {
        console.error("加载宠物详情失败:", error);
      }
    }
    function onSpeciesChange(e) {
      form.value.species = speciesList[e.detail.value];
    }
    function onGenderChange(e) {
      form.value.gender = genderList[e.detail.value].value;
    }
    function onDateChange(e) {
      form.value.birthDate = e.detail.value;
    }
    function getGenderText(gender) {
      const item = genderList.find((g) => g.value === gender);
      return item ? item.label : "请选择性别";
    }
    async function chooseImage() {
      try {
        const res = await new Promise((resolve, reject) => {
          common_vendor.index.chooseImage({
            count: 1,
            sizeType: ["compressed"],
            sourceType: ["album", "camera"],
            success: resolve,
            fail: reject
          });
        });
        if (res.tempFilePaths && res.tempFilePaths.length > 0) {
          const tempFilePath = res.tempFilePaths[0];
          form.value.photoUrl = tempFilePath;
          common_vendor.index.showLoading({ title: "上传中..." });
          try {
            const remoteUrl = await api_pet.petApi.uploadImage(tempFilePath);
            form.value.photoUrl = remoteUrl;
            common_vendor.index.hideLoading();
            common_vendor.index.showToast({
              title: "上传成功",
              icon: "success"
            });
          } catch (error) {
            form.value.photoUrl = "";
            common_vendor.index.hideLoading();
            console.error("上传图片失败:", error);
          }
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        console.error("选择图片失败:", error);
      }
    }
    async function handleSubmit() {
      if (!form.value.name) {
        common_vendor.index.showToast({
          title: "请输入宠物昵称",
          icon: "none"
        });
        return;
      }
      if (!form.value.species) {
        common_vendor.index.showToast({
          title: "请选择物种",
          icon: "none"
        });
        return;
      }
      loading.value = true;
      try {
        if (isEdit.value) {
          await api_pet.petApi.updatePet(petId.value, form.value);
          common_vendor.index.showToast({
            title: "保存成功",
            icon: "success"
          });
        } else {
          await api_pet.petApi.createPet(form.value);
          common_vendor.index.showToast({
            title: "添加成功",
            icon: "success"
          });
        }
        setTimeout(() => {
          common_vendor.index.navigateBack();
        }, 1e3);
      } catch (error) {
        console.error("保存失败:", error);
      } finally {
        loading.value = false;
      }
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: form.value.photoUrl
      }, form.value.photoUrl ? {
        b: form.value.photoUrl
      } : {}, {
        c: common_vendor.o(chooseImage, "4f"),
        d: form.value.name,
        e: common_vendor.o(($event) => form.value.name = $event.detail.value, "64"),
        f: common_vendor.t(form.value.species || "请选择物种"),
        g: speciesList,
        h: common_vendor.o(onSpeciesChange, "70"),
        i: form.value.breed,
        j: common_vendor.o(($event) => form.value.breed = $event.detail.value, "29"),
        k: common_vendor.t(getGenderText(form.value.gender)),
        l: genderList,
        m: common_vendor.o(onGenderChange, "b1"),
        n: common_vendor.t(form.value.birthDate || "请选择日期"),
        o: form.value.birthDate,
        p: common_vendor.o(onDateChange, "8d"),
        q: form.value.weight,
        r: common_vendor.o(common_vendor.m(($event) => form.value.weight = $event.detail.value, {
          number: true
        }), "61"),
        s: form.value.notes,
        t: common_vendor.o(($event) => form.value.notes = $event.detail.value, "22"),
        v: common_vendor.t(isEdit.value ? "保存" : "添加"),
        w: common_vendor.o(handleSubmit, "12")
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-8adf17b9"]]);
wx.createPage(MiniProgramPage);
