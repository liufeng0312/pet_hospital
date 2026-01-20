import api from './index'
import type { PageResult } from './customer'

export interface Registration {
    id?: number
    customerId: number
    petId: number
    doctorId?: number
    status?: 'PENDING' | 'WAITING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
    type?: 'APPOINTMENT' | 'ONSITE'
    appointmentTime?: string
    queueNumber?: number
    createdAt?: string
    updatedAt?: string
    // 关联字段
    customer?: {
        name?: string
        phone?: string
    }
    pet?: {
        name?: string
        species?: string
    }
    doctor?: {
        name?: string
    }
}

export const registrationApi = {
    list: (params: { status?: string; date?: string; page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<Registration> }>('/registrations', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: Registration }>(`/registrations/${id}`),

    create: (data: Registration) =>
        api.post<any, { code: number; data: Registration }>('/registrations', data),

    update: (id: number, data: Partial<Registration>) =>
        api.put<any, { code: number; data: boolean }>(`/registrations/${id}`, data),

    cancel: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/registrations/${id}`),

    getQueueBoard: () =>
        api.get<any, { code: number; data: Registration[] }>('/registrations/queue'),

    callNext: (id: number) =>
        api.put<any, { code: number; data: boolean }>(`/registrations/${id}/call`),

    complete: (id: number) =>
        api.put<any, { code: number; data: boolean }>(`/registrations/${id}/complete`)
}
