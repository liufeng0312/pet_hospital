<template>
    <div class="report-center">
        <el-card class="page-header-card">
            <template #header>
                <div class="page-header">
                    <span class="page-title">数据统计报表</span>
                    <el-radio-group v-model="dateRange" @change="handleDateRangeChange" size="default">
                        <el-radio-button value="today">今日</el-radio-button>
                        <el-radio-button value="week">本周</el-radio-button>
                        <el-radio-button value="month">本月</el-radio-button>
                        <el-radio-button value="30days">近30天</el-radio-button>
                        <el-radio-button value="90days">近90天</el-radio-button>
                    </el-radio-group>
                </div>
            </template>

            <!-- 关键指标卡片 -->
            <el-row :gutter="16" class="metric-cards">
                <el-col :xs="12" :sm="8" :md="4">
                    <div class="metric-card">
                        <div class="metric-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                            <el-icon :size="24"><Money /></el-icon>
                        </div>
                        <div class="metric-content">
                            <div class="metric-label">今日收入</div>
                            <div class="metric-value">¥{{ metrics.todayIncome?.toFixed(2) || '0.00' }}</div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="12" :sm="8" :md="4">
                    <div class="metric-card">
                        <div class="metric-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                            <el-icon :size="24"><DocumentCopy /></el-icon>
                        </div>
                        <div class="metric-content">
                            <div class="metric-label">今日订单</div>
                            <div class="metric-value">{{ metrics.todayOrderCount || 0 }}</div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="12" :sm="8" :md="4">
                    <div class="metric-card">
                        <div class="metric-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                            <el-icon :size="24"><TrendCharts /></el-icon>
                        </div>
                        <div class="metric-content">
                            <div class="metric-label">累计总收入</div>
                            <div class="metric-value">¥{{ metrics.totalIncome?.toFixed(2) || '0.00' }}</div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="12" :sm="8" :md="4">
                    <div class="metric-card">
                        <div class="metric-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
                            <el-icon :size="24"><User /></el-icon>
                        </div>
                        <div class="metric-content">
                            <div class="metric-label">在诊宠物</div>
                            <div class="metric-value">{{ registrationStats.inProgress || 0 }}</div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="12" :sm="8" :md="4">
                    <div class="metric-card">
                        <div class="metric-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
                            <el-icon :size="24"><Clock /></el-icon>
                        </div>
                        <div class="metric-content">
                            <div class="metric-label">待支付账单</div>
                            <div class="metric-value">{{ billStats.pending || 0 }}</div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="12" :sm="8" :md="4">
                    <div class="metric-card">
                        <div class="metric-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
                            <el-icon :size="24"><DataAnalysis /></el-icon>
                        </div>
                        <div class="metric-content">
                            <div class="metric-label">平均客单价</div>
                            <div class="metric-value">¥{{ calculateAvgOrderValue() }}</div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </el-card>

        <!-- 图表区域 -->
        <el-row :gutter="20" class="chart-row">
            <el-col :span="16">
                <el-card shadow="hover">
                    <template #header>
                        <div class="chart-header">
                            <span>收入趋势</span>
                            <el-tag size="small">近30天</el-tag>
                        </div>
                    </template>
                    <div ref="lineChartRef" class="chart-container" v-loading="loading"></div>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover">
                    <template #header>
                        <div class="chart-header">
                            <span>收入构成</span>
                        </div>
                    </template>
                    <div ref="pieChartRef" class="chart-container" v-loading="loading"></div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 第二行图表 -->
        <el-row :gutter="20" class="chart-row">
            <el-col :span="12">
                <el-card shadow="hover">
                    <template #header>
                        <div class="chart-header">
                            <span>支付方式统计</span>
                        </div>
                    </template>
                    <div ref="paymentChartRef" class="chart-container" v-loading="loading"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover">
                    <template #header>
                        <div class="chart-header">
                            <span>就诊量趋势</span>
                            <el-tag size="small">近7天</el-tag>
                        </div>
                    </template>
                    <div ref="registrationChartRef" class="chart-container" v-loading="loading"></div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 数据明细表格 -->
        <el-card shadow="hover" class="data-table-card">
            <template #header>
                <div class="chart-header">
                    <span>收入明细</span>
                    <el-button size="small" @click="exportData">
                        <el-icon><Download /></el-icon> 导出
                    </el-button>
                </div>
            </template>
            <el-table :data="dailyDetails" stripe style="width: 100%" :default-sort="{prop: 'date', order: 'descending'}">
                <el-table-column prop="date" label="日期" width="120" sortable></el-table-column>
                <el-table-column prop="amount" label="收入金额" width="150" sortable>
                    <template #default="{ row }">
                        <span style="color: #67C23A; font-weight: bold;">¥{{ row.amount?.toFixed(2) || '0.00' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="orderCount" label="订单数" width="100" sortable></el-table-column>
                <el-table-column prop="avgAmount" label="平均客单价" width="150">
                    <template #default="{ row }">
                        ¥{{ row.avgAmount?.toFixed(2) || '0.00' }}
                    </template>
                </el-table-column>
                <el-table-column prop="note" label="备注"></el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Money, DocumentCopy, TrendCharts, User, Clock, DataAnalysis, Download } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import api from '@/api'

const loading = ref(false)
const dateRange = ref('30days')

const metrics = ref<any>({})
const registrationStats = ref<any>({})
const billStats = ref<any>({})
const dailyDetails = ref<any[]>([])

const lineChartRef = ref<HTMLElement | null>(null)
const pieChartRef = ref<HTMLElement | null>(null)
const paymentChartRef = ref<HTMLElement | null>(null)
const registrationChartRef = ref<HTMLElement | null>(null)

let lineChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let paymentChart: echarts.ECharts | null = null
let registrationChart: echarts.ECharts | null = null

// 获取关键指标
const fetchMetrics = async () => {
    try {
        const res = await api.get('/stats/metrics')
        metrics.value = res.data
    } catch (e) {
        console.error('获取指标失败：', e)
    }
}

// 获取仪表盘数据（包含挂号统计）
const fetchDashboardData = async () => {
    try {
        const res = await api.get('/stats/dashboard')
        const data = res.data
        if (data.kpis) {
            registrationStats.value = {
                inProgress: data.kpis.inProgressPets || 0
            }
            billStats.value = {
                pending: data.kpis.pendingBills || 0
            }
        }
    } catch (e) {
        console.error('获取仪表盘数据失败：', e)
    }
}

// 计算平均客单价
const calculateAvgOrderValue = () => {
    if (!metrics.value.todayOrderCount || metrics.value.todayOrderCount === 0) {
        return '0.00'
    }
    const avg = (metrics.value.totalIncome || 0) / metrics.value.todayOrderCount
    return avg.toFixed(2)
}

// 初始化收入趋势图
const initLineChart = async () => {
    if (!lineChartRef.value) return
    
    loading.value = true
    try {
        const res = await api.get('/stats/daily-income')
        const data = res.data || []
        
        // 同时用于明细表格
        dailyDetails.value = data.map((item: any) => ({
            date: item.date,
            amount: item.amount || 0,
            orderCount: item.orderCount || Math.floor(Math.random() * 20) + 1, // 模拟数据
            avgAmount: item.amount && item.orderCount ? item.amount / item.orderCount : 0,
            note: ''
        }))
        
        lineChart = echarts.init(lineChartRef.value)
        lineChart.setOption({
            tooltip: {
                trigger: 'axis',
                formatter: (params: any) => {
                    const item = params[0]
                    return `${item.name}<br/>收入：¥${item.value?.toFixed(2) || '0.00'}`
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: data.map((item: any) => item.date)
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                    formatter: '¥{value}'
                }
            },
            series: [
                {
                    name: '收入',
                    data: data.map((item: any) => item.amount || 0),
                    type: 'line',
                    smooth: true,
                    areaStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
                            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
                        ])
                    },
                    itemStyle: {
                        color: '#409EFF'
                    },
                    label: {
                        show: false
                    }
                }
            ]
        })
    } catch (e) {
        console.error('初始化收入趋势图失败：', e)
    } finally {
        loading.value = false
    }
}

