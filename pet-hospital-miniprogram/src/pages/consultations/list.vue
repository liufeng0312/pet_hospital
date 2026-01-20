<template>
  <view class="consultations-page">
    <view class="status-tabs">
      <view 
        v-for="tab in tabs" 
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentStatus === tab.value }"
        @click="changeStatus(tab.value)"
      >
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="consultations.length === 0" class="empty">
      <text class="empty-icon">💬</text>
      <text class="empty-text">暂无咨询记录</text>
    </view>

    <view v-else class="consultations-list">
      <view
        v-for="item in consultations"
        :key="item.id"
        class="consultation-card"
        @click="viewDetail(item.id)"
      >
        <view class="card-header">
          <text class="title">{{ item.title }}</text>
          <view class="status-tag" :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </view>
        </view>
        <view class="card-content">
          <text class="content">{{ item.content }}</text>
        </view>
        <view class="card-footer">
          <text class="time">{{ item.createdAt }}</text>
          <text v-if="item.pet" class="pet">{{ item.pet.name }}</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-btn">
      <button class="consult-btn" @click="goToCreate">
        <text>✨ 在线咨询</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { consultationApi, type Consultation } from '@/api/consultation'

const loading = ref(true)
const consultations = ref<Consultation[]>([])
const currentStatus = ref('')

const tabs = [
  { label: '全部', value: '' },
  { label: '待回复', value: 'PENDING' },
  { label: '已回复', value: 'ANSWERED' }
]

onMounted(async () => {
  await loadConsultations()
})

async function loadConsultations() {
  try {
    loading.value = true
    const params: any = { page: 1, size: 20 }
    if (currentStatus.value) {
      params.status = currentStatus.value
    }
    const res = await consultationApi.getMyConsultations(params)
    if (res.code === 200) {
      consultations.value = res.data.records
    }
  } catch (error) {
    console.error('加载咨询列表失败:', error)
  } finally {
    loading.value = false
  }
}

function changeStatus(status: string) {
  currentStatus.value = status
  loadConsultations()
}

function getStatusClass(status: string) {
  const map: any = {
    PENDING: 'status-pending',
    ANSWERED: 'status-answered',
    CLOSED: 'status-closed'
  }
  return map[status] || ''
}

function getStatusText(status: string) {
  const map: any = {
    PENDING: '待回复',
    ANSWERED: '已回复',
    CLOSED: '已关闭'
  }
  return map[status] || status
}

function viewDetail(id: number) {
  uni.navigateTo({
    url: `/pages/consultations/detail?id=${id}`
  })
}

function goToCreate() {
  uni.navigateTo({
    url: '/pages/consultations/create'
  })
}
</script>

<style scoped>
.consultations-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.status-tabs {
  display: flex;
  background: #fff;
  padding: 20rpx 0;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #3b82f6;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background: #3b82f6;
  border-radius: 2rpx;
}

.loading, .empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.empty-btn {
  background: #3b82f6;
  color: #fff;
  border: none;
  padding: 24rpx 48rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
}

.consultations-list {
  padding: 20rpx;
}

.consultation-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.title {
  flex: 1;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-right: 20rpx;
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-pending {
  background: #fef3c7;
  color: #f59e0b;
}

.status-answered {
  background: #d1fae5;
  color: #10b981;
}

.status-closed {
  background: #f3f4f6;
  color: #9ca3af;
}

.card-content {
  margin-bottom: 20rpx;
}

.content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time {
  font-size: 24rpx;
  color: #999;
}

.pet {
  font-size: 24rpx;
  color: #3b82f6;
}

.bottom-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
}

.consult-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  padding: 28rpx;
  border-radius: 24rpx;
  font-size: 32rpx;
  font-weight: bold;
}
</style>
