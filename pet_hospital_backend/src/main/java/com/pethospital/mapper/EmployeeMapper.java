package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update; // Added this import

import java.util.List;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 获取指定角色的员工列表
     */
    @Select("SELECT id, name, username, role, phone, status, created_at " +
            "FROM employees WHERE role = #{role} AND status = 1")
    List<Employee> selectByRole(@Param("role") String role);

    /**
     * 根据用户名查询用户(含密码hash)，用于登录验证
     */
    @Select("SELECT id, name, username, password_hash, role, phone, status, created_at " +
            "FROM employees WHERE username = #{username} AND status = 1")
    Employee selectByUsernameForLogin(@Param("username") String username);

    /**
     * 更新员工密码
     */
    @Update("UPDATE employees SET password_hash = #{password} WHERE id = #{id}")
    int updatePasswordById(@Param("id") Long id, @Param("password") String password);
}
