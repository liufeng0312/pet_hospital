package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Consultation;
import com.pethospital.service.ConsultationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "在线咨询管理")
@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @ApiOperation("创建咨询")
    @PostMapping("/create")
    public Result<Consultation> create(
            @RequestAttribute("customerId") Long customerId,
            @RequestBody Consultation consultation) {
        consultation.setCustomerId(customerId);
        return Result.success(consultationService.createConsultation(consultation));
    }

    @ApiOperation("获取我的咨询列表")
    @GetMapping("/my-consultations")
    public Result<Page<Consultation>> getMyConsultations(
            @RequestAttribute("customerId") Long customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(consultationService.getMyConsultations(customerId, status, page, size));
    }

    @ApiOperation("获取咨询列表（管理端）")
    @GetMapping("/list")
    public Result<Page<Consultation>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(consultationService.getAllConsultations(status, page, size));
    }

    @ApiOperation("获取咨询详情")
    @GetMapping("/{id}")
    public Result<Consultation> getDetail(@PathVariable Long id) {
        return Result.success(consultationService.getDetail(id));
    }

    @ApiOperation("回复咨询（管理端）")
    @PutMapping("/{id}/reply")
    public Result<Boolean> reply(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> request) {
        Long doctorId = 1L; // TODO: 从当前登录用户获取
        String replyContent = request.get("replyContent");
        return Result.success(consultationService.replyConsultation(id, doctorId, replyContent));
    }
}
