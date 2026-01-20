import api from './index'
import type { Employee } from './employee'

export interface LoginResponse {
    token: string;
    user: Employee;
}

export const authApi = {
    login: (data: any) =>
        api.post<any, { code: number; data: LoginResponse }>('/auth/login', data),

    logout: () =>
        api.post<any, { code: number; data: boolean }>('/auth/logout')
}
