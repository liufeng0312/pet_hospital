package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Customer;
import com.pethospital.mapper.CustomerMapper;
import com.pethospital.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public Page<Customer> searchCustomers(String keyword, Integer level, int page, int size) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                    .or()
                    .like(Customer::getPhone, keyword));
        }
        
        if (level != null) {
            wrapper.eq(Customer::getLevel, level);
        }
        
        wrapper.orderByDesc(Customer::getCreatedAt);
        
        return this.page(new Page<>(page, size), wrapper);
    }
}
