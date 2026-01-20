package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.entity.Hospitalization;
import com.pethospital.service.HospitalizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "住院管理")
@RestController
@RequestMapping("/api/hospitalizations")
@RequiredArgsConstructor
public class HospitalizationController {

    private final HospitalizationService hospitalizationService;

    @ApiOperation("获取床位状态")
    @GetMapping("/beds")
    public Result<List<Map<String, Object>>> getBedStatus() {
        return Result.success(hospitalizationService.getBedStatus());
    }

    @ApiOperation("办理入院")
    @PostMapping("/admit")
    public Result<Void> admit(@RequestBody Hospitalization hospitalization) {
        hospitalizationService.admit(hospitalization);
        return Result.success(null);
    }

    @ApiOperation("办理出院")
    @PostMapping("/{id}/discharge")
    public Result<Void> discharge(@PathVariable Long id) {
        hospitalizationService.discharge(id);
        return Result.success(null);
    }

    @ApiOperation("分页查询住院记录")
    @GetMapping
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Hospitalization>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(hospitalizationService.list(page, size, petId, petName, status, startDate, endDate));
    }
}
