package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.VaccineStockLog;
import com.pethospital.mapper.VaccineStockLogMapper;
import com.pethospital.service.VaccineStockLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineStockLogServiceImpl extends ServiceImpl<VaccineStockLogMapper, VaccineStockLog> 
        implements VaccineStockLogService {

    private final VaccineStockLogMapper stockLogMapper;

    @Override
    public List<VaccineStockLog> listByInventoryId(Long inventoryId) {
        return stockLogMapper.selectByInventoryId(inventoryId);
    }
}
