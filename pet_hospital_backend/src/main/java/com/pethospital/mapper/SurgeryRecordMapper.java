package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pethospital.entity.SurgeryRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SurgeryRecordMapper extends BaseMapper<SurgeryRecord> {

    /**
     * 分页查询手术记录（带关联信息）
     */
    @Select("<script>" +
            "SELECT sr.*, " +
            "si.name as service_name, si.price as service_price, " +
            "p.name as pet_name, p.species as pet_species, " +
            "c.id as customer_id, c.name as customer_name, " +
            "e.name as surgeon_name " +
            "FROM surgery_records sr " +
            "LEFT JOIN services si ON sr.service_item_id = si.id " +
            "LEFT JOIN pets p ON sr.pet_id = p.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "LEFT JOIN employees e ON sr.surgeon_id = e.id " +
            "WHERE sr.deleted_at IS NULL " +
            "<if test='status != null'>AND sr.status = #{status}</if>" +
            "<if test='petName != null and petName != \"\"'>" +
            "AND p.name LIKE CONCAT('%', #{petName}, '%')" +
            "</if>" +
            "<if test='startDate != null and startDate != \"\"'>" +
            "AND sr.surgery_date &gt;= #{startDate}" +
            "</if>" +
            "<if test='endDate != null and endDate != \"\"'>" +
            "AND sr.surgery_date &lt;= #{endDate}" +
            "</if>" +
            "ORDER BY sr.surgery_date DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "serviceItemId", column = "service_item_id"),
            @Result(property = "medicalRecordId", column = "medical_record_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "surgeonId", column = "surgeon_id"),
            @Result(property = "assistantIds", column = "assistant_ids"),
            @Result(property = "surgeryType", column = "surgery_type"),
            @Result(property = "surgeryName", column = "surgery_name"),
            @Result(property = "surgeryDate", column = "surgery_date"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "preDiagnosis", column = "pre_diagnosis"),
            @Result(property = "surgeryProcess", column = "surgery_process"),
            @Result(property = "postDiagnosis", column = "post_diagnosis"),
            @Result(property = "anesthesiaType", column = "anesthesia_type"),
            @Result(property = "complications", column = "complications"),
            @Result(property = "notes", column = "notes"),
            @Result(property = "status", column = "status"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "serviceItem.name", column = "service_name"),
            @Result(property = "serviceItem.price", column = "service_price"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "pet.customer.id", column = "customer_id"),
            @Result(property = "pet.customer.name", column = "customer_name"),
            @Result(property = "surgeon.name", column = "surgeon_name")
    })
    IPage<SurgeryRecord> selectPageWithDetails(
            IPage<SurgeryRecord> page,
            @Param("status") Integer status,
            @Param("petName") String petName,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 根据ID查询手术详情（带关联信息）
     */
    @Select("SELECT sr.*, " +
            "si.name as service_name, si.price as service_price, " +
            "p.name as pet_name, p.species as pet_species, " +
            "c.id as customer_id, c.name as customer_name, " +
            "e.name as surgeon_name " +
            "FROM surgery_records sr " +
            "LEFT JOIN services si ON sr.service_item_id = si.id " +
            "LEFT JOIN pets p ON sr.pet_id = p.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "LEFT JOIN employees e ON sr.surgeon_id = e.id " +
            "WHERE sr.id = #{id} AND sr.deleted_at IS NULL")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "serviceItemId", column = "service_item_id"),
            @Result(property = "medicalRecordId", column = "medical_record_id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "surgeonId", column = "surgeon_id"),
            @Result(property = "assistantIds", column = "assistant_ids"),
            @Result(property = "surgeryType", column = "surgery_type"),
            @Result(property = "surgeryName", column = "surgery_name"),
            @Result(property = "surgeryDate", column = "surgery_date"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "preDiagnosis", column = "pre_diagnosis"),
            @Result(property = "surgeryProcess", column = "surgery_process"),
            @Result(property = "postDiagnosis", column = "post_diagnosis"),
            @Result(property = "anesthesiaType", column = "anesthesia_type"),
            @Result(property = "complications", column = "complications"),
            @Result(property = "notes", column = "notes"),
            @Result(property = "status", column = "status"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "serviceItem.name", column = "service_name"),
            @Result(property = "serviceItem.price", column = "service_price"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "pet.customer.id", column = "customer_id"),
            @Result(property = "pet.customer.name", column = "customer_name"),
            @Result(property = "surgeon.name", column = "surgeon_name")
    })
    SurgeryRecord selectByIdWithDetails(@Param("id") Long id);

    /**
     * 查询宠物的手术历史
     */
    @Select("SELECT sr.*, " +
            "si.name as service_name, " +
            "e.name as surgeon_name " +
            "FROM surgery_records sr " +
            "LEFT JOIN services si ON sr.service_item_id = si.id " +
            "LEFT JOIN employees e ON sr.surgeon_id = e.id " +
            "WHERE sr.pet_id = #{petId} AND sr.deleted_at IS NULL " +
            "ORDER BY sr.surgery_date DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "surgeryName", column = "surgery_name"),
            @Result(property = "surgeryDate", column = "surgery_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "serviceItem.name", column = "service_name"),
            @Result(property = "surgeon.name", column = "surgeon_name")
    })
    List<SurgeryRecord> selectByPetId(@Param("petId") Long petId);
}
