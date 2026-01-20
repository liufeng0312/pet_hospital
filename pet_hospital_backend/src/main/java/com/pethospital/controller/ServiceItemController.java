package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.ServiceItem;
import com.pethospital.service.ServiceItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "服务项目管理")
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @ApiOperation("查询服务项目列表")
    @GetMapping
    public Result<Page<ServiceItem>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(serviceItemService.page(new Page<>(page, size)));
    }

    @ApiOperation("获取所有启用的服务")
    @GetMapping("/active")
    public Result<List<ServiceItem>> listActive() {
        return Result.success(serviceItemService.list());
    }

    @ApiOperation("新建服务项目")
    @PostMapping
    public Result<Boolean> create(@RequestBody ServiceItem serviceItem) {
        return Result.success(serviceItemService.save(serviceItem));
    }

    @ApiOperation("更新服务项目")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ServiceItem serviceItem) {
        serviceItem.setId(id);
        return Result.success(serviceItemService.updateById(serviceItem));
    }

    @ApiOperation("删除服务项目")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(serviceItemService.removeById(id));
    }
}
