<template>
  <view class="records-page">
    <view class="filter-bar">
      <picker mode="selector" :range="pets" range-key="name" @change="onPetChange">
        <view class="filter-btn">
          <text>{{ selectedPet ? selectedPet.name : '全部宠物' }}</text>
          <text class="arrow">▼</text>
        </view>
      </picker>
    </view>

    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="records.length === 0" class="empty">
      <text class="empty-icon">📋</text>
      <text class="empty-text">暂无病历记录</text>
    </view>

    <view v-else class="records-list">
      <view
        v-for="record in records"
        :key="record.id"
        class="record-card"
        @click="viewDetail(record.id)"
      >
        <view class="record-header">
          <text class="pet-name">{{ record.pet?.name || '未知宠物' }}</text>
          <text class="visit-date">{{ formatDate(record.visitTime) }}</text>
        </view>
        <view class="record-content">
          <view class="info-row">
            <text class="label">医生：</text>
            <text class="value">{{ record.doctor?.name || '未知医生' }}</text>
          </view>
          <view class="info-row">
            <text class="label">诊断：</text>
            <text class="value">{{ record.diagnosis }}</text>
          </view>
        </view>
        <view class="record-footer">
          <text class="view-detail">查看详情 →</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { recordApi, type MedicalRecord } from '@/api/record'
import { petApi } from '@/api/pet'

const loading = ref(true)
const records = ref<MedicalRecord[]>([])
const pets = ref<any[]>([])
const selectedPet = ref<any>(null)

onMounted(async () => {
  await loadPets()
  await loadRecords()
})

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

async function loadRecords() {
  try {
    loading.value = true
    const params: any = { page: 1, size: 20 }
    if (selectedPet.value) {
      params.petId = selectedPet.value.id
    }
    const res = await recordApi.getMyRecords(params)
    if (res.code === 200) {
      records.value = res.data.records
    }
  } catch (error) {
    console.error('加载病历失败:', error)
  } finally {
    loading.value = false
  }
}

function onPetChange(e: any) {
  const index = e.detail.value
  selectedPet.value = index >= 0 ? pets.value[index] : null
  loadRecords()
}

function viewDetail(id: number) {
  uni.navigateTo({
    url: `/pages/records/detail?id=${id}`
  })
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return dateStr.split(' ')[0]
}
</script>

<style scoped>
.records-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  background: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.filter-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
}

.arrow {
  font-size: 20rpx;
  color: #999;
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
}

.records-list {
  padding: 0 20rpx;
}

.record-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.pet-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.visit-date {
  font-size: 24rpx;
  color: #999;
}

.record-content {
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  margin-bottom: 12rpx;
  font-size: 26rpx;
}

.label {
  color: #999;
  width: 120rpx;
}

.value {
  flex: 1;
  color: #333;
}

.record-footer {
  text-align: right;
}

.view-detail {
  font-size: 26rpx;
  color: #3b82f6;
}
</style>
