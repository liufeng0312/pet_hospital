package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.MedicalRecord;

import java.util.List;

public interface MedicalRecordService extends IService<MedicalRecord> {

    /**
     * 创建病历
     */
    MedicalRecord createMedicalRecord(MedicalRecord medicalRecord);

    /**
     * 分页查询病历列表
     */
    Page<MedicalRecord> listMedicalRecords(Long petId, Long doctorId, Integer page, Integer size);

    /**
     * 查询宠物的病历历史
     */
    List<MedicalRecord> listByPet(Long petId);

    /**
     * 查询挂号的病历
     */
    List<MedicalRecord> listByRegistration(Long registrationId);

    /**
     * 获取病历详情（含关联信息）
     */
    MedicalRecord getDetail(Long id);

    /**
     * 获取客户的病历列表（小程序）
     */
    Page<MedicalRecord> getMyRecords(Long customerId, Long petId, Integer page, Integer size);
}
