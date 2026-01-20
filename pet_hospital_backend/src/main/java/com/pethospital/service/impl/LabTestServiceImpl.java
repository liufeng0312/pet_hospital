package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.LabTest;
import com.pethospital.mapper.LabTestMapper;
import com.pethospital.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabTestServiceImpl extends ServiceImpl<LabTestMapper, LabTest> implements LabTestService {

    private final LabTestMapper labTestMapper;

    @Override
    @Transactional
    public LabTest createTest(LabTest labTest) {
        // 保存检验记录
        save(labTest);
        return labTest;
    }

    @Override
    public LabTest getByIdWithDetails(Long id) {
        LabTest labTest = labTestMapper.selectByIdWithDetails(id);
        if (labTest == null) {
            throw new RuntimeException("检验记录不存在");
        }
        return labTest;
    }

    @Override
    @Transactional
    public LabTest completeTest(Long id, String result, String reportUrl) {
        LabTest test = getById(id);
        if (test == null) {
            throw new RuntimeException("检验记录不存在");
        }
        
        test.setResult(result);
        if (reportUrl != null) {
            test.setReportUrl(reportUrl);
        }
        
        updateById(test);
        return test;
    }

    @Override
    public Page<LabTest> listTests(Integer status, String searchText, Integer page, Integer size) {
        Page<LabTest> pageParam = new Page<>(page, size);
        List<LabTest> records = labTestMapper.selectPageWithDetails(pageParam, status, searchText);
        pageParam.setRecords(records);
        return pageParam;
    }
}
