import api from './index'
import type { PageResult } from './customer'

export interface Drug {
    id?: number
    name: string
    code: string
    specification?: string
    manufacturer?: string
    price: number
    stockQuantity?: number
    warningThreshold?: number
    unit?: string
    isActive?: number
}

export const drugApi = {
    list: (params: { page: number; size: number; keyword?: string; status?: number }) => {
        return api.get<any, { code: number; data: PageResult<Drug> }>('/drugs', { params })
    },

    listActive: () =>
        api.get<any, { code: number; data: Drug[] }>('/drugs/active'),

    getLowStock: () =>
        api.get<any, { code: number; data: Drug[] }>('/drugs/low-stock'),

    getById: (id: number) =>
        api.get<any, { code: number; data: Drug }>(`/drugs/${id}`),

    create: (data: Drug) =>
        api.post<any, { code: number; data: boolean }>('/drugs', data),

    update: (id: number, data: Drug) =>
        api.put<any, { code: number; data: boolean }>(`/drugs/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/drugs/${id}`)
}
