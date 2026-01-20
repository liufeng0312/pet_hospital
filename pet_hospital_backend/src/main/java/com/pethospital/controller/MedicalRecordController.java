package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.MedicalRecord;
import com.pethospital.service.MedicalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "病历管理")
@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @ApiOperation("分页查询病历列表")
    @GetMapping
    public Result<Page<MedicalRecord>> list(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(medicalRecordService.listMedicalRecords(petId, doctorId, page, size));
    }

    @ApiOperation("获取病历详情")
    @GetMapping("/{id}")
    public Result<MedicalRecord> getById(@PathVariable Long id) {
        return Result.success(medicalRecordService.getDetail(id));
    }

    @ApiOperation("新建病历")
    @PostMapping
    public Result<MedicalRecord> create(@RequestBody MedicalRecord medicalRecord) {
        return Result.success(medicalRecordService.createMedicalRecord(medicalRecord));
    }

    @ApiOperation("更新病历")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody MedicalRecord medicalRecord) {
        medicalRecord.setId(id);
        return Result.success(medicalRecordService.updateById(medicalRecord));
    }

    @ApiOperation("删除病历")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(medicalRecordService.removeById(id));
    }

    @ApiOperation("查询宠物病历历史")
    @GetMapping("/pet/{petId}")
    public Result<List<MedicalRecord>> listByPet(@PathVariable Long petId) {
        return Result.success(medicalRecordService.listByPet(petId));
    }

    @ApiOperation("查询挂号的病历")
    @GetMapping("/registration/{registrationId}")
    public Result<List<MedicalRecord>> listByRegistration(@PathVariable Long registrationId) {
        return Result.success(medicalRecordService.listByRegistration(registrationId));
    }

    @ApiOperation("获取我的病历列表（小程序）")
    @GetMapping("/my-records")
    public Result<Page<MedicalRecord>> getMyRecords(
            @RequestAttribute("customerId") Long customerId,
            @RequestParam(required = false) Long petId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(medicalRecordService.getMyRecords(customerId, petId, page, size));
    }
}
