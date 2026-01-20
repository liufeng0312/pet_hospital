package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.VaccineStockLog;

import java.util.List;

public interface VaccineStockLogService extends IService<VaccineStockLog> {
    
    /**
     * 查询某库存的所有变动记录
     */
    List<VaccineStockLog> listByInventoryId(Long inventoryId);
}
