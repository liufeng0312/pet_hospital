package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Employee;
import com.pethospital.mapper.EmployeeMapper;
import com.pethospital.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> 
        implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Employee> getDoctors() {
        return employeeMapper.selectByRole("DOCTOR");
    }

    @Override
    public boolean create(Employee employee) {
        // 使用BCrypt加密默认密码
        if (employee.getPasswordHash() == null || employee.getPasswordHash().isEmpty()) {
            employee.setPasswordHash(passwordEncoder.encode("123456")); 
        } else {
            // 如果提供了密码，也需要加密
            employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));
        }
        if (employee.getStatus() == null) {
            employee.setStatus(1);
        }
        return save(employee);
    }

    @Override
    public boolean update(Employee employee) {
        // 从数据库获取现有员工信息
        Employee existing = getById(employee.getId());
        if (existing == null) {
            return false;
        }
        
        // 更新基本信息
        if (employee.getName() != null) {
            existing.setName(employee.getName());
        }
        if (employee.getPhone() != null) {
            existing.setPhone(employee.getPhone());
        }
        if (employee.getAvatar() != null) {
            existing.setAvatar(employee.getAvatar());
        }
        
        // 如果有密码字段，更新密码（使用BCrypt加密）
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            // 使用BCrypt加密密码
            String encryptedPassword = passwordEncoder.encode(employee.getPassword());
            employeeMapper.updatePasswordById(employee.getId(), encryptedPassword);
        }
        
        return updateById(existing);
    }

    @Override
    public boolean delete(Long id) {
        // 逻辑删除
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(0);
        return updateById(employee);
    }
}
