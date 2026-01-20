package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Customer;
import com.pethospital.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "客户管理")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @ApiOperation("分页查询客户列表")
    @GetMapping
    public Result<Page<Customer>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer level,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(customerService.searchCustomers(keyword, level, page, size));
    }

    @ApiOperation("获取客户详情")
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Long id) {
        return Result.success(customerService.getById(id));
    }

    @ApiOperation("新增客户")
    @PostMapping
    public Result<Boolean> save(@RequestBody Customer customer) {
        return Result.success(customerService.save(customer));
    }

    @ApiOperation("更新客户")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        return Result.success(customerService.updateById(customer));
    }

    @ApiOperation("删除客户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(customerService.removeById(id));
    }
}
