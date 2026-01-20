import api from './index'

export interface Article {
    id?: number
    title: string
    category: string
    coverImage: string
    summary: string
    content: string
    author: string
    viewCount?: number
    isPublished: number
    publishedAt?: string
    createdAt?: string
    updatedAt?: string
}

export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
}

export const articleApi = {
    // 管理后台列表（支持搜索和所有状态）
    adminList: (params: { keyword?: string; category?: string; page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<Article> }>('/articles/admin/list', { params }),

    // 小程序端/公开列表（仅已发布）
    list: (params: { category?: string; page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<Article> }>('/articles/list', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: Article }>(`/articles/${id}`),

    create: (data: Article) =>
        api.post<any, { code: number; data: Article }>('/articles', data),

    update: (id: number, data: Article) =>
        api.put<any, { code: number; data: boolean }>(`/articles/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/articles/${id}`)
}
