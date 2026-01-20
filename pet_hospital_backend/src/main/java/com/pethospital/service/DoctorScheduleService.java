package com.pethospital.service;

import com.pethospital.entity.DoctorSchedule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

public interface DoctorScheduleService extends IService<DoctorSchedule> {
    
    /**
     * Get schedules by doctor ID and date range
     */
    List<DoctorSchedule> getSchedulesByDoctorAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get schedules by date range (all doctors)
     */
    List<DoctorSchedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Create or update schedule
     */
    boolean saveSchedule(DoctorSchedule schedule);
    
    /**
     * Batch create schedules
     */
    boolean batchSaveSchedules(List<DoctorSchedule> schedules);
    
    /**
     * Delete schedule
     */
    boolean deleteSchedule(Long id);
}
