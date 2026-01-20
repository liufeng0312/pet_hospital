<template>
    <div class="registration-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>挂号管理</span>
                    <el-button type="primary" @click="showCreateDialog">
                        <el-icon><Plus /></el-icon>新建挂号
                    </el-button>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchParams" class="search-form">
                <el-form-item label="状态">
                    <el-select v-model="searchParams.status" placeholder="全部" clearable style="width: 150px">
                        <el-option label="候诊中" value="WAITING" />
                        <el-option label="就诊中" value="IN_PROGRESS" />
                        <el-option label="已完成" value="COMPLETED" />
                        <el-option label="已取消" value="CANCELLED" />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="日期">
                    <el-date-picker
                        v-model="searchParams.date"
                        type="date"
                        placeholder="选择日期"
                        value-format="YYYY-MM-DD"
                        style="width: 150px"
                    />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="fetchData">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 数据表格 -->
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="queueNumber" label="排队号" min-width="80">
                    <template #default="{ row }">
                        <el-tag type="info" size="large">{{ row.queueNumber }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="customer.name" label="客户" min-width="120">
                    <template #default="{ row }">
                        {{ row.customer?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="pet.name" label="宠物" min-width="150">
                    <template #default="{ row }">
                        {{ row.pet?.name || '-' }}
                        <el-tag size="small" v-if="row.pet?.species">{{ row.pet?.species }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="doctor.name" label="医生" min-width="100">
                    <template #default="{ row }">
                        {{ row.doctor?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusLabel(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="类型" min-width="100">
                    <template #default="{ row }">
                        {{ row.type === 'ONSITE' ? '现场挂号' : '预约' }}
                    </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="挂号时间" min-width="180" />
                <el-table-column label="操作" min-width="250" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-if="row.status === 'WAITING'"
                            type="success"
                            size="small"
                            @click="handleCall(row.id)"
                        >叫号</el-button>
                        <el-button
                            v-if="row.status === 'IN_PROGRESS'"
                            type="primary"
                            size="small"
                            @click="handleComplete(row.id)"
                        >完成</el-button>
                        <el-button
                            v-if="['WAITING', 'IN_PROGRESS'].includes(row.status)"
                            type="danger"
                            size="small"
                            @click="handleCancel(row.id)"
                        >取消</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-wrapper">
                <el-pagination
                    v-model:current-page="searchParams.page"
                    v-model:page-size="searchParams.size"
                    :total="total"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="fetchData"
                    @current-change="fetchData"
                />
            </div>
        </el-card>

        <!-- 新建挂号弹窗 -->
        <el-dialog v-model="dialogVisible" title="新建挂号" width="500px">
            <el-form :model="form" label-width="80px">
                <el-form-item label="客户" required>
                    <el-select
                        v-model="form.customerId"
                        filterable
                        placeholder="请选择客户"
                        style="width: 100%"
                        @change="onCustomerChange"
                    >
                        <el-option
                            v-for="c in customers"
                            :key="c.id"
                            :label="c.name + ' - ' + c.phone"
                            :value="c.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="宠物" required>
                    <el-select
                        v-model="form.petId"
                        filterable
                        placeholder="请选择宠物"
                        style="width: 100%"
                    >
                        <el-option
                            v-for="p in pets"
                            :key="p.id"
                            :label="p.name + ' (' + p.species + ')'"
                            :value="p.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="医生">
                    <el-select
                        v-model="form.doctorId"
                        filterable
                        placeholder="请选择医生"
                        style="width: 100%"
                        clearable
                    >
                        <el-option
                            v-for="d in doctors"
                            :key="d.id"
                            :label="d.name"
                            :value="d.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="类型">
                    <el-radio-group v-model="form.type">
                        <el-radio value="ONSITE">现场挂号</el-radio>
                        <el-radio value="APPOINTMENT">预约</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleCreate" :loading="submitting">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { registrationApi, type Registration } from '@/api/registration'
import { customerApi, type Customer } from '@/api/customer'
import { petApi, type Pet } from '@/api/pet'
import { employeeApi, type Employee } from '@/api/employee'

const loading = ref(false)
const tableData = ref<Registration[]>([])
const total = ref(0)

const searchParams = reactive({
    status: '',
    date: '',
    page: 1,
    size: 10
})

// 弹窗相关
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive<Registration>({
    customerId: undefined,
    petId: undefined,
    doctorId: undefined,
    type: 'ONSITE'
})

// 选择器数据
const customers = ref<Customer[]>([])
const pets = ref<Pet[]>([])
const doctors = ref<Employee[]>([])

const fetchData = async () => {
    loading.value = true
    try {
        const res = await registrationApi.list({
            status: searchParams.status || undefined,
            date: searchParams.date || undefined,
            page: searchParams.page,
            size: searchParams.size
        })
        tableData.value = res.data.records
        total.value = res.data.total
    } catch (error) {
        console.error('获取挂号列表失败', error)
    } finally {
        loading.value = false
    }
}

const resetSearch = () => {
    searchParams.status = ''
    searchParams.date = ''
    searchParams.page = 1
    fetchData()
}

const fetchCustomers = async () => {
    try {
        const res = await customerApi.list({ page: 1, size: 1000 })
        customers.value = res.data.records
    } catch (error) {
        console.error('获取客户列表失败', error)
    }
}

const fetchDoctors = async () => {
    try {
        const res = await employeeApi.getDoctors()
        doctors.value = res.data
    } catch (error) {
        console.error('获取医生列表失败', error)
    }
}

const onCustomerChange = async (customerId: number) => {
    if (!customerId) {
        pets.value = []
        return
    }
    try {
        const res = await petApi.list({ customerId, page: 1, size: 100 })
        pets.value = res.data.records
    } catch (error) {
        console.error('获取宠物列表失败', error)
    }
}

const showCreateDialog = () => {
    form.customerId = undefined
    form.petId = undefined
    form.doctorId = undefined
    form.type = 'ONSITE'
    pets.value = []
    dialogVisible.value = true
}

const handleCreate = async () => {
    if (!form.customerId || !form.petId) {
        ElMessage.warning('请选择客户和宠物')
        return
    }
    if (submitting.value) {
        return // 防止重复提交
    }
    submitting.value = true
    try {
        await registrationApi.create(form)
        ElMessage.success('挂号成功')
        dialogVisible.value = false
        await fetchData()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '挂号失败')
    } finally {
        submitting.value = false
    }
}

const handleCall = async (id: number) => {
    try {
        await registrationApi.callNext(id)
        ElMessage.success('叫号成功')
        fetchData()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '叫号失败')
    }
}

const handleComplete = async (id: number) => {
    try {
        await registrationApi.complete(id)
        ElMessage.success('已完成就诊')
        fetchData()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '操作失败')
    }
}

const handleCancel = async (id: number) => {
    try {
        await ElMessageBox.confirm('确定要取消此挂号吗？', '提示', { type: 'warning' })
        await registrationApi.cancel(id)
        ElMessage.success('已取消')
        fetchData()
    } catch (error: any) {
        if (error !== 'cancel') {
            ElMessage.error(error.response?.data?.msg || '取消失败')
        }
    }
}

const getStatusType = (status: string) => {
    const map: Record<string, string> = {
        PENDING: 'info',
        WAITING: 'warning',
        IN_PROGRESS: 'primary',
        COMPLETED: 'success',
        CANCELLED: 'info'
    }
    return map[status] || 'info'
}

const getStatusLabel = (status: string) => {
    const map: Record<string, string> = {
        PENDING: '待确认',
        WAITING: '候诊中',
        IN_PROGRESS: '就诊中',
        COMPLETED: '已完成',
        CANCELLED: '已取消'
    }
    return map[status] || status
}

onMounted(() => {
    fetchData()
    fetchCustomers()
    fetchDoctors()
})
</script>

<style scoped>
.registration-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-bar {
    margin-bottom: 20px;
}

.pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-start;
}
</style>
