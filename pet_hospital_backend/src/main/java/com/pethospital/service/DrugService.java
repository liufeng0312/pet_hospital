package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Drug;

import java.util.List;

public interface DrugService extends IService<Drug> {

    /**
     * 分页查询药品
     */
    Page<Drug> pageDrugs(String keyword, Integer status, Integer page, Integer size);

    /**
     * 获取启用的药品列表
     */
    List<Drug> listActiveDrugs();

    /**
     * 获取库存预警药品列表（库存低于预警阈值）
     */
    List<Drug> getLowStockDrugs();
}
