"use strict";
const common_vendor = require("../../common/vendor.js");
const api_pet = require("../../api/pet.js");
const api_appointment = require("../../api/appointment.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "create",
  setup(__props) {
    const currentStep = common_vendor.ref(0);
    const loading = common_vendor.ref(false);
    const steps = ["选择宠物", "选择服务", "选择医生", "选择时间", "确认信息"];
    const pets = common_vendor.ref([]);
    const doctors = common_vendor.ref([]);
    const timeSlots = common_vendor.ref([]);
    const serviceTypes = [
      { label: "常规门诊", value: "APPOINTMENT", icon: "🏥" },
      { label: "疫苗接种", value: "VACCINE", icon: "💉" },
      { label: "体检套餐", value: "CHECKUP", icon: "📋" },
      { label: "手术预约", value: "SURGERY", icon: "⚕️" }
    ];
    const form = common_vendor.ref({
      petId: 0,
      type: "",
      doctorId: 0,
      date: "",
      time: "",
      symptoms: ""
    });
    const availableDates = common_vendor.ref([]);
    common_vendor.onMounted(() => {
      loadPets();
      loadDoctors();
      generateDates();
    });
    function generateDates() {
      const dates = [];
      const weeks = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
      for (let i = 0; i < 5; i++) {
        const d = /* @__PURE__ */ new Date();
        d.setDate(d.getDate() + i);
        const dateStr = d.toISOString().split("T")[0];
        const dayStr = d.getMonth() + 1 + "-" + d.getDate();
        const weekStr = i === 0 ? "今天" : i === 1 ? "明天" : weeks[d.getDay()];
        dates.push({
          date: dateStr,
          week: weekStr,
          day: dayStr
        });
      }
      availableDates.value = dates;
    }
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
    async function loadDoctors() {
      try {
        const res = await api_appointment.appointmentApi.getDoctors();
        if (res.code === 200) {
          doctors.value = res.data;
        }
      } catch (error) {
        console.error("加载医生列表失败:", error);
      }
    }
    async function loadTimeSlots() {
      if (!form.value.doctorId || !form.value.date)
        return;
      try {
        const res = await api_appointment.appointmentApi.getAvailableSlots(form.value.doctorId, form.value.date);
        if (res.code === 200) {
          if (res.data && res.data.length > 0 && typeof res.data[0] === "string") {
            console.warn("检测到旧版后端数据格式，正在自动转换...");
            timeSlots.value = res.data.map((time) => ({
              time,
              available: true,
              remaining: 1
            }));
          } else {
            timeSlots.value = res.data;
          }
          console.log("加载时间段成功:", timeSlots.value);
        }
      } catch (error) {
        console.error("加载时间段失败:", error);
      }
    }
    function selectPet(id) {
      console.log("选择宠物ID:", id);
      form.value.petId = Number(id);
    }
    function selectService(type) {
      form.value.type = type;
    }
    function selectDoctor(id) {
      form.value.doctorId = id;
    }
    function selectDate(date) {
      form.value.date = date;
      form.value.time = "";
      loadTimeSlots();
    }
    function selectTime(slot) {
      if (!slot.available)
        return;
      form.value.time = slot.time;
    }
    function getSelectedPetName() {
      const pet = pets.value.find((p) => p.id === form.value.petId);
      return pet ? pet.name : "";
    }
    function getSelectedServiceName() {
      const service = serviceTypes.find((s) => s.value === form.value.type);
      return service ? service.label : "";
    }
    function getSelectedDoctorName() {
      const doctor = doctors.value.find((d) => d.id === form.value.doctorId);
      return doctor ? doctor.name : "";
    }
    function prevStep() {
      if (currentStep.value > 0) {
        currentStep.value--;
      }
    }
    async function nextStep() {
      if (currentStep.value === 0 && !form.value.petId) {
        common_vendor.index.showToast({ title: "请选择宠物", icon: "none" });
        return;
      }
      if (currentStep.value === 1 && !form.value.type) {
        common_vendor.index.showToast({ title: "请选择服务类型", icon: "none" });
        return;
      }
      if (currentStep.value === 2 && !form.value.doctorId) {
        common_vendor.index.showToast({ title: "请选择医生", icon: "none" });
        return;
      }
      if (currentStep.value === 3 && (!form.value.date || !form.value.time)) {
        common_vendor.index.showToast({ title: "请选择预约时间", icon: "none" });
        return;
      }
      if (currentStep.value === 4) {
        await submitAppointment();
        return;
      }
      currentStep.value++;
    }
    async function submitAppointment() {
      loading.value = true;
      try {
        const appointmentTime = `${form.value.date} ${form.value.time}:00`;
        await api_appointment.appointmentApi.createAppointment({
          petId: form.value.petId,
          doctorId: form.value.doctorId,
          appointmentTime,
          type: form.value.type,
          symptoms: form.value.symptoms
        });
        common_vendor.index.showToast({
          title: "预约成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.switchTab({
            url: "/pages/appointment/list"
          });
        }, 1e3);
      } catch (error) {
        console.error("提交预约失败:", error);
      } finally {
        loading.value = false;
      }
    }
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(steps, (step, index, i0) => {
          return {
            a: common_vendor.t(index + 1),
            b: common_vendor.t(step),
            c: index,
            d: currentStep.value === index ? 1 : "",
            e: currentStep.value > index ? 1 : ""
          };
        }),
        b: currentStep.value === 0
      }, currentStep.value === 0 ? {
        c: common_vendor.f(pets.value, (pet, k0, i0) => {
          return {
            a: pet.photoUrl || "/static/images/default-pet.png",
            b: common_vendor.t(pet.name),
            c: pet.id,
            d: form.value.petId === pet.id ? 1 : "",
            e: common_vendor.o(($event) => selectPet(pet.id), pet.id)
          };
        })
      } : {}, {
        d: currentStep.value === 1
      }, currentStep.value === 1 ? {
        e: common_vendor.f(serviceTypes, (service, k0, i0) => {
          return {
            a: common_vendor.t(service.icon),
            b: common_vendor.t(service.label),
            c: service.value,
            d: form.value.type === service.value ? 1 : "",
            e: common_vendor.o(($event) => selectService(service.value), service.value)
          };
        })
      } : {}, {
        f: currentStep.value === 2
      }, currentStep.value === 2 ? {
        g: common_vendor.f(doctors.value, (doctor, k0, i0) => {
          return {
            a: common_vendor.t(doctor.name),
            b: common_vendor.t(doctor.role),
            c: doctor.id,
            d: form.value.doctorId === doctor.id ? 1 : "",
            e: common_vendor.o(($event) => selectDoctor(doctor.id), doctor.id)
          };
        })
      } : {}, {
        h: currentStep.value === 3
      }, currentStep.value === 3 ? common_vendor.e({
        i: common_vendor.f(availableDates.value, (day, k0, i0) => {
          return {
            a: common_vendor.t(day.week),
            b: common_vendor.t(day.day),
            c: day.date,
            d: form.value.date === day.date ? 1 : "",
            e: common_vendor.o(($event) => selectDate(day.date), day.date)
          };
        }),
        j: form.value.date
      }, form.value.date ? {
        k: common_vendor.f(timeSlots.value, (slot, k0, i0) => {
          return {
            a: common_vendor.t(slot.time),
            b: slot.time,
            c: form.value.time === slot.time ? 1 : "",
            d: !slot.available ? 1 : "",
            e: common_vendor.o(($event) => selectTime(slot), slot.time)
          };
        })
      } : {}) : {}, {
        l: currentStep.value === 4
      }, currentStep.value === 4 ? {
        m: form.value.symptoms,
        n: common_vendor.o(($event) => form.value.symptoms = $event.detail.value, "3e"),
        o: common_vendor.t(getSelectedPetName()),
        p: common_vendor.t(getSelectedServiceName()),
        q: common_vendor.t(getSelectedDoctorName()),
        r: common_vendor.t(form.value.date),
        s: common_vendor.t(form.value.time)
      } : {}, {
        t: currentStep.value > 0
      }, currentStep.value > 0 ? {
        v: common_vendor.o(prevStep, "55")
      } : {}, {
        w: common_vendor.t(currentStep.value === 4 ? "提交预约" : "下一步"),
        x: common_vendor.o(nextStep, "26"),
        y: loading.value
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-27b7818a"]]);
wx.createPage(MiniProgramPage);
