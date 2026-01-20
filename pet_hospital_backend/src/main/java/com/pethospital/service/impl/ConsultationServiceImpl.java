package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Consultation;
import com.pethospital.mapper.ConsultationMapper;
import com.pethospital.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation> 
        implements ConsultationService {

    private final ConsultationMapper consultationMapper;

    @Override
    @Transactional
    public Consultation createConsultation(Consultation consultation) {
        consultation.setStatus("PENDING");
        save(consultation);
        return consultation;
    }

    @Override
    public Page<Consultation> getMyConsultations(Long customerId, String status, Integer page, Integer size) {
        Page<Consultation> pageParam = new Page<>(page, size);
        List<Consultation> records = consultationMapper.selectPageByCustomer(pageParam, customerId, status);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public Page<Consultation> getAllConsultations(String status, Integer page, Integer size) {
        Page<Consultation> pageParam = new Page<>(page, size);
        List<Consultation> records = consultationMapper.selectPageWithDetails(pageParam, status);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public Consultation getDetail(Long id) {
        return consultationMapper.selectByIdWithDetails(id);
    }

    @Override
    @Transactional
    public boolean replyConsultation(Long id, Long doctorId, String replyContent) {
        Consultation consultation = getById(id);
        if (consultation == null) {
            return false;
        }
        consultation.setReplyContent(replyContent);
        consultation.setReplyDoctorId(doctorId);
        consultation.setReplyTime(LocalDateTime.now());
        consultation.setStatus("ANSWERED");
        return updateById(consultation);
    }
}
