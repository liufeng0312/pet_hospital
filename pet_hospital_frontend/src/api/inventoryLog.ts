import api from './index'
import type { PageResult } from './customer'

export interface InventoryLog {
    id?: number
    drugId: number
    type: number // 1:入库, 2:出库, 3:盘点调整
    quantity: number
    batchNumber?: string
    expiryDate?: string
    operatorId?: number
    createdAt?: string
    drug?: {
        name?: string
        specification?: string
        unit?: string
    }
    operator?: {
        name?: string
    }
}

export const inventoryLogApi = {
    list: (params: { drugId?: number; type?: number; page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<InventoryLog> }>('/inventory-logs', { params }),

    inbound: (data: InventoryLog) =>
        api.post<any, { code: number; data: any }>('/inventory-logs/inbound', data),

    outbound: (data: InventoryLog) =>
        api.post<any, { code: number; data: any }>('/inventory-logs/outbound', data),

    adjustment: (data: InventoryLog) =>
        api.post<any, { code: number; data: any }>('/inventory-logs/adjustment', data)
}
