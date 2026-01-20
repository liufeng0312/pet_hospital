package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.dto.LoginRequest;
import com.pethospital.dto.LoginResponse;
import com.pethospital.entity.Employee;
import com.pethospital.mapper.EmployeeMapper;
import com.pethospital.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        System.out.println("=== Login Debug ===");
        System.out.println("Username: " + request.getUsername());
        System.out.println("Password: " + request.getPassword());
        
        // 使用自定义 mapper 方法查询包含密码的用户
        Employee userWithPassword = employeeMapper.selectByUsernameForLogin(request.getUsername());

        if (userWithPassword == null) {
            System.out.println("User not found");
            return Result.error("用户名或密码错误");
        }
        
        System.out.println("DB Password Hash: " + userWithPassword.getPasswordHash());
        
        // 智能密码验证：支持BCrypt和明文两种格式
        boolean matches;
        if (userWithPassword.getPasswordHash().startsWith("$2a$") || 
            userWithPassword.getPasswordHash().startsWith("$2b$") ||
            userWithPassword.getPasswordHash().startsWith("$2y$")) {
            // BCrypt加密密码
            matches = passwordEncoder.matches(request.getPassword(), userWithPassword.getPasswordHash());
            System.out.println("BCrypt Password matches: " + matches);
        } else {
            // 明文密码（向后兼容）
            matches = request.getPassword().equals(userWithPassword.getPasswordHash());
            System.out.println("Plain text Password matches: " + matches);
        }
        
        if (!matches) {
            return Result.error("用户名或密码错误");
        }

        // 成功登录返回用户信息 (不含密码)
        Employee safeUser = employeeService.getById(userWithPassword.getId());
        return Result.success(new LoginResponse("mock-jwt-token", safeUser));
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        return Result.success(true);
    }
}
