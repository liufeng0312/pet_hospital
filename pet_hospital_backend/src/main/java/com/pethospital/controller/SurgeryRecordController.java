package com.pethospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pethospital.common.Result;
import com.pethospital.entity.SurgeryRecord;
import com.pethospital.service.BillService;
import com.pethospital.service.SurgeryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surgeries")
public class SurgeryRecordController {

    @Autowired
    private SurgeryRecordService surgeryRecordService;
    
    @Autowired
    private BillService billService;

    /**
     * 创建手术记录
     */
    @PostMapping
    public Result<SurgeryRecord> create(@RequestBody SurgeryRecord surgeryRecord) {
        if (surgeryRecord.getStatus() == null) {
            surgeryRecord.setStatus(0);
        }
        surgeryRecordService.save(surgeryRecord);
        // 自动创建账单
        billService.createFromSurgery(surgeryRecord.getId());
        return Result.success(surgeryRecord);
    }

    /**
     * 分页查询手术记录
     */
    @GetMapping
    public Result<IPage<SurgeryRecord>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        IPage<SurgeryRecord> result = surgeryRecordService.listWithDetails(
                page, size, status, petName, startDate, endDate);
        return Result.success(result);
    }

    /**
     * 查询手术详情
     */
    @GetMapping("/{id}")
    public Result<SurgeryRecord> getById(@PathVariable Long id) {
        SurgeryRecord record = surgeryRecordService.getByIdWithDetails(id);
        return Result.success(record);
    }

    /**
     * 查询宠物的手术历史
     */
    @GetMapping("/pet/{petId}")
    public Result<List<SurgeryRecord>> listByPetId(@PathVariable Long petId) {
        List<SurgeryRecord> records = surgeryRecordService.listByPetId(petId);
        return Result.success(records);
    }

    /**
     * 更新手术记录
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SurgeryRecord surgeryRecord) {
        surgeryRecord.setId(id);
        surgeryRecordService.updateById(surgeryRecord);
        return Result.success(null);
    }

    /**
     @ApiOperation("更新手术状态")
    */
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        return Result.success(surgeryRecordService.updateStatus(id, status));
    }

    @PostMapping("/sync-payment")
    public Result<Void> syncPayment() {
        surgeryRecordService.syncPaymentStatus();
        return Result.success(null);
    }

    /**
     * 删除手术记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        surgeryRecordService.removeById(id);
        return Result.success(null);
    }
}
