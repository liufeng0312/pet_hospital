package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.VaccineInventory;
import com.pethospital.entity.VaccineStockLog;
import com.pethospital.mapper.VaccineInventoryMapper;
import com.pethospital.service.VaccineInventoryService;
import com.pethospital.service.VaccineStockLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineInventoryServiceImpl extends ServiceImpl<VaccineInventoryMapper, VaccineInventory> 
        implements VaccineInventoryService {

    private final VaccineInventoryMapper vaccineInventoryMapper;
    private final VaccineStockLogService stockLogService;

    @Override
    public List<VaccineInventory> getLowStockItems() {
        return vaccineInventoryMapper.getLowStockItems();
    }

    @Override
    public List<VaccineInventory> getExpiringItems(int days) {
        return vaccineInventoryMapper.getExpiringItems(days);
    }

    @Override
    public VaccineInventory findByBatchNumber(String batchNumber) {
        return vaccineInventoryMapper.findByBatchNumber(batchNumber);
    }

    @Override
    @Transactional
    public void adjustStock(Long id, int quantity, String reason, Long operatorId) {
        VaccineInventory inventory = getById(id);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        
        int newQuantity = inventory.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new RuntimeException("库存不足");
        }
        
        inventory.setQuantity(newQuantity);
        updateById(inventory);
        
        // 记录日志
        VaccineStockLog log = new VaccineStockLog();
        log.setInventoryId(id);
        log.setChangeType(quantity > 0 ? "IN" : "ADJUST");
        log.setQuantity(quantity);
        log.setOperatorId(operatorId);
        log.setReason(reason);
        stockLogService.save(log);
    }

    @Override
    @Transactional
    public void deductStock(Long id, int quantity, Long recordId) {
        VaccineInventory inventory = getById(id);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        inventory.setQuantity(inventory.getQuantity() - quantity);
        updateById(inventory);
        
        // 记录日志
        VaccineStockLog log = new VaccineStockLog();
        log.setInventoryId(id);
        log.setChangeType("OUT");
        log.setQuantity(-quantity);
        log.setRelatedRecordId(recordId);
        log.setReason("疫苗接种");
        stockLogService.save(log);
    }
}
