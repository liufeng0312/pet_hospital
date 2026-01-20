<template>
  <view class="articles-page">
    <view class="category-scroll">
      <scroll-view scroll-x class="category-bar">
        <view
          v-for="cat in categories"
          :key="cat.value"
          class="category-item"
          :class="{ active: currentCategory === cat.value }"
          @click="changeCategory(cat.value)"
        >
          <text>{{ cat.label }}</text>
        </view>
      </scroll-view>
    </view>

    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="articles.length === 0" class="empty">
      <text class="empty-icon">📰</text>
      <text class="empty-text">暂无文章</text>
    </view>

    <view v-else class="articles-list">
      <view
        v-for="article in articles"
        :key="article.id"
        class="article-card"
        @click="viewDetail(article.id)"
      >
        <image v-if="article.coverImage" :src="article.coverImage" class="cover" mode="aspectFill" />
        <view class="article-info">
          <text class="title">{{ article.title }}</text>
          <view v-if="article.summary" class="summary">
            <text>{{ article.summary }}</text>
          </view>
          <view class="meta">
            <text class="author">{{ article.author || '管理员' }}</text>
            <text class="views">👁 {{ article.viewCount || 0 }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi, type Article } from '@/api/article'

const loading = ref(true)
const articles = ref<Article[]>([])
const currentCategory = ref('')

const categories = [
  { label: '全部', value: '' },
  { label: '疾病预防', value: '疾病预防' },
  { label: '日常护理', value: '日常护理' },
  { label: '饮食营养', value: '饮食营养' },
  { label: '行为训练', value: '行为训练' }
]

onMounted(async () => {
  await loadArticles()
})

async function loadArticles() {
  try {
    loading.value = true
    const params: any = { page: 1, size: 20 }
    if (currentCategory.value) {
      params.category = currentCategory.value
    }
    const res = await articleApi.getList(params)
    if (res.code === 200) {
      articles.value = res.data.records
    }
  } catch (error) {
    console.error('加载文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

function changeCategory(category: string) {
  currentCategory.value = category
  loadArticles()
}

function viewDetail(id: number) {
  uni.navigateTo({
    url: `/pages/articles/detail?id=${id}`
  })
}
</script>

<style scoped>
.articles-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.category-scroll {
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.category-bar {
  white-space: nowrap;
  padding: 20rpx 0;
}

.category-item {
  display: inline-block;
  padding: 16rpx 32rpx;
  margin: 0 12rpx;
  font-size: 28rpx;
  color: #666;
  border-radius: 24rpx;
}

.category-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: bold;
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

.articles-list {
  padding: 20rpx;
}

.article-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.cover {
  width: 100%;
  height: 360rpx;
}

.article-info {
  padding: 30rpx;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  display: block;
  margin-bottom: 16rpx;
}

.summary {
  margin-bottom: 20rpx;
}

.summary text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author, .views {
  font-size: 24rpx;
  color: #999;
}
</style>
