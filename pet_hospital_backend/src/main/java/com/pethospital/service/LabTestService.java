package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.LabTest;

public interface LabTestService extends IService<LabTest> {

    /**
     * 创建检验单
     */
    LabTest createTest(LabTest labTest);

    /**
     * 根据ID查询检验详情（包含关联信息）
     */
    LabTest getByIdWithDetails(Long id);

    /**
     * 完成检验（录入结果）
     */
    LabTest completeTest(Long id, String result, String reportUrl);

    /**
     * 查询检验记录（可按状态筛选）
     */
    Page<LabTest> listTests(Integer status, String searchText, Integer page, Integer size);
}
