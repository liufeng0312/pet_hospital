import api from './index'
import type { PageResult } from './customer'

export interface PrescriptionItem {
    id?: number
    prescriptionId?: number
    drugId: number
    quantity: number
    dosage?: string
    price?: number
    drug?: {
        name?: string
        specification?: string
        unit?: string
    }
}

export interface Prescription {
    id?: number
    medicalRecordId: number
    status?: number // 0:未支付, 1:已支付, 2:已发药
    totalAmount?: number
    createdAt?: string
    items?: PrescriptionItem[]
    medicalRecord?: {
        pet?: {
            name?: string
        }
        doctor?: {
            name?: string
        }
    }
}

export const prescriptionApi = {
    list: (params: {
        page: number;
        size: number;
        medicalRecordId?: number;
        status?: number;
        startDate?: string;
        endDate?: string;
        searchText?: string;
    }) =>
        api.get<any, { code: number; data: PageResult<Prescription> }>('/prescriptions', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: Prescription }>(`/prescriptions/${id}`),

    create: (data: Prescription) =>
        api.post<any, { code: number; data: Prescription }>('/prescriptions', data),

    updateStatus: (id: number, status: number) =>
        api.put<any, { code: number; data: boolean }>(`/prescriptions/${id}/status?status=${status}`)
}
