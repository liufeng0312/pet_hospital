import request from './index'

export interface VaccineRecord {
    id?: number
    petId: number
    vaccineName: string
    vaccineType?: string
    batchNumber?: string
    vaccinationDate: string
    nextDueDate?: string
    doctorId?: number
    status?: number
    notes?: string
    createdAt?: string
    price?: number
    pet?: {
        name?: string
        species?: string
    }
    doctor?: {
        name?: string
    }
}

export const vaccineRecordApi = {
    list: (params?: { petId?: number }) =>
        request.get<VaccineRecord[]>('/vaccine-records', { params }),

    getById: (id: number) =>
        request.get<VaccineRecord>(`/vaccine-records/${id}`),

    create: (data: VaccineRecord) =>
        request.post<VaccineRecord>('/vaccine-records', data),

    update: (id: number, data: VaccineRecord) =>
        request.put<boolean>(`/vaccine-records/${id}`, data),

    updateStatus: (id: number, status: number) =>
        request.put<boolean>(`/vaccine-records/${id}/status`, null, { params: { status } }),

    delete: (id: number) =>
        request.delete<boolean>(`/vaccine-records/${id}`)
}
