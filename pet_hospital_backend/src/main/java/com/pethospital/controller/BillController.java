package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Bill;
import com.pethospital.service.BillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "收银台")
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @ApiOperation("获取待支付账单")
    @GetMapping("/unpaid")
    public Result<List<Bill>> listUnpaid() {
        return Result.success(billService.listUnpaid());
    }

    @ApiOperation("删除账单")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(billService.removeById(id));
    }

    @ApiOperation("获取我的账单列表（小程序）")
    @GetMapping("/my-bills")
    public Result<Page<Bill>> getMyBills(
            @RequestAttribute("customerId") Long customerId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(billService.getMyBills(customerId, page, size));
    }

    @ApiOperation("获取已支付账单")
    @GetMapping("/paid")
    public Result<List<Bill>> listPaid() {
        return Result.success(billService.listPaid());
    }

    @ApiOperation("从处方创建账单")
    @PostMapping("/create/prescription/{id}")
    public Result<Void> createFromPrescription(@PathVariable Long id) {
        billService.createFromPrescription(id);
        return Result.success(null);
    }

    @ApiOperation("从检查单创建账单")
    @PostMapping("/create/lab-test/{id}")
    public Result<Void> createFromLabTest(@PathVariable Long id) {
        billService.createFromLabTest(id);
        return Result.success(null);
    }

    @ApiOperation("从手术记录创建账单")
    @PostMapping("/create/surgery/{id}")
    public Result<Void> createFromSurgery(@PathVariable Long id) {
        billService.createFromSurgery(id);
        return Result.success(null);
    }

    @ApiOperation("支付账单")
    @PostMapping("/pay/{id}")
    public Result<Void> pay(@PathVariable Long id, @RequestParam String paymentMethod) {
        billService.pay(id, paymentMethod);
        return Result.success(null);
    }
}
