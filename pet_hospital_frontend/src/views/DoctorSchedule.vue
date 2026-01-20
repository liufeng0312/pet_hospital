<template>
    <div class="schedule-management">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>医生排班管理</span>
                    <el-button type="primary" @click="handleAdd">新增排班</el-button>
                </div>
            </template>

            <!-- 筛选栏 -->
            <el-form :inline="true" class="filter-form">
                <el-form-item label="医生">
                    <el-select v-model="filterDoctorId" placeholder="全部医生" clearable style="width: 200px" @change="fetchSchedules">
                        <el-option label="全部医生" :value="null" />
                        <el-option 
                            v-for="doctor in doctors" 
                            :key="doctor.id" 
                            :label="doctor.name" 
                            :value="doctor.id" 
                        />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="日期范围">
                    <el-date-picker
                        v-model="dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        @change="fetchSchedules"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                    />
                </el-form-item>
            </el-form>

            <!-- 排班日历 -->
            <div class="schedule-calendar">
                <table class="calendar-table">
                    <thead>
                        <tr>
                            <th style="width: 120px">医生</th>
                            <th v-for="date in dateList" :key="date">
                                {{ formatDateHeader(date) }}
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="doctor in doctors" :key="doctor.id">
                            <td class="doctor-name">{{ doctor.name }}</td>
                            <td v-for="date in dateList" :key="date" class="schedule-cell">
                                <div class="schedule-items">
                                    <div 
                                        v-for="schedule in getSchedulesByDoctorAndDate(doctor.id, date)" 
                                        :key="schedule.id"
                                        :class="[
                                            'schedule-item', 
                                            `shift-${schedule.shift_type || schedule.shiftType}`,
                                            { 'unavailable': (schedule.is_available || schedule.isAvailable) === 0 }
                                        ]"
                                        @click="handleEdit(schedule)"
                                    >
                                        <div>{{ getShiftLabel(schedule.shift_type || schedule.shiftType) }}</div>
                                        <div v-if="(schedule.is_available || schedule.isAvailable) === 0" class="unavailable-badge">
                                            不可预约
                                        </div>
                                    </div>
                                    <el-button 
                                        v-if="getSchedulesByDoctorAndDate(doctor.id, date).length === 0"
                                        type="text" 
                                        size="small" 
                                        @click="handleAddForDoctorAndDate(doctor.id, date)"
                                        class="add-schedule-btn"
                                    >
                                        +
                                    </el-button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </el-card>

        <!-- 编辑对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="isEdit ? '编辑排班' : '新增排班'"
            width="500px"
        >
            <el-form :model="form" label-width="100px">
                <el-form-item label="医生">
                    <el-select v-model="form.doctorId" placeholder="请选择医生" style="width: 100%" :disabled="isEdit">
                        <el-option 
                            v-for="doctor in doctors" 
                            :key="doctor.id" 
                            :label="doctor.name" 
                            :value="doctor.id" 
                        />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="工作日期">
                    <el-date-picker
                        v-model="form.workDate"
                        type="date"
                        placeholder="选择日期"
                        style="width: 100%"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        :disabled="isEdit"
                    />
                </el-form-item>
                
                <el-form-item label="班次类型">
                    <el-select v-model="form.shiftType" placeholder="请选择班次" style="width: 100%">
                        <el-option label="上午班" value="MORNING" />
                        <el-option label="下午班" value="AFTERNOON" />
                        <el-option label="全天班" value="FULL_DAY" />
                        <el-option label="休息" value="OFF" />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="最大预约数">
                    <el-input-number v-model="form.maxAppointments" :min="0" :max="50" style="width: 100%" />
                </el-form-item>
                
                <el-form-item label="可预约">
                    <el-switch v-model="form.isAvailable" :active-value="1" :inactive-value="0" />
                    <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                        关闭后前端将无法预约此排班
                    </span>
                </el-form-item>
            </el-form>
            
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmit">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { scheduleApi, type DoctorSchedule } from '@/api/schedule'
import { employeeApi, type Employee } from '@/api/employee'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const schedules = ref<DoctorSchedule[]>([])
const doctors = ref<Employee[]>([])
const filterDoctorId = ref<number | null>(null)

// 默认显示最近7天
const dateRange = ref<string[]>([
    dayjs().format('YYYY-MM-DD'),
    dayjs().add(6, 'day').format('YYYY-MM-DD')
])

const form = reactive<DoctorSchedule>({
    doctorId: 0,
    doctor_id: 0,
    workDate: '',
    work_date: '',
    shiftType: 'MORNING',
    shift_type: 'MORNING',
    maxAppointments: 10,
    max_appointments: 10,
    isAvailable: 1,
    is_available: 1
})

// 监听班次类型变化，当选择休息时自动关闭可预约
watch(() => form.shiftType, (newValue) => {
    if (newValue === 'OFF') {
        form.isAvailable = 0
        form.is_available = 0
        form.maxAppointments = 0
        form.max_appointments = 0
    } else if (form.isAvailable === 0 && form.maxAppointments === 0) {
        // 如果从休息切换到其他班次，恢复默认值
        form.isAvailable = 1
        form.is_available = 1
        if (newValue === 'FULL_DAY') {
            form.maxAppointments = 15
            form.max_appointments = 15
        } else {
            form.maxAppointments = 8
            form.max_appointments = 8
        }
    }
})


// 生成日期列表
const dateList = computed(() => {
    if (!dateRange.value || dateRange.value.length !== 2) return []
    const start = dayjs(dateRange.value[0])
    const end = dayjs(dateRange.value[1])
    const dates: string[] = []
    let current = start
    while (current.isBefore(end) || current.isSame(end, 'day')) {
        dates.push(current.format('YYYY-MM-DD'))
        current = current.add(1, 'day')
    }
    return dates
})

