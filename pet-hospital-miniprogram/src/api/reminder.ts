import request from './request'

export interface Reminder {
    id: number
    customerId: number
    petId: number
    type: string
    content: string
    dueDate: string
    status: number
}

export const reminderApi = {
    // 获取我的提醒列表
    getMyReminders: () =>
        request.get<Reminder[]>('/api/reminders/my-reminders'),

    // 标记提醒已读
    markAsRead: (id: number) =>
        request.put(`/api/reminders/${id}/read`)
}
