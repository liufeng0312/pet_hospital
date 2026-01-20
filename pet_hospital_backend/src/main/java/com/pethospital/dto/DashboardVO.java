package com.pethospital.dto;

import com.pethospital.entity.Article;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    // 关键指标
    private Map<String, Object> kpis;
    
    // 趋势图数据 (日期 -> 数量)
    private Map<String, Object> trendChart;
    
    // 最近动态
    private List<Map<String, Object>> activities;
    
    // 待办事项
    private List<Map<String, Object>> todos;
    
    // 健康小知识
    private Article healthTip;
}
