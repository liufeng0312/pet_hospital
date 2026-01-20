package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.Hospitalization;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HospitalizationMapper extends BaseMapper<Hospitalization> {

    @Select("SELECT h.*, p.name as pet_name, c.name as customer_name, c.phone as customer_phone " +
            "FROM hospitalizations h " +
            "LEFT JOIN pets p ON h.pet_id = p.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "WHERE h.status = 'ACTIVE'")
    @Results(id = "hospitalizationMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "bedNumber", column = "bed_number"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "status", column = "status"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.customer.name", column = "customer_name"),
            @Result(property = "pet.customer.phone", column = "customer_phone")
    })
    List<Hospitalization> selectActiveWithPet();

    @Select("<script>" +
            "SELECT h.*, p.name as pet_name, c.name as customer_name, c.phone as customer_phone " +
            "FROM hospitalizations h " +
            "LEFT JOIN pets p ON h.pet_id = p.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "<where>" +
            "<if test=\"petId != null\">AND h.pet_id = #{petId}</if> " +
            "<if test=\"petName != null and petName != ''\">AND p.name LIKE CONCAT('%', #{petName}, '%')</if> " +
            "<if test=\"status != null and status != ''\">AND h.status = #{status}</if> " +
            "<if test=\"startDate != null and startDate != ''\">AND DATE(h.start_time) <![CDATA[ >= ]]> #{startDate}</if> " +
            "<if test=\"endDate != null and endDate != ''\">AND DATE(h.start_time) <![CDATA[ <= ]]> #{endDate}</if> " +
            "</where>" +
            "ORDER BY h.start_time DESC" +
            "</script>")
    @ResultMap("hospitalizationMap")
    com.baomidou.mybatisplus.core.metadata.IPage<Hospitalization> selectPageWithDetail(
            com.baomidou.mybatisplus.core.metadata.IPage<Hospitalization> page, 
            @Param("petId") Long petId,
            @Param("petName") String petName,
            @Param("status") String status,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
