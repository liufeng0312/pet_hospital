package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.entity.LabTest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LabTestMapper extends BaseMapper<LabTest> {

    @Select("SELECT lt.*, " +
            "mr.pet_id, p.name as pet_name, c.name as customer_name, " +
            "si.name as service_name, si.price as service_price " +
            "FROM lab_tests lt " +
            "LEFT JOIN medical_records mr ON lt.medical_record_id = mr.id " +
            "LEFT JOIN pets p ON mr.pet_id = p.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "LEFT JOIN services si ON lt.service_id = si.id " +
            "WHERE lt.id = #{id}")
    @ResultMap("labTestResultMap")
    LabTest selectByIdWithDetails(Long id);

    @Select("<script>" +
            "SELECT lt.*, " +
            "mr.pet_id, p.name as pet_name, c.name as customer_name, " +
            "si.name as service_name, si.price as service_price " +
            "FROM lab_tests lt " +
            "LEFT JOIN medical_records mr ON lt.medical_record_id = mr.id " +
            "LEFT JOIN pets p ON mr.pet_id = p.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "LEFT JOIN services si ON lt.service_id = si.id " +
            "WHERE 1=1 " +
            "<if test='status == 0'> AND (lt.result IS NULL OR lt.result = '') </if>" +
            "<if test='status == 1'> AND lt.result IS NOT NULL AND lt.result != '' </if>" +
            "<if test='searchText != null and searchText != \"\"'>" +
            " AND (p.name LIKE CONCAT('%', #{searchText}, '%') " +
            " OR c.name LIKE CONCAT('%', #{searchText}, '%') " +
            " OR si.name LIKE CONCAT('%', #{searchText}, '%'))" +
            "</if>" +
            "ORDER BY lt.test_time DESC" +
            "</script>")
    @Results(id = "labTestResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "medicalRecordId", column = "medical_record_id"),
            @Result(property = "serviceId", column = "service_id"),
            @Result(property = "result", column = "result"),
            @Result(property = "reportUrl", column = "report_url"),
            @Result(property = "testTime", column = "test_time"),
            @Result(property = "medicalRecord.pet.name", column = "pet_name"),
            @Result(property = "medicalRecord.pet.customer.name", column = "customer_name"),
            @Result(property = "serviceItem.name", column = "service_name"),
            @Result(property = "serviceItem.price", column = "service_price")
    })
    List<LabTest> selectPageWithDetails(Page<LabTest> page, @Param("status") Integer status, @Param("searchText") String searchText);
}
