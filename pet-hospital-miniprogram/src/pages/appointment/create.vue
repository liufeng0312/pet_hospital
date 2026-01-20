<template>
  <view class="create-container">
    <view class="steps-indicator">
      <view 
        v-for="(step, index) in steps" 
        :key="index"
        class="step-item"
        :class="{ active: currentStep === index, completed: currentStep > index }"
      >
        <view class="step-number">{{ index + 1 }}</view>
        <text class="step-text">{{ step }}</text>
      </view>
    </view>
    
    <!-- 步骤1: 选择宠物 -->
    <view v-if="currentStep === 0" class="step-content">
      <text class="step-title">选择宠物</text>
      <view class="pets-grid">
        <view 
          v-for="pet in pets" 
          :key="pet.id"
          class="pet-option"
          :class="{ selected: form.petId === pet.id }"
          @tap="selectPet(pet.id)"
        >
          <image 
            class="pet-avatar-small" 
            :src="pet.photoUrl || '/static/images/default-pet.png'" 
            mode="aspectFill"
          ></image>
          <text class="pet-name-small">{{ pet.name }}</text>
        </view>
      </view>
    </view>
    
    <!-- 步骤2: 选择服务类型 -->
    <view v-if="currentStep === 1" class="step-content">
      <text class="step-title">选择服务类型</text>
      <view class="service-list">
        <view 
          v-for="service in serviceTypes" 
          :key="service.value"
          class="service-option"
          :class="{ selected: form.type === service.value }"
          @tap="selectService(service.value)"
        >
          <text class="service-icon">{{ service.icon }}</text>
          <text class="service-name">{{ service.label }}</text>
        </view>
      </view>
    </view>
    
    <!-- 步骤3: 选择医生 -->
    <view v-if="currentStep === 2" class="step-content">
      <text class="step-title">选择医生</text>
      <view class="doctor-list">
        <view 
          v-for="doctor in doctors" 
          :key="doctor.id"
          class="doctor-option card"
          :class="{ selected: form.doctorId === doctor.id }"
          @tap="selectDoctor(doctor.id)"
        >
          <text class="doctor-name">{{ doctor.name }}</text>
          <text class="doctor-role">{{ doctor.role }}</text>
        </view>
      </view>
    </view>
    
    <!-- 步骤4: 选择时间 -->
    <view v-if="currentStep === 3" class="step-content">
      <text class="step-title">选择预约日期</text>
      
      <!-- 日期选择横向滚动 -->
      <scroll-view scroll-x class="date-scroll" show-scrollbar="false">
        <view class="date-list">
          <view 
            v-for="day in availableDates" 
            :key="day.date"
            class="date-card"
            :class="{ selected: form.date === day.date }"
            @tap="selectDate(day.date)"
          >
            <text class="date-week">{{ day.week }}</text>
            <text class="date-day">{{ day.day }}</text>
          </view>
        </view>
      </scroll-view>
      
      <view class="time-slots" v-if="form.date">
        <text class="slot-label">可预约时间段</text>
        <view class="slots-grid">
          <view 
            v-for="slot in timeSlots" 
            :key="slot.time"
            class="time-slot"
            :class="{ 
              selected: form.time === slot.time,
              disabled: !slot.available 
            }"
            @tap="selectTime(slot)"
          >
            {{ slot.time }}
          </view>
        </view>
      </view>
    </view>
    
    <!-- 步骤5: 填写主诉 -->
    <view v-if="currentStep === 4" class="step-content">
      <text class="step-title">填写主诉症状（可选）</text>
      <textarea 
        class="symptoms-input" 
        v-model="form.symptoms" 
        placeholder="请简要描述宠物的症状"
        maxlength="200"
      ></textarea>
      
      <view class="summary-section">
        <text class="summary-title">预约信息确认</text>
        <view class="summary-item">
          <text class="summary-label">宠物</text>
          <text class="summary-value">{{ getSelectedPetName() }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">服务</text>
          <text class="summary-value">{{ getSelectedServiceName() }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">医生</text>
          <text class="summary-value">{{ getSelectedDoctorName() }}</text>
        </view>
        <view class="summary-item">
          <text class="summary-label">时间</text>
          <text class="summary-value">{{ form.date }} {{ form.time }}</text>
        </view>
      </view>
    </view>
    
    <!-- 底部按钮 -->
    <view class="bottom-actions">
      <button 
        v-if="currentStep > 0" 
        class="btn-secondary" 
        @tap="prevStep"
      >
        上一步
      </button>
      <button 
        class="btn-primary" 
        @tap="nextStep"
        :loading="loading"
      >
        {{ currentStep === 4 ? '提交预约' : '下一步' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { petApi, type Pet } from '@/api/pet'
import { appointmentApi, type Doctor, type TimeSlot } from '@/api/appointment'

const currentStep = ref(0)
const loading = ref(false)
const steps = ['选择宠物', '选择服务', '选择医生', '选择时间', '确认信息']

const pets = ref<Pet[]>([])
const doctors = ref<Doctor[]>([])
const timeSlots = ref<TimeSlot[]>([])

const serviceTypes = [
  { label: '常规门诊', value: 'APPOINTMENT', icon: '🏥' },
  { label: '疫苗接种', value: 'VACCINE', icon: '💉' },
  { label: '体检套餐', value: 'CHECKUP', icon: '📋' },
  { label: '手术预约', value: 'SURGERY', icon: '⚕️' }
]

const form = ref({
  petId: 0,
  type: '',
  doctorId: 0,
  date: '',
  time: '',
  symptoms: ''
})

interface DateItem {
  date: string
  week: string
  day: string
}

const availableDates = ref<DateItem[]>([])

onMounted(() => {
  loadPets()
  loadDoctors()
  generateDates()
})

function generateDates() {
  const dates: DateItem[] = []
  const weeks = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  
  for (let i = 0; i < 5; i++) {
    const d = new Date()
    d.setDate(d.getDate() + i)
    
    const dateStr = d.toISOString().split('T')[0]
    const dayStr = (d.getMonth() + 1) + '-' + d.getDate()
    const weekStr = i === 0 ? '今天' : (i === 1 ? '明天' : weeks[d.getDay()])
    
    dates.push({
      date: dateStr,
      week: weekStr,
      day: dayStr
    })
  }
  availableDates.value = dates
}

async function loadPets() {
  try {
    const res = await petApi.getMyPets()
    if (res.code === 200) {
      pets.value = res.data
    }
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
}

async function loadDoctors() {
  try {
    const res = await appointmentApi.getDoctors()
    if (res.code === 200) {
      doctors.value = res.data
    }
  } catch (error) {
    console.error('加载医生列表失败:', error)
  }
}

async function loadTimeSlots() {
  if (!form.value.doctorId || !form.value.date) return
  
  try {
    const res = await appointmentApi.getAvailableSlots(form.value.doctorId, form.value.date)
    if (res.code === 200) {
      // 兼容处理：如果后端返回的是字符串数组（旧版后端未重启），转换对象
      if (res.data && res.data.length > 0 && typeof res.data[0] === 'string') {
        console.warn('检测到旧版后端数据格式，正在自动转换...')
        timeSlots.value = (res.data as any[]).map(time => ({
          time: time,
          available: true,
          remaining: 1
        }))
      } else {
        timeSlots.value = res.data
      }
      console.log('加载时间段成功:', timeSlots.value)
    }
  } catch (error) {
    console.error('加载时间段失败:', error)
  }
}

function selectPet(id: number | string) {
  console.log('选择宠物ID:', id)
  form.value.petId = Number(id)
}

function selectService(type: string) {
  form.value.type = type
}

function selectDoctor(id: number) {
  form.value.doctorId = id
}

function selectDate(date: string) {
  form.value.date = date
  form.value.time = ''
  loadTimeSlots()
}

function selectTime(slot: TimeSlot) {
  if (!slot.available) return
  form.value.time = slot.time
}

function getSelectedPetName() {
  const pet = pets.value.find(p => p.id === form.value.petId)
  return pet ? pet.name : ''
}

function getSelectedServiceName() {
  const service = serviceTypes.find(s => s.value === form.value.type)
  return service ? service.label : ''
}

function getSelectedDoctorName() {
  const doctor = doctors.value.find(d => d.id === form.value.doctorId)
  return doctor ? doctor.name : ''
}

function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

async function nextStep() {
  if (currentStep.value === 0 && !form.value.petId) {
    uni.showToast({ title: '请选择宠物', icon: 'none' })
    return
  }
  
  if (currentStep.value === 1 && !form.value.type) {
    uni.showToast({ title: '请选择服务类型', icon: 'none' })
    return
  }
  
  if (currentStep.value === 2 && !form.value.doctorId) {
    uni.showToast({ title: '请选择医生', icon: 'none' })
    return
  }
  
  if (currentStep.value === 3 && (!form.value.date || !form.value.time)) {
    uni.showToast({ title: '请选择预约时间', icon: 'none' })
    return
  }
  
  if (currentStep.value === 4) {
    await submitAppointment()
    return
  }
  
  currentStep.value++
}

async function submitAppointment() {
  loading.value = true
  
  try {
    const appointmentTime = `${form.value.date} ${form.value.time}:00`
    
    await appointmentApi.createAppointment({
      petId: form.value.petId,
      doctorId: form.value.doctorId,
      appointmentTime,
      type: form.value.type,
      symptoms: form.value.symptoms
    })
    
    uni.showToast({
      title: '预约成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/appointment/list'
      })
    }, 1000)
  } catch (error) {
    console.error('提交预约失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-container {
  min-height: 100vh;
  padding-bottom: 160rpx;
}

.steps-indicator {
  display: flex;
  justify-content: space-between;
  padding: 32rpx 24rpx;
  background: #FFFFFF;
  border-bottom: 1rpx solid var(--border-color);
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  flex: 1;
}

.step-number {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #F1F5F9;
  color: #64748B;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 600;
  margin-bottom: 8rpx;
}

.step-item.active .step-number {
  background: #3B82F6;
  color: #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.3);
  transform: scale(1.1);
  transition: all 0.2s;
}

.step-item.completed .step-number {
  background: #DBEAFE; /* Light blue */
  color: #3B82F6;
}

.step-text {
  font-size: 24rpx;
  color: #94A3B8;
  font-weight: 500;
}

.step-item.active .step-text {
  color: #3B82F6;
  font-weight: 700;
}

.step-item.completed .step-text {
  color: #3B82F6;
  font-weight: 500;
}

.step-content {
  padding: 32rpx 24rpx;
}

.step-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 24rpx;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
}

.pet-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx;
  background: #FFFFFF;
  border-radius: 20rpx;
  border: 2rpx solid var(--border-color);
  transition: all 0.2s;
}

.pet-option.selected {
  border-color: #3B82F6;
  background: #EFF6FF;
  box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.2);
}

.pet-avatar-small {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
}

.pet-name-small {
  font-size: 24rpx;
  color: var(--text-color);
}

.service-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}

.service-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 32rpx 20rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  border: 2rpx solid var(--border-color);
}

