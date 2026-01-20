<template>
    <div class="vaccine-record-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>疫苗接种记录</span>
                    <el-button type="primary" @click="showCreateDialog">
                        <el-icon><Plus /></el-icon>新建记录
                    </el-button>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="{ searchPetId }" class="search-form">
                <el-form-item label="宠物">
                    <el-select v-model="searchPetId" clearable placeholder="全部" style="width: 200px">
                        <el-option v-for="pet in pets" :key="pet.id" :label="`${pet.name} (${pet.species})`" :value="pet.id" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="fetchData">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 数据表格 -->
            <el-table :data="records" style="width: 100%" v-loading="loading">
                <el-table-column prop="pet.name" label="宠物" min-width="120">
                    <template #default="{ row }">
                        {{ row.pet?.name || '-' }}
                        <el-tag size="small" v-if="row.pet?.species">{{ row.pet.species }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="vaccineName" label="疫苗名称" min-width="150" />
                <el-table-column prop="vaccineType" label="类型" min-width="100" />
                <el-table-column prop="vaccinationDate" label="接种日期" min-width="120" />
                <el-table-column prop="nextDueDate" label="下次接种日期" min-width="130">
                    <template #default="{ row }">
                        <span v-if="row.nextDueDate">
                            {{ row.nextDueDate }}
                            <el-tag v-if="isOverdue(row.nextDueDate)" type="danger" size="small">已到期</el-tag>
                            <el-tag v-else-if="isUpcoming(row.nextDueDate)" type="warning" size="small">即将到期</el-tag>
                        </span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column prop="doctor.name" label="接种医生" min-width="100">
                    <template #default="{ row }">
                        {{ row.doctor?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="price" label="费用" min-width="100">
                    <template #default="{ row }">
                        <span class="amount">¥{{ row.price?.toFixed(2) || '0.00' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusLabel(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="batchNumber" label="批号" min-width="120" />
                <el-table-column label="操作" min-width="180" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" @click="handleViewDetail(row)">查看详情</el-button>
                        <el-button size="small" type="success" @click="handleMarkCompleted(row.id)" 
                                   v-if="row.status !== 2">已接种</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 编辑对话框 -->
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑疫苗记录' : '新建疫苗记录'" width="600px">
            <el-form :model="form" label-width="120px">
                <el-form-item label="宠物" required>
                    <el-select v-model="form.petId" placeholder="请选择宠物" style="width: 100%">
                        <el-option v-for="pet in pets" :key="pet.id" 
                                   :label="`${pet.name} (${pet.species})`" :value="pet.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="选择疫苗" required>
                    <el-select v-model="selectedInventoryId" placeholder="请选择库存疫苗" style="width: 100%" 
                               @change="handleInventoryChange" clearable filterable>
                        <el-option v-for="item in inventoryList" :key="item.id" 
                                   :label="`${item.vaccineName} - ${item.batchNumber} (库存:${item.quantity})`" 
                                   :value="item.id"
                                   :disabled="item.quantity <= 0" />
                    </el-select>
                </el-form-item>
                <el-form-item label="疫苗名称">
                    <el-input v-model="form.vaccineName" disabled placeholder="选择疫苗后自动填充" />
                </el-form-item>
                <el-form-item label="疫苗类型">
                    <el-input v-model="form.vaccineType" disabled placeholder="选择疫苗后自动填充" />
                </el-form-item>
                <el-form-item label="批号">
                    <el-input v-model="form.batchNumber" disabled placeholder="选择疫苗后自动填充" />
                </el-form-item>
                <el-form-item label="接种日期" required>
                    <el-date-picker v-model="form.vaccinationDate" type="date" 
                                    value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
                <el-form-item label="下次接种日期">
                    <el-date-picker v-model="form.nextDueDate" type="date" 
                                    value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
                <el-form-item label="接种医生">
                    <el-select v-model="form.doctorId" placeholder="请选择医生" style="width: 100%" clearable>
                        <el-option v-for="doc in doctors" :key="doc.id" 
                                   :label="doc.name" :value="doc.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="form.notes" type="textarea" :rows="3" placeholder="备注信息" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
            </template>
        </el-dialog>

        <!-- 查看详情对话框 -->
        <el-dialog v-model="detailDialogVisible" title="疫苗记录详情" width="600px">
            <el-descriptions v-if="currentRecord" :column="1" border>
                <el-descriptions-item label="宠物">
                    {{ currentRecord.pet?.name || '-' }} ({{ currentRecord.pet?.species || '-' }})
                </el-descriptions-item>
                <el-descriptions-item label="疫苗名称">{{ currentRecord.vaccineName }}</el-descriptions-item>
                <el-descriptions-item label="疫苗类型">{{ currentRecord.vaccineType || '-' }}</el-descriptions-item>
                <el-descriptions-item label="批号">{{ currentRecord.batchNumber }}</el-descriptions-item>
                <el-descriptions-item label="费用">
                    <span class="amount">¥{{ currentRecord.price?.toFixed(2) || '0.00' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                    <el-tag :type="getStatusType(currentRecord.status)">
                        {{ getStatusLabel(currentRecord.status) }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="接种日期">{{ currentRecord.vaccinationDate }}</el-descriptions-item>
                <el-descriptions-item label="下次接种日期">{{ currentRecord.nextDueDate || '-' }}</el-descriptions-item>
                <el-descriptions-item label="接种医生">{{ currentRecord.doctor?.name || '-' }}</el-descriptions-item>
                <el-descriptions-item label="备注">{{ currentRecord.notes || '-' }}</el-descriptions-item>
            </el-descriptions>
            <template #footer>
                <el-button @click="detailDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { vaccineRecordApi, type VaccineRecord } from '@/api/vaccineRecord'
import { petApi, type Pet } from '@/api/pet'
import { employeeApi, type Employee } from '@/api/employee'
import { vaccineInventoryApi, type VaccineInventory } from '@/api/vaccineInventory'

const loading = ref(false)
const records = ref<VaccineRecord[]>([])
const pets = ref<Pet[]>([])
const doctors = ref<Employee[]>([])
const inventoryList = ref<VaccineInventory[]>([])
const selectedInventoryId = ref<number>()
const searchPetId = ref<number>()
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentRecord = ref<VaccineRecord | null>(null)

const form = reactive<VaccineRecord>({
    petId: undefined,
    vaccineName: '',
    vaccineType: '',
    batchNumber: '',
    vaccinationDate: '',
    nextDueDate: '',
    doctorId: undefined,
    notes: ''
})

const fetchData = async () => {
    loading.value = true
    try {
        const res = await vaccineRecordApi.list({ petId: searchPetId.value })
        records.value = res.data
    } finally {
        loading.value = false
    }
}

const resetSearch = () => {
    searchPetId.value = undefined
    fetchData()
}

const fetchPets = async () => {
    try {
        const res = await petApi.list({ page: 1, size: 1000 })
        pets.value = res.data.records || res.data
    } catch (error) {
        console.error('获取宠物列表失败', error)
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

const fetchInventory = async () => {
    try {
        const res = await vaccineInventoryApi.list(1, 1000)
        inventoryList.value = res.data.records || []
    } catch (error) {
        console.error('获取疫苗库存失败', error)
    }
}

const handleInventoryChange = (inventoryId: number) => {
    if (!inventoryId) {
        form.vaccineName = ''
        form.vaccineType = ''
        form.batchNumber = ''
        return
    }
    const inventory = inventoryList.value.find(item => item.id === inventoryId)
    if (inventory) {
        form.vaccineName = inventory.vaccineName
        form.vaccineType = inventory.vaccineType || ''
        form.batchNumber = inventory.batchNumber
    }
}

const showCreateDialog = () => {
    isEdit.value = false
    selectedInventoryId.value = undefined
    form.id = undefined
    form.petId = undefined
    form.vaccineName = ''
    form.vaccineType = ''
    form.batchNumber = ''
    form.vaccinationDate = ''
    form.nextDueDate = ''
    form.doctorId = undefined
    form.notes = ''
    dialogVisible.value = true
}

const handleSubmit = async () => {
    if (!form.petId || !form.vaccineName || !form.vaccinationDate) {
        ElMessage.warning('请填写必填项')
        return
    }
    submitting.value = true
    try {
        if (isEdit.value && form.id) {
            await vaccineRecordApi.update(form.id, form)
            ElMessage.success('更新成功')
        } else {
            await vaccineRecordApi.create(form)
            ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await fetchData()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '操作失败')
    } finally {
        submitting.value = false
    }
}

const handleViewDetail = (row: VaccineRecord) => {
    currentRecord.value = row
    detailDialogVisible.value = true
}

const handleMarkCompleted = async (id: number) => {
    try {
        await ElMessageBox.confirm('确认标记为已接种吗？', '提示', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'info'
        })
        await vaccineRecordApi.updateStatus(id, 2) // 2: 已完成
        ElMessage.success('标记成功')
        await fetchData()
    } catch (error: any) {
        if (error !== 'cancel') {
            ElMessage.error(error.response?.data?.msg || '操作失败')
        }
    }
}

const getStatusLabel = (status: number | undefined) => {
    const labels: Record<number, string> = {
        0: '未支付',
        1: '待接种',
        2: '已完成'
    }
    return labels[status ?? 0] || '未知'
}

const getStatusType = (status: number | undefined) => {
    const types: Record<number, string> = {
        0: 'danger',
        1: 'warning',
        2: 'success'
    }
    return types[status ?? 0] || 'info'
}

const isOverdue = (date: string) => {
    return new Date(date) < new Date()
}

const isUpcoming = (date: string) => {
    const dueDate = new Date(date)
    const today = new Date()
    const diffDays = Math.floor((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
    return diffDays >= 0 && diffDays <= 14
}

onMounted(() => {
    fetchData()
    fetchPets()
    fetchDoctors()
    fetchInventory()
})
</script>

<style scoped>
.vaccine-record-list {
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

.amount {
    color: #f56c6c;
    font-weight: bold;
    font-size: 16px;
}
</style>
