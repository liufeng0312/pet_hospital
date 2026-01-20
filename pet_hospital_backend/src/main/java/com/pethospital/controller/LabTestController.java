package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.LabTest;
import com.pethospital.service.BillService;
import com.pethospital.service.LabTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "检验管理")
@RestController
@RequestMapping("/api/lab-tests")
@RequiredArgsConstructor
public class LabTestController {

    private final LabTestService labTestService;
    private final BillService billService;

    @ApiOperation("查询检验记录")
    @GetMapping
    public Result<Page<LabTest>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(labTestService.listTests(status, searchText, page, size));
    }

    @ApiOperation("开具检验单")
    @PostMapping
    public Result<LabTest> create(@RequestBody LabTest labTest) {
        LabTest created = labTestService.createTest(labTest);
        // 自动创建账单
        billService.createFromLabTest(created.getId());
        return Result.success(created);
    }

    @ApiOperation("录入检验结果")
    @PutMapping("/{id}/result")
    public Result<LabTest> complete(
            @PathVariable Long id,
            @RequestBody LabTest labTest) {
        return Result.success(labTestService.completeTest(id, labTest.getResult(), labTest.getReportUrl()));
    }
}
