package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.CareRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CareRecordMapper extends BaseMapper<CareRecord> {

    @Select("SELECT cr.*, e.name as operator_name " +
            "FROM care_records cr " +
            "LEFT JOIN employees e ON cr.operator_id = e.id " +
            "WHERE cr.hospitalization_id = #{hospitalizationId} " +
            "ORDER BY cr.record_time DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "hospitalizationId", column = "hospitalization_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "operatorId", column = "operator_id"),
            @Result(property = "recordTime", column = "record_time"),
            @Result(property = "operator.name", column = "operator_name")
    })
    List<CareRecord> selectByHospitalizationId(@Param("hospitalizationId") Long hospitalizationId);

    @Select("<script>" +
            "SELECT cr.*, e.name as operator_name, " +
            "h.bed_number as bed_number, p.name as pet_name " +
            "FROM care_records cr " +
            "LEFT JOIN employees e ON cr.operator_id = e.id " +
            "LEFT JOIN hospitalizations h ON cr.hospitalization_id = h.id " +
            "LEFT JOIN pets p ON h.pet_id = p.id " +
            "<where>" +
            "<if test=\"petName != null and petName != ''\">AND p.name LIKE CONCAT('%', #{petName}, '%')</if>" +
            "<if test=\"operatorId != null\">AND cr.operator_id = #{operatorId}</if>" +
            "<if test=\"startDate != null and startDate != ''\">AND DATE(cr.record_time) <![CDATA[ >= ]]> #{startDate}</if>" +
            "<if test=\"endDate != null and endDate != ''\">AND DATE(cr.record_time) <![CDATA[ <= ]]> #{endDate}</if>" +
            "</where>" +
            "ORDER BY cr.record_time DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "hospitalizationId", column = "hospitalization_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "operatorId", column = "operator_id"),
            @Result(property = "recordTime", column = "record_time"),
            @Result(property = "operator.name", column = "operator_name"),
            @Result(property = "hospitalization.bedNumber", column = "bed_number"),
            @Result(property = "hospitalization.pet.name", column = "pet_name")
    })
    com.baomidou.mybatisplus.core.metadata.IPage<CareRecord> selectPageWithDetail(
            com.baomidou.mybatisplus.core.metadata.IPage<CareRecord> page,
            @Param("petName") String petName,
            @Param("operatorId") Long operatorId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
