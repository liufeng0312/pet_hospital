package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Drug;
import com.pethospital.entity.InventoryLog;
import com.pethospital.mapper.InventoryLogMapper;
import com.pethospital.service.DrugService;
import com.pethospital.service.InventoryLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryLogServiceImpl extends ServiceImpl<InventoryLogMapper, InventoryLog> 
        implements InventoryLogService {

    private final InventoryLogMapper inventoryLogMapper;
    private final DrugService drugService;

    @Override
    @Transactional
    public void recordInbound(InventoryLog log) {
        // 设置类型为入库
        log.setType(1);
        
        // 更新库存
        Drug drug = drugService.getById(log.getDrugId());
        if (drug == null) {
            throw new RuntimeException("药品不存在");
        }
        drug.setStockQuantity(drug.getStockQuantity() + log.getQuantity());
        drugService.updateById(drug);
        
        // 记录流水
        save(log);
    }

    @Override
    @Transactional
    public void recordOutbound(InventoryLog log) {
        // 设置类型为出库，数量为负数
        log.setType(2);
        log.setQuantity(-Math.abs(log.getQuantity()));
        
        // 更新库存
        Drug drug = drugService.getById(log.getDrugId());
        if (drug == null) {
            throw new RuntimeException("药品不存在");
        }
        
        int newStock = drug.getStockQuantity() + log.getQuantity(); // log.getQuantity() 是负数
        if (newStock < 0) {
            throw new RuntimeException("库存不足");
        }
        
        drug.setStockQuantity(newStock);
        drugService.updateById(drug);
        
        // 记录流水
        save(log);
    }

    @Override
    @Transactional
    public void recordAdjustment(InventoryLog log) {
        // 设置类型为盘点调整
        log.setType(3);
        
        // 更新库存
        Drug drug = drugService.getById(log.getDrugId());
        if (drug == null) {
            throw new RuntimeException("药品不存在");
        }
        
        drug.setStockQuantity(drug.getStockQuantity() + log.getQuantity());
        drugService.updateById(drug);
        
        // 记录流水
        save(log);
    }

    @Override
    public Page<InventoryLog> listLogs(Long drugId, Integer type, Integer page, Integer size) {
        Page<InventoryLog> pageParam = new Page<>(page, size);
        List<InventoryLog> logs = inventoryLogMapper.selectPageWithDetails(pageParam, drugId, type);
        pageParam.setRecords(logs);
        return pageParam;
    }
}
