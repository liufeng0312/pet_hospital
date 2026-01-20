package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.CareRecord;

import java.util.List;

public interface CareRecordService extends IService<CareRecord> {
    
    /**
     * 添加护理记录
     */
    void addRecord(CareRecord careRecord);
    
    /**
     * 获取指定住院记录的护理列表
     */
    List<CareRecord> listByHospitalizationId(Long hospitalizationId);

    /**
     * 分页查询所有护理记录
     */
    com.baomidou.mybatisplus.core.metadata.IPage<CareRecord> listAll(Integer page, Integer size, String petName, Long operatorId, String startDate, String endDate);
}
