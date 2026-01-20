package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Registration;
import com.pethospital.mapper.RegistrationMapper;
import com.pethospital.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> 
        implements RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final com.pethospital.mapper.DoctorScheduleMapper doctorScheduleMapper;

    @Override
    @Transactional
    public Registration createRegistration(Registration registration) {
        // 检查挂号冲突
        int conflict = registrationMapper.countConflict(registration.getPetId());
        if (conflict > 0) {
            throw new RuntimeException("该宠物今日已有未完成的挂号，请勿重复挂号");
        }
        
        // 检查该时间段医生是否已被预约
        if (registration.getAppointmentTime() != null) {
            Long slotConflict = lambdaQuery()
                    .eq(Registration::getDoctorId, registration.getDoctorId())
                    .eq(Registration::getAppointmentTime, registration.getAppointmentTime())
                    .ne(Registration::getStatus, "CANCELLED")
                    .count();
            if (slotConflict > 0) {
                throw new RuntimeException("该时间段已被预约，请选择其他时间");
            }
        }
        
        // 生成排队号
        Integer maxQueueNumber = registrationMapper.getTodayMaxQueueNumber();
        registration.setQueueNumber(maxQueueNumber + 1);
        
        // 设置初始状态
        if (registration.getStatus() == null) {
            registration.setStatus("WAITING");
        }
        if (registration.getType() == null) {
            registration.setType("ONSITE");
        }
        
        // 保存
        save(registration);
        return registration;
    }

    @Override
    public Page<Registration> listRegistrations(String status, LocalDate date, Integer page, Integer size) {
        Page<Registration> pageParam = new Page<>(page, size);
        List<Registration> records = registrationMapper.selectPageWithDetails(pageParam, status, date);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public List<Registration> getQueueBoard() {
        return registrationMapper.getQueueBoard();
    }

    @Override
    @Transactional
    public boolean callNext(Long id) {
        Registration registration = getById(id);
        if (registration == null) {
            throw new RuntimeException("挂号记录不存在");
        }
        if (!"WAITING".equals(registration.getStatus())) {
            throw new RuntimeException("只能叫号候诊中的记录");
        }
        registration.setStatus("IN_PROGRESS");
        return updateById(registration);
    }

    @Override
    @Transactional
    public boolean complete(Long id) {
        Registration registration = getById(id);
        if (registration == null) {
            throw new RuntimeException("挂号记录不存在");
        }
        if (!"IN_PROGRESS".equals(registration.getStatus())) {
            throw new RuntimeException("只能完成正在就诊的记录");
        }
        registration.setStatus("COMPLETED");
        return updateById(registration);
    }

    @Override
    @Transactional
    public boolean cancelRegistration(Long id) {
        Registration registration = getById(id);
        if (registration == null) {
            throw new RuntimeException("挂号记录不存在");
        }
        if ("COMPLETED".equals(registration.getStatus()) || "CANCELLED".equals(registration.getStatus())) {
            throw new RuntimeException("该记录已完成或已取消，无法再次取消");
        }
        registration.setStatus("CANCELLED");
        return updateById(registration);
    }

    @Override
    public List<Registration> getByCustomerId(Long customerId) {
        return lambdaQuery()
                .eq(Registration::getCustomerId, customerId)
                .orderByDesc(Registration::getCreatedAt)
                .list();
    }

    @Override
    public List<com.pethospital.dto.TimeSlotDTO> getAvailableSlots(Long doctorId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        
        // 1. 检查医生是否排班
        com.pethospital.entity.DoctorSchedule schedule = doctorScheduleMapper.findByDoctorAndDate(doctorId, date);
        if (schedule == null) {
            return new java.util.ArrayList<>();
        }
        
        // 2. 生成标准时间段 (09:00 - 17:00, 1小时间隔, 12:00-14:00午休)
        List<String> allSlots = java.util.Arrays.asList(
            "09:00", "10:00", "11:00",
            "14:00", "15:00", "16:00", "17:00"
        );
        
        // 3. 查询该医生当天的所有预约
        // 构造开始和结束时间
        java.time.LocalDateTime startOfDay = date.atStartOfDay();
        java.time.LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        List<Registration> todayAppointments = lambdaQuery()
                .eq(Registration::getDoctorId, doctorId)
                .between(Registration::getAppointmentTime, startOfDay, endOfDay)
                .ne(Registration::getStatus, "CANCELLED")
                .list();
                
        java.util.Set<String> bookedTimes = new java.util.HashSet<>();
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        
        for (Registration reg : todayAppointments) {
            if (reg.getAppointmentTime() != null) {
                bookedTimes.add(reg.getAppointmentTime().format(timeFormatter));
            }
        }
        
        // 4. 构建DTO列表
        List<com.pethospital.dto.TimeSlotDTO> result = new java.util.ArrayList<>();
        for (String time : allSlots) {
            boolean isBooked = bookedTimes.contains(time);
            
            result.add(new com.pethospital.dto.TimeSlotDTO(
                time,
                !isBooked, 
                isBooked ? 0 : 1
            ));
        }
        
        return result;
    }
}
