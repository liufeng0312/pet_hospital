import api from './index'

export interface Employee {
    id?: number
    name: string
    username: string
    role: string // DOCTOR, RECEPTIONIST, ADMIN
    phone: string
    bio?: string
    avatar?: string
    status?: number // 1: Active, 0: Inactive
    createdAt?: string
}

export const employeeApi = {
    list: (params?: { page?: number; size?: number }) =>
        api.get<any, { code: number; data: any }>('/employees', { params }),

    getDoctors: () =>
        api.get<any, { code: number; data: Employee[] }>('/employees/doctors'),

    getById: (id: number) =>
        api.get<any, { code: number; data: Employee }>(`/employees/${id}`),

    create: (data: Employee) =>
        api.post<any, { code: number; data: boolean }>('/employees', data),

    update: (id: number, data: Employee) =>
        api.put<any, { code: number; data: boolean }>(`/employees/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/employees/${id}`)
}
