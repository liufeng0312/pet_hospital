import request from './index'

export interface VaccineInventory {
    id?: number
    vaccineName: string
    vaccineType?: string
    manufacturer?: string
    batchNumber: string
    quantity: number
    minQuantity?: number
    unitPrice?: number
    productionDate?: string
    expiryDate?: string
    storageLocation?: string
    notes?: string
    createdAt?: string
}

export interface VaccineStockLog {
    id?: number
    inventoryId: number
    changeType: string
    quantity: number
    relatedRecordId?: number
    operatorId?: number
    reason?: string
    createdAt?: string
    inventory?: {
        vaccineName?: string
        batchNumber?: string
    }
    operator?: {
        name?: string
    }
}

export const vaccineInventoryApi = {
    list: (page: number = 1, size: number = 10, searchParams?: {
        vaccineName?: string
        vaccineType?: string
        batchNumber?: string
    }) =>
        request.get<{
            records: VaccineInventory[]
            total: number
            size: number
            current: number
        }>('/vaccine-inventory', { params: { page, size, ...searchParams } }),

    getAlerts: () =>
        request.get<{
            lowStock: VaccineInventory[]
            expiring: VaccineInventory[]
            lowStockCount: number
            expiringCount: number
        }>('/vaccine-inventory/alerts'),

    getById: (id: number) =>
        request.get<VaccineInventory>(`/vaccine-inventory/${id}`),

    create: (data: VaccineInventory) =>
        request.post<VaccineInventory>('/vaccine-inventory', data),

    update: (id: number, data: VaccineInventory) =>
        request.put<boolean>(`/vaccine-inventory/${id}`, data),

    delete: (id: number) =>
        request.delete<boolean>(`/vaccine-inventory/${id}`),

    adjustStock: (id: number, quantity: number, reason: string, operatorId?: number) =>
        request.post<void>(`/vaccine-inventory/${id}/adjust`, { quantity, reason, operatorId }),

    getLogs: (id: number) =>
        request.get<VaccineStockLog[]>(`/vaccine-inventory/${id}/logs`)
}
