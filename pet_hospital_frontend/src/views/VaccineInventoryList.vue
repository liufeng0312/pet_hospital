<template>
    <div class="vaccine-inventory">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>疫苗库存管理</span>
                    <el-button type="primary" @click="showDialog(null)">
                        <el-icon><Plus /></el-icon>新增库存
                    </el-button>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" style="margin-bottom: 20px">
                <el-form-item label="疫苗名称">
                    <el-input v-model="searchForm.vaccineName" placeholder="请输入疫苗名称" clearable style="width: 200px" />
                </el-form-item>
                <el-form-item label="疫苗类型">
                    <el-select v-model="searchForm.vaccineType" placeholder="请选择类型" clearable style="width: 150px">
                        <el-option label="狂犬" value="狂犬" />
                        <el-option label="六联" value="六联" />
                        <el-option label="三联" value="三联" />
                        <el-option label="其他" value="其他" />
                    </el-select>
                </el-form-item>
                <el-form-item label="批号">
                    <el-input v-model="searchForm.batchNumber" placeholder="请输入批号" clearable style="width: 200px" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="handleReset">
                        <el-icon><RefreshLeft /></el-icon> 重置
                    </el-button>
                </el-form-item>
            </el-form>

            <!-- 预警卡片 -->
            <el-row :gutter="20" style="margin-bottom: 20px" v-if="alerts">
                <el-col :span="12">
                    <el-alert title="低库存预警" type="warning" :closable="false">
                        <template #default>共{{ alerts.lowStockCount }}种疫苗库存不足</template>
                    </el-alert>
                </el-col>
                <el-col :span="12">
                    <el-alert title="即将过期预警" type="error" :closable="false">
                        <template #default>共{{ alerts.expiringCount }}种疫苗即将过期</template>
                    </el-alert>
                </el-col>
            </el-row>

            <!-- 数据表格 -->
            <el-table :data="inventory" v-loading="loading">
                <el-table-column prop="vaccineName" label="疫苗名称" min-width="120" />
                <el-table-column prop="vaccineType" label="类型" min-width="100" />
                <el-table-column prop="batchNumber" label="批号" min-width="120" />
                <el-table-column prop="quantity" label="库存数量" min-width="100">
                    <template #default="{ row }">
                        {{ row.quantity }}
                        <el-tag v-if="row.quantity < row.minQuantity" type="warning" size="small">低库存</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="expiryDate" label="有效期" min-width="120">
                    <template #default="{ row }">
                        {{ row.expiryDate || '-' }}
                        <el-tag v-if="isExpiring(row.expiryDate)" type="danger" size="small">即将过期</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="unitPrice" label="单价" min-width="100">
                    <template #default="{ row }">
                        <span class="amount">¥{{ row.unitPrice?.toFixed(2) || '0.00' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="manufacturer" label="厂家" min-width="120" />
                <el-table-column label="操作" min-width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" @click="showDialog(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
                        <el-button size="small" type="primary" @click="showAdjustDialog(row)">调整库存</el-button>
                        <el-button size="small" type="danger" @click="handleDelete(row.id)"><el-icon><Delete /></el-icon> 删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            
            <!-- 分页 -->
            <div style="margin-top: 20px; display: flex; justify-content: flex-start">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <!-- 编辑对话框 -->
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑库存' : '新增库存'" width="600px">
            <el-form :model="form" label-width="100px">
                <el-form-item label="疫苗名称" required>
                    <el-input v-model="form.vaccineName" />
                </el-form-item>
                <el-form-item label="疫苗类型">
                    <el-select v-model="form.vaccineType" placeholder="请选择疫苗类型" style="width: 100%">
                        <el-option label="狂犬" value="狂犬" />
                        <el-option label="六联" value="六联" />
                        <el-option label="三联" value="三联" />
                    </el-select>
                </el-form-item>
                <el-form-item label="批号" required>
                    <el-input v-model="form.batchNumber" />
                </el-form-item>
                <el-form-item label="库存数量" required>
                    <el-input-number v-model="form.quantity" :min="0" placeholder="请输入数量" />
                </el-form-item>
                <el-form-item label="最低库存">
                    <el-input-number v-model="form.minQuantity" :min="0" placeholder="请输入最小库存" />
                </el-form-item>
                <el-form-item label="单价">
                    <el-input-number v-model="form.unitPrice" :min="0" :precision="2" placeholder="请输入单价" />
                </el-form-item>
                <el-form-item label="生产厂家">
                    <el-input v-model="form.manufacturer" />
                </el-form-item>
                <el-form-item label="生产日期">
                    <el-date-picker v-model="form.productionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
                <el-form-item label="有效期至">
                    <el-date-picker v-model="form.expiryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
                <el-form-item label="存储位置">
                    <el-input v-model="form.storageLocation" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </template>
        </el-dialog>

        <!-- 调整库存对话框 -->
        <el-dialog v-model="adjustDialogVisible" title="调整库存" width="500px">
            <el-form label-width="100px">
                <el-form-item label="当前库存">
                    <span>{{ currentInventory?.quantity || 0 }}</span>
                </el-form-item>
                <el-form-item label="调整数量">
                    <el-input-number v-model="adjustQuantity" placeholder="请输入调整数量" />
                    <span style="margin-left: 10px; color: #909399">正数为入库，负数为出库</span>
                </el-form-item>
                <el-form-item label="调整原因">
                    <el-input v-model="adjustReason" type="textarea" :rows="3" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="adjustDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAdjust">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft, Edit, Delete } from '@element-plus/icons-vue'
import { vaccineInventoryApi, type VaccineInventory } from '@/api/vaccineInventory'

const loading = ref(false)
const inventory = ref<VaccineInventory[]>([])
const alerts = ref<any>(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const adjustDialogVisible = ref(false)
const isEdit = ref(false)
const currentInventory = ref<VaccineInventory | null>(null)
const searchForm = reactive({
    vaccineName: '',
    vaccineType: '',
    batchNumber: ''
})
const adjustQuantity = ref(0)
const adjustReason = ref('')

const form = reactive<VaccineInventory>({
    vaccineName: '',
    vaccineType: '',
    manufacturer: '',
    batchNumber: '',
    quantity: 0,
    minQuantity: 10,
    unitPrice: undefined,
    productionDate: '',
    expiryDate: '',
    storageLocation: ''
})

const fetchData = async () => {
    loading.value = true
    try {
        const searchParams: any = {}
        if (searchForm.vaccineName) searchParams.vaccineName = searchForm.vaccineName
        if (searchForm.vaccineType) searchParams.vaccineType = searchForm.vaccineType
        if (searchForm.batchNumber) searchParams.batchNumber = searchForm.batchNumber
        
        const [inventoryRes, alertsRes] = await Promise.all([
            vaccineInventoryApi.list(currentPage.value, pageSize.value, searchParams),
            vaccineInventoryApi.getAlerts()
        ])
        inventory.value = inventoryRes.data.records
        total.value = inventoryRes.data.total
        alerts.value = alertsRes.data
    } finally {
        loading.value = false
    }
}

const handleSizeChange = (val: number) => {
    pageSize.value = val
    currentPage.value = 1
    fetchData()
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val
    fetchData()
}

const handleSearch = () => {
    currentPage.value = 1
    fetchData()
}

const handleReset = () => {
    searchForm.vaccineName = ''
    searchForm.vaccineType = ''
    searchForm.batchNumber = ''
    currentPage.value = 1
    fetchData()
}

const showDialog = (row: VaccineInventory | null) => {
    isEdit.value = !!row
    if (row) {
        Object.assign(form, row)
    } else {
        form.id = undefined
        form.vaccineName = ''
        form.vaccineType = ''
        form.manufacturer = ''
        form.batchNumber = ''
        form.quantity = 0
        form.minQuantity = 10
        form.unitPrice = undefined
        form.productionDate = ''
        form.expiryDate = ''
        form.storageLocation = ''
    }
    dialogVisible.value = true
}

const handleSubmit = async () => {
    if (!form.vaccineName || !form.batchNumber) {
        ElMessage.warning('请填写必填项')
        return
    }
    try {
        if (isEdit.value && form.id) {
            await vaccineInventoryApi.update(form.id, form)
            ElMessage.success('更新成功')
        } else {
            await vaccineInventoryApi.create(form)
            ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await fetchData()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '操作失败')
    }
}

const showAdjustDialog = (row: VaccineInventory) => {
    currentInventory.value = row
    adjustQuantity.value = 0
    adjustReason.value = ''
    adjustDialogVisible.value = true
}

const handleAdjust = async () => {
    if (!currentInventory.value || !adjustReason.value) {
        ElMessage.warning('请填写调整原因')
        return
    }
    try {
        await vaccineInventoryApi.adjustStock(
            currentInventory.value.id!,
            adjustQuantity.value,
            adjustReason.value
        )
        ElMessage.success('调整成功')
        adjustDialogVisible.value = false
        await fetchData()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '调整失败')
    }
}

const handleDelete = async (id: number) => {
    try {
        await ElMessageBox.confirm('确定删除该库存记录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        await vaccineInventoryApi.delete(id)
        ElMessage.success('删除成功')
        await fetchData()
    } catch (error: any) {
        if (error !== 'cancel') {
            ElMessage.error(error.response?.data?.msg || '删除失败')
        }
    }
}

const isExpiring = (date: string | undefined) => {
    if (!date) return false
    const expiryDate = new Date(date)
    const today = new Date()
    const diffDays = Math.floor((expiryDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
    return diffDays >= 0 && diffDays <= 30
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.vaccine-inventory {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.amount {
    color: #f56c6c;
    font-weight: bold;
    font-size: 16px;
}
</style>
