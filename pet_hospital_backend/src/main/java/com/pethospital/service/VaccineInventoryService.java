package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.VaccineInventory;

import java.util.List;

public interface VaccineInventoryService extends IService<VaccineInventory> {
    
    /**
     * 获取低库存项
     */
    List<VaccineInventory> getLowStockItems();
    
    /**
     * 获取即将过期项
     */
    List<VaccineInventory> getExpiringItems(int days);
    
    /**
     * 根据批号查询
     */
    VaccineInventory findByBatchNumber(String batchNumber);
    
    /**
     * 调整库存
     */
    void adjustStock(Long id, int quantity, String reason, Long operatorId);
    
    /**
     * 扣减库存（接种时）
     */
    void deductStock(Long id, int quantity, Long recordId);
}
