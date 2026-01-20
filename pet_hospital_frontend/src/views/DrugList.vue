<template>
    <div class="drug-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>药品库存管理</span>
                    <el-button type="primary" @click="showAddDialog">
                        <el-icon><Plus /></el-icon>新建药品
                    </el-button>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchParams" class="search-form">
                <el-form-item label="关键词">
                    <el-input 
                        v-model="searchParams.keyword" 
                        placeholder="药品名称/编码" 
                        style="width: 200px" 
                        clearable
                        @clear="fetchData" 
                        @keyup.enter="fetchData"
                    />
                </el-form-item>
                
                <el-form-item label="状态">
                    <el-select 
                        v-model="searchParams.status" 
                        placeholder="全部" 
                        clearable 
                        style="width: 120px"
                    >
                        <el-option label="启用" :value="1" />
                        <el-option label="停用" :value="0" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="fetchData">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch">
                        <el-icon><RefreshLeft /></el-icon> 重置
                    </el-button>
                </el-form-item>
            </el-form>

            <!-- 库存预警提示 -->
            <el-alert
                v-if="lowStockDrugs.length > 0"
                title="库存预警"
                type="warning"
                :description="`有 ${lowStockDrugs.length} 种药品库存不足，请及时补货`"
                show-icon
                style="margin-bottom: 20px"
            />

            <!-- 数据表格 -->
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="name" label="药品名称" min-width="180" />
                <el-table-column prop="code" label="编码" min-width="120" />
                <el-table-column prop="specification" label="规格" min-width="120" />
                <el-table-column prop="manufacturer" label="厂家" min-width="150" />
                <el-table-column prop="price" label="价格" min-width="100">
                    <template #default="{ row }">
                        ¥{{ row.price?.toFixed(2) }}
                    </template>
                </el-table-column>
                <el-table-column prop="stockQuantity" label="库存" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.stockQuantity <= row.warningThreshold ? 'danger' : 'success'">
                            {{ row.stockQuantity }} {{ row.unit }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="warningThreshold" label="预警阈值" min-width="100" />
                <el-table-column label="操作" min-width="300" fixed="right">
                    <template #default="{ row }">
                        <el-button type="success" size="small" @click="showInboundDialog(row)">
                            入库
                        </el-button>
                        <el-button type="warning" size="small" @click="showOutboundDialog(row)">
                            出库
                        </el-button>
                        <el-button type="primary" size="small" @click="showEditDialog(row)">
                            <el-icon><Edit /></el-icon> 编辑
                        </el-button>
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

        <!-- 入库弹窗 -->
        <el-dialog v-model="inboundDialogVisible" title="药品入库" width="500px">
            <el-form :model="inboundForm" label-width="100px">
                <el-form-item label="药品名称">
                    <el-input v-model="currentDrug.name" disabled />
                </el-form-item>
                <el-form-item label="入库数量" required>
                    <el-input-number v-model="inboundForm.quantity" :min="1" placeholder="请输入数量" />
                </el-form-item>
                <el-form-item label="批号">
                    <el-input v-model="inboundForm.batchNumber" placeholder="可选" />
                </el-form-item>
                <el-form-item label="有效期">
                    <el-date-picker v-model="inboundForm.expiryDate" type="date" placeholder="可选" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="inboundDialogVisible = false">
                    <el-icon><Close /></el-icon> 取消
                </el-button>
                <el-button type="primary" @click="handleInbound" :loading="submitting">
                    <el-icon><Check /></el-icon> 确定
                </el-button>
            </template>
        </el-dialog>

        <!-- 出库弹窗 -->
        <el-dialog v-model="outboundDialogVisible" title="药品出库" width="500px">
            <el-form :model="outboundForm" label-width="100px">
                <el-form-item label="药品名称">
                    <el-input v-model="currentDrug.name" disabled />
                </el-form-item>
                <el-form-item label="当前库存">
                    <el-input :value="`${currentDrug.stockQuantity} ${currentDrug.unit}`" disabled />
                </el-form-item>
                <el-form-item label="出库数量" required>
                    <el-input-number v-model="outboundForm.quantity" :min="1" :max="currentDrug.stockQuantity" placeholder="请输入数量" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="outboundDialogVisible = false">
                    <el-icon><Close /></el-icon> 取消
                </el-button>
                <el-button type="primary" @click="handleOutbound" :loading="submitting">
                    <el-icon><Check /></el-icon> 确定
                </el-button>
            </template>
        </el-dialog>
        <!-- 编辑/新增弹窗 -->
        <el-dialog
            v-model="editDialogVisible"
            :title="currentDrugForm.id ? '编辑药品' : '新建药品'"
            width="500px"
        >
            <el-form
                ref="drugFormRef"
                :model="currentDrugForm"
                :rules="rules"
                label-width="100px"
            >
                <el-form-item label="药品名称" prop="name">
                    <el-input v-model="currentDrugForm.name" placeholder="请输入药品名称" />
                </el-form-item>
                <el-form-item label="编码" prop="code">
                    <el-input v-model="currentDrugForm.code" placeholder="请输入药品编码" />
                </el-form-item>
                <el-form-item label="规格" prop="specification">
                    <el-input v-model="currentDrugForm.specification" placeholder="如：10mg*10片" />
                </el-form-item>
                <el-form-item label="厂家" prop="manufacturer">
                    <el-input v-model="currentDrugForm.manufacturer" placeholder="请输入生产厂家" />
                </el-form-item>
                <el-form-item label="单位" prop="unit">
                    <el-input v-model="currentDrugForm.unit" placeholder="如：盒、瓶" />
                </el-form-item>
                <el-form-item label="价格" prop="price">
                    <el-input-number v-model="currentDrugForm.price" :precision="2" :min="0" :step="0.1" placeholder="请输入价格" style="width: 100%" />
                </el-form-item>
                <el-form-item label="初始库存" prop="stockQuantity">
                    <el-input-number v-model="currentDrugForm.stockQuantity" :min="0" :step="1" placeholder="请输入库存数量" style="width: 100%" :disabled="!!currentDrugForm.id" />
                    <div v-if="currentDrugForm.id" class="form-tip">编辑模式下请通过入库/出库功能调整库存</div>
                </el-form-item>
                <el-form-item label="预警阈值" prop="warningThreshold">
                    <el-input-number v-model="currentDrugForm.warningThreshold" :min="0" :step="1" placeholder="请输入预警阈值" style="width: 100%" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editDialogVisible = false">
                    <el-icon><Close /></el-icon> 取消
                </el-button>
                <el-button type="primary" @click="handleSave" :loading="submitting">
                    <el-icon><Check /></el-icon> 保存
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus, Search, RefreshLeft, Edit, Check, Close } from '@element-plus/icons-vue'
import { drugApi, type Drug } from '@/api/drug'
import { inventoryLogApi } from '@/api/inventoryLog'

