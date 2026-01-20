package com.pethospital.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethospital.entity.Registration;
import com.pethospital.mapper.RegistrationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 挂号自动过期任务
 * 每天凌晨自动将前一天未完成的挂号标记为已取消
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationExpireTask {

    private final RegistrationMapper registrationMapper;

    /**
     * 每天凌晨1点执行
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void expireOldRegistrations() {
        log.info("开始执行挂号过期任务...");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = LocalDate.now();
            LocalDateTime todayStart = today.atStartOfDay();
            
            // 查询需要过期的挂号记录：
            // 1. 有预约时间的：appointment_time < now
            // 2. 无预约时间的（现场挂号）：created_at在今天之前
            LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w
                    .lt(Registration::getAppointmentTime, now)  // 预约时间已过
                    .or()
                    .isNull(Registration::getAppointmentTime)   // 或没有预约时间
                        .lt(Registration::getCreatedAt, todayStart)) // 且创建于今天之前
                   .in(Registration::getStatus, "WAITING", "IN_PROGRESS", "PENDING");
            
            List<Registration> expiredRegistrations = registrationMapper.selectList(wrapper);
            
            if (expiredRegistrations.isEmpty()) {
                log.info("没有需要过期的挂号记录");
                return;
            }
            
            // 将这些挂号状态改为CANCELLED
            int count = 0;
            for (Registration registration : expiredRegistrations) {
                registration.setStatus("CANCELLED");
                registration.setUpdatedAt(LocalDateTime.now());
                registrationMapper.updateById(registration);
                count++;
            }
            
            log.info("挂号过期任务执行完成，共处理 {} 条记录", count);
            
        } catch (Exception e) {
            log.error("执行挂号过期任务时发生错误", e);
        }
    }
    
    /**
     * 测试方法：手动触发过期任务
     * 可以通过接口调用来测试
     */
    public int manualExpire() {
        log.info("手动触发挂号过期任务...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        
        // 查询需要过期的挂号记录（同主任务逻辑）
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .lt(Registration::getAppointmentTime, now)
                .or()
                .isNull(Registration::getAppointmentTime)
                    .lt(Registration::getCreatedAt, todayStart))
               .in(Registration::getStatus, "WAITING", "IN_PROGRESS", "PENDING");
        
        List<Registration> expiredRegistrations = registrationMapper.selectList(wrapper);
        
        int count = 0;
        for (Registration registration : expiredRegistrations) {
            registration.setStatus("CANCELLED");
            registration.setUpdatedAt(LocalDateTime.now());
            registrationMapper.updateById(registration);
            count++;
        }
        
        log.info("手动过期任务执行完成，共处理 {} 条记录", count);
        return count;
    }
}
