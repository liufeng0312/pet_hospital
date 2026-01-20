package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.SurgeryRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface SurgeryRecordService extends IService<SurgeryRecord> {
    
    /**
     * 分页查询手术记录
     */
    IPage<SurgeryRecord> listWithDetails(Integer page, Integer size, Integer status, 
                                          String petName, String startDate, String endDate);
    
    /**
     * 根据ID查询手术详情
     */
    SurgeryRecord getByIdWithDetails(Long id);
    
    /**
     * 查询宠物的手术历史
     */
    List<SurgeryRecord> listByPetId(Long petId);
    
    /**
     * 更新手术状态
     */
    boolean updateStatus(Long id, Integer status);
    /**
     * 同步支付状态
     */
    void syncPaymentStatus();
}
