<template>
  <view class="detail-page">
    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="record" class="content">
      <view class="section">
        <view class="section-title">基本信息</view>
        <view class="info-grid">
          <view class="info-item">
            <text class="label">宠物</text>
            <text class="value">{{ record.pet?.name || '未知宠物' }}</text>
          </view>
          <view class="info-item">
            <text class="label">医生</text>
            <text class="value">{{ record.doctor?.name || '未知医生' }}</text>
          </view>
          <view class="info-item">
            <text class="label">就诊时间</text>
            <text class="value">{{ record.visitTime }}</text>
          </view>
        </view>
      </view>

      <view class="section">
        <view class="section-title">症状描述</view>
        <view class="section-content">
          <text>{{ record.symptoms || '无' }}</text>
        </view>
      </view>

      <view class="section">
        <view class="section-title">诊断结果</view>
        <view class="section-content diagnosis">
          <text>{{ record.diagnosis }}</text>
        </view>
      </view>

      <view class="section">
        <view class="section-title">治疗方案</view>
        <view class="section-content">
          <text>{{ record.treatment || '无' }}</text>
        </view>
      </view>

      <view v-if="record.notes" class="section">
        <view class="section-title">医嘱</view>
        <view class="section-content notes">
          <text>{{ record.notes }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { recordApi, type MedicalRecord } from '@/api/record'

const loading = ref(true)
const record = ref<MedicalRecord | null>(null)
const recordId = ref(0)

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  recordId.value = parseInt(currentPage.options.id)
  await loadDetail()
})

async function loadDetail() {
  try {
    loading.value = true
    const res = await recordApi.getDetail(recordId.value)
    if (res.code === 200) {
      record.value = res.data
    }
  } catch (error) {
    console.error('加载病历详情失败:', error)
  } finally {
    loading.value = false
  }
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

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.label {
  font-size: 24rpx;
  color: #999;
}

.value {
  font-size: 28rpx;
  color: #333;
}

.section-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}

.diagnosis {
  color: #333;
  font-weight: 500;
  background: #eff6ff;
  padding: 20rpx;
  border-radius: 12rpx;
}

.notes {
  background: #fef3c7;
  padding: 20rpx;
  border-radius: 12rpx;
  border-left: 4rpx solid #f59e0b;
}
</style>
