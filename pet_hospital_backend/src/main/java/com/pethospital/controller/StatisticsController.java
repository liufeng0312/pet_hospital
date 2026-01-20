package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Api(tags = "统计报表")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @ApiOperation("获取每日收入趋势")
    @GetMapping("/daily-income")
    public Result<List<Map<String, Object>>> getDailyIncome() {
        // 默认最近 30 天
        return Result.success(statisticsService.getDailyIncome(LocalDate.now().minusDays(30), LocalDate.now()));
    }

    @ApiOperation("获取收入构成")
    @GetMapping("/composition")
    public Result<List<Map<String, Object>>> getComposition() {
        return Result.success(statisticsService.getIncomeComposition(LocalDate.now().minusDays(30), LocalDate.now()));
    }

    @ApiOperation("获取关键指标")
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics() {
        return Result.success(statisticsService.getKeyMetrics());
    }

    @ApiOperation("获取仪表盘数据")
    @GetMapping("/dashboard")
    public Result<com.pethospital.dto.DashboardVO> getDashboard() {
        return Result.success(statisticsService.getDashboardData());
    }
}
