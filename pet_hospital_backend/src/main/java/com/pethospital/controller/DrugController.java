package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Drug;
import com.pethospital.service.DrugService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "药品管理")
@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService drugService;

    @ApiOperation("查询药品列表")
    @GetMapping
    public Result<Page<Drug>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(drugService.pageDrugs(keyword, status, page, size));
    }

    @ApiOperation("获取启用的药品列表")
    @GetMapping("/active")
    public Result<List<Drug>> listActive() {
        return Result.success(drugService.listActiveDrugs());
    }

    @ApiOperation("获取药品详情")
    @GetMapping("/{id}")
    public Result<Drug> getById(@PathVariable Long id) {
        return Result.success(drugService.getById(id));
    }

    @ApiOperation("新建药品")
    @PostMapping
    public Result<Boolean> create(@RequestBody Drug drug) {
        return Result.success(drugService.save(drug));
    }

    @ApiOperation("更新药品")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Drug drug) {
        drug.setId(id);
        return Result.success(drugService.updateById(drug));
    }

    @ApiOperation("删除药品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(drugService.removeById(id));
    }

    @ApiOperation("获取库存预警药品")
    @GetMapping("/low-stock")
    public Result<List<Drug>> listLowStock() {
        return Result.success(drugService.getLowStockDrugs());
    }
}
