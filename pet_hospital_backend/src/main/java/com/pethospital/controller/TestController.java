package com.pethospital.controller;

import com.pethospital.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @ApiOperation("健康检查")
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Pet Hospital Backend is running!");
    }
}
