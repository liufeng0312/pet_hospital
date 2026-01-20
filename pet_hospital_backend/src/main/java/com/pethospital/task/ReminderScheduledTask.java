package com.pethospital.task;

import com.pethospital.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 智能提醒定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduledTask {

    private final ReminderService reminderService;

    /**
     * 每天凌晨2点自动生成智能提醒
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoGenerateReminders() {
        log.info("开始执行智能提醒生成任务...");
        try {
            reminderService.generateReminders();
            log.info("智能提醒生成任务执行完成");
        } catch (Exception e) {
            log.error("智能提醒生成任务执行失败", e);
        }
    }
}
