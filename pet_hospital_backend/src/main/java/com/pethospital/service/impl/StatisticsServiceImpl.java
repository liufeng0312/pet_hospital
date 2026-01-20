package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.dto.DashboardVO;
import com.pethospital.entity.Article;
import com.pethospital.entity.Bill;
import com.pethospital.entity.Registration;
import com.pethospital.mapper.ArticleMapper;
import com.pethospital.mapper.BillMapper;
import com.pethospital.mapper.DrugMapper;
import com.pethospital.mapper.RegistrationMapper;
import com.pethospital.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final BillMapper billMapper;
    private final RegistrationMapper registrationMapper;
    private final ArticleMapper articleMapper;
    private final DrugMapper drugMapper;
    private final com.pethospital.mapper.ReminderMapper reminderMapper;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 1. KPIs
        Map<String, Object> kpis = new HashMap<>();
        
        // 今日预约/挂号
        Long todayRegCount = registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .apply("DATE(created_at) = {0}", today));
        kpis.put("todayRegistrations", todayRegCount);
        
        // 在诊宠物 (IN_PROGRESS)
        Long inProgressCount = registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getStatus, "IN_PROGRESS"));
        kpis.put("inProgressPets", inProgressCount);
        
        // 待处理账单 (status=0)
        Long pendingBillsCount = billMapper.selectCount(new LambdaQueryWrapper<Bill>()
                .eq(Bill::getStatus, 0));
        kpis.put("pendingBills", pendingBillsCount);
        
        // 昨日营收
        List<Bill> yesterdayBills = billMapper.selectList(new LambdaQueryWrapper<Bill>()
                .eq(Bill::getStatus, 1)
                .apply("DATE(paid_at) = {0}", yesterday));
        BigDecimal yesterdayRevenue = yesterdayBills.stream()
                .map(Bill::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        kpis.put("yesterdayRevenue", yesterdayRevenue);
        
        vo.setKpis(kpis);

        // 2. 趋势图 (近7天)
        // 简化实现：循环查询7天的数据（生产环境应优化为SQL聚合）
        Map<String, Object> trend = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> regCounts = new ArrayList<>();
        List<Long> appCounts = new ArrayList<>(); // 假设这里我们区分 现场挂号 vs 预约，或者 门诊 vs ??? 
        // 暂时用 Total vs Appointment
        
        LocalDate startDate = today.minusDays(6);
        for (int i = 0; i < 7; i++) {
            LocalDate d = startDate.plusDays(i);
            String dateStr = d.format(DateTimeFormatter.ofPattern("MM-dd"));
            dates.add(dateStr);
            
            // 每日总挂号
            regCounts.add(registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                    .apply("DATE(created_at) = {0}", d)));
            
            // 每日预约 (APPOINTMENT)
            appCounts.add(registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                    .eq(Registration::getType, "APPOINTMENT")
                    .apply("DATE(created_at) = {0}", d)));
        }
        trend.put("dates", dates);
        trend.put("total", regCounts);
        trend.put("appointment", appCounts);
        vo.setTrendChart(trend);

        // 3. 最近动态 (最新的5条挂号)
        // 使用 selectPageWithDetails 以获取客户名
        List<Registration> recentRegs = registrationMapper.selectPageWithDetails(
                new Page<>(1, 5), null, null);
        
        List<Map<String, Object>> activities = recentRegs.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            String name = r.getCustomer() != null ? r.getCustomer().getName() : "未知客户";
            String pet = r.getPet() != null ? r.getPet().getName() : "未知宠物";
            String action = r.getStatus().equals("WAITING") ? "预约了挂号" : 
                           r.getStatus().equals("IN_PROGRESS") ? "正在就诊" : "完成了就诊";
            
            map.put("text", String.format("%s 的宠物“%s”%s", name, pet, action));
            map.put("time",  r.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));
            map.put("color", "#3b82f6"); // 默认蓝
            return map;
        }).collect(Collectors.toList());
        vo.setActivities(activities);

        // 4. 智能提醒 (替换原待办事项)
        List<com.pethospital.entity.Reminder> reminders = reminderMapper.selectAllWithDetails();
        List<Map<String, Object>> todos = reminders.stream()
                .filter(r -> r.getStatus() == 0) // 仅显示未发送/未处理的
                .limit(5) // 只显示最近5条
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    String name = r.getCustomer() != null ? r.getCustomer().getName() : "未知客户";
                    String petName = r.getPet() != null ? r.getPet().getName() : "未知宠物";
                    String typeText = "提醒";
                    String color = "#8b5cf6"; // 紫色
                    
                    if ("VACCINE".equals(r.getType())) {
                        typeText = "疫苗";
                        color = "#10b981"; // 绿
                    } else if ("BIRTHDAY".equals(r.getType())) {
                        typeText = "生日";
                        color = "#f59e0b"; // 橙
                    } else if ("FOLLOW_UP".equals(r.getType())) {
                        typeText = "回访";
                        color = "#3b82f6"; // 蓝
                    }
                    
                    map.put("text", String.format("%s 的宠物“%s”需要%s", name, petName, typeText));
                    map.put("tag", typeText);
                    map.put("tagColor", color);
                    map.put("done", false);
                    return map;
                })
                .collect(Collectors.toList());
        
        // 如果没有提醒，保留待处理账单作为补充
        if (todos.isEmpty() && pendingBillsCount > 0) {
             Map<String, Object> t = new HashMap<>();
            t.put("text", "有 " + pendingBillsCount + " 笔账单待支付");
            t.put("tag", "财务");
            t.put("tagColor", "#ef4444");
            t.put("done", false);
            todos.add(t);
        }
        
        vo.setTodos(todos);

        // 5. 健康小知识
        Page<Article> articlePage = articleMapper.selectPage(new Page<>(1, 1), 
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getIsPublished, 1)
                        .orderByDesc(Article::getCreatedAt));
        if (!articlePage.getRecords().isEmpty()) {
            vo.setHealthTip(articlePage.getRecords().get(0));
        }

        return vo;
    }

    @Override
    public List<Map<String, Object>> getDailyIncome(LocalDate start, LocalDate end) {
        List<Bill> bills = billMapper.selectList(null);
        Map<String, BigDecimal> dailyMap = bills.stream()
                .filter(b -> b.getStatus() == 1 && b.getPaidAt() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getPaidAt().toLocalDate().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Bill::getFinalAmount, BigDecimal::add)
                ));
        List<Map<String, Object>> result = new ArrayList<>();
        dailyMap.forEach((date, amount) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);
            item.put("amount", amount);
            result.add(item);
        });
        result.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
        return result;
    }

    @Override
    public List<Map<String, Object>> getIncomeComposition(LocalDate start, LocalDate end) {
        List<Bill> bills = billMapper.selectList(null);
        Map<String, BigDecimal> typeMap = bills.stream()
                .filter(b -> b.getStatus() == 1)
                .collect(Collectors.groupingBy(
                        b -> "PRESCRIPTION".equals(b.getRelatedType()) ? "医疗收入" : "其他收入",
                        Collectors.reducing(BigDecimal.ZERO, Bill::getFinalAmount, BigDecimal::add)
                ));
        List<Map<String, Object>> result = new ArrayList<>();
        typeMap.forEach((type, amount) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", type);
            item.put("value", amount);
            result.add(item);
        });
        return result;
    }

    @Override
    public Map<String, Object> getKeyMetrics() {
        List<Bill> bills = billMapper.selectList(null);
        LocalDate today = LocalDate.now();
        BigDecimal todayIncome = bills.stream()
                .filter(b -> b.getStatus() == 1 && b.getPaidAt().toLocalDate().isEqual(today))
                .map(Bill::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long todayOrderCount = bills.stream()
                .filter(b -> b.getStatus() == 1 && b.getPaidAt().toLocalDate().isEqual(today))
                .count();
        BigDecimal totalIncome = bills.stream()
                .filter(b -> b.getStatus() == 1)
                .map(Bill::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> map = new HashMap<>();
        map.put("todayIncome", todayIncome);
        map.put("todayOrderCount", todayOrderCount);
        map.put("totalIncome", totalIncome);
        return map;
    }
}
