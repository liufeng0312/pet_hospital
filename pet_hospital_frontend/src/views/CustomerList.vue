<template>
  <div class="customer-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增客户
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="姓名/电话" clearable />
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="searchForm.level" placeholder="全部" clearable style="width: 200px;">
            <el-option label="VIP" :value="1" />
            <el-option label="普通" :value="2" />
            <el-option label="黑名单" :value="3" />
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

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" min-width="80" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="150" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="level" label="等级" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">{{ getLevelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 16px"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" append-to-body>
      <el-form :model="formData" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="formData.name" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="formData.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="formData.level" placeholder="请选择等级">
            <el-option label="VIP" :value="1" />
            <el-option label="普通" :value="2" />
            <el-option label="黑名单" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.notes" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          <el-icon><Close /></el-icon> 取消
        </el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          <el-icon><Check /></el-icon> 确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft, Edit, Delete, Check, Close } from '@element-plus/icons-vue'
import { customerApi, type Customer } from '../api/customer'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<Customer[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增客户')
const isEdit = ref(false)

const searchForm = reactive({
  keyword: '',
  level: undefined as number | undefined
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const formData = reactive<Customer>({
  name: '',
  phone: '',
  address: '',
  level: 2,
  notes: ''
})

const getLevelType = (level: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: '', 3: 'danger' }
  return types[level] || 'info'
}

const getLevelText = (level: number) => {
  const texts: Record<number, string> = { 0: '微信注册', 1: 'VIP', 2: '普通', 3: '黑名单' }
  return texts[level] || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await customerApi.list({
      keyword: searchForm.keyword,
      level: searchForm.level,
      page: pagination.page,
      size: pagination.size
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.level = undefined
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增客户'
  Object.assign(formData, { id: undefined, name: '', phone: '', address: '', level: 2, notes: '' })
  dialogVisible.value = true
}

const handleEdit = (row: Customer) => {
  isEdit.value = true
  dialogTitle.value = '编辑客户'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (isEdit.value && formData.id) {
      await customerApi.update(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await customerApi.create(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row: Customer) => {
  ElMessageBox.confirm('确认删除该客户?', '提示', { type: 'warning' })
    .then(async () => {
      await customerApi.delete(row.id!)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.customer-list {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 16px;
}
</style>
