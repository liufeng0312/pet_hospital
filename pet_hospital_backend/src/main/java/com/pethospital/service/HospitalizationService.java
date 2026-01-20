package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Hospitalization;
import java.util.List;
import java.util.Map;

public interface HospitalizationService extends IService<Hospitalization> {

    /**
     * 办理入院
     */
    void admit(Hospitalization hospitalization);

    /**
     * 办理出院
     */
    void discharge(Long id);

    /**
     * 获取床位状态列表 (模拟 20 个床位)
     */
    List<Map<String, Object>> getBedStatus();

    /**
     * 分页查询住院记录
     */
    com.baomidou.mybatisplus.core.metadata.IPage<Hospitalization> list(Integer page, Integer size, Long petId, String petName, String status, String startDate, String endDate);
}
