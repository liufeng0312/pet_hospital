package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.Reminder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReminderMapper extends BaseMapper<Reminder> {
    
    @Select("SELECT r.*, c.name as customer_name, c.phone as customer_phone, p.name as pet_name " +
            "FROM reminders r " +
            "LEFT JOIN customers c ON r.customer_id = c.id " +
            "LEFT JOIN pets p ON r.pet_id = p.id " +
            "ORDER BY r.due_date ASC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "dueDate", column = "due_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "sentTime", column = "sent_time"),
            @Result(property = "customer.name", column = "customer_name"),
            @Result(property = "customer.phone", column = "customer_phone"),
            @Result(property = "pet.name", column = "pet_name")
    })
    List<Reminder> selectAllWithDetails();
}
