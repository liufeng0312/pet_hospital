package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.CareRecord;
import com.pethospital.mapper.CareRecordMapper;
import com.pethospital.service.CareRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareRecordServiceImpl extends ServiceImpl<CareRecordMapper, CareRecord> implements CareRecordService {

    private final CareRecordMapper careRecordMapper;

    @Override
    public void addRecord(CareRecord careRecord) {
        save(careRecord);
    }

    @Override
    public List<CareRecord> listByHospitalizationId(Long hospitalizationId) {
        return careRecordMapper.selectByHospitalizationId(hospitalizationId);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<CareRecord> listAll(Integer page, Integer size, String petName, Long operatorId, String startDate, String endDate) {
        return careRecordMapper.selectPageWithDetail(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), petName, operatorId, startDate, endDate);
    }
}
