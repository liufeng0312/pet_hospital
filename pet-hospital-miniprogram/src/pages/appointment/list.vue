<template>
  <view class="appointments-container">
    <!-- Tab切换 -->
    <view class="tabs">
      <view 
        v-for="tab in tabs" 
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentTab === tab.value }"
        @tap="currentTab = tab.value"
      >
        <text class="tab-text">{{ tab.label }}</text>
      </view>
    </view>
    
    <!-- 预约列表 -->
    <view v-if="filteredAppointments.length > 0" class="appointments-list">
      <view 
        v-for="appointment in filteredAppointments" 
        :key="appointment.id"
        class="appointment-card card"
      >
        <view class="card-header">
          <text class="pet-name">{{ appointment.petName }}</text>
          <view class="status-badge" :class="getStatusClass(appointment.status)">
            {{ getStatusText(appointment.status) }}
          </view>
        </view>
        <view class="card-body">
          <view class="info-row">
            <text class="label">预约时间</text>
            <text class="value">{{ formatDateTime(appointment.appointmentTime) }}</text>
          </view>
          <view class="info-row">
            <text class="label">接诊医生</text>
            <text class="value">{{ appointment.doctorName }}</text>
          </view>
          <view class="info-row" v-if="appointment.queueNumber">
            <text class="label">排队号</text>
            <text class="value">{{ appointment.queueNumber }}号</text>
          </view>
        </view>
        <view class="card-footer" v-if="appointment.status === 'PENDING'">
          <button class="cancel-btn" @tap="cancelAppointment(appointment.id)">取消预约</button>
        </view>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view v-else class="empty-state">
      <text class="empty-icon">📅</text>
      <text class="empty-text">暂无预约记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { appointmentApi, type Appointment } from '@/api/appointment'

const tabs = [
  { label: '待就诊', value: 'PENDING' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

const currentTab = ref('PENDING')
const appointments = ref<Appointment[]>([])

const filteredAppointments = computed(() => {
  if (currentTab.value === 'PENDING') {
    // 待就诊包含: PENDING(待确认), WAITING(候诊中), CONFIRMED(已确认)
    return appointments.value.filter(item => ['PENDING', 'WAITING', 'CONFIRMED'].includes(item.status))
  }
  return appointments.value.filter(item => item.status === currentTab.value)
})

onMounted(() => {
  loadAppointments()
})

async function loadAppointments() {
  try {
    const res = await appointmentApi.getMyAppointments()
    if (res.code === 200) {
      appointments.value = res.data
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
  }
}

function getStatusText(status: string) {
  const map: Record<string, string> = {
    'PENDING': '待确认',
    'WAITING': '待就诊', // 候诊中
    'CONFIRMED': '已预约',
    'IN_PROGRESS': '就诊中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

function getStatusClass(status: string) {
  const map: Record<string, string> = {
    'PENDING': 'status-pending',
    'WAITING': 'status-pending',
    'CONFIRMED': 'status-confirmed',
    'IN_PROGRESS': 'status-confirmed',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  }
  return map[status] || ''
}

function formatDateTime(dateTime: string) {
  const date = new Date(dateTime)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours().toString().padStart(2, '0')
  const minute = date.getMinutes().toString().padStart(2, '0')
  return `${month}月${day}日 ${hour}:${minute}`
}

function cancelAppointment(id: number) {
  uni.showModal({
    title: '提示',
    content: '确定要取消预约吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await appointmentApi.cancelAppointment(id)
          uni.showToast({
            title: '取消成功',
            icon: 'success'
          })
          loadAppointments()
        } catch (error) {
          console.error('取消预约失败:', error)
        }
      }
    }
  })
}
</script>

<style scoped>
.appointments-container {
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.tabs {
  display: flex;
  background: #FFFFFF;
  padding: 16rpx 24rpx;
  gap: 16rpx;
  border-bottom: 1rpx solid var(--border-color);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  border-radius: 8rpx;
}

.tab-item.active {
  background: #EFF6FF; /* #3B82F6 with opacity roughly */
}

.tab-text {
  font-size: 28rpx;
  color: var(--text-color);
  transition: all 0.2s;
}

.tab-item.active .tab-text {
  color: #3B82F6;
  font-weight: 600;
}

.appointments-list {
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.appointment-card {
  padding: 24rpx;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.pet-name {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-color);
}

.status-badge {
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
  font-size: 24rpx;
  font-weight: 600;
}

.status-pending {
  background: #FEF3C7;
  color: #F59E0B;
}

.status-confirmed {
  background: #DBEAFE;
  color: #3B82F6;
}

.status-completed {
  background: #D1FAE5;
  color: #10B981;
}

.status-cancelled {
  background: #F3F4F6;
  color: #6B7280;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.value {
  font-size: 26rpx;
  color: var(--text-color);
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.cancel-btn {
  background: transparent;
  color: var(--error-color);
  border: 1rpx solid var(--error-color);
  padding: 12rpx 32rpx;
  border-radius: 8rpx;
  font-size: 26rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  color: var(--text-secondary);
}
</style>
