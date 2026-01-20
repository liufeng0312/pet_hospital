package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Employee;

import java.util.List;

public interface EmployeeService extends IService<Employee> {

    /**
     * 获取医生列表
     */
    List<Employee> getDoctors();
    
    /**
     * 创建员工
     */
    boolean create(Employee employee);
    
    /**
     * 更新员工
     */
    boolean update(Employee employee);
    
    /**
     * 删除员工 (逻辑删除 or 物理删除，这里数据库设计是 status 字段)
     */
    boolean delete(Long id);
}
