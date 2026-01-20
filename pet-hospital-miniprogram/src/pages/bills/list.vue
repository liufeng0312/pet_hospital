<template>
  <view class="bills-page">
    <view class="header">
      <text class="title">我的账单</text>
    </view>

    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="bills.length === 0" class="empty">
      <text class="empty-icon">💰</text>
      <text class="empty-text">暂无账单记录</text>
    </view>

    <view v-else class="bills-list">
      <view
        v-for="bill in bills"
        :key="bill.id"
        class="bill-card"
        @click="viewDetail(bill.id)"
      >
        <view class="bill-header">
          <text class="bill-id">账单 #{{ bill.id }}</text>
          <view class="status-badge" :class="getStatusClass(bill.paymentStatus)">
            {{ getStatusText(bill.paymentStatus) }}
          </view>
        </view>
        <view class="bill-content">
          <view class="amount-row">
            <text class="label">总金额</text>
            <text class="amount">¥{{ bill.totalAmount.toFixed(2) }}</text>
          </view>
          <view v-if="bill.paidAmount > 0" class="amount-row">
            <text class="label">已支付</text>
            <text class="paid">¥{{ bill.paidAmount.toFixed(2) }}</text>
          </view>
          <view class="info-row">
            <text class="label">创建时间</text>
            <text class="value">{{ formatDate(bill.createdAt) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { billApi, type Bill } from '@/api/bill'

const loading = ref(true)
const bills = ref<Bill[]>([])

onMounted(async () => {
  await loadBills()
})

async function loadBills() {
  try {
    loading.value = true
    const res = await billApi.getMyBills({ page: 1, size: 20 })
    if (res.code === 200) {
      bills.value = res.data.records
    }
  } catch (error) {
    console.error('加载账单失败:', error)
  } finally {
    loading.value = false
  }
}

function viewDetail(id: number) {
  uni.navigateTo({
    url: `/pages/bills/detail?id=${id}`
  })
}

function getStatusClass(status: string): string {
  const classes: Record<string, string> = {
    UNPAID: 'status-unpaid',
    PAID: 'status-paid',
    PARTIAL: 'status-partial'
  }
  return classes[status] || ''
}

function getStatusText(status: string): string {
  const texts: Record<string, string> = {
    UNPAID: '未支付',
    PAID: '已支付',
    PARTIAL: '部分支付'
  }
  return texts[status] || status
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return dateStr.split(' ')[0]
}
</script>

<style scoped>
.bills-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.header {
  padding: 30rpx 20rpx;
  background: #fff;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
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

.bills-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.bill-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.bill-id {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.status-badge {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.status-unpaid {
  background: #fee2e2;
  color: #dc2626;
}

.status-paid {
  background: #d1fae5;
  color: #059669;
}

.status-partial {
  background: #fef3c7;
  color: #d97706;
}

.bill-content {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.amount {
  font-size: 36rpx;
  font-weight: bold;
  color: #dc2626;
}

.paid {
  font-size: 28rpx;
  color: #059669;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 24rpx;
}

.label {
  color: #999;
}

.value {
  color: #666;
}
</style>