// 初始化收入构成饼图
const initPieChart = async () => {
    if (!pieChartRef.value) return
    
    try {
        const res = await api.get('/stats/composition')
        const data = res.data || []
        
        pieChart = echarts.init(pieChartRef.value)
        pieChart.setOption({
            tooltip: {
                trigger: 'item',
                formatter: '{b}: ¥{c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                top: 'center'
            },
            series: [
                {
                    name: '收入来源',
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        borderRadius: 10,
                        borderColor: '#fff',
                        borderWidth: 2
                    },
                    label: {
                        show: true,
                        formatter: '{b}\n{d}%'
                    },
                    emphasis: {
                        label: {
                            show: true,
                            fontSize: 16,
                            fontWeight: 'bold'
                        },
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    data: data,
                    color: ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE']
                }
            ]
        })
    } catch (e) {
        console.error('初始化收入构成图失败：', e)
    }
}

// 初始化支付方式统计图
const initPaymentChart = () => {
    if (!paymentChartRef.value) return
    
    // 模拟数据，实际应从后端获取
    const paymentData = [
        { method: '微信支付', amount: 15680, count: 45 },
        { method: '支付宝', amount: 12340, count: 32 },
        { method: '现金', amount: 8920, count: 28 }
    ]
    
    paymentChart = echarts.init(paymentChartRef.value)
    paymentChart.setOption({
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            formatter: (params: any) => {
                const item = params[0]
                const data = paymentData[item.dataIndex]
                return `${item.name}<br/>金额：¥${data.amount}<br/>笔数：${data.count}`
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: paymentData.map(item => item.method)
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '¥{value}'
            }
        },
        series: [
            {
                name: '支付金额',
                type: 'bar',
                data: paymentData.map(item => item.amount),
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#83bff6' },
                        { offset: 1, color: '#188df0' }
                    ]),
                    borderRadius: [8, 8, 0, 0]
                },
                barWidth: '50%'
            }
        ]
    })
}

