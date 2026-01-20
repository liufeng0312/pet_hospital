package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Pet;
import com.pethospital.entity.Reminder;
import com.pethospital.entity.VaccineRecord;
import com.pethospital.mapper.ReminderMapper;
import com.pethospital.service.PetService;
import com.pethospital.service.ReminderService;
import com.pethospital.service.VaccineRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder> implements ReminderService {

    private final PetService petService;
    private final ReminderMapper reminderMapper;
    private final VaccineRecordService vaccineRecordService;

    @Override
    @Transactional
    public void generateReminders() {
        LocalDate today = LocalDate.now();
        List<Pet> pets = petService.list();
        
        for (Pet pet : pets) {
            if (pet.getBirthDate() == null || pet.getCustomerId() == null) continue;
            
            // 1. 生日提醒：生日前3天生成
            generateBirthdayReminder(pet, today);
            
            // 2. 疫苗提醒：基于疫苗记录，如无记录则使用默认规则
            generateVaccineReminder(pet, today);
        }
    }
    
    /**
     * 生成生日提醒
     */
    private void generateBirthdayReminder(Pet pet, LocalDate today) {
        // 计算今年的生日
        LocalDate birthdayThisYear = pet.getBirthDate()
                .withYear(today.getYear());
        
        // 如果今年生日已过，则计算明年的
        if (birthdayThisYear.isBefore(today)) {
            birthdayThisYear = birthdayThisYear.plusYears(1);
        }
        
        // 生日前3天生成提醒
        LocalDate reminderDate = birthdayThisYear.minusDays(3);
        
        // 只为未来10天内的生日生成提醒
        if (reminderDate.isAfter(today) && reminderDate.isBefore(today.plusDays(10))) {
            // 检查是否已存在
            Long count = lambdaQuery()
                    .eq(Reminder::getPetId, pet.getId())
                    .eq(Reminder::getType, "BIRTHDAY")
                    .eq(Reminder::getDueDate, birthdayThisYear)
                    .count();
            
            if (count == 0) {
                Reminder reminder = new Reminder();
                reminder.setCustomerId(pet.getCustomerId());
                reminder.setPetId(pet.getId());
                reminder.setType("BIRTHDAY");
                reminder.setDueDate(birthdayThisYear);
                reminder.setStatus(0);
                save(reminder);
            }
        }
    }
    
    /**
     * 生成疫苗提醒
     */
    private void generateVaccineReminder(Pet pet, LocalDate today) {
        // 首先查询疫苗记录
        List<VaccineRecord> records = vaccineRecordService.getByPetId(pet.getId());
        
        boolean hasVaccineRecords = false;
        
        // 基于实际疫苗记录生成提醒
        for (VaccineRecord record : records) {
            if (record.getNextDueDate() == null) continue;
            
            hasVaccineRecords = true;
            LocalDate dueDate = record.getNextDueDate();
            LocalDate reminderDate = dueDate.minusDays(7); // 提前7天提醒
            
            // 只为未来14天内的疫苗生成提醒
            if (reminderDate.isAfter(today) && reminderDate.isBefore(today.plusDays(14))) {
                Long count = lambdaQuery()
                        .eq(Reminder::getPetId, pet.getId())
                        .eq(Reminder::getType, "VACCINE")
                        .eq(Reminder::getDueDate, dueDate)
                        .count();
                
                if (count == 0) {
                    Reminder reminder = new Reminder();
                    reminder.setCustomerId(pet.getCustomerId());
                    reminder.setPetId(pet.getId());
                    reminder.setType("VACCINE");
                    reminder.setDueDate(dueDate);
                    reminder.setStatus(0);
                    save(reminder);
                }
            }
        }
        
        // 如果没有任何疫苗记录，使用基于年龄的默认规则
        if (!hasVaccineRecords) {
            generateDefaultVaccineReminder(pet, today);
        }
    }
    
    /**
     * 基于年龄的默认疫苗提醒（当无疫苗记录时）
     */
    private void generateDefaultVaccineReminder(Pet pet, LocalDate today) {
        long ageInDays = java.time.temporal.ChronoUnit.DAYS.between(pet.getBirthDate(), today);
        
        // 疫苗计划（单位：天）
        int[] vaccineDays = {45, 75, 105, 135}; // 幼年期疫苗
        
        // 幼年期疫苗（0-150天）
        if (ageInDays < 150) {
            for (int vaccineDay : vaccineDays) {
                // 提前7天提醒
                int reminderDay = vaccineDay - 7;
                if (ageInDays >= reminderDay && ageInDays <= vaccineDay) {
                    LocalDate vaccineDate = pet.getBirthDate().plusDays(vaccineDay);
                    
                    // 检查是否已生成
                    Long count = lambdaQuery()
                            .eq(Reminder::getPetId, pet.getId())
                            .eq(Reminder::getType, "VACCINE")
                            .eq(Reminder::getDueDate, vaccineDate)
                            .count();
                    
                    if (count == 0) {
                        Reminder reminder = new Reminder();
                        reminder.setCustomerId(pet.getCustomerId());
                        reminder.setPetId(pet.getId());
                        reminder.setType("VACCINE");
                        reminder.setDueDate(vaccineDate);
                        reminder.setStatus(0);
                        save(reminder);
                    }
                }
            }
        } else {
            // 成年宠物：每年生日前14天提醒年度疫苗
            LocalDate birthdayThisYear = pet.getBirthDate().withYear(today.getYear());
            if (birthdayThisYear.isBefore(today)) {
                birthdayThisYear = birthdayThisYear.plusYears(1);
            }
            
            LocalDate annualVaccineDate = birthdayThisYear;
            LocalDate reminderDate = annualVaccineDate.minusDays(14);
            
            // 只为未来30天内的疫苗生成提醒
            if (reminderDate.isAfter(today) && reminderDate.isBefore(today.plusDays(30))) {
                Long count = lambdaQuery()
                        .eq(Reminder::getPetId, pet.getId())
                        .eq(Reminder::getType, "VACCINE")
                        .eq(Reminder::getDueDate, annualVaccineDate)
                        .count();
                
                if (count == 0) {
                    Reminder reminder = new Reminder();
                    reminder.setCustomerId(pet.getCustomerId());
                    reminder.setPetId(pet.getId());
                    reminder.setType("VACCINE");
                    reminder.setDueDate(annualVaccineDate);
                    reminder.setStatus(0);
                    save(reminder);
                }
            }
        }
    }

    @Override
    @Transactional
    public void send(Long id) {
        Reminder reminder = getById(id);
        if (reminder != null) {
            reminder.setStatus(1); // SENT
            reminder.setSentTime(LocalDateTime.now());
            updateById(reminder);
            // 实际场景这里会调用 SMS/Email 网关
        }
    }

    @Override
    public List<Reminder> listAll() {
        return reminderMapper.selectAllWithDetails();
    }

    @Override
    public List<Reminder> getMyReminders(Long customerId) {
        return lambdaQuery()
                .eq(Reminder::getCustomerId, customerId)
                .orderByDesc(Reminder::getDueDate)
                .list();
    }
}