.service-option.selected {
  border-color: #3B82F6;
  background: rgba(59, 130, 246, 0.05);
}

.service-icon {
  font-size: 48rpx;
  margin-bottom: 8rpx;
}

.service-name {
  font-size: 26rpx;
  color: var(--text-color);
  font-weight: 500;
}

.doctor-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.doctor-option {
  padding: 24rpx;
  border: 2rpx solid var(--border-color);
}

.doctor-option.selected {
  border-color: #3B82F6;
  background: rgba(59, 130, 246, 0.05);
}

.doctor-name {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 8rpx;
}

.doctor-role {
  display: block;
  font-size: 24rpx;
  color: var(--text-secondary);
}

.date-scroll {
  white-space: nowrap;
  margin-bottom: 32rpx;
}

.date-list {
  display: flex;
  gap: 16rpx;
}

.date-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 140rpx;
  height: 120rpx;
  background: #FFFFFF;
  border-radius: 16rpx;
  border: 2rpx solid var(--border-color, #E2E8F0);
  padding: 16rpx;
  flex-shrink: 0;
}

.date-card.selected {
  background: #3B82F6;
  border-color: #3B82F6;
  box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.3);
}

.date-week {
  font-size: 24rpx;
  color: #64748B;
  margin-bottom: 8rpx;
}

