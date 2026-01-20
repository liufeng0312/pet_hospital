<template>
  <view class="detail-page">
    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="article" class="content">
      <view class="article-header">
        <text class="title">{{ article.title }}</text>
        <view class="meta">
          <text class="author">作者：{{ article.author || '管理员' }}</text>
          <text class="date">{{ article.publishedAt || article.createdAt }}</text>
          <text class="views">浏览：{{ article.viewCount }}</text>
        </view>
      </view>

      <view v-if="article.coverImage" class="cover-section">
        <image :src="article.coverImage" class="cover" mode="widthFix" />
      </view>

      <view class="article-content">
        <rich-text :nodes="article.content"></rich-text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi, type Article } from '@/api/article'

const loading = ref(true)
const article = ref<Article | null>(null)
const articleId = ref(0)

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  articleId.value = parseInt(currentPage.options.id)
  console.log('文章ID:', articleId.value)
  await loadDetail()
})

async function loadDetail() {
  try {
    loading.value = true
    console.log('正在加载文章详情，ID:', articleId.value)
    const res = await articleApi.getDetail(articleId.value)
    console.log('API响应:', res)
    if (res.code === 200) {
      article.value = res.data
      console.log('文章数据:', article.value)
    } else {
      uni.showToast({
        title: res.msg || '加载失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('加载文章详情失败:', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #fff;
}

.loading {
  text-align: center;
  padding: 100rpx 0;
}

.content {
  padding: 30rpx;
}

.article-header {
  margin-bottom: 30rpx;
}

.title {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #1f2937;
  line-height: 1.4;
  margin-bottom: 24rpx;
}

.meta {
  display: flex;
  gap: 24rpx;
  font-size: 24rpx;
  color: #9ca3af;
}

.cover-section {
  margin-bottom: 30rpx;
}

.cover {
  width: 100%;
  border-radius: 12rpx;
}

.article-content {
  font-size: 30rpx;
  color: #374151;
  line-height: 1.8;
}

/* Rich text styling */
.article-content :deep(p) {
  margin-bottom: 24rpx;
}

.article-content :deep(h1), .article-content :deep(h2), .article-content :deep(h3) {
  font-weight: bold;
  margin: 32rpx 0 24rpx 0;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 12rpx;
  margin: 20rpx 0;
}
</style>
