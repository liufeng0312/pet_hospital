package com.pethospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pethospital.common.Result;
import com.pethospital.entity.Prescription;
import com.pethospital.service.PrescriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "处方管理")
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final com.pethospital.service.BillService billService;

    @ApiOperation("分页查询处方列表")
    @GetMapping
    public Result<IPage<Prescription>> list(
            @RequestParam(required = false) Long medicalRecordId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(prescriptionService.listPrescriptions(medicalRecordId, status, startDate, endDate, searchText, page, size));
    }

    @ApiOperation("获取处方详情")
    @GetMapping("/{id}")
    public Result<Prescription> getById(@PathVariable Long id) {
        return Result.success(prescriptionService.getDetail(id));
    }

    @ApiOperation("新建处方")
    @PostMapping
    public Result<Prescription> create(@RequestBody Prescription prescription) {
        Prescription created = prescriptionService.createPrescription(prescription);
        // 自动生成账单
        billService.createFromPrescription(created.getId());
        return Result.success(created);
    }

    @ApiOperation("更新处方状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(prescriptionService.updateStatus(id, status));
    }
}
