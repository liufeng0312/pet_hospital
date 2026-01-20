package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.MedicalRecord;
import com.pethospital.entity.Pet;
import com.pethospital.mapper.MedicalRecordMapper;
import com.pethospital.service.MedicalRecordService;
import com.pethospital.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> 
        implements MedicalRecordService {

    private final MedicalRecordMapper medicalRecordMapper;
    private final PetService petService;

    @Override
    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        save(medicalRecord);
        return medicalRecord;
    }

    @Override
    public Page<MedicalRecord> listMedicalRecords(Long petId, Long doctorId, Integer page, Integer size) {
        Page<MedicalRecord> pageParam = new Page<>(page, size);
        List<MedicalRecord> records = medicalRecordMapper.selectPageWithDetails(pageParam, petId, doctorId);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public List<MedicalRecord> listByPet(Long petId) {
        return medicalRecordMapper.selectByPetId(petId);
    }

    @Override
    public List<MedicalRecord> listByRegistration(Long registrationId) {
        return medicalRecordMapper.selectByRegistrationId(registrationId);
    }

    @Override
    public MedicalRecord getDetail(Long id) {
        return medicalRecordMapper.selectByIdWithDetails(id);
    }

    @Override
    public Page<MedicalRecord> getMyRecords(Long customerId, Long petId, Integer page, Integer size) {
        Page<MedicalRecord> pageParam = new Page<>(page, size);
        List<MedicalRecord> records = medicalRecordMapper.selectPageByCustomer(pageParam, customerId, petId);
        pageParam.setRecords(records);
        return pageParam;
    }
}
