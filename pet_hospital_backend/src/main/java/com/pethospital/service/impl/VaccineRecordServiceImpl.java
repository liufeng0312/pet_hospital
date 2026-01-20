package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.VaccineRecord;
import com.pethospital.mapper.VaccineRecordMapper;
import com.pethospital.service.VaccineRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineRecordServiceImpl extends ServiceImpl<VaccineRecordMapper, VaccineRecord> 
        implements VaccineRecordService {

    private final VaccineRecordMapper vaccineRecordMapper;

    @Override
    public List<VaccineRecord> listWithDetails(Long petId) {
        return vaccineRecordMapper.selectWithDetails(petId);
    }

    @Override
    @Transactional
    public VaccineRecord createRecord(VaccineRecord record) {
        save(record);
        return record;
    }

    @Override
    public List<VaccineRecord> getUpcomingVaccines(LocalDate startDate, LocalDate endDate) {
        return vaccineRecordMapper.getUpcomingVaccines(startDate, endDate);
    }

    @Override
    public List<VaccineRecord> getByPetId(Long petId) {
        return lambdaQuery()
                .eq(VaccineRecord::getPetId, petId)
                .orderByDesc(VaccineRecord::getVaccinationDate)
                .list();
    }
}
