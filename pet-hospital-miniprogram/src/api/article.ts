import request from './request'

export interface Article {
    id?: number
    title: string
    category?: string
    coverImage?: string
    summary?: string
    content: string
    author?: string
    viewCount?: number
    isPublished?: number
    publishedAt?: string
    createdAt?: string
    updatedAt?: string
}

export const articleApi = {
    // 获取文章列表
    getList: (params?: { category?: string; page?: number; size?: number }) => {
        return request.get('/api/articles/list', params)
    },

    // 获取文章详情
    getDetail: (id: number) => {
        return request.get(`/api/articles/${id}`)
    }
}
