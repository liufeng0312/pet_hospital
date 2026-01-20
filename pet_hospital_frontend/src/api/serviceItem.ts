import api from './index'
import type { PageResult } from './customer'

export interface ServiceItem {
    id?: number
    name: string
    category: string // VACCINE, SURGERY, EXAM, LAB
    price: number
    description?: string
    isActive?: number
}

export const serviceItemApi = {
    list: (params: { page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<ServiceItem> }>('/services', { params }),

    listActive: () =>
        api.get<any, { code: number; data: ServiceItem[] }>('/services/active'),

    create: (data: ServiceItem) =>
        api.post<any, { code: number; data: boolean }>('/services', data),

    update: (id: number, data: ServiceItem) =>
        api.put<any, { code: number; data: boolean }>(`/services/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/services/${id}`)
}
