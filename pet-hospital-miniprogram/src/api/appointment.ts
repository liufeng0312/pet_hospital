import request from './request'

export interface Doctor {
    id: number
    name: string
    role: string
    phone?: string
    specialty?: string
}

export interface TimeSlot {
    time: string
    available: boolean
    remaining: number
}

export interface Appointment {
    id: number
    customerId: number
    petId: number
    doctorId: number
    status: string
    type: string
    appointmentTime: string
    queueNumber?: number
    symptoms?: string
    createdAt: string
    petName?: string
    doctorName?: string
}

export interface AppointmentForm {
    petId: number
    doctorId: number
    appointmentTime: string
    type: string
    symptoms?: string
}

export const appointmentApi = {
    // 获取医生列表
    getDoctors: () =>
        request.get<Doctor[]>('/api/employees/doctors'),

    // 获取可预约时间段
    getAvailableSlots: (doctorId: number, date: string) =>
        request.get<TimeSlot[]>('/api/registrations/available-slots', { doctorId, date }),

    // 创建预约
    createAppointment: (data: AppointmentForm) =>
        request.post<Appointment>('/api/registrations', data),

    // 我的预约列表
    getMyAppointments: () =>
        request.get<Appointment[]>('/api/registrations/my-appointments'),

    // 取消预约
    cancelAppointment: (id: number) =>
        request.put(`/api/registrations/${id}/cancel`)
}
