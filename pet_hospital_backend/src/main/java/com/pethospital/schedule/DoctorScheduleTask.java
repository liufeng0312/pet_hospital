package com.pethospital.schedule;

import com.pethospital.entity.DoctorSchedule;
import com.pethospital.entity.Employee;
import com.pethospital.service.DoctorScheduleService;
import com.pethospital.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 医生排班定时任务
 * 每天凌晨1点自动生成第7天的排班
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DoctorScheduleTask {
    
    private final DoctorScheduleService scheduleService;
    private final EmployeeService employeeService;
    
    /**
     * 每天凌晨1点执行，自动生成第7天的排班
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateSeventhDaySchedule() {
        log.info("开始执行定时任务：生成第7天排班");
        
        try {
            // 计算第7天的日期
            LocalDate targetDate = LocalDate.now().plusDays(6);
            
            // 检查该日期是否已有排班
            List<DoctorSchedule> existingSchedules = scheduleService.getSchedulesByDateRange(targetDate, targetDate);
            if (!existingSchedules.isEmpty()) {
                log.info("第7天 ({}) 已有排班数据，跳过生成", targetDate);
                return;
            }
            
            // 获取所有在职医生
            List<Employee> doctors = employeeService.getDoctors();
            if (doctors.isEmpty()) {
                log.warn("没有找到在职医生，无法生成排班");
                return;
            }
            
            // 生成排班数据
            List<DoctorSchedule> newSchedules = new ArrayList<>();
            
            // 为每位医生根据固定轮班模式生成排班
            for (int i = 0; i < doctors.size(); i++) {
                Employee doctor = doctors.get(i);
                DoctorSchedule schedule = new DoctorSchedule();
                schedule.setDoctorId(doctor.getId());
                schedule.setWorkDate(targetDate);
                schedule.setIsAvailable(1);
                
                // 根据医生索引和日期决定班次（简单的轮班逻辑）
                int dayOffset = (int) (targetDate.toEpochDay() % 4);
                int doctorIndex = i % 4;
                
                // 轮班逻辑：保证每天至少有2位医生全天值班
                if ((doctorIndex + dayOffset) % 4 == 0) {
                    schedule.setShiftType("MORNING");
                    schedule.setMaxAppointments(8);
                } else if ((doctorIndex + dayOffset) % 4 == 1) {
                    schedule.setShiftType("AFTERNOON");
                    schedule.setMaxAppointments(8);
                } else if ((doctorIndex + dayOffset) % 4 == 2) {
                    schedule.setShiftType("FULL_DAY");
                    schedule.setMaxAppointments(15);
                } else {
                    // 每4天轮休一次
                    if (dayOffset == 3 && doctorIndex == 3) {
                        schedule.setShiftType("OFF");
                        schedule.setIsAvailable(0);
                        schedule.setMaxAppointments(0);
                    } else {
                        schedule.setShiftType("FULL_DAY");
                        schedule.setMaxAppointments(15);
                    }
                }
                
                newSchedules.add(schedule);
            }
            
            // 批量保存排班
            boolean success = scheduleService.batchSaveSchedules(newSchedules);
            if (success) {
                log.info("成功生成第7天 ({}) 的排班，共 {} 条记录", targetDate, newSchedules.size());
            } else {
                log.error("生成第7天排班失败");
            }
            
        } catch (Exception e) {
            log.error("执行定时任务失败", e);
        }
    }
    
    /**
     * 测试方法：手动触发排班生成（在开发时可以取消注释测试）
     * 正式环境应注释掉此方法
     */
    // @Scheduled(fixedDelay = 60000) // 每60秒执行一次，仅用于测试
    public void testGenerateSchedule() {
        log.info("测试：手动触发排班生成");
        generateSeventhDaySchedule();
    }
}
