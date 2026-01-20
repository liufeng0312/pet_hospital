<template>
  <view class="detail-page">
    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="consultation" class="content">
      <!-- 咨询信息 -->
      <view class="section">
        <view class="section-header">
          <text class="section-title">我的咨询</text>
          <view class="status-tag" :class="getStatusClass(consultation.status)">
            {{ getStatusText(consultation.status) }}
          </view>
        </view>
        <view class="info-item">
          <text class="label">标题</text>
          <text class="value">{{ consultation.title }}</text>
        </view>
        <view class="info-item">
          <text class="label">内容</text>
          <text class="value">{{ consultation.content }}</text>
        </view>
        <view v-if="consultation.pet" class="info-item">
          <text class="label">宠物</text>
          <text class="value">{{ consultation.pet.name }} ({{ consultation.pet.species }})</text>
        </view>
        <view class="info-item">
          <text class="label">提交时间</text>
          <text class="value">{{ consultation.createdAt }}</text>
        </view>
      </view>

      <!-- 回复信息 -->
      <view v-if="consultation.replyContent" class="section reply-section">
        <view class="section-title">医生回复</view>
        <view class="reply-box">
          <view class="doctor-info">
            <text class="doctor-name">{{ consultation.doctor?.name || '医生' }}</text>
            <text class="reply-time">{{ consultation.replyTime }}</text>
          </view>
          <view class="reply-content">
            <text>{{ consultation.replyContent }}</text>
          </view>
        </view>
      </view>

      <!-- 等待回复提示 -->
      <view v-else class="section waiting-section">
        <text class="waiting-icon">⏰</text>
        <text class="waiting-text">医生正在为您解答，请耐心等待</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { consultationApi, type Consultation } from '@/api/consultation'

const loading = ref(true)
const consultation = ref<Consultation | null>(null)
const consultationId = ref(0)

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  consultationId.value = parseInt(currentPage.options.id)
  await loadDetail()
})

async function loadDetail() {
  try {
    loading.value = true
    const res = await consultationApi.getDetail(consultationId.value)
    if (res.code === 200) {
      consultation.value = res.data
    }
  } catch (error) {
    console.error('加载咨询详情失败:', error)
  } finally {
    loading.value = false
  }
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
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.loading {
  text-align: center;
  padding: 100rpx 0;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.section {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
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

.info-item {
  margin-bottom: 24rpx;
}

.label {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.value {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
}

.reply-section {
  background: linear-gradient(135deg, #e0f2fe 0%, #dbeafe 100%);
}

.reply-box {
  margin-top: 20rpx;
}

.doctor-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.doctor-name {
  font-size: 28rpx;
  color: #3b82f6;
  font-weight: bold;
}

.reply-time {
  font-size: 24rpx;
  color: #6b7280;
}

.reply-content {
  background: #fff;
  padding: 24rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 1.8;
}

.waiting-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0;
}

.waiting-icon {
  font-size: 100rpx;
  margin-bottom: 24rpx;
}

.waiting-text {
  font-size: 28rpx;
  color: #9ca3af;
}
</style>
