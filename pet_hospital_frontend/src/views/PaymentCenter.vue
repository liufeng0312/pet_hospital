<template>
    <div class="payment-center">
        <el-card>
            <template #header>
        <div class="card-header">
          <span>支付中心</span>
        </div>
      </template>
      
            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="关键词">
                    <el-input v-model="searchForm.keyword" placeholder="客户姓名" clearable style="width: 200px" />
                </el-form-item>
                
                <el-form-item label="账单类型">
                    <el-select v-model="searchForm.relatedType" placeholder="全部" clearable style="width: 150px">
                        <el-option label="处方" value="PRESCRIPTION" />
                        <el-option label="检验" value="LAB_TEST" />
                        <el-option label="挂号" value="REGISTRATION" />
                        <el-option label="手术" value="SURGERY" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
                </el-form-item>
            </el-form>

            <el-tabs v-model="activeTab" type="card">
                <el-tab-pane label="待支付账单" name="unpaid">
                    <el-table :data="unpaidBills" style="width: 100%" v-loading="loading">
                        <el-table-column prop="id" label="单号" min-width="100" />
                        <el-table-column label="客户" min-width="140">
                            <template #default="{ row }">
                                <span>{{ row.customer?.name }}</span>
                                <el-tag v-if="isVip(row)" type="warning" size="small" effect="dark" round class="ml-2">VIP</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="relatedType" label="类型" min-width="120">
                            <template #default="{ row }">
                                <el-tag>{{ getBillTypeLabel(row.relatedType) }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="应收金额" min-width="120">
                            <template #default="{ row }">
                                <div v-if="isVip(row)" class="price-cell">
                                    <span class="original-price">¥{{ row.totalAmount.toFixed(2) }}</span>
                                    <span class="vip-price">¥{{ (row.totalAmount * 0.9).toFixed(2) }}</span>
                                </div>
                                <span v-else>¥{{ row.totalAmount.toFixed(2) }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="createdAt" label="创建时间" min-width="180">
                            <template #default="{ row }">
                                {{ formatTime(row.createdAt) }}
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" min-width="120" fixed="right">
                            <template #default="{ row }">
                                <el-button type="primary" size="small" @click="showPayDialog(row)">收银</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                        v-model:current-page="unpaidPage"
                        v-model:page-size="unpaidPageSize"
                        :page-sizes="[10, 20, 50, 100]"
                        :total="unpaidTotal"
                        layout="total, sizes, prev, pager, next, jumper"
                        @size-change="fetchUnpaidBills"
                        @current-change="fetchUnpaidBills"
                        style="margin-top: 20px; justify-content: flex-start;"
                    />
                </el-tab-pane>

                <el-tab-pane label="已支付记录" name="paid">
                    <el-table :data="paidBills" style="width: 100%" v-loading="loading">
                        <el-table-column prop="id" label="单号" min-width="100" />
                        <el-table-column label="客户" min-width="140">
                            <template #default="{ row }">
                                <span>{{ row.customer?.name }}</span>
                                <el-tag v-if="row.discountAmount > 0" type="warning" size="small" effect="dark" round class="ml-2">VIP优惠</el-tag>
                            </template>
                        </el-table-column>
                    <el-table-column prop="relatedType" label="类型" min-width="120">
                        <template #default="{ row }">
                            <el-tag>{{ getBillTypeLabel(row.relatedType) }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="实收金额" min-width="120">
                        <template #default="{ row }">
                            <span class="final-amount">¥{{ row.finalAmount.toFixed(2) }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="优惠金额" min-width="100">
                        <template #default="{ row }">
                            <span v-if="row.discountAmount > 0" class="discount-amount">-¥{{ row.discountAmount.toFixed(2) }}</span>
                            <span v-else>-</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="paymentMethod" label="支付方式" min-width="120">
                        <template #default="{ row }">
                            <el-tag type="success">{{ getPaymentMethodLabel(row.paymentMethod) }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="paidAt" label="支付时间" min-width="180">
                        <template #default="{ row }">
                            {{ formatTime(row.paidAt) }}
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination
                    v-model:current-page="paidPage"
                    v-model:page-size="paidPageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="paidTotal"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="fetchPaidBills"
                    @current-change="fetchPaidBills"
                    style="margin-top: 20px; justify-content: flex-start;"
                />
            </el-tab-pane>
        </el-tabs>

        <!-- 支付弹窗 -->
        <el-dialog v-model="payDialogVisible" title="收银台" width="500px">
            <div v-if="currentBill" class="pay-detail">
                <div class="amount-row">
                    <label>应收金额：</label>
                    <span class="original-price">¥{{ currentBill.totalAmount.toFixed(2) }}</span>
                </div>
                
                <div v-if="isVip(currentBill)" class="discount-tip">
                    <el-icon><Star /></el-icon> VIP 客户享受 9 折优惠
                </div>

                <div class="amount-row final">
                    <label>实收金额：</label>
                    <span class="final-price">¥{{ calculateFinalAmount(currentBill).toFixed(2) }}</span>
                </div>

                <div class="payment-methods">
                    <div 
                        class="method-card" 
                        :class="{ active: selectedMethod === 'WECHAT' }"
                        @click="selectedMethod = 'WECHAT'"
                    >
                        <el-icon color="#09BB07" size="24"><ChatDotRound /></el-icon>
                        <span>微信支付</span>
                    </div>
                    <div 
                        class="method-card" 
                        :class="{ active: selectedMethod === 'ALIPAY' }"
                        @click="selectedMethod = 'ALIPAY'"
                    >
                        <el-icon color="#1678FF" size="24"><CreditCard /></el-icon>
                        <span>支付宝</span>
                    </div>
                    <div 
                        class="method-card" 
                        :class="{ active: selectedMethod === 'CASH' }"
                        @click="selectedMethod = 'CASH'"
                    >
                        <el-icon color="#F56C6C" size="24"><Money /></el-icon>
                        <span>现金</span>
                    </div>
                </div>
            </div>
            <template #footer>
                <el-button @click="payDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handlePay" :loading="paying">确认收款</el-button>
            </template>
        </el-dialog>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, ChatDotRound, CreditCard, Money, Star } from '@element-plus/icons-vue'
import { billApi, type Bill } from '@/api/bill'
import dayjs from 'dayjs'

const activeTab = ref('unpaid')
const loading = ref(false)
const unpaidBills = ref<Bill[]>([])
const paidBills = ref<Bill[]>([])

const searchForm = reactive({
    keyword: '',
    relatedType: ''
})

// 分页相关
const unpaidPage = ref(1)
const unpaidPageSize = ref(10)
const unpaidTotal = ref(0)
const paidPage = ref(1)
const paidPageSize = ref(10)
const paidTotal = ref(0)

// 支付相关
const payDialogVisible = ref(false)
const currentBill = ref<Bill | null>(null)
const selectedMethod = ref('WECHAT')
const paying = ref(false)

const fetchUnpaidBills = async () => {
    loading.value = true
    try {
        const res = await billApi.listUnpaid()
        const allBills = res.data || []
        unpaidTotal.value = allBills.length
        
        // 客户端分页
        const start = (unpaidPage.value - 1) * unpaidPageSize.value
        const end = start + unpaidPageSize.value
        unpaidBills.value = allBills.slice(start, end)
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const fetchPaidBills = async () => {
    loading.value = true
    try {
        // 注意：当前后端API不支持搜索参数，先使用原有API
        const res = await billApi.listPaid()
        paidBills.value = res.data
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    // TODO: 等后端支持搜索参数后再实现过滤功能
    // 目前先获取所有数据
    if (activeTab.value === 'unpaid') {
        fetchUnpaidBills()
    } else {
        fetchPaidBills()
    }
}

const resetSearch = () => {
    searchForm.keyword = ''
    searchForm.relatedType = ''
    handleSearch()
}

watch(activeTab, () => {
    handleSearch()
})

const showPayDialog = (bill: Bill) => {
    currentBill.value = bill
    selectedMethod.value = 'WECHAT'
    payDialogVisible.value = true
}

const isVip = (bill: Bill) => {
    // 检查客户是否为VIP (level == 1)
    return bill.customer?.level === 1
}

const calculateFinalAmount = (bill: Bill) => {
    // 如果是VIP客户，显示9折后的价格
    if (isVip(bill)) {
        return bill.totalAmount * 0.9
    }
    return bill.totalAmount
}

const handlePay = async () => {
    if (!currentBill.value) return
    
    paying.value = true
    try {
        await billApi.pay(currentBill.value.id!, selectedMethod.value)
        ElMessage.success('收款成功')
        payDialogVisible.value = false
        handleSearch()
    } catch (error: any) {
        ElMessage.error('收款失败')
    } finally {
        paying.value = false
    }
}

const getPaymentMethodLabel = (method?: string) => {
    const map: Record<string, string> = {
        'WECHAT': '微信',
        'ALIPAY': '支付宝',
        'CASH': '现金'
    }
    return method ? map[method] || method : '-'
}

const getBillTypeLabel = (type?: string) => {
    const map: Record<string, string> = {
        'PRESCRIPTION': '处方单',
        'LAB_TEST': '检验单',
        'REGISTRATION': '挂号单',
        'SURGERY': '手术单'
    }
    return type ? map[type] || '其他' : '其他'
}

const formatTime = (time?: string) => {
    return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : ''
}

onMounted(() => {
    handleSearch()
})
</script>

<style scoped>
.payment-center {
    padding: 20px;
}

.pay-detail {
    padding: 10px;
}
.amount-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    font-size: 16px;
}
.amount-row.final {
    margin-top: 20px;
    font-size: 20px;
    font-weight: bold;
    color: #F56C6C;
}
.discount-tip {
    font-size: 12px;
    color: #E6A23C;
    display: flex;
    align-items: center;
    gap: 5px;
}
.payment-methods {
    display: flex;
    gap: 15px;
    margin-top: 30px;
}
.method-card {
    flex: 1;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    padding: 15px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    transition: all 0.3s;
}
.method-card:hover {
    border-color: #409eff;
    color: #409eff;
}
.method-card.active {
    background-color: #ecf5ff;
    border-color: #409eff;
    color: #409eff;
}
.ml-2 {
    margin-left: 8px;
}
.price-cell {
    display: flex;
    flex-direction: column;
    line-height: 1.2;
}
.original-price {
    text-decoration: line-through;
    color: #909399;
    font-size: 12px;
}
.vip-price {
    color: #F56C6C;
    font-weight: bold;
}
.final-amount {
    font-weight: bold;
    color: #303133;
}
.discount-amount {
    color: #67C23A;
}
</style>