// 初始化就诊量趋势图
const initRegistrationChart = () => {
    if (!registrationChartRef.value) return
    
    // 模拟数据
    const dates = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    const totalData = [32, 28, 35, 42, 38, 45, 40]
    const appointmentData = [12, 10, 15, 18, 16, 20, 18]
    
    registrationChart = echarts.init(registrationChartRef.value)
    registrationChart.setOption({
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['总就诊量', '预约就诊'],
            bottom: 0
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: dates
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: '总就诊量',
                type: 'line',
                data: totalData,
                smooth: true,
                itemStyle: {
                    color: '#409EFF'
                }
            },
            {
                name: '预约就诊',
                type: 'line',
                data: appointmentData,
                smooth: true,
                itemStyle: {
                    color: '#67C23A'
                }
            }
        ]
    })
}

// 日期范围变化处理
const handleDateRangeChange = (value: string) => {
    ElMessage.info(`切换到: ${
        value === 'today' ? '今日' :
        value === 'week' ? '本周' :
        value === 'month' ? '本月' :
        value === '30days' ? '近30天' : '近90天'
    }`)
    // 实际应该调用API重新获取数据
    fetchAllData()
}

// 导出数据
const exportData = () => {
    ElMessage.success('导出功能开发中...')
    // 实际应该生成CSV并下载
}

// 图表自适应
const handleResize = () => {
    lineChart?.resize()
    pieChart?.resize()
    paymentChart?.resize()
    registrationChart?.resize()
}

// 获取所有数据
const fetchAllData = async () => {
    await Promise.all([
        fetchMetrics(),
        fetchDashboardData()
    ])
    initLineChart()
    initPieChart()
    initPaymentChart()
    initRegistrationChart()
}

onMounted(() => {
    fetchAllData()
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    lineChart?.dispose()
    pieChart?.dispose()
    paymentChart?.dispose()
    registrationChart?.dispose()
})
</script>

<style scoped>
.report-center {
    padding: 20px;
    background: #f5f7fa;
}

.page-header-card {
    margin-bottom: 20px;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
}

.page-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
}

.metric-cards {
    margin-top: 16px;
}

.metric-card {
    display: flex;
    align-items: center;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;
    height: 100%;
}

.metric-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.metric-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    margin-right: 12px;
    flex-shrink: 0;
}

.metric-content {
    flex: 1;
    min-width: 0;
}

.metric-label {
    font-size: 13px;
    color: #909399;
    margin-bottom: 4px;
}

.metric-value {
    font-size: 22px;
    font-weight: bold;
    color: #303133;
    line-height: 1.2;
}

.chart-row {
    margin-top: 20px;
}

.chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.chart-container {
    height: 350px;
    width: 100%;
}

.data-table-card {
    margin-top: 20px;
}

@media (max-width: 768px) {
    .page-header {
        flex-direction: column;
        align-items: flex-start;
    }
    
    .metric-value {
        font-size: 18px;
    }
    
    .chart-container {
        height: 280px;
    }
}
</style>
