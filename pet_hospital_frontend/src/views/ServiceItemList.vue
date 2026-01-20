<template>
    <div class="service-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>服务项目管理</span>
                    <el-button type="primary" @click="showAddDialog">
                        <el-icon><Plus /></el-icon>新建项目
                    </el-button>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchParams" class="search-form">
                <el-form-item label="项目名称">
                    <el-input v-model="searchParams.keyword" placeholder="项目名称" clearable style="width: 200px" />
                </el-form-item>
                
                <el-form-item label="类别">
                    <el-select v-model="searchParams.category" placeholder="全部" clearable style="width: 150px">
                        <el-option label="疫苗" value="VACCINE" />
                        <el-option label="手术" value="SURGERY" />
                        <el-option label="检查" value="EXAM" />
                        <el-option label="化验" value="LAB" />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="状态">
                    <el-select v-model="searchParams.isActive" placeholder="全部" clearable style="width: 120px">
                        <el-option label="启用" :value="1" />
                        <el-option label="禁用" :value="0" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch">
                        <el-icon><RefreshLeft /></el-icon> 重置
                    </el-button>
                </el-form-item>
            </el-form>


            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="name" label="项目名称" min-width="180" />
                <el-table-column prop="category" label="类别" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="getCategoryType(row.category)">{{ getCategoryLabel(row.category) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="price" label="价格" min-width="120">
                    <template #default="{ row }">
                        ¥{{ row.price?.toFixed(2) }}
                    </template>
                </el-table-column>
                <el-table-column prop="description" label="描述" />
                <el-table-column prop="isActive" label="状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.isActive ? 'success' : 'danger'">
                            {{ row.isActive ? '启用' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="showEditDialog(row)">
                            <el-icon><Edit /></el-icon> 编辑
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-wrapper">
                <el-pagination
                    v-model:current-page="searchParams.page"
                    v-model:page-size="searchParams.size"
                    :total="total"
                    layout="total, prev, pager, next"
                    @current-change="fetchData"
                />
            </div>
        </el-card>

        <!-- 编辑/新建弹窗 -->
        <el-dialog v-model="dialogVisible" :title="currentForm.id ? '编辑项目' : '新建项目'" width="500px">
            <el-form :model="currentForm" label-width="100px">
                <el-form-item label="名称" required>
                    <el-input v-model="currentForm.name" />
                </el-form-item>
                <el-form-item label="类别" required>
                    <el-select v-model="currentForm.category" placeholder="请选择类别">
                        <el-option label="疫苗" value="VACCINE" />
                        <el-option label="手术" value="SURGERY" />
                        <el-option label="检查" value="EXAM" />
                        <el-option label="化验" value="LAB" />
                    </el-select>
                </el-form-item>
                <el-form-item label="价格" required>
                    <el-input-number v-model="currentForm.price" :min="0" :precision="2" placeholder="请输入价格" />
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="currentForm.description" type="textarea" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-switch
                        v-model="currentForm.isActive"
                        :active-value="1"
                        :inactive-value="0"
                        active-text="启用"
                        inactive-text="禁用"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">
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
import { ElMessage } from 'element-plus'
import { Plus, Search, RefreshLeft, Edit, Check, Close } from '@element-plus/icons-vue'
import { serviceItemApi, type ServiceItem } from '@/api/serviceItem'

const loading = ref(false)
const tableData = ref<ServiceItem[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const submitting = ref(false)

const searchParams = reactive({
    page: 1,
    size: 10,
    keyword: '',
    category: '',
    isActive: undefined as number | undefined
})

const currentForm = reactive<ServiceItem>({
    name: '',
    category: 'EXAM',
    price: 0,
    isActive: 1
})

const fetchData = async () => {
    loading.value = true
    try {
        const res = await serviceItemApi.list(searchParams)
        tableData.value = res.data.records || res.data
        total.value = res.data.total || tableData.value.length
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    searchParams.page = 1
    fetchData()
}

const resetSearch = () => {
    searchParams.keyword = ''
    searchParams.category = ''
    searchParams.isActive = undefined
    searchParams.page = 1
    fetchData()
}

const showAddDialog = () => {
    currentForm.id = undefined
    currentForm.name = ''
    currentForm.category = 'EXAM'
    currentForm.price = 0
    currentForm.description = ''
    currentForm.isActive = 1
    dialogVisible.value = true
}

const showEditDialog = (row: ServiceItem) => {
    Object.assign(currentForm, row)
    dialogVisible.value = true
}

const handleSave = async () => {
    submitting.value = true
    try {
        if (currentForm.id) {
            await serviceItemApi.update(currentForm.id, currentForm)
            ElMessage.success('更新成功')
        } else {
            await serviceItemApi.create(currentForm)
            ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchData()
    } catch (error: any) {
        ElMessage.error('保存失败')
    } finally {
        submitting.value = false
    }
}

const getCategoryLabel = (category: string) => {
    const map: Record<string, string> = {
        'VACCINE': '疫苗',
        'SURGERY': '手术',
        'EXAM': '检查',
        'LAB': '化验'
    }
    return map[category] || category
}

const getCategoryType = (category: string) => {
    const map: Record<string, string> = {
        'VACCINE': 'success',
        'SURGERY': 'danger',
        'EXAM': 'warning',
        'LAB': 'info'
    }
    return map[category] || ''
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.service-list {
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
</style>