// 获取指定医生和日期的排班
const getSchedulesByDoctorAndDate = (doctorId: number, date: string) => {
    return schedules.value.filter(s => 
        (s.doctorId || s.doctor_id) === doctorId && 
        (s.workDate || s.work_date) === date
    )
}

// 获取班次标签
const getShiftLabel = (shiftType: string) => {
    const labels: Record<string, string> = {
        'MORNING': '上午',
        'AFTERNOON': '下午',
        'FULL_DAY': '全天',
        'OFF': '休息'
    }
    return labels[shiftType] || shiftType
}

// 格式化日期表头
const formatDateHeader = (date: string) => {
    const d = dayjs(date)
    const weekDays = ['日', '一', '二', '三', '四', '五', '六']
    return `${d.format('MM-DD')} 周${weekDays[d.day()]}`
}

// 获取医生列表
const fetchDoctors = async () => {
    try {
        const res = await employeeApi.getDoctors()
        doctors.value = res.data
    } catch (error) {
        ElMessage.error('加载医生列表失败')
    }
}

// 获取排班列表
const fetchSchedules = async () => {
    if (!dateRange.value || dateRange.value.length !== 2) return
    
    loading.value = true
    try {
        const res = await scheduleApi.list({
            doctorId: filterDoctorId.value || undefined,
            startDate: dateRange.value[0],
            endDate: dateRange.value[1]
        })
        schedules.value = res.data
    } catch (error) {
        ElMessage.error('加载排班失败')
    } finally {
        loading.value = false
    }
}

// 新增排班
const handleAdd = () => {
    isEdit.value = false
    form.doctorId = doctors.value[0]?.id || 0
    form.doctor_id = form.doctorId
    form.workDate = dateRange.value[0]
    form.work_date = form.workDate
    form.shiftType = 'MORNING'
    form.shift_type = 'MORNING'
    form.maxAppointments = 10
    form.max_appointments = 10
    dialogVisible.value = true
}

// 为特定医生和日期添加排班
const handleAddForDoctorAndDate = (doctorId: number, date: string) => {
    isEdit.value = false
    form.doctorId = doctorId
    form.doctor_id = doctorId
    form.workDate = date
    form.work_date = date
    form.shiftType = 'MORNING'
    form.shift_type = 'MORNING'
    form.maxAppointments = 10
    form.max_appointments = 10
    dialogVisible.value = true
}

// 编辑排班
const handleEdit = (schedule: DoctorSchedule) => {
    isEdit.value = true
    Object.assign(form, {
        ...schedule,
        doctorId: schedule.doctorId || schedule.doctor_id,
        workDate: schedule.workDate || schedule.work_date,
        shiftType: schedule.shiftType || schedule.shift_type,
        maxAppointments: schedule.maxAppointments || schedule.max_appointments,
        isAvailable: schedule.isAvailable ?? schedule.is_available ?? 1
    })
    dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
    if (!form.doctorId || !form.workDate || !form.shiftType) {
        ElMessage.warning('请填写必填项')
        return
    }
    
    try {
        const data = {
            ...form,
            doctor_id: form.doctorId,
            work_date: form.workDate,
            shift_type: form.shiftType,
            max_appointments: form.maxAppointments,
            is_available: form.isAvailable
        }
        
        if (isEdit.value) {
            await scheduleApi.update(form.id!, data)
            ElMessage.success('更新成功')
        } else {
            await scheduleApi.create(data)
            ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchSchedules()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

onMounted(() => {
    fetchDoctors()
    fetchSchedules()
})
</script>

<style scoped>
.schedule-management {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.filter-form {
    margin-bottom: 20px;
}

.schedule-calendar {
    overflow-x: auto;
}

.calendar-table {
    width: 100%;
    border-collapse: collapse;
    min-width: 800px;
}

.calendar-table th,
.calendar-table td {
    border: 1px solid #e0e0e0;
    padding: 12px 8px;
    text-align: center;
}

.calendar-table th {
    background-color: #f5f7fa;
    font-weight: 600;
    color: #606266;
}

.doctor-name {
    font-weight: 600;
    background-color: #fafafa;
}

.schedule-cell {
    vertical-align: top;
    min-height: 80px;
    position: relative;
}

.schedule-items {
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-height: 60px;
}

.schedule-item {
    padding: 6px 12px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    transition: all 0.3s;
    text-align: center;
}

.schedule-item:hover {
    opacity: 0.8;
    transform: translateY(-1px);
}

.shift-MORNING {
    background-color: #e3f2fd;
    color: #1976d2;
    border: 1px solid #90caf9;
}

.shift-AFTERNOON {
    background-color: #fff3e0;
    color: #f57c00;
    border: 1px solid #ffb74d;
}

.shift-FULL_DAY {
    background-color: #e8f5e9;
    color: #388e3c;
    border: 1px solid #81c784;
}

.shift-OFF {
    background-color: #f5f5f5;
    color: #757575;
    border: 1px solid #e0e0e0;
}

.add-schedule-btn {
    color: #909399;
    font-size: 20px;
    margin-top: 4px;
}

.add-schedule-btn:hover {
    color: #409eff;
}

.schedule-item.unavailable {
    opacity: 0.5;
    position: relative;
}

.unavailable-badge {
    font-size: 10px;
    margin-top: 2px;
    color: #f56c6c;
    font-weight: bold;
}
</style>
