package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.Prescription;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {

    /**
     * 查询处方详情（含明细和药品信息）
     */
    /**
     * 查询处方详情（含明细和药品信息，以及关联的病历、宠物、客户信息）
     */
    @Select("SELECT p.*, " +
            "mr.id as mr_id, mr.symptoms as mr_symptoms, mr.diagnosis as mr_diagnosis, " +
            "pet.id as pet_id, pet.name as pet_name, pet.customer_id as c_id, " + // 注意这里获取 customer_id
            "c.name as c_name, " +
            "doc.id as doc_id, doc.name as doc_name " +
            "FROM prescriptions p " +
            "LEFT JOIN medical_records mr ON p.medical_record_id = mr.id " +
            "LEFT JOIN pets pet ON mr.pet_id = pet.id " +
            "LEFT JOIN customers c ON pet.customer_id = c.id " +
            "LEFT JOIN employees doc ON mr.doctor_id = doc.id " +
            "WHERE p.id = #{id}")
    @Results(id = "prescriptionDetailMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "medicalRecordId", column = "medical_record_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "medicalRecord.id", column = "mr_id"),
            @Result(property = "medicalRecord.symptoms", column = "mr_symptoms"),
            @Result(property = "medicalRecord.diagnosis", column = "mr_diagnosis"),
            @Result(property = "medicalRecord.pet.id", column = "pet_id"),
            @Result(property = "medicalRecord.pet.name", column = "pet_name"),
            @Result(property = "medicalRecord.pet.customerId", column = "c_id"), // 确保映射 customerId
            @Result(property = "medicalRecord.pet.customer.id", column = "c_id"),
            @Result(property = "medicalRecord.pet.customer.name", column = "c_name"),
            @Result(property = "medicalRecord.doctor.id", column = "doc_id"),
            @Result(property = "medicalRecord.doctor.name", column = "doc_name"),
            @Result(property = "items", column = "id", many = @Many(select = "selectItemsByPrescriptionId"))
    })
    Prescription selectDetailById(@Param("id") Long id);

    /**
     * 查询处方明细
     */
    @Select("SELECT pi.*, d.name as drug_name, d.specification as drug_specification, d.unit as drug_unit " +
            "FROM prescription_items pi " +
            "LEFT JOIN drugs d ON pi.drug_id = d.id " +
            "WHERE pi.prescription_id = #{prescriptionId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "prescriptionId", column = "prescription_id"),
            @Result(property = "drugId", column = "drug_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "dosage", column = "dosage"),
            @Result(property = "price", column = "price"),
            @Result(property = "drug.name", column = "drug_name"),
            @Result(property = "drug.specification", column = "drug_specification"),
            @Result(property = "drug.unit", column = "drug_unit")
    })
    List<com.pethospital.entity.PrescriptionItem> selectItemsByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
    @Select({
            "<script>",
            "SELECT p.*, ",
            "mr.id as mr_id, mr.symptoms as mr_symptoms, mr.diagnosis as mr_diagnosis, ",
            "pet.id as pet_id, pet.name as pet_name, ",
            "c.id as c_id, c.name as c_name, ",
            "doc.id as doc_id, doc.name as doc_name ",
            "FROM prescriptions p ",
            "LEFT JOIN medical_records mr ON p.medical_record_id = mr.id ",
            "LEFT JOIN pets pet ON mr.pet_id = pet.id ",
            "LEFT JOIN customers c ON pet.customer_id = c.id ",
            "LEFT JOIN employees doc ON mr.doctor_id = doc.id ",
            "WHERE 1=1 ",
            "<if test='status != null'>AND p.status = #{status}</if>",
            "<if test='startDate != null and startDate != \"\"'>AND p.created_at &gt;= #{startDate}</if>",
            "<if test='endDate != null and endDate != \"\"'>AND p.created_at &lt;= #{endDate}</if>",
            "<if test='searchText != null and searchText != \"\"'>",
            "AND (pet.name LIKE CONCAT('%', #{searchText}, '%') ",
            "OR c.name LIKE CONCAT('%', #{searchText}, '%') ",
            "OR doc.name LIKE CONCAT('%', #{searchText}, '%'))",
            "</if>",
            "ORDER BY p.created_at DESC",
            "</script>"
    })
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "medicalRecordId", column = "medical_record_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "medicalRecord.id", column = "mr_id"),
            @Result(property = "medicalRecord.pet.id", column = "pet_id"),
            @Result(property = "medicalRecord.pet.name", column = "pet_name"),
            @Result(property = "medicalRecord.pet.customer.id", column = "c_id"),
            @Result(property = "medicalRecord.pet.customer.name", column = "c_name"),
            @Result(property = "medicalRecord.doctor.id", column = "doc_id"),
            @Result(property = "medicalRecord.doctor.name", column = "doc_name")
    })
    com.baomidou.mybatisplus.core.metadata.IPage<Prescription> selectPageWithDetail(
            com.baomidou.mybatisplus.core.metadata.IPage<Prescription> page, 
            @Param("status") Integer status,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("searchText") String searchText);

}
