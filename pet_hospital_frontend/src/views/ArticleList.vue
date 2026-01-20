<template>
  <div class="article-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康资讯管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 发布文章
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="标题搜索" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部分类" clearable style="width: 150px">
            <el-option label="疾病预防" value="疾病预防" />
            <el-option label="日常护理" value="日常护理" />
            <el-option label="饮食营养" value="饮食营养" />
            <el-option label="行为训练" value="行为训练" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" min-width="100" />
        <el-table-column label="封面" min-width="100">
          <template #default="{ row }">
            <el-image 
              style="width: 80px; height: 45px; border-radius: 4px" 
              :src="row.coverImage" 
              fit="cover"
              v-if="row.coverImage"
              :preview-src-list="[row.coverImage]"
              preview-teleported
            />
            <span v-else style="color: #909399; font-size: 12px">无封面</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" min-width="100" />
        <el-table-column prop="viewCount" label="浏览量" min-width="80" align="center" />
        <el-table-column label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isPublished === 1 ? 'success' : 'info'">
              {{ row.isPublished === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" min-width="160" />
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
            <el-button 
              size="small" 
              :type="row.isPublished === 1 ? 'warning' : 'success'" 
              @click="handleTogglePublish(row)"
            >
              {{ row.isPublished === 1 ? '下架' : '发布' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)"><el-icon><Delete /></el-icon> 删除</el-button>
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

    <!-- 编辑弹窗 (全屏) -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="80%"
      top="5vh"
      destroy-on-close
      class="article-dialog"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px" style="max-width: 1200px; margin: 0 auto;">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="文章标题" prop="title">
              <el-input v-model="formData.title" placeholder="请输入标题" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="文章分类" prop="category">
              <el-select v-model="formData.category" placeholder="选择分类" style="width: 100%">
                <el-option label="疾病预防" value="疾病预防" />
                <el-option label="日常护理" value="日常护理" />
                <el-option label="饮食营养" value="饮食营养" />
                <el-option label="行为训练" value="行为训练" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
             <el-form-item label="封面图片">
               <el-upload
                 class="cover-uploader"
                 action="#"
                 :show-file-list="false"
                 :http-request="customCoverUpload"
               >
                 <img v-if="formData.coverImage" :src="formData.coverImage" class="cover-image" />
                 <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
               </el-upload>
             </el-form-item>
          </el-col>
           <el-col :span="16">
            <el-form-item label="摘要" prop="summary">
              <el-input v-model="formData.summary" type="textarea" :rows="3" placeholder="文章摘要（选填）" />
            </el-form-item>
            <el-form-item label="作者" prop="author">
              <el-input v-model="formData.author" placeholder="作者名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="正文内容" prop="content">
          <div style="border: 1px solid #ccc">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 500px; overflow-y: hidden;"
              v-model="formData.content"
              :defaultConfig="editorConfig"
              :mode="mode"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="info" @click="handleSubmit(0)">存为草稿</el-button>
          <el-button type="primary" @click="handleSubmit(1)">发布文章</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { articleApi, type Article } from '../api/article'
import { uploadApi } from '../api/pet'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { Search, RefreshLeft, Edit, Delete } from '@element-plus/icons-vue'

// --- WangEditor 配置 ---
const editorRef = shallowRef() // 编辑器实例，必须用 shallowRef
const mode = 'default' // or 'simple'
const toolbarConfig = {}
const editorConfig = { 
    placeholder: '请输入内容...',
    MENU_CONF: {
        uploadImage: {
             // 自定义上传
             async customUpload(file: File, insertFn: any) {
                try {
                    const res = await uploadApi.uploadImage(file)
                    if (res.code === 200) {
                        const url = 'http://localhost:8080' + res.data
                        insertFn(url, file.name, url)
                    } else {
                        ElMessage.error('图片上传失败')
                    }
                } catch (e) {
                    ElMessage.error('图片上传出错')
                }
             }
        }
    }
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
})

const handleCreated = (editor: any) => {
    editorRef.value = editor // 记录 editor 实例，重要！
}

// --- 业务逻辑 ---

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('发布文章')
const formRef = ref()

const searchForm = reactive({
  keyword: '',
  category: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tableData = ref<Article[]>([])

const formData = reactive<Article>({
  id: undefined,
  title: '',
  category: '',
  coverImage: '',
  summary: '',
  content: '',
  author: 'PetHospital',
  isPublished: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}


const customCoverUpload = async (options: any) => {
  const { file } = options
  try {
    const res = await uploadApi.uploadImage(file)
    if (res.code === 200) {
       formData.coverImage = 'http://localhost:8080' + res.data
       ElMessage.success('封面上传成功')
    } else {
       ElMessage.error('上传失败')
    }
  } catch (e) {
    ElMessage.error('上传错误')
  }
}


const fetchData = async () => {
  loading.value = true
  try {
    const res = await articleApi.adminList({
      keyword: searchForm.keyword,
      category: searchForm.category,
      page: pagination.page,
      size: pagination.size
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.category = ''
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '发布文章'
  Object.assign(formData, {
      id: undefined,
      title: '',
      category: '',
      coverImage: '',
      summary: '',
      content: '', // Reset content
      author: 'PetHospital',
      isPublished: 0
  })
  // Clear editor content if exists?
  // Note: v-model handles it, but sometimes explicit clear is safer
  /* if (editorRef.value) {
      editorRef.value.setHtml('<p></p>')
  } */ 
  dialogVisible.value = true
}

const handleEdit = (row: Article) => {
  dialogTitle.value = '编辑文章'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleTogglePublish = async (row: Article) => {
    const newStatus = row.isPublished === 1 ? 0 : 1
    const action = newStatus === 1 ? '发布' : '下架'
    try {
        await articleApi.update(row.id!, { ...row, isPublished: newStatus })
        ElMessage.success(`${action}成功`)
        fetchData()
    } catch (e) {
        ElMessage.error(`${action}失败`)
    }
}

const handleDelete = (row: Article) => {
  ElMessageBox.confirm('确认删除该文章?', '提示', { type: 'warning' })
    .then(async () => {
      await articleApi.delete(row.id!)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}

const handleSubmit = async (publishStatus: number) => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid: boolean) => {
        if (valid) {
            formData.isPublished = publishStatus
            try {
                if (formData.id) {
                    await articleApi.update(formData.id, formData)
                    ElMessage.success('更新成功')
                } else {
                    await articleApi.create(formData)
                    ElMessage.success('创建成功')
                }
                dialogVisible.value = false
                fetchData()
            } catch (e) {
                ElMessage.error('操作失败')
            }
        }
    })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.article-list {
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
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 67.5px; /* 16:9 ratio */
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-uploader:hover {
  border-color: #409eff;
}
.cover-image {
  width: 120px;
  height: 67.5px;
  object-fit: cover;
}
.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
