import api from './index'

export interface Hospitalization {
    id?: number
    petId: number
    bedNumber: string
    startTime?: string
    endTime?: string
    status?: string // ACTIVE, DISCHARGED
    pet?: {
        name?: string
        breed?: string
        sex?: string
        customer?: {
            name?: string
            phone?: string
        }
    }
}

export interface BedStatus {
    bedNumber: string
    isOccupied: boolean
    hospitalization?: Hospitalization
}

export const hospitalizationApi = {
    getBedStatus: () =>
        api.get<any, { code: number; data: BedStatus[] }>('/hospitalizations/beds'),

    admit: (data: Hospitalization) =>
        api.post<any, { code: number; data: boolean }>('/hospitalizations/admit', data),

    discharge: (id: number) =>
        api.post<any, { code: number; data: boolean }>(`/hospitalizations/${id}/discharge`),

    list: (params: { page: number; size: number; petId?: number }) =>
        api.get<any, { code: number; data: { records: Hospitalization[]; total: number } }>('/hospitalizations', { params })
}
