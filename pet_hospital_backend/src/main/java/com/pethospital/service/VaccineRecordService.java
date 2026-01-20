package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.VaccineRecord;

import java.time.LocalDate;
import java.util.List;

public interface VaccineRecordService extends IService<VaccineRecord> {
    
    /**
     * 查询疫苗记录（带详情）
     */
    List<VaccineRecord> listWithDetails(Long petId);
    
    /**
     * 创建疫苗记录
     */
    VaccineRecord createRecord(VaccineRecord record);
    
    /**
     * 获取即将到期的疫苗
     */
    List<VaccineRecord> getUpcomingVaccines(LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据宠物ID查询疫苗记录
     */
    List<VaccineRecord> getByPetId(Long petId);
}
