package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Consultation;

public interface ConsultationService extends IService<Consultation> {
    
    /**
     * 创建咨询
     */
    Consultation createConsultation(Consultation consultation);
    
    /**
     * 获取客户咨询列表
     */
    Page<Consultation> getMyConsultations(Long customerId, String status, Integer page, Integer size);
    
    /**
     * 获取所有咨询列表（管理端）
     */
    Page<Consultation> getAllConsultations(String status, Integer page, Integer size);
    
    /**
     * 获取咨询详情
     */
    Consultation getDetail(Long id);
    
    /**
     * 医生回复咨询
     */
    boolean replyConsultation(Long id, Long doctorId, String replyContent);
}
