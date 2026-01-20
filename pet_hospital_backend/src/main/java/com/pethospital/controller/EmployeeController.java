package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.entity.Employee;
import com.pethospital.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "员工管理")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @ApiOperation("获取医生列表")
    @GetMapping("/doctors")
    public Result<List<Employee>> getDoctors() {
        return Result.success(employeeService.getDoctors());
    }

    @ApiOperation("获取员工列表")
    @GetMapping
    public Result<List<Employee>> list() {
        return Result.success(employeeService.list());
    }

    @ApiOperation("获取员工详情")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        return Result.success(employeeService.getById(id));
    }

    @ApiOperation("创建员工")
    @PostMapping
    public Result<Boolean> create(@RequestBody Employee employee) {
        return Result.success(employeeService.create(employee));
    }

    @ApiOperation("更新员工")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        return Result.success(employeeService.update(employee));
    }

    @ApiOperation("删除员工")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(employeeService.delete(id));
    }
}
