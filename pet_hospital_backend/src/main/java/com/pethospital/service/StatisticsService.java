package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.dto.DashboardVO;
import com.pethospital.entity.Bill;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsService {

    /**
     * 获取每日收入统计
     * @return List<Map<String, Object>> key: date, value: amount
     */
    List<Map<String, Object>> getDailyIncome(LocalDate start, LocalDate end);

    /**
     * 获取收入构成统计
     * @return List<Map<String, Object>> key: type, value: amount
     */
    List<Map<String, Object>> getIncomeComposition(LocalDate start, LocalDate end);
    
    /**
     * 获取今日关键指标
     */
    Map<String, Object> getKeyMetrics();
    /**
     * 获取仪表盘聚合数据
     */
    DashboardVO getDashboardData();
}
