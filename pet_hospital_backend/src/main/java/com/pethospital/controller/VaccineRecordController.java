package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.entity.VaccineRecord;
import com.pethospital.service.VaccineRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "疫苗接种记录")
@RestController
@RequestMapping("/api/vaccine-records")
@RequiredArgsConstructor
public class VaccineRecordController {

    private final VaccineRecordService vaccineRecordService;

    @ApiOperation("查询疫苗记录")
    @GetMapping
    public Result<List<VaccineRecord>> list(@RequestParam(required = false) Long petId) {
        return Result.success(vaccineRecordService.listWithDetails(petId));
    }

    @ApiOperation("获取单条记录")
    @GetMapping("/{id}")
    public Result<VaccineRecord> getById(@PathVariable Long id) {
        return Result.success(vaccineRecordService.getById(id));
    }

    @ApiOperation("新建疫苗记录")
    @PostMapping
    public Result<VaccineRecord> create(@RequestBody VaccineRecord record) {
        return Result.success(vaccineRecordService.createRecord(record));
    }

    @ApiOperation("更新疫苗记录")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody VaccineRecord record) {
        record.setId(id);
        return Result.success(vaccineRecordService.updateById(record));
    }

    @ApiOperation("更新疫苗记录状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        VaccineRecord record = new VaccineRecord();
        record.setId(id);
        record.setStatus(status);
        return Result.success(vaccineRecordService.updateById(record));
    }

    @ApiOperation("删除疫苗记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(vaccineRecordService.removeById(id));
    }
}
