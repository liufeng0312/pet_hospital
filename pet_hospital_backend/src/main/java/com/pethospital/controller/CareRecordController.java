package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.entity.CareRecord;
import com.pethospital.service.CareRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "护理记录管理")
@RestController
@RequestMapping("/api/care-records")
@RequiredArgsConstructor
public class CareRecordController {

    private final CareRecordService careRecordService;

    @ApiOperation("获取护理记录列表")
    @GetMapping
    public Result<Object> list(
            @RequestParam(required = false) Long hospitalizationId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        if (hospitalizationId != null) {
            return Result.success(careRecordService.listByHospitalizationId(hospitalizationId));
        }
        return Result.success(careRecordService.listAll(page, size, petName, operatorId, startDate, endDate));
    }

    @ApiOperation("添加护理记录")
    @PostMapping
    public Result<Boolean> add(@RequestBody CareRecord careRecord) {
        careRecordService.addRecord(careRecord);
        return Result.success(true);
    }
}
