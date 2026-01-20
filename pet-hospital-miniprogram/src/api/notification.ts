import request from './request'

export interface Notification {
    id?: number
    customerId?: number
    type: string
    title: string
    content: string
    relatedType?: string
    relatedId?: number
    isRead?: number
    readAt?: string
    createdAt?: string
}

export const notificationApi = {
    // 获取我的通知列表
    getMyNotifications: (params?: { page?: number; size?: number }) => {
        return request.get('/api/notifications/my-notifications', params)
    },

    // 标记已读
    markAsRead: (id: number) => {
        return request.put(`/api/notifications/${id}/read`)
    },

    // 全部标记已读
    markAllAsRead: () => {
        return request.put('/api/notifications/read-all')
    },

    // 获取未读数量
    getUnreadCount: () => {
        return request.get('/api/notifications/unread-count')
    }
}
