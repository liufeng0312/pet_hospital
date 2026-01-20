package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.entity.Consultation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {

    /**
     * 分页查询客户的咨询列表，带关联信息
     */
    @Select("<script>" +
            "SELECT c.*, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name " +
            "FROM consultations c " +
            "LEFT JOIN pets p ON c.pet_id = p.id " +
            "LEFT JOIN employees e ON c.reply_doctor_id = e.id " +
            "WHERE c.deleted_at IS NULL " +
            "AND c.customer_id = #{customerId} " +
            "<if test='status != null'> AND c.status = #{status} </if>" +
            "ORDER BY c.created_at DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "images", column = "images"),
            @Result(property = "status", column = "status"),
            @Result(property = "replyContent", column = "reply_content"),
            @Result(property = "replyDoctorId", column = "reply_doctor_id"),
            @Result(property = "replyTime", column = "reply_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name")
    })
    List<Consultation> selectPageByCustomer(Page<Consultation> page,
                                            @Param("customerId") Long customerId,
                                            @Param("status") String status);

    /**
     * 分页查询所有咨询列表（管理端），带关联信息
     */
    @Select("<script>" +
            "SELECT c.*, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name " +
            "FROM consultations c " +
            "LEFT JOIN pets p ON c.pet_id = p.id " +
            "LEFT JOIN employees e ON c.reply_doctor_id = e.id " +
            "WHERE c.deleted_at IS NULL " +
            "<if test='status != null'> AND c.status = #{status} </if>" +
            "ORDER BY c.created_at DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "images", column = "images"),
            @Result(property = "status", column = "status"),
            @Result(property = "replyContent", column = "reply_content"),
            @Result(property = "replyDoctorId", column = "reply_doctor_id"),
            @Result(property = "replyTime", column = "reply_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name")
    })
    List<Consultation> selectPageWithDetails(Page<Consultation> page,
                                            @Param("status") String status);

    /**
     * 根据ID查询咨询详情，带关联信息
     */
    @Select("SELECT c.*, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name " +
            "FROM consultations c " +
            "LEFT JOIN pets p ON c.pet_id = p.id " +
            "LEFT JOIN employees e ON c.reply_doctor_id = e.id " +
            "WHERE c.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "images", column = "images"),
            @Result(property = "status", column = "status"),
            @Result(property = "replyContent", column = "reply_content"),
            @Result(property = "replyDoctorId", column = "reply_doctor_id"),
            @Result(property = "replyTime", column = "reply_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name")
    })
    Consultation selectByIdWithDetails(@Param("id") Long id);
}