.date-card.selected .date-week {
  color: #BFDBFE;
}

.date-day {
  font-size: 30rpx;
  font-weight: 600;
  color: #1E293B;
}

.date-card.selected .date-day {
  color: #FFFFFF;
}

.time-slots {
  margin-top: 24rpx;
}

.slot-label {
  display: block;
  font-size: 26rpx;
  color: var(--text-secondary);
  margin-bottom: 16rpx;
}

.slots-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12rpx;
}

.time-slot {
  padding: 20rpx;
  background: #FFFFFF;
  border-radius: 12rpx;
  border: 1rpx solid var(--border-color, #E2E8F0);
  text-align: center;
  font-size: 26rpx;
  color: var(--text-color, #1E293B);
}

.time-slot.selected {
  border-color: #3B82F6;
  background: #3B82F6;
  color: #FFFFFF;
}

.time-slot.disabled {
  background: #F1F5F9;
  color: #CBD5E1;
}

.time-slot.disabled {
  background: #F1F5F9;
  color: #CBD5E1;
}

.symptoms-input {
  width: 100%;
  min-height: 200rpx;
  padding: 20rpx;
  background: #FFFFFF;
  border-radius: 12rpx;
  border: 1rpx solid var(--border-color);
  font-size: 28rpx;
  margin-bottom: 32rpx;
}

.summary-section {
  background: #FFFFFF;
  padding: 24rpx;
  border-radius: 16rpx;
}

.summary-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 20rpx;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid var(--border-color);
}

.summary-item:last-child {
  border-bottom: none;
}

.summary-label {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.summary-value {
  font-size: 26rpx;
  color: var(--text-color);
  font-weight: 500;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx 32rpx;
  background: #FFFFFF;
  border-top: 1rpx solid #E2E8F0;
  display: flex;
  gap: 24rpx;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.btn-primary,
.btn-secondary {
  flex: 1;
  height: 96rpx;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-primary {
  background: #3B82F6;
  color: #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.3);
}

.btn-secondary {
  background: #F8FAFC;
  color: #64748B;
  border: 2rpx solid #E2E8F0;
}

.btn-secondary:active {
  background: #F1F5F9;
}
</style>
