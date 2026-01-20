package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.DoctorSchedule;
import com.pethospital.mapper.DoctorScheduleMapper;
import com.pethospital.service.DoctorScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoctorScheduleServiceImpl extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> 
        implements DoctorScheduleService {
    
    @Override
    public List<DoctorSchedule> getSchedulesByDoctorAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<DoctorSchedule> wrapper = new QueryWrapper<>();
        wrapper.eq("doctor_id", doctorId)
               .ge("work_date", startDate)
               .le("work_date", endDate)
               .orderByAsc("work_date");
        return list(wrapper);
    }
    
    @Override
    public List<DoctorSchedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<DoctorSchedule> wrapper = new QueryWrapper<>();
        wrapper.ge("work_date", startDate)
               .le("work_date", endDate)
               .orderByAsc("work_date", "doctor_id");
        return list(wrapper);
    }
    
    @Override
    @Transactional
    public boolean saveSchedule(DoctorSchedule schedule) {
        return saveOrUpdate(schedule);
    }
    
    @Override
    @Transactional
    public boolean batchSaveSchedules(List<DoctorSchedule> schedules) {
        return saveBatch(schedules);
    }
    
    @Override
    @Transactional
    public boolean deleteSchedule(Long id) {
        return removeById(id);
    }
}
