package com.pethospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Prescription;

public interface PrescriptionService extends IService<Prescription> {

    /**
     * 创建处方，自动计算总金额
     */
    Prescription createPrescription(Prescription prescription);

    /**
     * 分页查询处方列表
     */
    /**
     * 分页查询处方列表
     */
    IPage<Prescription> listPrescriptions(Long medicalRecordId, Integer status, String startDate, String endDate, String searchText, Integer page, Integer size);

    /**
     * 获取处方详情（含明细）
     */
    Prescription getDetail(Long id);

    /**
     * 更新处方状态
     */
    boolean updateStatus(Long id, Integer status);
}
