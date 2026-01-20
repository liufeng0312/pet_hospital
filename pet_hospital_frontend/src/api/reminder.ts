import api from './index'

export interface Reminder {
    id?: number
    customerId: number
    petId: number
    type: string // VACCINE, BIRTHDAY
    dueDate: string
    status: number // 0:UNSENT, 1:SENT
    sentTime?: string
    customer?: {
        name: string
        phone: string
    }
    pet?: {
        name: string
    }
}

export const reminderApi = {
    list: (params?: { page?: number; size?: number }) =>
        api.get<any, { code: number; data: any }>('/reminders', { params }),

    generate: () =>
        api.post<any, { code: number; data: null }>('/reminders/generate'),

    send: (id: number) =>
        api.post<any, { code: number; data: null }>(`/reminders/send/${id}`)
}
