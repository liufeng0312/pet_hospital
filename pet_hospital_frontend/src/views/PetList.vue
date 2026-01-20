<template>
  <div class="pet-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>宠物管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增宠物
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="宠物名称">
          <el-input v-model="searchForm.keyword" placeholder="宠物名称" clearable />
        </el-form-item>
        <el-form-item label="物种">
          <el-select v-model="searchForm.species" placeholder="全部" clearable style="width: 200px;">
            <el-option label="狗" value="狗" />
            <el-option label="猫" value="猫" />
            <el-option label="兔子" value="兔子" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><RefreshLeft /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column label="照片" min-width="80">
          <template #default="{ row }">
            <el-avatar :src="row.photoUrl || defaultAvatar" :size="50" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="宠物名称" min-width="120" />
        <el-table-column prop="species" label="物种" min-width="80" />
        <el-table-column prop="breed" label="品种" min-width="100" />
        <el-table-column label="性别" min-width="80">
          <template #default="{ row }">
            {{ getGenderText(row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="体重(kg)" min-width="100" />
        <el-table-column label="主人" min-width="120">
          <template #default="{ row }">
            {{ row.customer?.name || '-' }}
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="选择主人" required>
          <el-select v-model="formData.customerId" filterable remote placeholder="搜索客户"
            :remote-method="searchCustomer" :loading="customerLoading" style="width: 100%">
            <el-option v-for="c in customerOptions" :key="c.id" :label="`${c.name} (${c.phone})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宠物名称" required>
          <el-input v-model="formData.name" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="物种" required>
              <el-select v-model="formData.species" placeholder="请选择" style="width: 100%">
                <el-option label="狗" value="狗" />
                <el-option label="猫" value="猫" />
                <el-option label="兔子" value="兔子" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品种">
              <el-input v-model="formData.breed" placeholder="如: 金毛、英短" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="formData.gender" style="width: 100%">
                <el-option label="公" :value="1" />
                <el-option label="母" :value="2" />
                <el-option label="未知" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)">
              <el-input-number v-model="formData.weight" :min="0" :precision="2" placeholder="请输入体重" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="出生日期">
          <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="照片">
          <div style="display: flex; gap: 10px; align-items: flex-start; width: 100%;">
            <el-upload
              class="avatar-uploader"
              action="#"
              :http-request="customUpload"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
            >
              <img v-if="formData.photoUrl" :src="formData.photoUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div style="flex: 1;">
              <el-input v-model="formData.photoUrl" placeholder="或输入图片 URL" clearable />
              <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                点击左侧加号上传，或直接粘贴图片链接
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.notes" type="textarea" rows="2" placeholder="过敏史、特殊注意事项等" />
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
import { petApi, uploadApi, type Pet } from '../api/pet'
import { customerApi, type Customer } from '../api/customer'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const loading = ref(false)
const submitting = ref(false)
const customerLoading = ref(false)
const tableData = ref<Pet[]>([])
const customerOptions = ref<Customer[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增宠物')
const isEdit = ref(false)

const customUpload = async (options: any) => {
  const { file, onSuccess, onError } = options
  try {
    const res = await uploadApi.uploadImage(file)
    onSuccess(res)
  } catch (e) {
    onError(e)
  }
}

const searchForm = reactive({
  keyword: '',
  species: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const formData = reactive<Pet>({
  customerId: undefined,
  name: '',
  species: '',
  breed: '',
  gender: undefined,
  birthDate: '',
  weight: undefined,
  photoUrl: '',
  notes: ''
})

const getGenderText = (gender: number) => {
  const texts: Record<number, string> = { 1: '公', 2: '母', 0: '未知' }
  return texts[gender] || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await petApi.list({
      keyword: searchForm.keyword,
      species: searchForm.species,
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

const searchCustomer = async (query: string) => {
  if (!query) return
  customerLoading.value = true
  try {
    const res = await customerApi.list({ keyword: query, page: 1, size: 20 })
    customerOptions.value = res.data.records
  } finally {
    customerLoading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.species = ''
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增宠物'
  Object.assign(formData, { id: undefined, customerId: undefined, name: '', species: '', breed: '', gender: undefined, birthDate: '', weight: undefined, photoUrl: '', notes: '' })
  dialogVisible.value = true
}

const handleEdit = (row: Pet) => {
  isEdit.value = true
  dialogTitle.value = '编辑宠物'
  Object.assign(formData, row)
  if (row.customer) {
    customerOptions.value = [row.customer as Customer]
  }
  dialogVisible.value = true
}

const handleUploadSuccess = (response: any) => {
  if (response.code === 200) {
    formData.photoUrl = 'http://localhost:8080' + response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (isEdit.value && formData.id) {
      await petApi.update(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await petApi.create(formData)
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

const handleDelete = (row: Pet) => {
  ElMessageBox.confirm('确认删除该宠物?', '提示', { type: 'warning' })
    .then(async () => {
      await petApi.delete(row.id!)
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
.pet-list {
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
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-uploader:hover {
  border-color: #409eff;
}
.avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