const loading = ref(false)
const tableData = ref<Drug[]>([])
const total = ref(0)
const lowStockDrugs = ref<Drug[]>([])
const submitting = ref(false)

const searchParams = reactive({
    page: 1,
    size: 10,
    keyword: '',
    status: undefined as number | undefined
})

const inboundDialogVisible = ref(false)
const outboundDialogVisible = ref(false)
const currentDrug = ref<Drug>({ name: '', code: '', price: 0, stockQuantity: 0 })

const inboundForm = reactive({
    drugId: 0,
    quantity: 1,
    batchNumber: '',
    expiryDate: '',
    operatorId: 1
})

const outboundForm = reactive({
    drugId: 0,
    quantity: 1,
    operatorId: 1
})

// 编辑/新增相关
const editDialogVisible = ref(false)
const drugFormRef = ref<FormInstance>()
const currentDrugForm = reactive<Drug>({
    name: '',
    code: '',
    specification: '',
    manufacturer: '',
    unit: '',
    price: 0,
    warningThreshold: 10,
    isActive: 1
})

const rules = {
    name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
    code: [{ required: true, message: '请输入药品编码', trigger: 'blur' }],
    unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
    price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await drugApi.list({ 
            page: searchParams.page, 
            size: searchParams.size,
            keyword: searchParams.keyword,
            status: searchParams.status
        })
        tableData.value = res.data.records
        total.value = res.data.total
    } catch (error) {
        console.error('获取药品列表失败', error)
    } finally {
        loading.value = false
    }
}

const resetSearch = () => {
    searchParams.keyword = ''
    searchParams.status = undefined
    searchParams.page = 1
    fetchData()
}

const fetchLowStockDrugs = async () => {
    try {
        const res = await drugApi.getLowStock()
        lowStockDrugs.value = res.data
    } catch (error) {
        console.error('获取库存预警失败', error)
    }
}

const showAddDialog = () => {
    currentDrugForm.id = undefined
    currentDrugForm.name = ''
    currentDrugForm.code = ''
    currentDrugForm.specification = ''
    currentDrugForm.manufacturer = ''
    currentDrugForm.unit = ''
    currentDrugForm.price = 0
    currentDrugForm.stockQuantity = 0
    currentDrugForm.warningThreshold = 10
    currentDrugForm.isActive = 1
    
    editDialogVisible.value = true
}

const showEditDialog = (drug: Drug) => {
    Object.assign(currentDrugForm, drug)
    editDialogVisible.value = true
}

const handleSave = async () => {
    if (!drugFormRef.value) return
    
    await drugFormRef.value.validate(async (valid) => {
        if (valid) {
            submitting.value = true
            try {
                if (currentDrugForm.id) {
                    await drugApi.update(currentDrugForm.id, currentDrugForm)
                    ElMessage.success('更新成功')
                } else {
                    await drugApi.create(currentDrugForm)
                    ElMessage.success('创建成功')
                }
                editDialogVisible.value = false
                fetchData()
            } catch (error: any) {
                ElMessage.error(error.response?.data?.msg || '保存失败')
            } finally {
                submitting.value = false
            }
        }
    })
}

const showInboundDialog = (drug: Drug) => {
    currentDrug.value = drug
    inboundForm.drugId = drug.id!
    inboundForm.quantity = 1
    inboundForm.batchNumber = ''
    inboundForm.expiryDate = ''
    inboundDialogVisible.value = true
}

const showOutboundDialog = (drug: Drug) => {
    currentDrug.value = drug
    outboundForm.drugId = drug.id!
    outboundForm.quantity = 1
    outboundDialogVisible.value = true
}

const handleInbound = async () => {
    submitting.value = true
    try {
        await inventoryLogApi.inbound(inboundForm)
        ElMessage.success('入库成功')
        inboundDialogVisible.value = false
        fetchData()
        fetchLowStockDrugs()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '入库失败')
    } finally {
        submitting.value = false
    }
}

const handleOutbound = async () => {
    submitting.value = true
    try {
        await inventoryLogApi.outbound(outboundForm)
        ElMessage.success('出库成功')
        outboundDialogVisible.value = false
        fetchData()
        fetchLowStockDrugs()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '出库失败')
    } finally {
        submitting.value = false
    }
}

onMounted(() => {
    fetchData()
    fetchLowStockDrugs()
})
</script>

<style scoped>
.drug-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-start;
}

.form-tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.5;
    margin-top: 5px;
}
</style>
