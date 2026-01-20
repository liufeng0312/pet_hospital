<template>
    <div class="employee-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>员工(医生)管理</span>
                    <el-button type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon> 新增员工
                    </el-button>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="姓名">
                    <el-input v-model="searchForm.keyword" placeholder="姓名/用户名" clearable style="width: 200px" />
                </el-form-item>
                
                <el-form-item label="角色">
                    <el-select v-model="searchForm.role" placeholder="全部" clearable style="width: 150px">
                        <el-option label="医生" value="DOCTOR" />
                        <el-option label="前台" value="RECEPTIONIST" />
                        <el-option label="管理员" value="ADMIN" />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="状态">
                    <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
                        <el-option label="在职" :value="1" />
                        <el-option label="离职" :value="0" />
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
            <el-table :data="employees" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" min-width="80" />
                <el-table-column label="头像" min-width="80">
                    <template #default="{ row }">
                        <el-avatar :size="40" :src="row.avatar">
                            {{ row.name?.charAt(0) }}
                        </el-avatar>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="姓名" min-width="120" />
                <el-table-column prop="username" label="用户名" min-width="120" />
                <el-table-column prop="role" label="角色" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="getRoleType(row.role)">{{ row.role }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="phone" label="电话" min-width="150" />
                <el-table-column prop="bio" label="个人介绍" min-width="250">
                    <template #default="{ row }">
                        {{ row.bio || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                            {{ row.status === 1 ? '在职' : '离职' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" @click="handleEdit(row)">
                            <el-icon><Edit /></el-icon> 编辑
                        </el-button>
                        <el-button 
                            size="small" 
                            type="danger" 
                            @click="handleDelete(row)"
                            v-if="row.status === 1"
                        >
                            <el-icon><Delete /></el-icon> 离职
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            
            <!-- 分页 -->
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="fetchData"
                @current-change="fetchData"
                style="margin-top: 20px"
            />
        </el-card>

        <!-- 编辑对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="isEdit ? '编辑员工' : '新增员工'"
            width="500px"
        >
            <el-form :model="form" label-width="80px">
                <el-form-item label="姓名">
                    <el-input v-model="form.name" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="form.username" placeholder="用于登录" :disabled="isEdit" />
                </el-form-item>
                <el-form-item label="角色">
                    <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
                        <el-option label="医生 (DOCTOR)" value="DOCTOR" />
                        <el-option label="前台 (RECEPTIONIST)" value="RECEPTIONIST" />
                        <el-option label="管理员 (ADMIN)" value="ADMIN" />
                    </el-select>
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="form.phone" placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item label="个人介绍">
                    <el-input v-model="form.bio" type="textarea" :rows="4" placeholder="请输入个人职业介绍" />
                </el-form-item>
                <div v-if="!isEdit" style="margin-left: 80px; color: #909399; font-size: 12px;">
                    * 默认密码为 123456
                </div>
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
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, RefreshLeft, Edit, Delete } from '@element-plus/icons-vue'
import { employeeApi, type Employee } from '@/api/employee'

const loading = ref(false)
const employees = ref<Employee[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)

const searchForm = reactive({
    keyword: '',
    role: '',
    status: undefined as number | undefined
})

const form = reactive<Employee>({
    name: '',
    username: '',
    role: 'DOCTOR',
    phone: '',
    bio: ''
})

const getRoleType = (role: string) => {
    switch(role) {
        case 'ADMIN': return 'danger'
        case 'DOCTOR': return 'success'
        default: return 'info'
    }
}

const fetchData = async () => {
    loading.value = true
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            ...searchForm
        }
        const res = await employeeApi.list(params)
        employees.value = res.data.records || res.data
        total.value = res.data.total || employees.value.length
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    currentPage.value = 1
    fetchData()
}

const resetSearch = () => {
    searchForm.keyword = ''
    searchForm.role = ''
    searchForm.status = undefined
    currentPage.value = 1
    fetchData()
}

const handleAdd = () => {
    isEdit.value = false
    form.id = undefined
    form.name = ''
    form.username = ''
    form.role = 'DOCTOR'
    form.phone = ''
    form.bio = ''
    dialogVisible.value = true
}

const handleEdit = (row: Employee) => {
    isEdit.value = true
    Object.assign(form, row)
    dialogVisible.value = true
}

const handleDelete = async (row: Employee) => {
    try {
        await ElMessageBox.confirm(`确定要将 ${row.name} 设为离职状态吗？`, '提示', {
            type: 'warning'
        })
        await employeeApi.delete(row.id!)
        ElMessage.success('操作成功')
        fetchData()
    } catch {
        // cancel
    }
}

const handleSubmit = async () => {
    if (!form.name || !form.username) {
        ElMessage.warning('请填写必填项')
        return
    }
    
    try {
        if (isEdit.value) {
            await employeeApi.update(form.id!, form)
            ElMessage.success('更新成功')
        } else {
            await employeeApi.create(form)
            ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchData()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.employee-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
