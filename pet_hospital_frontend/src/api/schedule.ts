import api from './index'

export interface DoctorSchedule {
    id?: number
    doctor_id: number
    doctorId?: number
    work_date: string
    workDate?: string
    shift_type: string
    shiftType?: string
    start_time?: string
    startTime?: string
    end_time?: string
    endTime?: string
    is_available?: number
    isAvailable?: number
    max_appointments?: number
    maxAppointments?: number
    notes?: string
    created_at?: string
    createdAt?: string
    updated_at?: string
    updatedAt?: string
}

export const scheduleApi = {
    list: (params: { doctorId?: number; startDate: string; endDate: string }) =>
        api.get<any, { code: number; data: DoctorSchedule[] }>('/schedules', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: DoctorSchedule }>(`/schedules/${id}`),

    create: (data: DoctorSchedule) =>
        api.post<any, { code: number; data: boolean }>('/schedules', data),

    batchCreate: (data: DoctorSchedule[]) =>
        api.post<any, { code: number; data: boolean }>('/schedules/batch', data),

    update: (id: number, data: DoctorSchedule) =>
        api.put<any, { code: number; data: boolean }>(`/schedules/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/schedules/${id}`)
}
