package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.InventoryLog;

public interface InventoryLogService extends IService<InventoryLog> {

    /**
     * 入库操作（增加库存+记录流水）
     */
    void recordInbound(InventoryLog log);

    /**
     * 出库操作（减少库存+记录流水）
     */
    void recordOutbound(InventoryLog log);

    /**
     * 盘点调整（调整库存+记录流水）
     */
    void recordAdjustment(InventoryLog log);

    /**
     * 分页查询流水列表
     */
    Page<InventoryLog> listLogs(Long drugId, Integer type, Integer page, Integer size);
}
