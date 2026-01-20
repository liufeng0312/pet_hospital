package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Customer;

public interface CustomerService extends IService<Customer> {
    Page<Customer> searchCustomers(String keyword, Integer level, int page, int size);
}
