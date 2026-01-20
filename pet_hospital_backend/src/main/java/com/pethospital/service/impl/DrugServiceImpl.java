package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Drug;
import com.pethospital.mapper.DrugMapper;
import com.pethospital.service.DrugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {

    @Override
    public List<Drug> listActiveDrugs() {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getIsActive, 1)
               .orderByAsc(Drug::getName);
        return list(wrapper);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Drug> pageDrugs(String keyword, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Drug::getName, keyword)
                    .or().like(Drug::getCode, keyword));
        }
        if (status != null) {
            wrapper.eq(Drug::getIsActive, status);
        }
        wrapper.orderByDesc(Drug::getId);
        return page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), wrapper);
    }

    @Override
    public List<Drug> getLowStockDrugs() {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getIsActive, 1)
               .apply("stock_quantity <= warning_threshold")
               .orderByAsc(Drug::getStockQuantity);
        return list(wrapper);
    }
}
