package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.entity.MedicalRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord> {

    @Select("<script>" +
            "SELECT m.*, " +
            "p.name as pet_name, p.species as pet_species, p.weight as pet_weight, " +
            "e.name as doctor_name, " +
            "c.name as customer_name, " +
            "r.queue_number as registration_queue_number " +
            "FROM medical_records m " +
            "LEFT JOIN pets p ON m.pet_id = p.id " +
            "LEFT JOIN employees e ON m.doctor_id = e.id " +
            "LEFT JOIN registrations r ON m.registration_id = r.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "WHERE m.deleted_at IS NULL " +
            "<if test='petId != null'> AND m.pet_id = #{petId} </if>" +
            "<if test='doctorId != null'> AND m.doctor_id = #{doctorId} </if>" +
            "ORDER BY m.visit_time DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "registrationId", column = "registration_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "symptoms", column = "symptoms"),
            @Result(property = "diagnosis", column = "diagnosis"),
            @Result(property = "treatmentPlan", column = "treatment_plan"),
            @Result(property = "doctorAdvice", column = "doctor_advice"),
            @Result(property = "visitTime", column = "visit_time"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "pet.weight", column = "pet_weight"),
            @Result(property = "doctor.name", column = "doctor_name"),
            @Result(property = "customer.name", column = "customer_name"),
            @Result(property = "registration.queueNumber", column = "registration_queue_number")
    })
    List<MedicalRecord> selectPageWithDetails(Page<MedicalRecord> page,
                                               @Param("petId") Long petId,
                                               @Param("doctorId") Long doctorId);

    /**
     * 查询宠物的病历历史
     */
    @Select("SELECT m.*, " +
            "e.name as doctor_name, " +
            "r.queue_number as registration_queue_number " +
            "FROM medical_records m " +
            "LEFT JOIN employees e ON m.doctor_id = e.id " +
            "LEFT JOIN registrations r ON m.registration_id = r.id " +
            "WHERE m.pet_id = #{petId} AND m.deleted_at IS NULL " +
            "ORDER BY m.visit_time DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "registrationId", column = "registration_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "symptoms", column = "symptoms"),
            @Result(property = "diagnosis", column = "diagnosis"),
            @Result(property = "treatmentPlan", column = "treatment_plan"),
            @Result(property = "doctorAdvice", column = "doctor_advice"),
            @Result(property = "visitTime", column = "visit_time"),
            @Result(property = "doctor.name", column = "doctor_name"),
            @Result(property = "registration.queueNumber", column = "registration_queue_number")
    })
    List<MedicalRecord> selectByPetId(@Param("petId") Long petId);

    /**
     * 查询挂号的病历
     */
    @Select("SELECT m.*, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name " +
            "FROM medical_records m " +
            "LEFT JOIN pets p ON m.pet_id = p.id " +
            "LEFT JOIN employees e ON m.doctor_id = e.id " +
            "WHERE m.registration_id = #{registrationId} AND m.deleted_at IS NULL " +
            "ORDER BY m.visit_time DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "registrationId", column = "registration_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "symptoms", column = "symptoms"),
            @Result(property = "diagnosis", column = "diagnosis"),
            @Result(property = "treatmentPlan", column = "treatment_plan"),
            @Result(property = "doctorAdvice", column = "doctor_advice"),
            @Result(property = "visitTime", column = "visit_time"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name")
    })
    List<MedicalRecord> selectByRegistrationId(@Param("registrationId") Long registrationId);
    
    /**
     * 根据ID查询病历详情，带关联信息
     */
    @Select("SELECT m.*, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name, " +
            "r.queue_number as registration_queue_number " +
            "FROM medical_records m " +
            "LEFT JOIN pets p ON m.pet_id = p.id " +
            "LEFT JOIN employees e ON m.doctor_id = e.id " +
            "LEFT JOIN registrations r ON m.registration_id = r.id " +
            "WHERE m.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "registrationId", column = "registration_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "symptoms", column = "symptoms"),
            @Result(property = "diagnosis", column = "diagnosis"),
            @Result(property = "treatmentPlan", column = "treatment_plan"),
            @Result(property = "doctorAdvice", column = "doctor_advice"),
            @Result(property = "visitTime", column = "visit_time"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name"),
            @Result(property = "registration.queueNumber", column = "registration_queue_number")
    })
    MedicalRecord selectByIdWithDetails(@Param("id") Long id);

    /**
     * 分页查询客户的病历列表，带关联信息
     */
    @Select("<script>" +
            "SELECT m.*, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name, " +
            "r.queue_number as registration_queue_number " +
            "FROM medical_records m " +
            "LEFT JOIN pets p ON m.pet_id = p.id " +
            "LEFT JOIN employees e ON m.doctor_id = e.id " +
            "LEFT JOIN registrations r ON m.registration_id = r.id " +
            "WHERE m.deleted_at IS NULL " +
            "AND p.customer_id = #{customerId} " +
            "<if test='petId != null'> AND m.pet_id = #{petId} </if>" +
            "ORDER BY m.visit_time DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "registrationId", column = "registration_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "symptoms", column = "symptoms"),
            @Result(property = "diagnosis", column = "diagnosis"),
            @Result(property = "treatmentPlan", column = "treatment_plan"),
            @Result(property = "doctorAdvice", column = "doctor_advice"),
            @Result(property = "visitTime", column = "visit_time"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name"),
            @Result(property = "registration.queueNumber", column = "registration_queue_number")
    })
    List<MedicalRecord> selectPageByCustomer(Page<MedicalRecord> page,
                                              @Param("customerId") Long customerId,
                                              @Param("petId") Long petId);
}
