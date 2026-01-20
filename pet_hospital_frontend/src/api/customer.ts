import api from './index'

export interface Customer {
    id?: number
    name: string
    phone: string
    address?: string
    level?: number
    notes?: string
    createdAt?: string
    updatedAt?: string
}

export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
}

export const customerApi = {
    list: (params: { keyword?: string; level?: number; page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<Customer> }>('/customers', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: Customer }>(`/customers/${id}`),

    create: (data: Customer) =>
        api.post<any, { code: number; data: boolean }>('/customers', data),

    update: (id: number, data: Customer) =>
        api.put<any, { code: number; data: boolean }>(`/customers/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/customers/${id}`)
}
