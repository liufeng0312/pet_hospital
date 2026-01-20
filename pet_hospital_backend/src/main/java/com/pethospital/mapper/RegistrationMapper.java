package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.entity.Registration;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    /**
     * 分页查询挂号列表，带关联信息
     */
    @Select("<script>" +
            "SELECT DISTINCT r.*, " +
            "COALESCE(wc.nickname, c.name) as customer_name, " +
            "COALESCE(wc.phone, c.phone) as customer_phone, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name " +
            "FROM registrations r " +
            "LEFT JOIN customers c ON r.customer_id = c.id " +
            "LEFT JOIN wechat_users wc ON wc.id = (" +
            "  SELECT wu.id FROM wechat_users wu " +
            "  WHERE wu.customer_id = r.customer_id " +
            "  ORDER BY wu.created_at DESC LIMIT 1" +
            ") " +
            "LEFT JOIN pets p ON r.pet_id = p.id " +
            "LEFT JOIN employees e ON r.doctor_id = e.id " +
            "WHERE 1=1 " +
            "<if test='status != null'> AND r.status = #{status} </if>" +
            "<if test='date != null'> AND DATE(r.created_at) = #{date} </if>" +
            "ORDER BY r.created_at DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "type", column = "type"),
            @Result(property = "appointmentTime", column = "appointment_time"),
            @Result(property = "queueNumber", column = "queue_number"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "customer.name", column = "customer_name"),
            @Result(property = "customer.phone", column = "customer_phone"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name")
    })
    List<Registration> selectPageWithDetails(Page<Registration> page,
                                              @Param("status") String status,
                                              @Param("date") LocalDate date);

    /**
     * 获取今日最大排队号
     */
    @Select("SELECT COALESCE(MAX(queue_number), 0) FROM registrations WHERE DATE(created_at) = CURDATE()")
    Integer getTodayMaxQueueNumber();

    /**
     * 查询排队看板数据 (今日候诊和就诊中的挂号)
     */
    @Select("SELECT DISTINCT r.*, " +
            "COALESCE(wc.nickname, c.name) as customer_name, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name " +
            "FROM registrations r " +
            "LEFT JOIN customers c ON r.customer_id = c.id " +
            "LEFT JOIN wechat_users wc ON wc.id = (" +
            "  SELECT wu.id FROM wechat_users wu " +
            "  WHERE wu.customer_id = r.customer_id " +
            "  ORDER BY wu.created_at DESC LIMIT 1" +
            ") " +
            "LEFT JOIN pets p ON r.pet_id = p.id " +
            "LEFT JOIN employees e ON r.doctor_id = e.id " +
            "WHERE DATE(r.created_at) = CURDATE() " +
            "AND r.status IN ('WAITING', 'IN_PROGRESS') " +
            "ORDER BY r.queue_number ASC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "type", column = "type"),
            @Result(property = "appointmentTime", column = "appointment_time"),
            @Result(property = "queueNumber", column = "queue_number"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "customer.name", column = "customer_name"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name")
    })
    List<Registration> getQueueBoard();

    /**
     * 检查挂号冲突 (同一宠物同一天是否已有未完成的挂号)
     */
    @Select("SELECT COUNT(*) FROM registrations " +
            "WHERE pet_id = #{petId} " +
            "AND DATE(created_at) = CURDATE() " +
            "AND status NOT IN ('COMPLETED', 'CANCELLED')")
    int countConflict(@Param("petId") Long petId);
}
